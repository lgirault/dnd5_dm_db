package dnd5_dm_db.lang.eng

import dnd5_dm_db.model._


trait UnitsText {

  val feetToMeter : Int => String ={
    import java.text.DecimalFormat
    val cmPerFoot = 30
    val formatter = new DecimalFormat("#.##")

    { i => formatter.format((i * cmPerFoot).toDouble / 100) }
  }

  val length : DnDLength => String = {
    case Feet(i) => feetToMeter(i) +" m"
    case Touch => "contact"
    case Self => "soit-même"
  }

  val rangeLength : ((DnDLength, DnDLength)) => String = {
    case (Feet(reg), Feet(extra)) => feetToMeter(reg) +"/" +feetToMeter(extra) +"m"
    case _ => error("rangeLength expect a pair of feet length")
  }



  val time : DnDTime => String = {
    case UpTo(t) => "jusqu'à " + time(t)
    case Minute(i) => i + " minute" + plural(i)
    case Hour(i) => i +" heure" + plural(i)
    case Round(i) => i +" tour" + plural(i)
    case RegularAction(i) => i + " action" + plural(i)
    case BonusAction(i) => i + " action" + plural(i) + " bonus"
    case Reaction(trigger) => "1 réaction" + trigger
    case Instant => "instantané"
  }
}
