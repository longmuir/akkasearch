package akkasearch


import scala.concurrent.Await
import scala.util.{Failure, Success, Random}

import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpecLike
import org.scalatest.Matchers

import com.typesafe.config.ConfigFactory

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.{ TestActors, DefaultTimeout, ImplicitSender, TestKit }
import scala.concurrent.duration._
import scala.collection.immutable
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global



class SearchCriteriaTest extends TestKit(ActorSystem("TestKitUsageSpec"))
with DefaultTimeout with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  val searchRef = system.actorOf(SearchCriteria.props)

  override def afterAll {
    shutdown()
  }

  "A SearchCriteria Actor" should {
    "Respond successfully added Person" in {
      within(500 millis) {
        searchRef ! AddPerson( Person(1,"dave", 46))
        expectMsg[Long]( 1 )
        searchRef ! AddPerson( Person(2,"jamie", 33))
        expectMsg[Long]( 2 )
      }
    }
  }

  "A SearchCriteria Actor" should {
    "Respond successfully found Person" in {
      within(500 millis) {
        val res = Await.result(searchRef ? FindWithNameAndAge( "dave", 46), 3 seconds)
        assert(res == Seq(1))
      }
    }
  }


}
