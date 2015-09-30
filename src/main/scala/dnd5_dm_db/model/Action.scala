package dnd5_dm_db.model

import dnd5_dm_db._
import dnd5_dm_db.lang.Lang

import scala.xml.Node

sealed abstract class Action
object Action {
  def fromXml(move : Node)(implicit lang : Lang) : Action = {
    singleAttribute(move, "type") match {
      case "attack" => AttackAction.fromXml(move)
      case "multiattack" => MultiAttack.fromXml(move)
      case "special" => SpecialAction.fromXml(move)
      case str => error(s"unknown action move : $str")
    }
  }

  def toHtml(a : Action)(implicit lang : Lang) : String = a match {
    case mwa : AttackAction => AttackAction.toHtml(mwa)
    case ma : MultiAttack => MultiAttack.toHtml(ma)
    case sa : SpecialAction => SpecialAction.toHtml(sa)
  }
}

case class MultiAttack(desc : String) extends Action

object MultiAttack {
  def fromXml(move : Node)(implicit lang : Lang) =
    MultiAttack( move \ lang.id)

  def toHtml( ma : MultiAttack)(implicit lang : Lang) : String =
    s"<p><b><em>${lang actionName ma}.</em></b>${ma.desc}</p>"
}


case class SpecialAction(name : String, description : String) extends Action

object SpecialAction {

  def fromXml(node : Node)(implicit lang : Lang) : Action =
    SpecialAction (node \ lang.id \ "name",
      node \ lang.id \ "description" )

  def toHtml(t : SpecialAction)(implicit lang: Lang): String =
    s"<div><b>${t.name}:</b> ${t.description}</div>"

}



sealed abstract class AttackKind
case class Melee(reach : DnDLength, numTarget : Int) extends AttackKind
case class Ranged(regularRange : DnDLength, max : DnDLength, numTarget : Int) extends AttackKind
case class MeleeOrRange(reach : DnDLength, regularRange : DnDLength, maxRange : DnDLength, numTarget : Int) extends AttackKind
case class RangedSpecial(desc : String) extends AttackKind

object AttackKind {

  def fromXml(node : Node)(implicit lang : Lang) : AttackKind = {
    val sreach : Option[DnDLength] =  (node \ "reach").toNodeOption map DnDLength.fromXml
    val srange : Option[(DnDLength, DnDLength)] =
      (node \ "range").toNodeOption map {
        n => (DnDLength fromXml (n \ "regular").toNode,
              DnDLength fromXml (n \ "max").toNode)
      }

    val sTarget : Option[AttackKind] = (node \ "rangeSpecial" \ lang.id).textOption map RangedSpecial.apply
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
          Ranged(range._1, range._2, numTarget)
        case (Some(reach), None) =>
          Melee(reach, numTarget)
        case (None, None) => error("an attack must have a reach or a range")
      }
    }
  }

  def toHtml( mwa : AttackKind)(implicit lang : Lang) : String =
    mwa match {
      case Melee(r, n) => s"${lang.reach} ${lang.length(r)} $n ${lang.target(n)}"
      case Ranged(reg, max, n)=> s"${lang.range} ${lang.rangeLength((reg, max))} $n ${lang.target(n)}"
      case MeleeOrRange(re, raReg, raMax, numTarget) =>
        toHtml(Melee(re, numTarget)) +" or " + toHtml(Ranged(raReg, raMax, numTarget))
      case RangedSpecial(desc) => desc
    }



}

case class AttackAction
( name : String,
  hitBonus : Int,
  kind : AttackKind,
  damages : Seq[Hit],
  desc : Option[String]
  ) extends Action

object AttackAction {

  def fromXml(move : Node)(implicit lang : Lang) =
    AttackAction(
      move \ "name" \ lang.id,
      (move \ "bonus").int,
      AttackKind.fromXml(move),
      move \ "hit" map Hit.fromXml,
      move \ "description" \ lang.id
      )

  def toHtml( mwa : AttackAction)(implicit lang : Lang) : String =
    s"""<p><b><em>${mwa.name}.</em></b>
       | ${lang.actionName(mwa)}: ${Die.bonus_str(mwa.hitBonus)} ${lang.toHit},
       | ${AttackKind.toHtml(mwa.kind)}
       | <em>${lang.hit} :</em> ${mwa.damages map lang.hits mkString ","}
       | ${mwa.desc.getOrElse("")}</p>""".stripMargin

}

sealed abstract class Hit
object Hit {
  def fromXml(move: Node)(implicit lang: Lang): Hit = {
    singleAttribute(move, "type") match {
      case "damage" => Damage.fromXml(move)
      case "special" => SpecialHit(move \ lang.id)
      case str => error(s"unknown action move : $str")
    }
  }
}

case class Damage(average : Int, die : Die, types : Seq[DamageType], special : Option[String]) extends Hit
case class SpecialHit(desc : String) extends Hit
object Damage{
  def fromXml(move : Node)(implicit lang: Lang) : Damage = {
    val (avg, d) = Die.fromXml(move)
    Damage(avg, d,
      (move \ "type") map (n => DamageType.fromString(n.text)),
      move \ "special" \ lang.id
    )
  }

}

