package br.com.gympass.adt

import java.time.LocalTime

import cats.InjectK
import cats.free.Free
import cats.free.Free._

import scala.language.higherKinds


sealed case class Pilot(code: String, name: String)
sealed case class Grid(hour: LocalTime, pilot: Pilot, lap: Int, lapTime: LocalTime, avgSpeed: BigDecimal)


sealed trait Race[F]
case class LoadGrid(srcFile: String) extends Race[List[Grid]]
case class BestPilot(grid: List[Grid]) extends Race[Pilot]
case class BestLap(grid: List[Grid]) extends Race[(Pilot, LocalTime)]
case class AvgLapByPilot(grid: List[Grid]) extends Race[Map[Pilot, LocalTime]]
case class TimeAfterWinner(grid: List[Grid]) extends Race[Map[Pilot, LocalTime]]


final class RaceOp[F[_]](implicit I: InjectK[Race,F]) {
  def loadGrid(srcFile: String): Free[F, List[Grid]] = inject(LoadGrid(srcFile = srcFile))
  def bestLap(grid: List[Grid]): Free[F, (Pilot, LocalTime)] = inject(BestLap(grid))
  def bestPilot(grid: List[Grid]): Free[F, Pilot] = inject(BestPilot(grid))
  def averageLapByPilot(grid: List[Grid]): Free[F, Map[Pilot, LocalTime]] = inject(AvgLapByPilot(grid))
  def timeAfterWinner(grid: List[Grid]): Free[F, Map[Pilot, LocalTime]] = inject(TimeAfterWinner(grid))
}

object RaceOp {
  implicit def create[F[_]](implicit I: InjectK[Race,F]): RaceOp[F] = new RaceOp[F]
}