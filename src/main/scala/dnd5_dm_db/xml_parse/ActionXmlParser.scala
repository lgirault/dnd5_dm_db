package dnd5_dm_db.xml_parse

import dnd5_dm_db._
import dnd5_dm_db.model._

import scala.xml.Node

import Utils._

import LocalXmlParser._

object ActionXmlParser extends DnDMeasuresXmlParser {

  def actionFromXml(move : Node)
                   (implicit weapons : Retriever[Weapon]) : Action = {
    singleAttribute(move, "type") match {
      case "weapon" => weaponActionFromXml(move)
      case "attack" => attackActionFromXml(move)
      case "multiattack" => multiAttackFromXml(move)
      case "special" => specialActionFromXml(move)
      case str => error(s"unknown action move : $str")
    }
  }

  def multiAttackFromXml(move : Node) =
    MultiAttack( localFromXml(move))

  def specialActionFromXml(node : Node) : Action =
    SpecialAction (localFromXml(node \ "name"),
      localFromXml(node \ "description" ))


  def attackActionFromXml(move : Node) =
    if(( move \ "description").nonEmpty) error("blurg")
    else
    AttackAction(
      localFromXml(move \ "name"),
      (move \ "bonus").int,
      attackKindFromXml(move),
      move \ "hit" map hitFromXml/*,
      move \ "description" \ lang.id*/
    )

  def weaponActionFromXml(move : Node)
                         (implicit weapons : Retriever[Weapon]) =
    WeaponAction(
      weapons(singleAttribute(move, "id")),
      (move \ "hitBonus").toNodeOption map (_.int) getOrElse 0,
      (move \ "damageBonus").toNodeOption map (_.int) getOrElse 0,
      (move \ "reach").toNodeOption map lengthFromXml getOrElse Feet(5))



  def hitFromXml(move: Node): Hit = {
    singleAttribute(move, "type") match {
      case "damage" => damageFromXml(move)
      case "special" => SpecialHit(localFromXml(move))
      case str => error(s"unknown action move : $str")
    }
  }

  def damageFromXml(move : Node) : Damage = {
    Damage(Die.fromString(singleAttribute(move, "die")),
      DamageType.fromString(singleAttribute(move, "damageType")),
      (move \ "special").toNodeOption map  localFromXml
    )
  }

  def attackKindFromXml(node : Node) : AttackKind = {
    val sreach : Option[DnDLength] =  (node \ "reach").toNodeOption map lengthFromXml

    val srange : Option[(DnDLength, DnDLength)] =
      (node \ "range").toNodeOption map WeaponXmlParser.range

    val sTarget : Option[AttackKind] = (node \ "rangeSpecial" ).toNodeOption map (n => RangedSpecial(localFromXml(n)))
    if(sTarget.nonEmpty){
      assert(srange.isEmpty && sreach.isEmpty)
      sTarget.get
    }
    else {
      val numTarget = (node \ "numTarget").int
      (sreach, srange) match {
        case (Some(reach), Some(range)) =>
          MeleeOrRange(reach, range._1, range._2, numTarget)
        case (None, Some(range)) =>
          RangedAttack(range._1, range._2, numTarget)
        case (Some(reach), None) =>
          MeleeAttack(reach, numTarget)
        case (None, None) => error("an attack must have a reach or a range")
      }
    }
  }
}
