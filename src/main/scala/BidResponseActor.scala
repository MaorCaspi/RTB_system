import akka.actor.Actor
import spray.json._
import spray.json.DefaultJsonProtocol._

class BidResponseActor extends Actor {
  def receive: Receive = {
    case bidRequestJson: String =>
      val errorMessage = "This field is missing at the bid request"
      try {
        val bidRequest = bidRequestJson.parseJson
        val mapBidRequest = bidRequest.asJsObject.fields
        val requestId = mapBidRequest.getOrElse("id", JsString(errorMessage)).convertTo[String]
        val impId = mapBidRequest.getOrElse("imp", JsObject.empty).asInstanceOf[JsArray].elements
          .headOption.flatMap(_.asJsObject.fields.get("id").map(_.convertTo[String])).getOrElse(errorMessage)

        val bidResponse = JsObject(
          "id" -> JsString(requestId), // ID of the bid request to which this is a response.
          "seatbid" -> JsArray(JsObject(
            "seat" -> JsString("512"), // ID of the buyer seat.
            "bid" -> JsArray(JsObject(
              "id" -> JsString("1"), // Bidder generated bid ID to assist with logging/tracking.
              "impid" -> JsString(impId), // ID of the Imp object in the related bid request.
              "price" -> JsNumber(9.43) // Bid price expressed as CPM.
            ))
          ))
        )
        sender() ! bidResponse
      }
      catch {
        case _: spray.json.JsonParser.ParsingException =>
          println("An error occurred: The bid request is not in a valid OpenRTB v2.5 JSON format")
      }
  }
}