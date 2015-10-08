package dnd5_dm_db.lang.eng

import dnd5_dm_db.model._

trait MonsterText {

  val size : Size => String = {
    case Tiny => "très petite"
    case Small => "petite"
    case Medium => "moyenne"
    case Large => "grande"
    case Huge => "très grande"
    case Gargantuan => "gigantesque"
  }


  val monsterType : MonsterType => String = {
    case Aberration => "Aberration"
    case Beast => "Bête"
    case CelestialType => "Célese"
    case Construct => "Construction"
    case Dragon => "Dragon"
    case Elemental => "Elémentaire"
    case Fey => "Fée"
    case Fiend => "Démon"
    case GiantType => "Géant"
    case Humanoid => "Humanoïde"
    case Monstrosity => "Monstruosité"
    case Ooze => "Gelée"
    case Plant => "Plante"
    case Undead => "Mort-Vivant"
    case AnyRace(t) => monsterType(t) + " (race quelconque)"
    case TaggedType(t, r) => s"${monsterType(t)} ($r)"
  }
  val monsterTypeAndSize : (Size, MonsterType) => String = {
    case (s, mt) => monsterType(mt)+ s" de taille ${size(s)}"
  }
}
