package info.ditrapani

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method.{DELETE, POST}
import fr.hmil.roshttp.response.SimpleHttpResponse
import monix.execution.Scheduler.Implicits.global
import org.scalajs.jquery.jQuery
import scala.util.{Failure, Success}

import org.scalajs.dom
import dom.document

object App {
  private val host = document.location.host.split(":")(0)
  private val port = document.location.host.split(":")(1).toInt
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    ui()
  }

  def ui(): Unit = {
    import scalatags.JsDom.all._
    val d =
      div(
        div(button(onclick := {() => getList()})("REFRESH"), button(onclick := {() => add()})("ADD")),
        div(id := "list")("hello world!")
      )
    jQuery("body").append(d.render)
    (): Unit
  }

  def add(): Unit = {
    println("clicked ADD!")
    HttpRequest()
      .withHost(host)
      .withPort(port)
      .withMethod(POST)
      .withPath("/add")
      .send()
      .onComplete({
      case res: Success[SimpleHttpResponse] => println(s"/add returns ${res.get.body}")
      case e: Failure[SimpleHttpResponse] => println(s"Error, /add failed with $e")
    })
  }

  def getList(): Unit = {
    println("clicked REFRESH!")
    (): Unit
  }
}
