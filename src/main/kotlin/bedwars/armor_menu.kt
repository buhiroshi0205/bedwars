package bedwars

import com.dummyc0m.pylon.app.component.AppRoot
import com.dummyc0m.pylon.app.component.Component
import com.dummyc0m.pylon.app.view.ViewElement
import com.dummyc0m.pylon.app.view.container
import org.bukkit.entity.Player

fun upgradeArmor(player: Player, buyConfig: BuyConfig, level: ArmorLevel): () -> Unit {
    val playerUpgrades = buyConfig.playerUpgrades

    return {
        val newUpgrade = playerUpgrades.getUpgrade(player).withArmorLevel(level)
        playerUpgrades.setUpgrade(player, newUpgrade)
        val team = player.scoreboard.getEntryTeam(player.name)
                ?: throw IllegalStateException("${player.name} has no team")
        val teamInfo = buyConfig.teamInfos[team.name]
                ?: throw IllegalStateException("$team does not exist in teamInfos")
        val armorSet = newUpgrade.getArmorSet(teamInfo.color)
        player.inventory.armorContents = armorSet
    }
}

class BuyArmor(app: AppRoot, buyConfig: BuyConfig, player: Player) : Component(app) {
    private val chainmail = UpgradableItem(app,
            chainmailLeggings,
            { buyConfig.playerUpgrades.getUpgrade(player).armorLevel < ArmorLevel.Chainmail },
            player,
            buyConfig.chainmailArmorCost,
            upgradeArmor(player, buyConfig, ArmorLevel.Chainmail)
    )

    private val iron = UpgradableItem(app,
            ironLeggings,
            { buyConfig.playerUpgrades.getUpgrade(player).armorLevel < ArmorLevel.Iron },
            player,
            buyConfig.ironArmorCost,
            upgradeArmor(player, buyConfig, ArmorLevel.Iron)
    )

    private val diamond = UpgradableItem(app,
            diamondLeggings,
            { buyConfig.playerUpgrades.getUpgrade(player).armorLevel < ArmorLevel.Diamond },
            player,
            buyConfig.diamondArmorCost,
            upgradeArmor(player, buyConfig, ArmorLevel.Diamond)
    )

    fun render(): ViewElement {
        return container {
            c(0, 0) {
                h(chainmail.render())
            }
            c(1, 0) {
                h(iron.render())
            }
            c(2, 0) {
                h(diamond.render())
            }
        }
    }
}
