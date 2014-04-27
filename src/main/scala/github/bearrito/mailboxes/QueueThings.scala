package github.bearrito.mailboxes


class ForgetfulQueue[A](capacity: Int) {

  val innerQueue = new java.util.concurrent.LinkedBlockingDeque[A](capacity)

  def hasMessages = !innerQueue.isEmpty
  def numberOfMessages = innerQueue.size
  def dequeue = {
    innerQueue.pollFirst()
  }
  def enqueue(e : A) = {
      while(!innerQueue.offerLast(e)){
        innerQueue.pollFirst()
      }
  }

}
