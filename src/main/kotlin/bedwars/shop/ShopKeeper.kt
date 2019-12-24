package bedwars.shop

import bedwars.Globals
import org.bukkit.Bukkit
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
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask


/**
 * Created by DEDZTBH on 2019/12/23.
 * Project bedwars
 */
class ShopKeeper (
        val name: String,
        val location: Location
        ) : Listener {
    var shopKeeperEntity: Entity? = null
    var shopKeeperTeleportTask: BukkitTask? = null

    // Spawning the ShopKeeper
    fun spawn() = location.run {
        if (shopKeeperEntity != null) return@run
        shopKeeperEntity = world.spawn(location, Villager::class.java)?.apply {
            customName = this@ShopKeeper.name
            isCustomNameVisible = true
            setAdult()
            ageLock = true
            profession = Villager.Profession.FARMER
            addPotionEffect(PotionEffect(PotionEffectType.SLOW, Int.MAX_VALUE, 30, false))
//            ShopKeeperListener.shopKeepers.add(this)
            shopKeeperTeleportTask = (object : BukkitRunnable() {
                override fun run() {
                    teleport(this@ShopKeeper.location)
                }
            }).runTaskTimer(Globals.plugin, 0, 20)
        }
    }

    // Despawn the ShopKeeper
    fun despawn() {
        shopKeeperEntity?.remove()
        shopKeeperEntity = null
        shopKeeperTeleportTask?.cancel()
        shopKeeperTeleportTask = null
    }

    object ShopKeeperListener : Listener {
//      Maybe record all shop keepers?
//        val shopKeepers: MutableSet<Entity> = Collections.newSetFromMap(WeakHashMap<Entity, Boolean>())

        // Disable Damage
        @EventHandler
        fun entityDamage(evt: EntityDamageEvent) {
//            if (shopKeepers.any { evt.entity === it })
            if (evt.entity is Villager)
                evt.isCancelled = true
        }

        @EventHandler
        fun entityDamage(evt: EntityDamageByEntityEvent) = entityDamage(evt as EntityDamageEvent)

        // Disable Trading
        @EventHandler
        fun entityInteract(evt: PlayerInteractEntityEvent) {
//            if (shopKeepers.any { evt.rightClicked === it })
            if (evt.rightClicked is Villager)
                evt.isCancelled = true

        }
    }

}