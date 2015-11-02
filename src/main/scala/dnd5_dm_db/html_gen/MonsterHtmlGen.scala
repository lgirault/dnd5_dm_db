package dnd5_dm_db.html_gen

import dnd5_dm_db.{Constant, ToHtml}
import dnd5_dm_db.lang.Lang
import dnd5_dm_db.model._


object MonsterHtmlGen extends ToHtml[Monster]{

  def titledSeq[A](title:String, s : Seq[A])(f : A => String) : String = {
    if(s.isEmpty) ""
    else s map f mkString(s"<div><b>$title :</b> ", ", ","</div>")
  }

  def toHtml
  ( id : String, m : Monster)
  ( implicit lang : Lang) : String = {
    val sepMonster =
      """<img src="images/sep-monster.gif" alt="" class="sep-monster" width="95%" height="4" />"""

    val (avgHp, hpDie) = (m.hitPoint.average, m.hitPoint)

    val saveStr = titledSeq(lang.savingThrows, m.savingThrows){
      case (ab, bonus) =>
        lang.ability_short(ab) + ":" + Die.bonus_str(bonus)
    }
    val skillStr = titledSeq(lang.skills, m.skills) {
      case (s, v) => lang.skill(s) + " " + Die.bonus_str(v)
    }


    val dmgVulnStr = titledSeq(lang.damageVulnerabilites, m.vulnerabilities)(lang.damageType)
    val dmgImmunStr = titledSeq(lang.damageImmunities, m.damageImmunities)(lang.damageType)
    val condImmunStr = titledSeq(lang.conditionImmunities, m.conditionImmunities)(lang.conditions)
    val resistancesStr = titledSeq(lang.resistance, m.resistances)(lang.damageType)
    val sensStr = titledSeq(lang.senses, m.sensList)(lang.sens)
    val langStr =
      if(m.languages.nonEmpty)
        titledSeq(lang.languages, m.languages)(lang.language)
      else s"<div><b>${lang.languages}</b>: --</div>"

    val armorDesc = m.armorDesc map {s => s"(${s.value})"} getOrElse ""

    def subSection[A](title : String, elts : Seq[A])(formatter : A => String) : String =
      if(elts.nonEmpty)
        s"""<div class="rub">$title</div>
           |${elts map formatter mkString ""}
         """.stripMargin
      else ""

    val actionsStr = subSection("ACTIONS", m.actions)(Action.toHtml)
    val reActionsStr = subSection("REACTIONS", m.reactions)(Action.toHtml)

    s"""<div id="monsters_$id" class="bloc">
       |   <div class="dragbar"><div></div></div>
       |   ${Templates.tradDiv(Constant.monsters, id, m.name, lang)}
       |   <div class="name">${m.name.value}</div>
       |   <div class="type sansSerif">
       |        <em>${lang.monsterTypeAndSize(m.size, m.typ, m.typeTags)}, ${lang.alignment(m.alignment)}</em>
       |     </div>
       |    $sepMonster
       |    <div class="red">
       |        <b>${lang.armorClass} :</b> ${m.armorClass} $armorDesc<br/>
       |        <b>${lang.hp} :</b> $avgHp ($hpDie) <br/>
       |        <b>${lang.speed} :</b> ${m.speed map lang.speedLength mkString ","} <br/>
       |    $sepMonster
       |    ${abilitiesToHtml(m.abilities)}
       |    $sepMonster
       |    $saveStr
       |    $skillStr
       |    $dmgVulnStr
       |    $dmgImmunStr
       |    $condImmunStr
       |    $resistancesStr
       |    $sensStr
       |    $langStr
       |    <div><b>${lang.challengeRanking}</b> ${m.challengeRanking} (${m.challengeRanking.xp} ${lang.xp})</div>
       |    </div><!-- /red -->
       |    $sepMonster
       |    ${m.traits map traitToHtml mkString ""}
       |    ${m.spellCasting.map{spellCastingToHtml(m.name.value, _)}.getOrElse("")}
       |    $actionsStr
       |    $reActionsStr
       |    ${Templates.sourceToHtml(m.source)}
       |</div> <!-- /block -->""".stripMargin
  }

  def spellCastingToHtml
  ( monster : String,
    spellCasting : SpellCasting)
  (implicit lang : Lang): String = {
    s"""<div>
       | <b><em>${lang.spells}</em></b> ${lang.spellCastingText(monster, spellCasting)}
       | ${spellCasting.spells map spellListToHtml mkString ""}
       |</div>""".stripMargin
  }

  def spellListToHtml
  (spellList: SpellList)
  (implicit lang : Lang): String = {
    s"<div>${lang.spellLvl(spellList.level)} (${lang.slots(spellList.slots)}) : " +
      spellList.spells.map(s => SpellHtmlGen.toHtmlWithClass(None, s,"innerBlock")).mkString("") +
      "</div>"
  }

  def abilitiesToHtml
  ( abilities: Abilities)
  ( implicit lang : Lang) : String = {
    val h = abilityToHtml _
    h(Strength, abilities.strength) +
      h(Dexterity, abilities.dexterity) +
      h(Constitution, abilities.constitution) +
      h(Intelligence, abilities.intelligence) +
      h(Wisdom, abilities.wisdom) +
      h(Charisma, abilities.charisma)
  }

  def abilityToHtml
  ( ability: Ability,
    value: Int)(implicit lang : Lang) : String = {
    s"""<div class="ability"><strong>${lang.ability_short(ability)}</strong><br/>
       |    $value (${Die.bonus_str(Ability.modifier(value))})
       |</div>""".stripMargin
  }

  def traitToHtml(t : Trait)(implicit lang: Lang): String =
    s"<div><b>${t.name.value}:</b> ${t.description.value}</div>"


}