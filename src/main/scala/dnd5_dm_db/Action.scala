package dnd5_dm_db

import scala.xml.Node

sealed abstract class Action
object Action {
  def fromXml(move : Node)(implicit lang : Lang) : Action = {
    singleAttribute(move, "type") match {
      case "mwa" => MeleeWeaponAttack.fromXml(move)
      case "rwa" => RangeWeaponAttack.fromXml(move)
      case str => error(s"unknown action move : $str")
    }
  }

  def toHtml(a : Action)(implicit lang : Lang) : String = a match {
    case mwa : MeleeWeaponAttack => MeleeWeaponAttack.toHtml(mwa)
    case rwa : RangeWeaponAttack => RangeWeaponAttack.toHtml(rwa)
  }
}
case class MeleeWeaponAttack
( name : String,
  hitBonus : Int,
  reach : DnDLength,
  numTarget : Int,
  hit : Hit,
  desc : Option[String]
  ) extends Action
object MeleeWeaponAttack {
  def fromXml(move : Node)(implicit lang : Lang) =
    MeleeWeaponAttack(
      move \ "name" \ lang.id,
      (move \ "bonus").int,
      DnDLength fromXml (move \ "reach").toNode,
      (move \ "numTarget").int,
      Hit.fromXml(move),
      move \ "description" \ lang.id
      )

  def toHtml( mwa : MeleeWeaponAttack)(implicit lang : Lang) : String =
    s"""<p><b><em>${mwa.name}.</em></b>
       | ${lang.actionName(mwa)}: ${Die.bonus_str(mwa.hitBonus)} ${lang.toHit},
       | ${lang.reach} ${lang.length(mwa.reach)},
       | ${mwa.numTarget} ${lang.target(mwa.numTarget)}.
       | ${lang.damages} : ${Hit.toString(mwa.hit)}.
       | ${mwa.desc.getOrElse("")}</p>""".stripMargin

}

case class Hit(average : Int, d : Die, types : Seq[DamageType])
object Hit{
  def fromXml(move : Node) : Hit =
    Hit((move \ "hit").int,
      Die.fromString(move \ "hitd"),
      (move \ "damageType") map (n => DamageType.fromString(n.text)))

  def toString(h : Hit)(implicit lang : Lang) : String = {
    val types =
      h.types map lang.damageType mkString (" ",", ", "")

    s"${h.average} (${h.d}) ${lang.damages.toLowerCase}$types"
  }


}

case class RangeWeaponAttack
( name : String,
  hitBonus : Int,
  range : (DnDLength, DnDLength),
  numTarget : Int,
  hit : Hit,
  desc : Option[String]
  ) extends Action

object RangeWeaponAttack {
  def fromXml(move : Node)(implicit lang : Lang) =
    RangeWeaponAttack(
      move \ "name" \ lang.id,
      (move \ "bonus").int,
      (DnDLength fromXml (move \ "range").toNode,
        DnDLength fromXml (move \ "rangeMax").toNode),
      (move \ "numTarget").int,
      Hit.fromXml(move),
      move \ "description" \ lang.id)

  def toHtml( rwa : RangeWeaponAttack)(implicit lang : Lang) : String =
    s"""<p><b><em>${rwa.name}.</em></b>
    | ${lang.actionName(rwa)}: ${Die.bonus_str(rwa.hitBonus)} ${lang.toHit},
    | ${lang.range} ${lang.rangeLength(rwa.range)},
    | ${rwa.numTarget} ${lang.target(rwa.numTarget)}.
    | ${lang.damages} : ${Hit.toString(rwa.hit)}.
    | ${rwa.desc.getOrElse("")}</p>""".stripMargin

  }