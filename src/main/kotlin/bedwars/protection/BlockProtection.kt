package bedwars.protection

import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityChangeBlockEvent

/**
 * Created by DEDZTBH on 2019/12/25.
 * Project bedwars
 */

typealias Vec3 = Triple<Float, Float, Float>

object BlockProtection : Listener {
    val protectedAreas = mutableSetOf<Pair<Vec3, Vec3>>()

    @JvmStatic
    fun loadProtectedAreas(config: ConfigurationSection, path: String = "protected") =
            config.getConfigurationSection(path).getKeys(false).forEach {
                protectedAreas.add(getPairVec3(config, "$path.$it"))
            }

    fun getPairVec3(config: ConfigurationSection, path: String): Pair<Vec3, Vec3> {
        return Pair(getVec3(config, "$path.low"), getVec3(config, "$path.high"))
    }

    fun getVec3(config: ConfigurationSection, path: String): Vec3 {
        val x = config.getDouble("$path.x").toFloat()
        val y = config.getDouble("$path.y").toFloat()
        val z = config.getDouble("$path.z").toFloat()
        return Vec3(x, y, z)
    }

    private fun <T> processEvent(evt: T): Boolean where T : BlockEvent, T : Cancellable =
            processBlock(evt.block).also {
                if (it) evt.isCancelled = true
            }

    private fun processBlock(b: Block): Boolean =
            protectedAreas.any {
                it.first.first <= b.x && b.x <= it.second.first &&
                it.first.second <= b.y && b.y <= it.second.second &&
                it.first.third <= b.z && b.z <= it.second.third
            }

    @EventHandler fun blockEvent(evt: BlockBreakEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockBurnEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockDamageEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockExplodeEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockFormEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockFadeEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockFromToEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockGrowEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockIgniteEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockMultiPlaceEvent) {
            evt.replacedBlockStates.forEach {
                if (processBlock(it.block)) {
                    evt.isCancelled = true
                    return@blockEvent
                }
            }
        }
    @EventHandler fun blockEvent(evt: BlockPlaceEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: BlockSpreadEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: EntityBlockFormEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: LeavesDecayEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: SignChangeEvent) = processEvent(evt)
    @EventHandler fun blockEvent(evt: EntityChangeBlockEvent) {
            if (processBlock(evt.block)) evt.isCancelled = true
        }
}