package dnd5_dm_db
package model

import dnd5_dm_db.xml_parse.Utils
import Utils._
import scala.xml.Node

case class Die(number : Int, faces: Int, bonus : Int){
  override def toString : String = {

    s"${number}d$faces" +
      (if(bonus != 0) Die.bonus_str(bonus)
      else "")
  }

  def average : Int = (number * ((faces / 2) + 0.5) + bonus).floor.toInt

  def +(i : Int) : Die= copy(bonus = bonus + i)
}

object Die {

  def bonus_str(bonus : Int) : String = {
    if(bonus >= 0) s"+$bonus"
//    else if (bonus == 0) ""
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

  def fromXml(node : Node) : Die = fromString( node )

}
