package akkasearch

import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive


case class AddPersonWithName(id: Long, name: String)
case class GetPeopleWithName(name: String)


object NameCriteria{
  def props:Props = Props(new NameCriteria)
}


class NameCriteria extends Actor {
  def receive: Receive = {
    case AddPersonWithName(id, name) => context.become(hasPeople(Map(name -> Set(id))))
  }


  def hasPeople(p: Map[String, Set[Long]]): Receive = {
    case AddPersonWithName(id, name) =>
      context.become(
        hasPeople(p + (name -> p.get(name).fold[Set[Long]](Set(id))(ids => ids + id)))
      )
    case GetPeopleWithName(name) =>
      sender ! p.get(name).fold(Set.empty[Long])(identity)
  }
}
