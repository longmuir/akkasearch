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

case class FindWithNameOrAge(name:String, age:Int)

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
      ageCriteriaRef ! AddPersonWithAge(p.id, p.age)
      nameCriteriaRef ! AddPersonWithName(p.id, p.name)
      context.become(hasPeople(Map(p.id -> p)))
      sender ! p.id
  }

  def hasPeople(p:Map[Long,Person]):Receive = {
    case  FindWithNameOrAge(name, age) => {
      def combined: Future[Set[Long]] =  async{
        val futureAgeResult:Future[Set[Long]] = (ageCriteriaRef ? GetPeopleWithAge(age)).mapTo[Set[Long]]
        val futureNameResult:Future[Set[Long]] = (nameCriteriaRef ? GetPeopleWithName(name)).mapTo[Set[Long]]
        Set(await(futureAgeResult),  await(futureNameResult)).flatten
      }
      sender ! Await.result(combined, 2 seconds)
    }

    case  FindWithNameAndAge(name, age) => {
      def combined: Future[Set[Long]] =  async{
        val futureAgeResult:Future[Set[Long]] = (ageCriteriaRef ? GetPeopleWithAge(age)).mapTo[Set[Long]]
        val futureNameResult:Future[Set[Long]] = (nameCriteriaRef ? GetPeopleWithName(name)).mapTo[Set[Long]]
        Set(await(futureAgeResult).intersect(await(futureNameResult))).flatten
      }
      sender ! Await.result(combined, 2 seconds)
    }


    case  AddPerson(p) =>
      ageCriteriaRef ! AddPersonWithAge(p.id, p.age)
      nameCriteriaRef ! AddPersonWithName(p.id, p.name)
      context.become(hasPeople(Map(p.id -> p)))
      sender ! p.id
  }


}
