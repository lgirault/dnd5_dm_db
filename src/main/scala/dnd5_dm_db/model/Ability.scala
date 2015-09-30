package dnd5_dm_db.model

import dnd5_dm_db._
import dnd5_dm_db.lang.Lang

import scala.xml.Node

case class Abilities
( strength : Int,
  dexterity: Int,
  constitution : Int,
  intelligence : Int,
  wisdom : Int,
  charisma : Int)

object Abilities {

  def fromXml(node : Node) : Abilities =
    Abilities(
      node \ "str",
      node \ "dex",
      node \ "con",
      node \ "int",
      node \ "wis",
      node \ "cha")


  def toHtml(abilities: Abilities)(implicit lang : Lang) : String = {
    import Ability.{toHtml => h}
    h(Strength, abilities.strength) +
     h(Dexterity, abilities.dexterity) +
     h(Constitution, abilities.constitution) +
     h(Intelligence, abilities.intelligence) +
     h(Wisdom, abilities.wisdom) +
     h(Charisma, abilities.charisma)
  }


}

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

  def savingThrowFromXml(node : Node) : (Ability, Int) =
      (fromString(singleAttribute(node, "name")),
        singleAttribute(node, "value").toInt)


  def fromString(str : String) : Ability = str match {
    case "str" => Strength
    case "dex" => Dexterity
    case "con" => Constitution
    case "int" => Intelligence
    case "wis" => Wisdom
    case "cha" => Charisma
    case _ => error(s"unknown ability : $str")
  }

  def toHtml
  ( ability: Ability,
    value: Int)(implicit lang : Lang) : String = {
   s"""<div class="ability"><strong>${lang.ability_short(ability)}</strong><br/>
      |    $value (${Die.bonus_str(modifier(value))})
      |</div>""".stripMargin
  }
}
