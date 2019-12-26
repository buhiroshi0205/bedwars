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
import java.util.*


/**
 * Created by DEDZTBH on 2019/12/23.
 * Project bedwars
 */
data class ShopKeeper(
        val name: String,
        val location: Location,
        val onInteract: (PlayerInteractEntityEvent, ShopKeeper) -> Unit
) {
    var shopKeeperEntity: Entity? = null
}

object ShopKeeperManager : Listener {

    val shopKeepers = mutableMapOf<UUID, ShopKeeper>()

    // Spawning the ShopKeeper
    fun spawn(name: String, location: Location, onInteract: (PlayerInteractEntityEvent, ShopKeeper) -> Unit): ShopKeeper =
            ShopKeeper(name, location, onInteract).also {
                it.location.run {
                    if (it.shopKeeperEntity != null) return@run
                    it.shopKeeperEntity = world.spawn(it.location, Villager::class.java)?.apply {
                        customName = it.name
                        isCustomNameVisible = true
                        setAdult()
                        ageLock = true
                        profession = Villager.Profession.FARMER
                        addPotionEffect(PotionEffect(PotionEffectType.SLOW, Int.MAX_VALUE, 65536, false))
                        shopKeepers[uniqueId] = it
                    }
                }
            }

    // Despawn the ShopKeeper
    // Does not remove from shopKeepers
    fun despawn(shopKeeper: ShopKeeper) = shopKeeper.run {
        shopKeeperEntity?.remove()
        shopKeeperEntity = null
    }

    // Clean up all shopkeepers
    fun cleanUp() {
        shopKeepers.forEach { (_, shopKeeper) -> despawn(shopKeeper) }
        shopKeepers.clear()
    }

    // Disable Damage
    @EventHandler
    fun entityDamage(evt: EntityDamageEvent) =
            shopKeepers.any { (uuid, _) ->
                uuid == evt.entity.uniqueId
            } then { evt.isCancelled = true }

    @EventHandler
    fun entityDamage(evt: EntityDamageByEntityEvent) = entityDamage(evt as EntityDamageEvent)

    // Disable Trading
    @EventHandler
    fun entityInteract(evt: PlayerInteractEntityEvent) =
            shopKeepers[evt.rightClicked.uniqueId]?.run {
                evt.isCancelled = true
                onInteract(evt, this)
            }
}
