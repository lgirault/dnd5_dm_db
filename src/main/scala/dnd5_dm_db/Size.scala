package dnd5_dm_db

sealed abstract class Size
case object Tiny extends Size
case object Small extends Size
case object Medium extends Size
case object Large extends Size
case object Huge extends Size
case object Gargantuan extends Size

object Size {
  def fromString(str : String) : Size =
  str match {
    case "Tiny" => Tiny
    case "Small" => Small
    case "Medium" => Medium
    case "Large" => Large
    case "Huge" => Huge
    case "Gargantuan" => Gargantuan
    case _ => error(s"unknown size : $str")
  }
}