package dnd5_dm_db

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
case class Melee(reach : DnDLength) extends AttackKind
case class Ranged(regularRange : DnDLength, max : DnDLength) extends AttackKind
case class MeleeOrRange(reach : DnDLength, regularRange : DnDLength, maxRange : DnDLength) extends AttackKind

object AttackKind {

  def fromXml(node : Node) : AttackKind = {
    val sreach : Option[DnDLength] =  (node \ "reach").toNodeOption map DnDLength.fromXml
    val srange : Option[(DnDLength, DnDLength)] =
      (node \ "range").toNodeOption map {
        n => (DnDLength fromXml (n \ "regular").toNode,
              DnDLength fromXml (n \ "max").toNode)
      }
    (sreach, srange) match {
      case (Some(reach), Some(range)) =>
        MeleeOrRange(reach, range._1, range._2)
      case (None, Some(range)) =>
        Ranged(range._1, range._2)
      case (Some(reach), None) =>
        Melee(reach)
      case (None, None) => error("an attack must have a reach or a range")
    }
  }

  def toHtml( mwa : AttackKind)(implicit lang : Lang) : String =
    mwa match {
      case Melee(r) => lang.reach+" " +lang.length(r)
      case Ranged(reg, max)=> lang.range + " " + lang.rangeLength((reg, max))
      case MeleeOrRange(re, raReg, raMax) =>
        toHtml(Melee(re)) +" or " + toHtml(Ranged(raReg, raMax))
    }

}

case class AttackAction
( name : String,
  hitBonus : Int,
  kind : AttackKind,
  numTarget : Int,
  damages : Seq[Damage],
  desc : Option[String]
  ) extends Action

object AttackAction {

  def fromXml(move : Node)(implicit lang : Lang) =
    AttackAction(
      move \ "name" \ lang.id,
      (move \ "bonus").int,
      AttackKind.fromXml(move),
      (move \ "numTarget").int,
      move \ "damage" map Damage.fromXml,
      move \ "description" \ lang.id
      )

  def toHtml( mwa : AttackAction)(implicit lang : Lang) : String =
    s"""<p><b><em>${mwa.name}.</em></b>
       | ${lang.actionName(mwa)}: ${Die.bonus_str(mwa.hitBonus)} ${lang.toHit},
       | ${AttackKind.toHtml(mwa.kind)}},
       | ${mwa.numTarget} ${lang.target(mwa.numTarget)}.
       | ${lang.damages} : ${mwa.damages map Damage.toString mkString ","}.
       | ${mwa.desc.getOrElse("")}</p>""".stripMargin

}

case class Damage(average : Int, d : Die, types : Seq[DamageType])
object Damage{
  def fromXml(move : Node) : Damage = {
    val (avg, d) = Die.fromXml(move)
    Damage(avg, d,
      (move \ "type") map (n => DamageType.fromString(n.text)))
  }

  def toString(h : Damage)(implicit lang : Lang) : String = {
    val types =
      h.types map lang.damageType mkString (" ",", ", "")

    s"${h.average} (${h.d}) ${lang.damages.toLowerCase}$types"
  }


}

