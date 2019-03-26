package br.com.gympass

import br.com.gympass.adt.Program
import cats.{Id, ~>}

package object interpreters {

  val mainInterpreter: Program ~> Id = IdDummyInterpreter or IdRaceInterpreter

}
