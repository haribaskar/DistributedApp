package com.hm.routes
import java.io.File

import spray.json._
import java.lang.Exception
import java.net.InetAddress
import java.util
import scala.collection.immutable.ListMap
import com.hm.connector.MysqlClient
import com.maxmind.geoip2.DatabaseReader

import collection.JavaConverters._
import scala.collection.JavaConversions._
import scala.util.control.Breaks._
import spray.http.MediaTypes.`text/html`
import spray.routing.HttpService

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
  * Created by hari on 17/2/17.
  */
trait Routes extends HttpService {

var map=new util.TreeMap[Int,Int]()
  var m=new util.TreeMap[Int,Int]()
def getVal= {

  map = MysqlClient.deSerialiseObject(1)
  if (map == null) {
    map.put(5, 6)
    map.put(4, 5)
    map.put(3, 4)
    map.put(2, 3)
    map.put(1, 2)
  }
}

  val route =

    path("")
  {

val rs=MysqlClient.getLiveInstances

   // rs.map(i=>print(i._1+"  "+i._2))
   /* val request: HttpRequest = Http("http://localhost:8080/add?e=56")
    val response = request.asString.body

    val tmp = response.parseJson.asInstanceOf[JsArray]


      //tmp.elements.foreach(i=>i.asInstanceOf[JsNumber].))
      complete(tmp.prettyPrint)
   func*/

   complete("")
       // complete(JsArray(a.map(i=>JsNumber(i)).toVector).prettyPrint)

  }~path("add") {
       parameter("e"){ (e)=>
         getVal
         add(map,e.toInt)


         if(MysqlClient.checkStatus(8083)) {
           getVal
           add(map,e.toInt)

         }
         MysqlClient.serialiseObject(map,8083)
         complete("add"+map)
       }


    }~path("del"){
      parameter("e"){ (e)=>
        getVal
        map.values().remove(e.toInt)
        if(MysqlClient.checkStatus(8083)) {
          getVal
          map.values().remove(e.toInt)

        }
        MysqlClient.serialiseObject(map,8083)
        complete("del"+map)
      }
    }~path("test")
  {
    getVal
    val q=ListMap(map.toSeq.sortWith(_._2 > _._2):_*)
    var max=0
    var min=0
    println(q)
    parameter("e"){ (e)=>
      breakable{
    q.foreach(i=>{
      println(i._2)
      if(i._2>e.toInt){max=i._2
      }

    })
        q.foreach(i=>{
          if(i._2<e.toInt) {
            min = i._2

          }
        })
      }
    complete("max "+max+" Min "+min)}
  }



def add(treeMap: util.TreeMap[Int,Int],e:Int)={

  var i=0;
  if(!map.isEmpty)
  {
    i=map.last._1+1
  }
  map.put(i,e.toInt)

}








}
