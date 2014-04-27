package github.bearrito.mailboxes

import org.scalatest._
import akka.actor.{Actor, ActorSystem}
import akka.testkit.TestKit
import akka.testkit.ImplicitSender
import akka.util.Timeout
import scala.concurrent.{ExecutionContext, Await, Future}
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
/**
 * Created by bearrito on 4/26/14.
 */

class RedisSpecs(_system: ActorSystem) extends TestKit(_system) with FunSuiteLike
with ImplicitSender with BeforeAndAfter with BeforeAndAfterAll {



  def this() = this(ActorSystem("demo"))
  //implicit val timeout = Timeout(3 seconds)

  test("Single"){

    val nbbq = new ForgetfulQueue[Int](2)

    implicit val _ =  Timeout(600.millis)
    val fut1 = Future{
      nbbq.enqueue(1)

    }

    val result = Await.result(fut1,500.millis)
    assert(nbbq.dequeue == 1)
  }

  test("One and then Two"){

    val nbbq = new ForgetfulQueue[Int](2)

    implicit val _ =  Timeout(600.millis)
    val fut1 = Future{
      nbbq.enqueue(1)
      nbbq.enqueue(2)

    }

    val result = Await.result(fut1,500.millis)
    assert(nbbq.dequeue == 1)
    assert(nbbq.dequeue == 2)
  }

  test("One and then Two then Three"){

    val nbbq = new ForgetfulQueue[Int](2)

    implicit val _ =  Timeout(600.millis)
    val fut1 = Future{
      nbbq.enqueue(1)
      nbbq.enqueue(2)
      nbbq.enqueue(3)

    }

    val result = Await.result(fut1,500.millis)
    assert(nbbq.dequeue == 2)
    assert(nbbq.dequeue == 3)
  }

  test("Two writers"){

    val nbbq = new ForgetfulQueue[Int](2)

    implicit val _ =  Timeout(600.millis)
    val fut1 = Future{
      nbbq.enqueue(1)
      nbbq.enqueue(2)
      nbbq.enqueue(3)

    }
    val fut2 = Future{

      nbbq.enqueue(4)
      nbbq.enqueue(5)
      nbbq.enqueue(6)

    }

    val r1 = Await.result(fut1,500.millis)
    val r2 = Await.result(fut2,500.millis)
    val head = nbbq.dequeue
    val tail = nbbq.dequeue
    assert(head == 2 || head == 5 )
    assert(tail == 2 || tail == 3 || tail == 5 || tail == 6)
  }

  test("Many writers"){

    val nbbq = new ForgetfulQueue[Int](2)

    implicit val _ =  Timeout(600.millis)
    val listOfFutures = (1 to 100).map(i =>{
      Future{
        nbbq.enqueue(i)
      }

    })


    val r1 = Await.result(Future.sequence(listOfFutures), 2 seconds)
    assert(nbbq.numberOfMessages == 2)

    nbbq.enqueue(1001)
    nbbq.enqueue(1002)

    val head = nbbq.dequeue
    val tail = nbbq.dequeue
    assert( head == 1001 )
    assert(tail == 1002 )

    assert(nbbq.numberOfMessages == 0)


  }

}