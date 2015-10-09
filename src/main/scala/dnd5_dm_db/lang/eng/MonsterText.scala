package dnd5_dm_db.lang.eng

import dnd5_dm_db.model._

trait MonsterText {

  val size : Size => String = {
    case Tiny => "tiny"
    case Small => "small"
    case Medium => "medium"
    case Large => "large"
    case Huge => "huge"
    case Gargantuan => "gargantuan"
  }


  val monsterType : MonsterType => String = {
    case Aberration => "Aberration"
    case Beast => "Beast"
    case CelestialType => "Celestial"
    case Construct => "Construct"
    case Dragon => "Dragon"
    case Elemental => "Elemental"
    case Fey => "Fey"
    case Fiend => "Fiend"
    case GiantType => "Giant"
    case Humanoid => "Humanoid"
    case Monstrosity => "Monstrosity"
    case Ooze => "Ooze"
    case Plant => "Plant"
    case Undead => "Undead"
    case AnyRace(t) => monsterType(t) + " (any race)"
    case TaggedType(t, r) => s"${monsterType(t)} ($r)"
  }
  val monsterTypeAndSize : (Size, MonsterType) => String = {
    case (s, mt) => size(s) + " " +monsterType(mt)
  }
}
