package github.bearrito.mailboxes

import akka.dispatch._
import akka.actor.{ActorSystem, ActorRef}
import com.typesafe.config.Config

trait ForgetFulQueueSemantics {
  def capacity: Int
}

object ForgetfulMailbox {

  class Q(val capacity: Int) extends MessageQueue with ForgetFulQueueSemantics {

    val queue = new ForgetfulQueue[Envelope](capacity)
    override def cleanUp(owner: ActorRef, deadLetters: MessageQueue): Unit = {
      while(hasMessages) { deadLetters.enqueue(owner, dequeue())}
    }

    override def hasMessages: Boolean = !queue.hasMessages
    override def numberOfMessages: Int = queue.numberOfMessages
    override def dequeue(): Envelope = queue.dequeue
    override def enqueue(receiver: ActorRef, handle: Envelope): Unit = queue.enqueue(handle)
  }
}

class ForgetfulMailbox(val capacity: Int)
  extends MailboxType with ProducesMessageQueue[ForgetfulMailbox.Q] {

  import ForgetfulMailbox._

  def this(settings: ActorSystem.Settings, config: Config) = {
    this(config.getInt("mailbox-capacity"))
  }

  final override def create(owner: Option[ActorRef],
                            system: Option[ActorSystem]): MessageQueue = {
    new Q(capacity)
  }
}