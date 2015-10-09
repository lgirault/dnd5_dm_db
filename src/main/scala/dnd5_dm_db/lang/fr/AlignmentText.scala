package dnd5_dm_db.lang.fr

import dnd5_dm_db.model._


trait AlignmentText {
  val moral : Moral => String = {
    case Good => "bon"
    case Neuter => "neutre"
    case Evil => "mauvais"
  }
  val order : Order => String = {
    case Lawful => "loyal"
    case Neuter => "neutre"
    case Chaotic => "chaotique"
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
    case Unaligned => "non aligné"
    case AnyAlignment(None) => "alignement quelconque"
    case AnyAlignment(Some(res : Restrict)) => s"alignement ${alignmentRestrictionToStr(res)} quelconque"
    case AnyAlignment(Some(res : RestrictNot)) => s"alignement quelconque non ${alignmentRestrictionToStr(res)}"
    case Aligned(Neuter, Neuter) => "neutre"
    case Aligned(o, m) => order(o) +" " + moral(m)
  }
}
