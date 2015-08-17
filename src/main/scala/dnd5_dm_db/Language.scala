package dnd5_dm_db

import scala.xml.Node


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

  def fromXml(languageList : Node) : Seq[Language] = {
    languageList.child
    val ls = (languageList \ "language") map (n => fromString(n.text))
    if(optionBooleanAttribute(languageList, "anyLanguage")){
      AnyLanguage(singleOptionAttribute(languageList, "default") map fromString) +: ls
    }
    else ls
  }

  def toHtml(ls : Seq[Language])(implicit lang : Lang) : String = {
    val start = s"<div><b>${lang.languages} :</b>"
    ls  map lang.language mkString (start, ", ", "</div>")
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
case class AnyLanguage(default : Option[Language]) extends Language
//case class CustomLanguage(name : String) extends Language
