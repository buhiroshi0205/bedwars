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
                    lore("$RESET$cost")
                    lore("$RESET${RED}INSUFFICIENT FUNDS")
                } else {
                    lore("$RESET$cost - ${GOLD}CLICK TO BUY")
                }
            } else {
                lore("$RESET${BLUE}ALREADY BOUGHT")
            }

            onClick { _, _ ->
                if (upgradePredicate()) {
                    if (player.inventory.containsAtLeast(cost.itemStack, cost.amount)) {
                        player.inventory.removeItem(cost.total)
                        postPurchase()
                        predicateValue = false
                        insufficientFunds = false
                        player.playSound(player.eyeLocation, Sound.NOTE_PLING, 100F, 2F)
                    } else {
                        player.playSound(player.eyeLocation, Sound.ENDERMAN_TELEPORT, 100F, 1F)
                        insufficientFunds = true
                    }
                } else {
                    predicateValue = false
                }
            }
        }
    }
}