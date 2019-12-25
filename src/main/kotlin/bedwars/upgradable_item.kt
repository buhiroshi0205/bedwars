package bedwars

import com.dummyc0m.pylon.app.component.AppRoot
import com.dummyc0m.pylon.app.component.Component
import com.dummyc0m.pylon.app.component.Renderable
import com.dummyc0m.pylon.app.view.ViewElement
import com.dummyc0m.pylon.app.view.item
import com.dummyc0m.pylon.util.*
import org.bukkit.Sound
import org.bukkit.entity.Player

class UpgradableItem(app: AppRoot,
                     private val displayItem: ItemStackBuilder,
                     private val upgradePredicate: () -> Boolean,
                     private val player: Player,
                     private val cost: Resource,
                     private val postPurchase: () -> Unit) : Component(app), Renderable {
    private var predicateValue by prop(upgradePredicate())
    private var insufficientFunds by prop(false)

    override fun render(): ViewElement {
        return item {
            item = displayItem
            if (predicateValue) {
                if (insufficientFunds) {
                    lore("$RESET${RED}INSUFFICIENT FUNDS")
                } else {
                    lore("$RESET${GOLD}CLICK TO BUY")
                }
            } else {
                lore("$RESET${BLUE}ALREADY BOUGHT")
            }

            onClick { _, _ ->
                if (upgradePredicate()) {
                    if (player.inventory.containsAtLeast(cost.itemStack, cost.amount)) {
                        player.inventory.remove(cost.total)
                        postPurchase()
                        predicateValue = false
                    } else {
                        player.playSound(player.eyeLocation, Sound.VILLAGER_NO, 1F, 0F)
                        insufficientFunds = true
                    }
                } else {
                    predicateValue = false
                }
            }
        }
    }
}