package dnd5_dm_db
package model

import dnd5_dm_db.lang.Lang

sealed abstract class Action
object Action {
  def toHtml(a : Action)(implicit lang : Lang) : String = a match {
    case mwa : AttackAction => AttackAction.toHtml(mwa)
    case wa : WeaponAction => AttackAction.toHtml(wa.toAttackAction(lang))
    case ma : MultiAttack => MultiAttack.toHtml(ma)
    case sa : SpecialAction => SpecialAction.toHtml(sa)
  }
}

case class MultiAttack(desc : Local) extends Action

object MultiAttack {
  def toHtml( ma : MultiAttack)(implicit lang : Lang) : String =
    s"<p><b><em>${lang actionName ma}.</em></b>${ma.desc.value}</p>"
}

case class SpecialAction(name : Local, description : Local) extends Action

object SpecialAction {
  def toHtml(t : SpecialAction)(implicit lang: Lang): String =
    s"<div><b>${t.name.value}:</b> ${t.description.value}</div>"
}



sealed abstract class AttackKind
case class MeleeAttack(reach : DnDLength, numTarget : Int) extends AttackKind
case class RangedAttack(regularRange : DnDLength, max : DnDLength, numTarget : Int) extends AttackKind
case class MeleeOrRange(reach : DnDLength, regularRange : DnDLength, maxRange : DnDLength, numTarget : Int) extends AttackKind
case class RangedSpecial(desc : Local) extends AttackKind

object AttackKind {

  def toHtml( mwa : AttackKind)(implicit lang : Lang) : String =
    mwa match {
      case MeleeAttack(r, n) => s"${lang.reach} ${lang.length(r)} $n ${lang.target(n)}"
      case RangedAttack(reg, max, n)=> s"${lang.range} ${lang.rangeLength((reg, max))} $n ${lang.target(n)}"
      case MeleeOrRange(re, raReg, raMax, numTarget) =>
        toHtml(MeleeAttack(re, numTarget)) +" or " + toHtml(RangedAttack(raReg, raMax, numTarget))
      case RangedSpecial(desc) => desc.value
    }

}

case class WeaponAction
( weapon : Weapon,
  hitBonus : Int,
  damageBonus : Int,
  baseReach : DnDLength) extends Action {

  private val langs = weapon.name.map.keys.toSeq

  def reach : DnDLength =
   if (weapon.properties contains Reach ){
     val Feet(br) = baseReach
     Feet(br + 5)
   }
   else baseReach


  def attackKind : AttackKind =
    (weapon.kind, weapon.rangeOption) match {
      case (Melee, None) => MeleeAttack(reach, 1)
      case (Melee, Some(r)) =>
        MeleeOrRange(reach, r.regularRange, r.maxRange, 1)
      case (Ranged, Some(r)) =>
        RangedAttack(r.regularRange, r.maxRange, 1)
      case (Ranged, None) => error(weapon.name + " : range kind requires range property")
    }

  def hit(lang : Lang) : Hit = {
    val special =
      weapon.properties find { _.isInstanceOf[Versatile]}


    Damage(weapon.damage + damageBonus, weapon.damageType,
      special map {case Versatile(d) =>
        val lseq = langs map (l => (l,l.versatile(d)))
        Local(lseq.toMap)
      })
  }


  def toAttackAction(lang : Lang) : AttackAction =
    AttackAction(
      weapon.name,
      hitBonus,
      attackKind,
      Seq(hit(lang)))


}


case class AttackAction
( name : Local,
  hitBonus : Int,
  kind : AttackKind,
  damages : Seq[Hit]//,
  //desc : Option[String]
  ) extends Action

object AttackAction {

  def toHtml( mwa : AttackAction)(implicit lang : Lang) : String ={
    val dmgStr0 = mwa.damages map lang.hits mkString " + "
    val dmgStr = if(dmgStr0 endsWith ".") dmgStr0 else dmgStr0 + "."
    s"""<p><b><em>${mwa.name.value}.</em></b>
       |   ${lang.actionName(mwa)}: ${Die.bonus_str(mwa.hitBonus)} ${lang.toHit},
       |   ${AttackKind.toHtml(mwa.kind)}.
       |   <em>${lang.hit} :</em> $dmgStr
       |</p>""".stripMargin
    //| ${mwa.desc.getOrElse("")}</p>""".stripMargin
  }


}

sealed abstract class Hit
case class Damage(die : Die, types : DamageType, special : Option[Local]) extends Hit
case class SpecialHit(desc : Local) extends Hit


