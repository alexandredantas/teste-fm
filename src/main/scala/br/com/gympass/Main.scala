package br.com.gympass

import java.time.LocalTime

import br.com.gympass.adt._
import br.com.gympass.interpreters._
import cats.free.Free

import scala.language.higherKinds

object Main extends App {


  override def main(args: Array[String]): Unit = {

    import br.com.gympass.adt.RaceOp._

    val a = evaluateRace("asda").foldMap(idInterpreter)


  }

  private def evaluateRace(raceResultFile: String)(implicit raceOp: RaceOp[Program]): Free[Program,
    (Pilot, (Pilot, LocalTime), Map[Pilot, LocalTime], Map[Pilot, LocalTime])] = {

    import raceOp._

    for {
      grid <- loadGrid(raceResultFile)
      (bestLap, bestPilot, avgByPilot, timeAfterWinner) <- for {
        pilot <- bestPilot(grid)
        last <- bestLap(grid)
        avg <- averageLapByPilot(grid)
        timeAfter <- timeAfterWinner(grid)
      } yield (pilot, last, avg, timeAfter)
    } yield (bestLap, bestPilot, avgByPilot, timeAfterWinner)
  }
}
