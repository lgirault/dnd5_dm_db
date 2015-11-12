package dnd5_dm_db
package lang.fr

import dnd5_dm_db.lang.Lang
import dnd5_dm_db.model._


trait SpellText extends lang.SpellText {
  self : Lang =>
  val castingTime: String = "Temps d'incantation"

  val components : String = "Composantes"

  val duration: String = "Durée"

  val concentration : String = "Concentration"

  val higherLevels = "Aux niveaux supérieurs"

  val component : Components => String = {
    case Verbose => "V"
    case Somatic => "G"
    case Material(txt) => s"M (${txt.value(self)})"
  }

  val sAreaOfEffect : Option[AreaOfEffect] => String = {
    case Some(Line(l)) => s" (ligne de ${length(l)})"
    case Some(Sphere(r)) => s" (sur un rayon de ${length(r)})"
    case Some(Cube(sl)) => s" (cube de ${length(sl)} de coté)"
    case None => ""
  }

  val magicSchool : MagicSchool => String = {
    case Abjuration => "abjuration"
    case Conjuration => "conjuration"
    case Divination => "divination"
    case Enchantment => "enchantement"
    case Evocation => "évocation"
    case Illusion => "illusion"
    case Necromancy => "nécromancie"
    case Transmutation => "transmutation"
  }

  val ritual : String = "Rituel"
}
