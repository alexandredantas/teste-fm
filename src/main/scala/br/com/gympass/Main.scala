package br.com.gympass

import java.time.LocalTime

import br.com.gympass.adt._
import br.com.gympass.adt.RaceOp
import cats.free.Free
import cats.Id
import cats.implicits._

import scala.language.higherKinds

object Main extends App {


  override def main(args: Array[String]): Unit = {

    implicit val a = RaceOp.create[Id]
    evaluateRace("asda")

  }

  private def evaluateRace[F[_]](raceResultFile: String)(implicit raceOp: RaceOp[F]): Free[F,
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
