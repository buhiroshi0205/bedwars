package bedwars.shop

import bedwars.util.then
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


/**
 * Created by DEDZTBH on 2019/12/23.
 * Project bedwars
 */
class ShopKeeper(
        val name: String,
        val location: Location,
        val onInteract: (PlayerInteractEntityEvent, ShopKeeper) -> Unit
) {
    var shopKeeperEntity: Entity? = null

    // Spawning the ShopKeeper
    fun spawn() = location.run {
        if (shopKeeperEntity != null) return@run
        shopKeeperEntity = world.spawn(location, Villager::class.java)?.apply {
            customName = this@ShopKeeper.name
            isCustomNameVisible = true
            setAdult()
            ageLock = true
            profession = Villager.Profession.FARMER
            addPotionEffect(PotionEffect(PotionEffectType.SLOW, Int.MAX_VALUE, 65536, false))
            ShopKeeperManager.shopKeepers.add(this@ShopKeeper)
        }
    }

    // Despawn the ShopKeeper
    fun despawn(removeFromManager: Boolean = true) {
        if (removeFromManager)
            ShopKeeperManager.shopKeepers.remove(this)
        shopKeeperEntity?.remove()
        shopKeeperEntity = null
    }
}

object ShopKeeperManager : Listener {

    val shopKeepers = mutableSetOf<ShopKeeper>()

    fun cleanUp() {
        shopKeepers.forEach {
            it.despawn(false)
        }
        shopKeepers.clear()
    }

    // Disable Damage
    @EventHandler
    fun entityDamage(evt: EntityDamageEvent) =
            shopKeepers.any { it.shopKeeperEntity === evt.entity } then { evt.isCancelled = true }

    @EventHandler
    fun entityDamage(evt: EntityDamageByEntityEvent) = entityDamage(evt as EntityDamageEvent)

    // Disable Trading
    @EventHandler
    fun entityInteract(evt: PlayerInteractEntityEvent) =
            shopKeepers.firstOrNull { it.shopKeeperEntity === evt.rightClicked }?.let {
                evt.isCancelled = true
                it.onInteract(evt, it)
            }
}
