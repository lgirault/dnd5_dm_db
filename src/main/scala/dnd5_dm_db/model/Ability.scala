package dnd5_dm_db
package model

case class Abilities
( strength : Int,
  dexterity: Int,
  constitution : Int,
  intelligence : Int,
  wisdom : Int,
  charisma : Int)

sealed abstract class Ability
case object Strength extends Ability
case object Dexterity extends Ability
case object Constitution extends Ability
case object Intelligence extends Ability
case object Wisdom extends Ability
case object Charisma extends Ability

object Ability {

  def modifier(value : Int) : Int =
    Math.floor((value - 10).toDouble / 2).toInt

  def fromString(str : String) : Ability = str match {
    case "str" => Strength
    case "dex" => Dexterity
    case "con" => Constitution
    case "int" => Intelligence
    case "wis" => Wisdom
    case "cha" => Charisma
    case _ => error(s"unknown ability : $str")
  }

}
