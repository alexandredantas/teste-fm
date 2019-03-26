package br.com.gympass

import java.time.LocalTime

import br.com.gympass.adt._
import br.com.gympass.interpreters._
import cats.free.Free

import scala.language.higherKinds

object Main extends App {

  type Stats = (List[GridResult], Map[Pilot, LocalTime], (Pilot, LocalTime), Map[Pilot, BigDecimal], Map[Pilot, Double])

  override def main(args: Array[String]): Unit = {
    val (result, lapByPilot, (bestPilot, bestLap), average, timeAfter) = evaluateRace(args(0)).foldMap(mainInterpreter)

    println(s"Race result: ${result.map(r => s"${r.pilot.name} - Position: ${r.position}").mkString("\n")}")
    println(s"Lap by pilot: ${lapByPilot.map{byPilotString}.mkString("\n")}")
    println(s"Best lap by ${bestPilot.name} with $bestLap")
    println(s"Average speed by pilot:\n ${average.map{byPilotString}.mkString("\n")}")
    println(s"Time after winner by pilot:\n ${timeAfter.map{byPilotString}.mkString("\n") }")
  }

  private def byPilotString[A](pilotTuple: (Pilot, A)): String = {
    val (pilot, value) = pilotTuple
    s"${pilot.name} - $value"
  }

  private def evaluateRace(raceResultFile: String)(implicit raceOp: RaceOp[Program]): Free[Program, Stats] = {

    import raceOp._

    for {
      grid <- loadGrid(raceResultFile)
      result <- raceResult(grid)
      bestPilot <- pilotBestLap(grid)
      bestLapTup <- bestLap(grid)
      avgByPilot <- averageLapByPilot(grid)
      timeAfterWinner <- timeAfterWinner(grid)
    } yield (result, bestPilot, bestLapTup, avgByPilot, timeAfterWinner)
  }
}
