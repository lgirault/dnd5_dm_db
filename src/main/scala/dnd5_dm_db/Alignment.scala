package dnd5_dm_db

import dnd5_dm_db.AlignmentRestriction.OrderOrMoral
import dnd5_dm_db.lang.Lang

import scala.xml.Node

sealed abstract class Alignment
object Aligned {
  def fromString(str : String) : Aligned = str match {
    case "n" => Aligned(Neuter, Neuter)
    case _ if str.length == 2 =>
      Aligned(Order.fromChar(str.head),
        Moral.fromChar(str.tail.head))
    case _ => error(s"unknown alignment $str")
  }



}
case class Aligned( order : Order, moral :Moral) extends Alignment
case object Unaligned extends Alignment
case class AnyAlignment(restrict : Option[AlignmentRestriction]) extends Alignment

object AlignmentRestriction {
  type OrderOrMoral = Either[Order, Moral]

  def orderOrMoralfromString(s : String) =
    try  Left(Order.fromChar(s.head))
    catch{
      case _ : Throwable =>
        Right(Moral.fromChar(s.head))
    }
}
sealed abstract class AlignmentRestriction
case class Restrict(a : OrderOrMoral ) extends AlignmentRestriction
case class RestrictNot(a : Either[Aligned, OrderOrMoral]) extends AlignmentRestriction


object Alignment {

  def fromXml(n : Node)(implicit lang : Lang) : Alignment = {

    val sr = singleOptionAttribute(n, "restrict") map {
      r =>
        if(r.startsWith("non")){

          val a =
            try Right(AlignmentRestriction.orderOrMoralfromString(r.substring(4)))
            catch {
              case _ : Throwable => Left(Aligned.fromString(r.substring(4)))
            }
          RestrictNot(a)
        }
        else Restrict(AlignmentRestriction.orderOrMoralfromString(r))

    }

    n.text match {
      case "any" => AnyAlignment(sr)
      case "none" => Unaligned
      case _ => Aligned.fromString(n.text)
    }


  }




}

sealed trait Order
object Order {
  val fromChar = PartialFunction[Char, Order] {
    case 'c' => Chaotic
    case 'l' => Lawful
    case 'n' => Neuter
  }

}
case object Chaotic extends Order
case object Lawful extends Order

sealed trait Moral
object Moral {
  val fromChar = PartialFunction[Char, Moral] {
    case 'g' => Good
    case 'e' => Evil
    case 'n' => Neuter
  }
}
case object Good extends Moral
case object Evil extends Moral

case object Neuter extends Order with Moral
