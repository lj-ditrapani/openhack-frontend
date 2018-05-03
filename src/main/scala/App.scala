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

  def test(): Unit = {

    val s = """
  [
  {
    "name": "tenant1",
    "endpoints": {
      "minecraft": "128.124.90.15:25565",
      "rcon": "128.124.90.15:25575"
    }
  },
  {
    "name": "tenant2",
    "endpoints": {
      "minecraft": "128.194.90.16:25565",
      "rcon": "128.194.90.16:25575"
    }
  }
  ]
  """
    val x = Jsoner.listStringToJson(s)
    println(x)
  }

  test()

  def main(args: Array[String]): Unit = {
    println("Hello world!")
    ui()
  }

  def ui(): Unit = {
    import scalatags.JsDom.all._
    val d =
      div(
        div(button(onclick := { () =>
          getList()
        })("REFRESH"), button(onclick := { () =>
          add()
        })("ADD")),
        div(id := "list")("hello world!")
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
              // servers.map(serverToHtml)
              println(s"/list returned servers $servers")
            case Left(error) => println(s"/list can't be parsed $error")
          }
          println(s"/list returns ${string}")
        case e: Failure[SimpleHttpResponse] =>
          println(s"Error, /list failed with $e")
      })
    (): Unit
  }

  // def serverToHtml(): ??? = { }
}

final case class Endpoints(minecraft: String, rcon: String)
final case class Server(name: String, endpoints: Endpoints)

@SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
object Jsoner {
  import io.circe.generic.auto._
  import io.circe.parser.decode

  type Servers = List[Server]

  def listStringToJson(servers: String): Either[io.circe.Error, Servers] =
    decode[Servers](servers)
}
