package github.bearrito.mailboxes


class ForgetfulQueue[A](capacity: Int) {

  val innerQueue = new java.util.concurrent.LinkedBlockingDeque[A](capacity)

  def hasMessages = !innerQueue.isEmpty
  def numberOfMessages = innerQueue.size
  def dequeue = {
    innerQueue.pollFirst()
  }
  def enqueue(e : A) = {
    // Not sure if this respects any constraints akka makes about message order.
    // Thread 1 tries to offer last is refused. Enters in loop.
    // Polls the first. Queue is now below capacity.
    // Thread 2 offers and and is accepted
    // Thread 1 offers is not accepted re-enters loops and polls
    // Thread 1 offers and is accepted.
      while(!innerQueue.offerLast(e)){
        innerQueue.pollFirst()
      }
  }

}
