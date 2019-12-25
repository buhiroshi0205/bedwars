package bedwars

import com.dummyc0m.pylon.app.component.AppRoot
import com.dummyc0m.pylon.app.component.Component
import com.dummyc0m.pylon.app.view.ViewElement
import com.dummyc0m.pylon.app.view.item
import com.dummyc0m.pylon.util.GOLD
import com.dummyc0m.pylon.util.ItemStackBuilder
import com.dummyc0m.pylon.util.RED
import com.dummyc0m.pylon.util.RESET
import org.bukkit.Sound
import org.bukkit.entity.Player

class PurchasableItem(app: AppRoot, val buyConfig: BuyConfig,
                      val player: Player, val displayItem: ItemStackBuilder, val cost: Resource) : Component(app) {
    private var insufficientFunds by prop(false)

    fun render(): ViewElement {
        return item {
            item = displayItem
            if (insufficientFunds) {
                lore("$RESET$cost")
                lore("$RESET${RED}INSUFFICIENT FUNDS")
            } else {
                lore("$RESET$cost - ${GOLD}CLICK TO BUY")
            }


            onClick { _, _ ->
                if (player.inventory.containsAtLeast(cost.itemStack, cost.amount)) {
                    player.inventory.removeItem(cost.total)
                    player.inventory.addItem(displayItem.render())
                    player.playSound(player.eyeLocation, Sound.NOTE_PLING, 100F, 2F)
                    insufficientFunds = false
                } else {
                    player.playSound(player.eyeLocation, Sound.ENDERMAN_TELEPORT, 100F, 1F)
                    insufficientFunds = true
                }
            }
        }
    }
}