import scala.io.Source
import scala.util.Using
import java.io.{FileNotFoundException, IOException}
import akka.actor.{ActorSystem, Props}

object Main {
  val JsonBidReqPath :String = "src/bid_request.json"

  def main(args: Array[String]): Unit = {

    val system = ActorSystem()
    // Create the BidResponseActor.
    val bidResponseActor = system.actorOf(Props[BidResponseActor])
    // Create the BidRequestActor and pass the reference of the BidResponseActor.
    val bidRequestActor = system.actorOf(Props(classOf[BidRequestActor], bidResponseActor))

    try {// Read the bid request JSON file, and call the bidRequestActor.
      val bidRequestJson = Using.resource(Source.fromFile(JsonBidReqPath)) { source => source.mkString }
      bidRequestActor ! bidRequestJson
    }
    catch {
      case _: FileNotFoundException => println(s"Couldn't find $JsonBidReqPath file")
      case _: IOException => println(s"An error occurred: IOException when trying to read $JsonBidReqPath file")
    }

    system.terminate() // Shutdown the actor system.
  }
}