package akkasearch

import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive
import akka.pattern.ask
import akka.util.Timeout
import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._



case class Person(id:Long,name:String, age:Int)

case class AddPerson(person:Person)

case class FindWithNameAndAge(name:String, age:Int)

object SearchCriteria{

  def props:Props = Props(new SearchCriteria())

}

class SearchCriteria extends Actor{

  implicit val timeout = Timeout(3 seconds)

  val ageCriteriaRef = context.actorOf(AgeCriteria.props)
  val nameCriteriaRef = context.actorOf(NameCriteria.props)


  def receive: Receive = {
    case  AddPerson(p) =>
     context.become(hasPeople(Map(p.id -> p)))
      sender ! p.id
  }

  def hasPeople(p:Map[Long,Person]):Receive = {
    case  FindWithNameAndAge(name, age) =>{
      def combined: Future[Seq[Long]] =  async{
        val futureAgeResult:Future[Seq[Long]] = (ageCriteriaRef ? GetPeopleWithAge(age)).mapTo[Seq[Long]]
        val futureNameResult:Future[Seq[Long]] = (ageCriteriaRef ? GetPeopleWithName(name)).mapTo[Seq[Long]]
        val res = Seq(await(futureAgeResult),  await(futureNameResult)).flatten
        res
      }

      sender ! Await.result(combined, 2 seconds)

    }
    case  AddPerson(p) =>
      context.become(hasPeople(Map(p.id -> p)))
      sender ! p.id

  }

}
