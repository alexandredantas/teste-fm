package br.com.gympass

import br.com.gympass.adt.Race
import cats.{Id, ~>}

package object interpreters {

  implicit val idInterpreter: Race ~> Id = new (Race ~> Id) {
    override def apply[A](fa: Race[A]): Id[A] = ???
  }

}
