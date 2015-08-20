package dnd5_dm_db

object ChallengeRanking {
  def fromString(str : String) : ChallengeRanking = {
    if(str.contains("/")){
      val s = str.split("/")
      CRRational(s(0).toInt,s(1).toInt)
    }
    else CRInt(str.toInt)
  }
}
sealed abstract class ChallengeRanking {
  def value : Double
}
case class CRInt(i : Int) extends ChallengeRanking {
  def value = i.toDouble
  override def toString = i.toString
}
case class CRRational(num : Int, den : Int) extends ChallengeRanking {
  def value = num.toDouble / den.toDouble
  override def toString = s"$num/$den"
}
