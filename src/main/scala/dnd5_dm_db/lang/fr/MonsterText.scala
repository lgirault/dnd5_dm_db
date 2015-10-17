package dnd5_dm_db.lang.fr

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
  }

  val monsterRace : Race => String = {
    case Human => "humain"
    case Goblinoid => "goblinoïde"
    case Kobold => "kobold"
    case Orc => "orc"
    case HalfDragon => "demi-dragon"
    case Shapechanger => "changeur de forme"
    case Troglodyte => "troglodyte"
    case AnyRace => "quelconque"
  }
  val monsterTypeAndSize : (Size, MonsterType, Seq[TypeTag]) => String = {
    case (s, mt, tts) => monsterType(mt) + (
        if(tts.isEmpty) ""
        else tts map {
          case r : Race => monsterRace(r)
        } mkString (" (", ", ", ")")
      ) + s" de taille ${size(s)}"
  }
}
