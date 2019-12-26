package bedwars

import com.dummyc0m.pylon.app.component.AppRoot
import com.dummyc0m.pylon.app.component.Component
import com.dummyc0m.pylon.app.view.ViewElement
import com.dummyc0m.pylon.app.view.container
import com.dummyc0m.pylon.util.ItemStackBuilder
import org.bukkit.entity.Player

class BuyTools(app: AppRoot, buyConfig: BuyConfig, player: Player) : Component(app) {
    private fun upgradePick(app: AppRoot, buyConfig: BuyConfig, player: Player,
                            cost: Resource, toolLevel: PickLevel, item: ItemStackBuilder): UpgradableItem {
        return UpgradableItem(app, item,
                { buyConfig.playerUpgrades.getUpgrade(player).pickLevel < toolLevel },
                player,
                cost,
                {
                    val newUpgrade = buyConfig.playerUpgrades.getUpgrade(player).withPickLevel(toolLevel)
                    buyConfig.playerUpgrades.setUpgrade(player, newUpgrade)
                    player.inventory.addPick(item.render())
                })
    }

    private fun upgradeAxe(app: AppRoot, buyConfig: BuyConfig, player: Player,
                           cost: Resource, toolLevel: AxeLevel, item: ItemStackBuilder): UpgradableItem {
        return UpgradableItem(app, item,
                { buyConfig.playerUpgrades.getUpgrade(player).axeLevel < toolLevel },
                player,
                cost,
                {
                    val newUpgrade = buyConfig.playerUpgrades.getUpgrade(player).withAxeLevel(toolLevel)
                    buyConfig.playerUpgrades.setUpgrade(player, newUpgrade)
                    player.inventory.addAxe(item.render())
                })
    }

    private val woodPick = upgradePick(app, buyConfig, player, buyConfig.woodPickCost, PickLevel.Wood, bedwars.woodPick)
    private val ironPick = upgradePick(app, buyConfig, player, buyConfig.ironPickCost, PickLevel.Iron, bedwars.ironPick)
    private val goldPick = upgradePick(app, buyConfig, player, buyConfig.goldPickCost, PickLevel.Gold, bedwars.goldPick)
    private val diamondPick = upgradePick(app, buyConfig, player, buyConfig.diamondPickCost, PickLevel.Diamond, bedwars.diamondPick)

    private val woodAxe = upgradeAxe(app, buyConfig, player, buyConfig.woodAxeCost, AxeLevel.Wood, bedwars.woodAxe)
    private val stoneAxe = upgradeAxe(app, buyConfig, player, buyConfig.stoneAxeCost, AxeLevel.Stone, bedwars.stoneAxe)
    private val ironAxe = upgradeAxe(app, buyConfig, player, buyConfig.ironAxeCost, AxeLevel.Iron, bedwars.ironAxe)
    private val diamondAxe = upgradeAxe(app, buyConfig, player, buyConfig.diamondAxeCost, AxeLevel.Diamond, bedwars.diamondAxe)

    private val shears = UpgradableItem(app, bedwars.shears, {
        !buyConfig.playerUpgrades.getUpgrade(player).hasShears
    }, player, buyConfig.shearsCost, {
        val newUpgrade = buyConfig.playerUpgrades.getUpgrade(player).withShears()
        buyConfig.playerUpgrades.setUpgrade(player, newUpgrade)
        player.inventory.addItem(bedwars.shears.render())
    })

    fun render(): ViewElement {
        return container {
            c(0, 0) {
                h(woodPick.render())
            }
            c(1, 0) {
                h(ironPick.render())
            }
            c(2, 0) {
                h(goldPick.render())
            }
            c(3, 0) {
                h(diamondPick.render())
            }

            c(0, 1) {
                h(woodAxe.render())
            }
            c(1, 1) {
                h(stoneAxe.render())
            }
            c(2, 1) {
                h(ironAxe.render())
            }
            c(3, 1) {
                h(diamondAxe.render())
            }

            c(0, 2) {
                h(shears.render())
            }
        }
    }
}