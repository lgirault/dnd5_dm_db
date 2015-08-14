package dnd5_dm_db

sealed abstract class Alignment
case class Aligned( order : Order, moral :Moral) extends Alignment
case object Unaligned extends Alignment
case object AnyAlignment extends Alignment


object Alignment {
  def fromString(str : String) : Alignment = str match {
    case "any" => AnyAlignment
    case "none" => Unaligned
    case "n" => Aligned(Neuter, Neuter)
    case _ if str.length == 2 =>
      Aligned(orderFromChar(str.head),
        moralFromChar(str.tail.head))
    case _ => error(s"unknown alignment $str")
  }

  val orderFromChar = PartialFunction[Char, Order] {
    case 'c' => Chaotic
    case 'l' => Lawful
    case 'n' => Neuter
  }

  val moralFromChar = PartialFunction[Char, Moral] {
    case 'g' => Good
    case 'e' => Evil
    case 'n' => Neuter
  }
}

sealed trait Order
case object Chaotic extends Order
case object Lawful extends Order

sealed trait Moral
case object Good extends Moral
case object Evil extends Moral

case object Neuter extends Order with Moral
