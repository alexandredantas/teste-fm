package br.com.gympass.interpreters

import br.com.gympass.adt._
import cats.~>
import cats.Id

object IdRaceInterpreter extends (Race ~> Id) {

  override def apply[A](fa: Race[A]): Id[A] = ???
}
