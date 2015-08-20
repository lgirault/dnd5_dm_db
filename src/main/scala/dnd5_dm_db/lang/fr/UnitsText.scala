package dnd5_dm_db.lang.fr

import dnd5_dm_db._


trait UnitsText {

  val feetToMeter : Int => String ={
    import java.text.DecimalFormat
    val cmPerFoot = 30
    val formatter = new DecimalFormat("#.##")

    { i => formatter.format((i * cmPerFoot).toDouble / 100) }
  }

  val length : DnDLength => String = {
    case Feet(i) => feetToMeter(i) +" m"
    case Contact => "contact"
  }

  val rangeLength : ((DnDLength, DnDLength)) => String = {
    case (Feet(reg), Feet(extra)) => feetToMeter(reg) +"/" +feetToMeter(extra) +"m"
    case _ => error("rangeLength expect a pair of feet length")
  }



  val time : DnDTime => String = {
    case UpTo(t) => "jusqu'à " + time(t)
    case Minute(i) => i + " minute" + plural(i)
    case Hour(i) => i +" minute" + plural(i)
    case RegularAction(i) => i + " action" + plural(i)
    case BonusAction(i) => i + " action" + plural(i) + " bonus"
    case Instant => "instantané"
  }
}
