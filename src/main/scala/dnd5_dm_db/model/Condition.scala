package dnd5_dm_db
package model

object Condition {
  def fromString(str : String) : Condition = str.toLowerCase match {
    case "prone" => Prone
    case "grappled" => Grappled
    case "deafened" => Deafened
    case "blinded" => Blinded
    case "charmed" => Charmed
    case "frightened" => Frightened
    case "poisoned" => Poisoned
    case "restrained" => Restrained
    case "stunned" => Stunned
    case "incapacitated" => Incapacitated
    case "unconscious" => Unconscious
    case "invisible" => Invisible
    case "paralyzed" => Paralyzed
    case "petrified" => Petrified
    case "exhaustion" => Exhaustion
    case _ => error(s"unknown Condition $str")
  }
}
sealed abstract class Condition
case object Prone extends Condition
case object Grappled extends Condition
case object Deafened extends Condition
case object Blinded extends Condition
case object Charmed extends Condition
case object Frightened extends Condition
case object Poisoned extends Condition
case object Restrained extends Condition
case object Stunned extends Condition
case object Incapacitated extends Condition
case object Unconscious extends Condition
case object Invisible extends Condition
case object Paralyzed extends Condition
case object Petrified extends Condition
case object Exhaustion extends Condition
