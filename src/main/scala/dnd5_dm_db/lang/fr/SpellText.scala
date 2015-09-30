package dnd5_dm_db.lang.fr

import dnd5_dm_db._


trait SpellText extends lang.SpellText {

  val castingTime: String = "Temps d'incantation"

  val components : String = "Composantes"

  val duration: String = "Durée"

  val higherLevels = "Aux niveaux supérieurs"

  val component : Components => String = {
    case Verbose => "V"
    case Somatic => "G"
    case Material(txt) => s"M ($txt)"
  }

  val sAreaOfEffect : Option[AreaOfEffect] => String = {
    case Some(Line(l)) => s" (ligne de ${unitsText.length(l)})"
    case Some(Sphere(r)) => s" (sur un rayon de ${unitsText.length(r)})"
    case None => ""
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

}