package dnd5_dm_db

import scala.xml.Node


sealed abstract class Skill
case object Athletics extends Skill
case object Acrobatics extends Skill
case object SleightOfHand extends Skill
case object Stealth extends Skill
case object Arcana extends Skill
case object History extends Skill
case object Investigation extends Skill
case object Nature extends Skill
case object Religion extends Skill
case object AnimalHandling extends Skill
case object Insight extends Skill
case object Medicine extends Skill
case object Perception extends Skill
case object Survival extends Skill
case object Deception extends Skill
case object Intimidation extends Skill
case object Performance extends Skill
case object Persuasion extends Skill

object Skill {
  def fromString(str : String) : Skill = str match {
    case "Athletics" => Athletics
    case "Acrobatics" => Acrobatics
    case "SleightOfHand" => SleightOfHand
    case "Stealth" => Stealth
    case "Arcana" => Arcana
    case "History" => History
    case "Investigation" => Investigation
    case "Nature" => Nature
    case "Religion" => Religion
    case "AnimalHandling" => AnimalHandling
    case "Insight" => Insight
    case "Medicine" => Medicine
    case "Perception" => Perception
    case "Survival" => Survival
    case "Deception" => Deception
    case "Intimidation" => Intimidation
    case "Performance" => Performance
    case "Persuasion" => Persuasion
    case _ => error(s"unknown skill $str")
  }

  def fromXml(node : Node) : (Skill, Int) =
    (fromString(singleAttribute(node, "name")),
      singleAttribute(node, "value").toInt)

}