package br.com.gympass.adt

import java.time.LocalTime

import cats.InjectK
import cats.free.Free
import cats.free.Free._

import scala.language.higherKinds

sealed case class Pilot(code: String, name: String)
sealed case class Grid(hour: LocalTime, pilot: Pilot, lap: Int, lapTime: LocalTime, avgSpeed: BigDecimal)
sealed case class GridResult(position: Int, pilot: Pilot, lapsCompleted: Int, raceTime: Double)

sealed trait Race[F]
case class LoadGrid(srcFile: String) extends Race[List[Grid]]
case class RaceResult(grid: List[Grid]) extends Race[List[GridResult]]
case class PilotBestLap(grid: List[Grid]) extends Race[Map[Pilot, LocalTime]]
case class BestLap(grid: List[Grid]) extends Race[(Pilot, LocalTime)]
case class AvgSpeedByPilot(grid: List[Grid]) extends Race[Map[Pilot, BigDecimal]]
case class TimeAfterWinner(grid: List[Grid]) extends Race[Map[Pilot, Double]]


final class RaceOp[F[_]](implicit I: InjectK[Race,F]) {
  def loadGrid(srcFile: String): Free[F, List[Grid]] = inject(LoadGrid(srcFile = srcFile))
  def raceResult(grid: List[Grid]): Free[F, List[GridResult]] = inject(RaceResult(grid))
  def bestLap(grid: List[Grid]): Free[F, (Pilot, LocalTime)] = inject(BestLap(grid))
  def pilotBestLap(grid: List[Grid]): Free[F, Map[Pilot, LocalTime]] = inject(PilotBestLap(grid))
  def averageLapByPilot(grid: List[Grid]): Free[F, Map[Pilot, BigDecimal]] = inject(AvgSpeedByPilot(grid))
  def timeAfterWinner(grid: List[Grid]): Free[F, Map[Pilot, Double]] = inject(TimeAfterWinner(grid))
}

object RaceOp {
  implicit def create[F[_]](implicit I: InjectK[Race,F]): RaceOp[F] = new RaceOp[F]
}