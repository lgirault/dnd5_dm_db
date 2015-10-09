package dnd5_dm_db.lang.eng

import dnd5_dm_db.model._


trait AlignmentText {
  val moral : Moral => String = {
    case Good => "good"
    case Neuter => "neutral"
    case Evil => "evil"
  }
  val order : Order => String = {
    case Lawful => "loyal"
    case Neuter => "neutral"
    case Chaotic => "chaotic"
  }

  def moralOrOrderToString : Either[Order, Moral] => String = {
    case Left(o) => order(o)
    case Right(m) => moral(m)
  }
  def alignmentRestrictionToStr : AlignmentRestriction => String ={
    case Restrict(om) => moralOrOrderToString(om)
    case RestrictNot(Left(a)) => alignment(a)
    case RestrictNot(Right(om)) => moralOrOrderToString(om)
  }

  val alignment : Alignment => String = {
    case Unaligned => "unaligned"
    case AnyAlignment(None) => "any alignment"
    case AnyAlignment(Some(res : Restrict)) => s"any ${alignmentRestrictionToStr(res)} alignment"
    case AnyAlignment(Some(res : RestrictNot)) => s"any non-${alignmentRestrictionToStr(res)} alignment"
    case Aligned(Neuter, Neuter) => "neutral"
    case Aligned(o, m) => order(o) +" " + moral(m)
  }
}
