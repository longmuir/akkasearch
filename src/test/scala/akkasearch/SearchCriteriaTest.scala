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
    "Find a single person by name or age, with one person indexed" in {
      within(500 millis) {
        searchRef ! AddPerson( Person(1,"dave", 46))
        expectMsg[Long]( 1 )

        val res = Await.result(searchRef ? FindWithNameOrAge( "dave", 46), 3 seconds)
        assert(res == Set(1))
      }
    }

    "Find a single person by name or age, with multiple people indexed" in {
      within(500 millis) {
        searchRef ! AddPerson( Person(1, "Dave", 15))
        searchRef ! AddPerson( Person(2, "Dave", 46))
        searchRef ! AddPerson( Person(3, "Jamie", 15))
        searchRef ! AddPerson( Person(4, "Jamie", 33))

        val res = Await.result(searchRef ? FindWithNameOrAge("Dave", 15), 3 seconds)
        assert(res == Set(1, 2, 3))
      }
    }

    "Find a single person by name and age, with multiple people indexed" in {
      within(500 millis) {
        searchRef ! AddPerson( Person(1, "Dave", 15))
        searchRef ! AddPerson( Person(2, "Dave", 46))
        searchRef ! AddPerson( Person(3, "Jamie", 15))
        searchRef ! AddPerson( Person(4, "Jamie", 33))

        val res = Await.result(searchRef ? FindWithNameAndAge("Dave", 15), 3 seconds)
        assert(res == Set(1))
      }
    }

  }


}
