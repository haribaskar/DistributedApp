package com.hm.app

import akka.actor.{Props}

import com.hm.ServerServiceActor
import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask
import akka.io.IO
import spray.http.{HttpRequest, HttpResponse, Uri}
import spray.can.Http
import spray.http._
import HttpMethods._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

/**
  * Created by hari on 17/2/17.
  */
object App extends App {

  implicit  val system=ActorSystem("on-spray-can")

  val service=system.actorOf(Props[ServerServiceActor],"DistributedApp")
  implicit  val timeout=Timeout(5)
  IO(Http) ! Http.Bind(service, "localhost", 8080)



}

