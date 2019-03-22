package br.com.gympass

import cats.Id
import cats.data.EitherK

package object adt {

  type Program[A] = EitherK[Id, Race, A]

}
