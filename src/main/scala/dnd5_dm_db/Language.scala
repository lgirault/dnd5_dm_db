package dnd5_dm_db


object Language {
  def fromString(str : String) : Language = str match {
    case "Common" => Common
    case "Dwarvish" => Dwarvish
    case "Elvish" => Elvish
    case "Giant" => Giant
    case "Gnomish" => Gnomish
    case "Goblin" => Goblin
    case "Halfling" => Halfling
    case "Orc" => Orc

    case "Abyssal" => Abyssal
    case "Celestial" => Celestial
    case "Draconic" => Draconic
    case "DeepSpeech" => DeepSpeech
    case "Infernal" => Infernal
    case "Primordial" => Primordial
    case "Sylvan" => Sylvan
    case "Undercommon" => Undercommon
    case _ => error(s"unknown language $str")
  }
}
sealed abstract class Language

case object Common extends Language
case object Dwarvish extends Language
case object Elvish extends Language
case object Giant extends Language
case object Gnomish extends Language
case object Goblin extends Language
case object Halfling extends Language
case object Orc extends Language

case object Abyssal extends Language
case object Celestial extends Language
case object Draconic extends Language
case object DeepSpeech extends Language
case object Infernal extends Language
case object Primordial extends Language
case object Sylvan extends Language
case object Undercommon extends Language
case class CustomLanguage(name : String) extends Language
