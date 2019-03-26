package br.com.gympass.interpreters

import java.nio.file.{Files, Path}
import java.time.{LocalDate, LocalTime, ZoneOffset}

import br.com.gympass.adt._
import cats.{Id, ~>}

import scala.collection.JavaConverters
import scala.util.Try

object IdRaceInterpreter extends (Race ~> Id) {

  private sealed trait GridRecord {
    def grid: Option[Grid]
  }

  private sealed case class ValidRecord(grid: Option[Grid]) extends GridRecord

  private case object InvalidRecord extends GridRecord {
    override def grid: Option[Grid] = None
  }

  private val gridRegex = "([0-9\\:\\.]+)\\s+([0-9 \\–\\-a-zA-Z\\.].*)\\s+([0-9])\\s+([0-9\\:\\.]+)\\s+([0-9,].*)".r
  private val pilotRegex = "([0-9]+)\\s+[–-]\\s+([0-9 \\–a-zA-Z\\.]+)".r

  private def loadGridFile(srcFile: String): List[Grid] = {
    JavaConverters.asScalaIterator[String](Files.readAllLines(Path.of(srcFile)).iterator()).drop(1).map {
      case gridRegex(hr, pl, lap, lapTime, avg) =>
        val pilot = pl.trim match {
          case pilotRegex(code, name) => Pilot(code, name)
          case _ => Pilot("", "")
        }

        Try {
          ValidRecord(Some(Grid(LocalTime.parse(hr), pilot, lap.toInt, LocalTime.parse(s"00:0$lapTime"),
            BigDecimal(avg.replace(",", ".")))))
        }
          .recover { case _ => InvalidRecord }
          .get

      case _ => InvalidRecord
    }
      .flatMap(_.grid)
      .toList
  }

  private def localTimeToMinutes(lc: LocalTime): Double = {
    lc.getHour / 60.0 + lc.getMinute + lc.getSecond / 60.0
  }

  private def getPilotAttr[B](attrs: Map[String, (Int, Double)])(code: String, default: B, fn: ((Int, Double)) => B): B = {
    attrs.get(code).map(fn(_)).getOrElse(default)
  }

  private def calculateRaceResult(grid: List[Grid]): List[GridResult] = {

    val lapAndTimeByPilot = grid
      .groupBy(_.pilot.code)
      .map { case (code, gr) => (code, (gr.map(_.lap).max, gr.map(g => localTimeToMinutes(g.lapTime)).sum)) }


    grid
      .filter(g => g.lap == getPilotAttr[Int](lapAndTimeByPilot)(g.pilot.code, 0, _._1))
      .sortBy(_.hour)
      .map(res => GridResult(0, res.pilot,
        getPilotAttr[Int](lapAndTimeByPilot)(res.pilot.code, 0, _._1),
        getPilotAttr[Double](lapAndTimeByPilot)(res.pilot.code, 0.0, _._2)))
      .sortBy(g => (g.lapsCompleted, g.raceTime))(Ordering.Tuple2(Ordering[Int].reverse, Ordering[Double]))
      .zipWithIndex
      .map { case (g, pos) => g.copy(position = pos + 1) }
  }

  private def pilotsBestLaps(grid: List[Grid]): Map[Pilot, LocalTime] = {
    grid
      .groupBy(_.pilot)
      .map { case (pl, laps) => (pl, laps.minBy(_.lapTime).lapTime) }
  }

  private def pilotsAvgSpeed(grid: List[Grid]): Map[Pilot, BigDecimal] = {
    grid
      .groupBy(_.pilot)
      .map { case (pl, laps) => (pl, laps.map(_.avgSpeed).sum / laps.size) }
  }

  private def timeAfterWinner(grid: List[Grid]): Map[Pilot, Double] = {
    val res = calculateRaceResult(grid)
    res.drop(1).foldLeft(Map.empty[Pilot, Double]){ case (acc, gr) => acc + ((gr.pilot, gr.raceTime - res.head.raceTime))}
  }

  override def apply[A](fa: Race[A]): Id[A] = fa match {
    case LoadGrid(srcFile) => loadGridFile(srcFile)
    case RaceResult(grid) => calculateRaceResult(grid)
    case PilotBestLap(grid) => pilotsBestLaps(grid)
    case BestLap(grid) => pilotsBestLaps(grid).minBy(_._2)
    case AvgSpeedByPilot(grid) => pilotsAvgSpeed(grid)
    case TimeAfterWinner(grid) => timeAfterWinner(grid)
  }
}