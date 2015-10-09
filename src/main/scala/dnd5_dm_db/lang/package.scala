package dnd5_dm_db

/**
 * Created by lorilan on 10/8/15.
 */
package object lang {

  val register = Map("fr" -> Fr, "eng" -> Eng)
  def langFromString(id : String) : Lang =
    register(id)

}
