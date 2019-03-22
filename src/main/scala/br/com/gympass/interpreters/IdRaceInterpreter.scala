package br.com.gympass.interpreters

import br.com.gympass.adt._
import cats.~>
import cats.Id

object OptionRaceInterpreter extends (RaceOp ~> Id) {

  override def apply[A](fa: RaceOp[A]): Id[A] = ???
}
