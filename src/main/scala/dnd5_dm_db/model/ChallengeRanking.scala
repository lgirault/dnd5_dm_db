package dnd5_dm_db.model

object ChallengeRanking {
  def fromString(str : String) : ChallengeRanking = {
    if(str.contains("/")){
      val s = str.split("/")
      CRRational(s(0).toInt,s(1).toInt)
    }
    else CRInt(str.toInt)
  }

  private val xpPerCr =
    Map[Double, Int](
      0d -> 0, // 0 or 10
      0.125 -> 25,
      0.25 -> 50,
      0.5 -> 100,
      1d -> 200,
      2d -> 450,
      3d -> 700,
      4d -> 1100,
      5d -> 1800,
      6d -> 2300,
      7d -> 2900,
      8d -> 3900,
      9d -> 5000,
      10d -> 5900,
      11d -> 7200,
      12d -> 8400,
      13d -> 10000,
      14d -> 11500,
      15d -> 13000,
      16d -> 15000,
      17d -> 18000,
      18d -> 20000,
      19d -> 22000,
      20d -> 25000,
      21d -> 33000,
      22d -> 41000,
      23d -> 50000,
      24d -> 62000,
      25d -> 75000,
      26d -> 90000,
      27d -> 105000,
      28d -> 120000,
      29d -> 135000,
      30d -> 155000
    )



}
sealed abstract class ChallengeRanking {
  def value : Double
  def xp : Int =
    try ChallengeRanking.xpPerCr(value)
    catch{
      case e : Exception => sys.error(s"$value : invalid challenge rating for default xp")
    }
}

case class XPCustomCR
(cr0 : ChallengeRanking,
 override val xp : Int) extends ChallengeRanking {
  def value = cr0.value
  override def toString = cr0.toString
}

case class CRInt(i : Int) extends ChallengeRanking {
  def value = i.toDouble
  override def toString = i.toString
}
case class CRRational(num : Int, den : Int) extends ChallengeRanking {
  def value = num.toDouble / den.toDouble
  override def toString = s"$num/$den"
}
