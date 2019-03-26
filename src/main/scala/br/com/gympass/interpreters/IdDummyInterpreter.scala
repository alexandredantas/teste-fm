package br.com.gympass.interpreters

import cats.{Id, ~>}

/**
  * This interpreter is only to satisfy EitherK and keep program to be extended with additional algebras without needing
  * refactor (changing from liftF to injectK)
  */
object IdDummyInterpreter extends (Id ~> Id) {
  override def apply[A](fa: Id[A]): Id[A] = fa
}
