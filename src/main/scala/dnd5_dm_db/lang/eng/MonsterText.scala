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
  }

  val monsterRace : Race => String = {
    case Human => "human"
    case Goblinoid => "goblinoid"
    case Kobold => "kobold"
    case Orc => "orc"
    case HalfDragon => "half-dragon"
    case Shapechanger => "shapechanger"
    case Troglodyte => "troglodyte"
    case AnyRace => "any"
  }
  val monsterTypeAndSize : (Size, MonsterType, Seq[TypeTag]) => String = {
    case (s, mt, tts) => size(s) + " " + monsterType(mt) + (
        if(tts.isEmpty) ""
        else tts map {
          case r : Race => monsterRace(r)
        } mkString (" (", ", ", ")")
      )
  }
}
