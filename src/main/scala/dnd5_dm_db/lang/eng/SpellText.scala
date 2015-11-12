package dnd5_dm_db
package lang.eng

import dnd5_dm_db.lang.Lang
import dnd5_dm_db.model._


trait SpellText extends lang.SpellText {
  self : Lang =>
  val castingTime: String = "Casting Time"

  val components : String = "Components"

  val duration: String = "Duration"

  val concentration : String = "Concentration"

  val higherLevels = "At Higher Levels"

  val component : Components => String = {
    case Verbose => "V"
    case Somatic => "S"
    case Material(txt) => s"M (${txt.value(self)})"
  }
  val lengthAdj : DnDLength => String = {
    case Feet(i) => s"$i-foot"
    case d => length(d)
  }

  val sAreaOfEffect : Option[AreaOfEffect] => String = {
    case Some(Line(l)) => s" (${lengthAdj(l)} line)"
    case Some(Sphere(r)) => s" (${lengthAdj(r)} radius)"
    case Some(Cube(sl)) => s" (${lengthAdj(sl)} cube)"
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

  val ritual : String = "Ritual"

}
