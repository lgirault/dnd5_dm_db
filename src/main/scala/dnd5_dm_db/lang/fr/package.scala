package dnd5_dm_db.lang

package object fr {
  def plural(i : Int) =
    if(i>1) "s"
    else ""

  val unitsText = new UnitsText {}
}
