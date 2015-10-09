package dnd5_dm_db
package lang.eng

import dnd5_dm_db.lang.Lang
import dnd5_dm_db.model._


trait UnitsText {
  self : Lang =>

  val length : DnDLength => String = {
    case Feet(i) => s"$i feet"
    case Touch => "Touch"
    case Self => "Self"
  }

  val rangeLength : ((DnDLength, DnDLength)) => String = {
    case (Feet(reg), Feet(extra)) => s"$reg/$extra feet"
    case _ => error("rangeLength expect a pair of feet length")
  }



  val time : DnDTime => String = {
    case UpTo(t) => "up to " + time(t)
    case Minute(i) => i + " minute" + plural(i)
    case Hour(i) => i +" hour" + plural(i)
    case Round(i) => i +" tour" + plural(i)
    case RegularAction(i) => i + " action" + plural(i)
    case BonusAction(i) => i + " bonus action" + plural(i)
    case Reaction(trigger) => "1 reaction" + trigger.value(self)
    case Instant => "instantaneous"
  }
}
