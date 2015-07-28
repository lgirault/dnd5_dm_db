package dnd5_dm_db

abstract class Lang {
  val id : String

  val level : String

  val castingTime : String

  val range : String

  val components : String

  val component : Components => String

  val duration : String

  val time : DnDTime => String

  val higherLevels : String
}

object Fr extends Lang {

  val id: String = "fr"

  val range: String = "Portée"

  val level: String = "niveau"

  val castingTime: String = "Temps d'incantation"

  val components : String = "Composantes"

  val component : Components => String = {
    case Verbose => "V"
    case Somatic => "G"
    case Material(txt) => s"M ($txt)"
  }

  val duration: String = "Durée"

  def plural(i : Int) =
    if(i>1) "s"
    else ""

  val time : DnDTime => String = {
    case UpTo(t) => "jusqu'à " + time(t)
    case Minute(i) => i + " minute" + plural(i)
    case Hour(i) => i +" minute" + plural(i)
    case Action(i) => i + " action" + plural(i)
    case BonusAction(i) => i + " action" + plural(i) + " bonus"
    case Instant => "instantané"
  }

  val higherLevels = "Aux niveaux supérieurs"
}