package com.hm.routes
import spray.json._
import java.lang.Exception
import scala.util.control.Breaks._
import spray.http.MediaTypes.`text/html`
import spray.routing.HttpService

import scala.collection.mutable.ArrayBuffer
import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
  * Created by hari on 17/2/17.
  */
trait Routes extends HttpService {

  var a=ArrayBuffer[Int](40,30,23,12,10)
  def func={
    val b=92;
    var min=0
    var max=0

   a= a.sortWith(_ > _)
    breakable {
      for (i <- a) {
        if (b < i) {
          max = i
          break()

        }
        if (b > i) {
          min = i
          break()
        }
      }
    }
    println("max "+max)
    println("min "+min)
    a+=11
    print("after adding 11 ")
    for(i<-a) print(i+" ")
    println()
    a-=12

    print("after deleting 12 ")

    for(i<-a) print(i+" ")
    println()
  }
  val route =

    path("")
  {

   /* val request: HttpRequest = Http("http://localhost:8081")
    val response = request.asString.body

    val tmp = response.parseJson.asInstanceOf[JsArray]


      //tmp.elements.foreach(i=>i.asInstanceOf[JsNumber].))
      complete(tmp.prettyPrint)
   func*/


        complete(JsArray(a.map(i=>JsNumber(i)).toVector).prettyPrint)

  }~path("") {

      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>welcome to DistributedApp :)</h1>
              </body>
            </html>
          }
        }
      }
    }












}
