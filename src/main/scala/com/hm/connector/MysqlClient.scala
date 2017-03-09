package com.hm.connector
import collection.JavaConversions._
import java.io.{ByteArrayInputStream, ObjectInputStream}
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}
import java.util
import akka.actor.ActorSystem

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

/**
  * Created by hari on 27/2/17.
  */
object MysqlClient {

  private val dbc = "jdbc:mysql://" + "127.0.0.1" + ":" + 3306 + "/" + "distributed" + "?user=" + "root" + "&password=" + "root"
  classOf[com.mysql.jdbc.Driver]
  private var conn: Connection = null



  def getConnection: Connection = {
    if(conn ==null) {
      conn = DriverManager.getConnection(dbc)
      conn.setAutoCommit(false)
      conn
    }else if (conn.isClosed) {
      conn = DriverManager.getConnection(dbc)
      conn.setAutoCommit(false)
      conn
    }else{
      conn
    }

  }

  val autoIncValuesForTable: Map[String, Array[String]] = Map(
    "liveinstance" -> Array("id")

  )


  def closeConnection() = conn.close()

  def executeQuery(query: String): Boolean = {
    val statement = getConnection.createStatement()
    try
      statement.execute(query)
    finally statement.close()
  }

  def getResultSet(query: String): ResultSet={
    val statement=getConnection.createStatement()
    statement.executeQuery(query)
  }

  def insert(tableName: String, elements: Map[String, Any]): Int = {
    try {
      val colNames: ArrayBuffer[String] = ArrayBuffer()
      val values: ArrayBuffer[Any] = ArrayBuffer()
      elements.foreach(i => {
        colNames += i._1
        values += i._2
      })

      val insertQuery = "INSERT INTO " + tableName + " (" + colNames.mkString(",") + ") VALUES (" + colNames.indices.map(i => "?").mkString(",") + ")"

      val returnColumns: Array[String] = autoIncValuesForTable.getOrElse(tableName, Array())
      val preparedStatement: PreparedStatement = getConnection.prepareStatement(insertQuery, returnColumns)

      values.zipWithIndex.foreach(i => addToPreparedStatement(i._1, i._2 + 1, preparedStatement))
      var generatedId: Int = 0
      try {

        preparedStatement.executeUpdate()
        if (returnColumns.nonEmpty) {
          val gkSet = preparedStatement.getGeneratedKeys
          if (gkSet.next()) {
            generatedId = gkSet.getInt(1)
          }
        }
      }
      finally preparedStatement.close()

      generatedId
    } catch {
      case e: Exception => e.printStackTrace()
        0
    }
  }
  private def addToPreparedStatement(value: Any, index: Int, preparedStatement: PreparedStatement) = {
    value match {
      case v: Long => preparedStatement.setLong(index, v)
      case v: Int => preparedStatement.setInt(index, v)
      case v: Double => preparedStatement.setDouble(index, v)
      case v: String => preparedStatement.setString(index, v)

      case v: Array[Byte] => preparedStatement.setBytes(index, v)
      case v: Serializable => preparedStatement.setObject(index, v)
      case _ => preparedStatement.setString(index, value.toString)
    }
  }




 /* import system.dispatcher
  import scala.concurrent.duration._
  // ...now with system in current scope:
  val system=ActorSystem("on-spray-can")
  system.scheduler.schedule(10 seconds, 10 seconds) {
    MysqlClient.statement.executeBatch()
    MysqlClient.getConnection.commit()
  }*/
  def serialiseObject(obj: Object,p:Int):Int={

    val pstmt = getConnection.prepareStatement("update object set value=?,updated=?");
    pstmt.setObject(1,obj)
    pstmt.setInt(2,p)
    val stat=pstmt.executeUpdate()
   /* val rs = pstmt.getGeneratedKeys();
    var serializedId = -1;
    if (rs.next()) {
      serializedId = rs.getInt(1);
    }*/
    //rs.close();
    pstmt.close();
    MysqlClient.getConnection.commit()
    println("Java object serialized to database. Object:"+stat)
    stat

  }
def deSerialiseObject(serializeId:Int):util.TreeMap[Int,Int]={
 val pstmt = getConnection.prepareStatement("select value from object where id=?")
  pstmt.setInt(1, serializeId)

  val rs = pstmt.executeQuery()
  rs.next()

  val buf = rs.getBytes(1)


   val objectIn = new ObjectInputStream(new ByteArrayInputStream(buf))

  val deSerializedObject = objectIn.readObject()
  println("Deserialise object"+deSerializedObject)
  rs.close()
  pstmt.close()
  deSerializedObject.asInstanceOf[util.TreeMap[Int,Int]]
}
  def getLiveInstances:Array[(String,String)]={
    val pstmt=getConnection.prepareStatement("select * from liveinstance")
    val rs=pstmt.executeQuery()
    val buffer=new collection.mutable.ArrayBuffer[(String,String)]
    while(rs.next())
      {
        buffer.add((rs.getString("interface"),rs.getString("port")))
      }
      buffer.toArray
  }
  def checkStatus(p:Int):Boolean={


    val rs = MysqlClient.getResultSet("select * from object where updated <> "+p)
    val response = if(rs.next()){
      true
    }else{
      println("Invalid session")
      false
    }
    rs.close()
    response
  }

}
