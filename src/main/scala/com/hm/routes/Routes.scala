package com.hm.routes
import java.io.File

import spray.json._
import java.lang.Exception
import java.net.InetAddress
import java.util

import com.hm.config.Configuration

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
trait Routes extends HttpService with Configuration{

var map=new util.TreeMap[Int,Int]()
  var m=new util.TreeMap[Int,Int]()
/*def getVal= {

  map = MysqlClient.deSerialiseObject(1)
  if (map == null) {
    map.put(5, 6)
    map.put(4, 5)
    map.put(3, 4)
    map.put(2, 3)
    map.put(1, 2)
  }
}*/

  val route =

    path("") {

      val rs = MysqlClient.getLiveInstances(serviceHost, servicePort)

      var host = ""
      var port = ""
      rs.foreach(i => {
        host = i._1
        port = i._2
      })
      println(host)
      println(port)
      if (!(host.equals("") && port.equals(""))) {

        try {


          val request: HttpRequest = Http("http://" + host + ":" + port + "/list")
          val response = request.asString.body

          val tmp = response.parseJson.asInstanceOf[JsArray]
          println("inside block" + tmp.elements.foreach(i => {
            var k = i.asInstanceOf[JsObject].toString()
            map.put(Integer.parseInt(k.substring(k.indexOf("{") + 2, k.indexOf(":") - 1)), Integer.parseInt(k.substring(k.indexOf(":") + 1, k.indexOf("}"))))
            /* println(k.substring(k.indexOf(":")+1,k.indexOf("}")))
         println(k.substring(k.indexOf("{")+2,k.indexOf(":")-1))
         println(k)*/
          }))

          //tmp.elements.foreach(i=>i.asInstanceOf[JsNumber].))
          complete("ii" + response)
          // func
        }
        catch
        {
          case e:Exception=> {MysqlClient.delInstance(host,port)

          }

        }
        finally
        {

        }
    }
   complete("io")
       // complete(JsArray(a.map(i=>JsNumber(i)).toVector).prettyPrint)

  }~path("list")
  {


    complete(JsArray(
      map.toVector.map(i=>JsObject(i._1+""->JsNumber(i._2)))
    ).prettyPrint)
    /*complete(""+map.map(i=>{
      JsArray(JsNumber(i._1),JsNumber(i._2)).toString()
    }))*/
  }~path("add") {
       parameter("e"){ (e)=>

         add(map,e.toInt)
         val rs=MysqlClient.getInstances(serviceHost,servicePort)

         rs.foreach(i=>{
           val request: HttpRequest = Http("http://"+i._1+":"+i._2+"/badd?e="+e.toInt+"")
           print(i._2)
           println(request.asString.body+"broad")
         })




         complete("add"+map)
       }


    }~path("badd")
  {
    parameter("e"){ (e)=>

      add(map,e.toInt)




      complete("add"+map)
    }

  }~path("bdel")
  {
    parameter("e"){ (e)=>

      map.values().remove(e.toInt)

      complete("del"+map)
    }
  }~path("del"){
      parameter("e"){ (e)=>

        map.values().remove(e.toInt)
        val rs=MysqlClient.getInstances(serviceHost,servicePort)
        rs.foreach(i=>{
          val request: HttpRequest = Http("http://"+i._1+":"+i._2+"/bdel?e="+e.toInt+"")
        })
        complete("del"+map)
      }
    }~path("test")
  {

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
