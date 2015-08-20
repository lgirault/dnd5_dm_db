package dnd5_dm_db.lang.fr

import dnd5_dm_db._


trait SpellText {

  val castingTime: String = "Temps d'incantation"

  val components : String = "Composantes"

  val duration: String = "Durée"

  val higherLevels = "Aux niveaux supérieurs"

  val component : Components => String = {
    case Verbose => "V"
    case Somatic => "G"
    case Material(txt) => s"M ($txt)"
  }

  val magicSchool : MagicSchool => String = {
    case Abjuration => "abjuration"
    case Conjuration => "invocation"
    case Divination => "divination"
    case Enchantment => "enchantement"
    case Evocation => "évocation"
    case Illusion => "illusion"
    case Necromancy => "nécromancie"
    case Transmutation => "transmutation"
  }

  val atWill : String = "à volonté"

  val spellLvl : Int => String = {
    case 0 => "Sorts mineurs"
    case x => s"Niveau $x"
  }
  val slots : Option[Int] => String = {
    case None => atWill
    case Some(x) => s"$x emplacement${plural(x)}"
  }
}
