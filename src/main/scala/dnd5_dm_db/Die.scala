package dnd5_dm_db

import scala.xml.Node

case class Die(number : Int, faces: Int, bonus : Int){
  override def toString : String = {
    import Die.bonus_str
    s"${number}d$faces${bonus_str(bonus)}"
  }
}

object Die {

  def bonus_str(bonus : Int) = {
    if(bonus > 0) s"+$bonus"
    else if (bonus == 0) ""
    else bonus.toString
  }

  val regex = "(\\d+)d(\\d+)(\\+|-)?(\\d+)?".r

  def fromString(str : String) = str match {
    case regex(n, f, sign, bonus) =>
      val b =
        if(bonus == null) 0
        else if(sign == "-")
          -1 * bonus.toInt
        else bonus.toInt

      Die(n.toInt, f.toInt,  b)
    case _ => error(s"unknown die pattern : $str")
  }

  def fromXml(node : Node) : (Int, Die) =
    (node \ "average", fromString(node \"die"))

}
