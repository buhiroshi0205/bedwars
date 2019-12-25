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
                lore("$RESET${RED}INSUFFICIENT FUNDS")
            } else {
                lore("$RESET${GOLD}CLICK TO BUY")
            }


            onClick { _, _ ->
                if (player.inventory.containsAtLeast(cost.itemStack, cost.amount)) {
                    player.inventory.remove(cost.total)
                    player.inventory.addItem(displayItem.render())
                } else {
                    player.playSound(player.eyeLocation, Sound.VILLAGER_NO, 1F, 0F)
                    insufficientFunds = true
                }
            }
        }
    }
}