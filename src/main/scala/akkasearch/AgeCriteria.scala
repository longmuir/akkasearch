package akkasearch

import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive
import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.{async, await}



case class AddPersonWithAge(id:Long, age:Int)
case class GetPeopleWithAge(age:Int)

object AgeCriteria{
  def props:Props = Props(new AgeCriteria)
}

class AgeCriteria extends Actor {
  def receive: Receive = {
    case AddPersonWithAge(id,age) => context.become(hasPeople(Map(age ->Set(id))))
  }

  def hasPeople(p:Map[Int,Set[Long]]): Receive = {
    case AddPersonWithAge(id,name) =>
      context.become(
        hasPeople(p + (name -> p.get(name).fold[Set[Long]](Set(id))(ids => ids + id)))
      )
    case GetPeopleWithAge(age) =>
      sender ! p.get(age).fold(Set.empty[Long])(identity)
  }
}
