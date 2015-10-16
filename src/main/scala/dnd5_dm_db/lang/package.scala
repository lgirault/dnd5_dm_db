package dnd5_dm_db

package object lang {

  val locales = Seq(Fr, Eng)
  def otherLocales(l : Lang) = locales.filterNot( _ == l )
  val register = Map("fr" -> Fr, "eng" -> Eng)
  def langFromString(id : String) : Lang =
    register(id)

}
