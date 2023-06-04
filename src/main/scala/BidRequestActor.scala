import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import spray.json.DefaultJsonProtocol.IntJsonFormat
import spray.json._

class BidRequestActor(responseActor: ActorRef) extends Actor {
  def receive: Receive = {
    case bidRequestJson: String =>
      try {
        val bidRequest = bidRequestJson.parseJson
        val mapBidRequest = bidRequest.asJsObject.fields
        val requestId = mapBidRequest.getOrElse("id", "Not found")
        println(s"Request ID: $requestId")
        val userAgent = mapBidRequest.getOrElse("device", JsObject.empty).asJsObject.fields.getOrElse("ua", "Not found")
        println(s"User Agent: $userAgent")

        //tmax in the JSON bid req is maximum time in milliseconds the exchange allows for bids to
        //be received. It's not required so if it's not mentioned we will wait 1000 milliseconds.
        val maxTime : Int = mapBidRequest.getOrElse("tmax", JsNumber(1000)).convertTo[Int]
        if(maxTime < 10 || maxTime > 10000)
          throw new Exception("tmax field max be greater than 10 milliseconds and smaller than 10000 milliseconds")

        // Send bid request to the BidResponseActor and wait for response with a maximum timeout of maxTime.
        implicit val timeout: Timeout = maxTime.millisecond
        val future: Future[JsObject] = (responseActor ? bidRequestJson).mapTo[JsObject]

        future.onComplete {
          case Success(bidResponse) =>
            println(s"Bid Response: $bidResponse")

          case Failure(_) =>
            println(s"BidRequest waited ${timeout.duration} and did not receive any response.")
        }
        Await.ready(future, timeout.duration) // Wait for the future to complete.
      }
      catch {
        case _: spray.json.JsonParser.ParsingException =>
          println("An error occurred: The bid request is not in a valid OpenRTB v2.5 JSON format")
        case _: DeserializationException =>
          println("An error occurred: tmax field must be a number")
        case ex: Exception =>
          println(s"An error occurred: ${ex.getMessage}")
      }
  }
}