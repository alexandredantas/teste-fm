package br.com.gympass.interpreters

import cats.{Id, ~>}

object IdDummyInterpreter extends (Id ~> Id) {
  override def apply[A](fa: Id[A]): Id[A] = fa
}
