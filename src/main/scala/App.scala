package info.ditrapani

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.Method.{DELETE, POST}
import fr.hmil.roshttp.response.SimpleHttpResponse
import monix.execution.Scheduler.Implicits.global
import org.scalajs.jquery.jQuery
import scala.util.{Failure, Success}
import scalatags.JsDom.TypedTag
import org.scalajs.dom.html.Div

import org.scalajs.dom
import dom.document

object App {
  val host = document.location.host.split(":")(0)
  val port = document.location.host.split(":")(1).toInt

  def main(args: Array[String]): Unit = {
    ui()
    getList()
  }

  def ui(): Unit = {
    import scalatags.JsDom.all._
    val d =
      div(
        div(
          button(onclick := { () =>
            getList()
          })("REFRESH"),
          button(onclick := { () =>
            add()
          })("ADD")
        ),
        div(
          id := "list"
        )
      )
    jQuery("body").append(d.render)
    (): Unit
  }

  @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
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
    HttpRequest()
      .withHost(host)
      .withPort(port)
      .withPath("/list")
      .send()
      .onComplete({
        case res: Success[SimpleHttpResponse] =>
          @SuppressWarnings(Array("org.wartremover.warts.TryPartial"))
          val string = res.get.body
          Jsoner.listStringToJson(string) match {
            case Right(servers) =>
              println(s"/list returned servers $servers")
              jQuery("#list").html("")
              jQuery("#list").append(divWrap(servers.map(serverToHtml)).render)
            case Left(error) => println(s"/list can't be parsed $error")
          }
          println(s"/list returns ${string}")
        case e: Failure[SimpleHttpResponse] =>
          println(s"Error, /list failed with $e")
      })
    (): Unit
  }

  def divWrap(divs: List[TypedTag[Div]]): TypedTag[Div] = {
    import scalatags.JsDom.all._
    div(divs)
  }

  def serverToHtml(server: Server): TypedTag[Div] = {
    import scalatags.JsDom.all._
    div(server.toString, button(onclick := { () =>
      server.delete()
    })("DELETE"))
  }
}

final case class Endpoints(minecraft: String, rcon: String)
final case class Server(name: String, endpoints: Endpoints) {
  def delete(): Unit = {
    HttpRequest()
      .withHost(App.host)
      .withPort(App.port)
      .withMethod(DELETE)
      .withPath("/" + this.endpoints.minecraft.split(":")(0).replace(".", "-"))
      .send()
      .onComplete({
        case res: Success[SimpleHttpResponse] => println(s"delete $this returns ${res.get.body}")
        case e: Failure[SimpleHttpResponse] => println(s"Error, delete $this failed with $e")
      })
  }
}

@SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
object Jsoner {
  import io.circe.generic.auto._
  import io.circe.parser.decode

  type Servers = List[Server]

  def listStringToJson(servers: String): Either[io.circe.Error, Servers] =
    decode[Servers](servers)
}
