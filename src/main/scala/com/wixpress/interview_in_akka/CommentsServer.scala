package com.wixpress.interview_in_akka

import java.util.UUID

import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl.Flow

object CommentsRepository {
  type SiteId = UUID

  case class Comment(text: String)
}

trait CommentsRepository {
  import CommentsRepository._

  def addComment(siteId: SiteId, text: String): Comment

  def getComments(siteId: SiteId): Seq[Comment]
}

object CommentsServer {

  import CommentsRepository._

  // External API
  type RequestId = Any

  sealed trait Request

  sealed trait Response

  case class AddCommentRequest(siteId: SiteId, text: String) extends Request

  case class AddCommentResponse(comment: Comment) extends Response

  case class GetCommentsRequest(siteId: SiteId) extends Request

  case class GetCommentsResponse(comments: Seq[Comment]) extends Response

  val repository: CommentsRepository = ???

  // In typical scenario, we express our server handlers as Request => Response

  val handler: Request ⇒ Response = {
    case AddCommentRequest(siteId, text) ⇒
      val comment = repository.addComment(siteId, text)

      AddCommentResponse(comment)
    case GetCommentsRequest(siteId) ⇒
      val comments = repository.getComments(siteId)

      GetCommentsResponse(comments)
  }

  // In Akka-streams, this would be a Flow:

  val handlerFlow: Flow[(RequestId, Request), (RequestId, Response), _] = Flow.fromFunction {
    case (rid, request) ⇒
      val response = handler(request)

      (rid, response)
  }

  val wsUnmarshaller: Flow[Message, (RequestId, Request), _] = ???
  val wsMarshaller: Flow[(RequestId, Response), Message, _] = ???
  val ws: UpgradeToWebSocket = ???

  // This will work:
  ws.handleMessages(wsUnmarshaller.via(handlerFlow).via(wsMarshaller))


  // Add throttling on adding comments




  // Show unidirectional example




}
