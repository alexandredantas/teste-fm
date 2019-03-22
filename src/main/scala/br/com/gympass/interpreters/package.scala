package br.com.gympass

import br.com.gympass.adt.Program
import cats.{Id, ~>}

package object interpreters {

  implicit val idInterpreter: Program ~> Id = new (Program ~> Id) {
    override def apply[A](fa: Program[A]): Id[A] = ???
  }
}
