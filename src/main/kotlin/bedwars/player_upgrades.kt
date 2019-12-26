package bedwars

import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import java.util.*

enum class AxeLevel {
    None,
    Wood,
    Stone,
    Iron,
    Diamond;

    fun downgrade(): AxeLevel =
            when (this) {
                None -> None
                Wood -> Wood
                Stone -> Wood
                Iron -> Stone
                Diamond -> Iron
            }
}

enum class PickLevel {
    None,
    Wood,
    Iron,
    Gold,
    Diamond;

    fun downgrade(): PickLevel =
            when (this) {
                None -> None
                Wood -> Wood
                Iron -> Wood
                Gold -> Iron
                Diamond -> Gold
            }
}

enum class ArmorLevel {
    Leather,
    Chainmail,
    Iron,
    Diamond
}

data class PlayerUpgrade(
        val pickLevel: PickLevel,
        val axeLevel: AxeLevel,
        val armorLevel: ArmorLevel,
        val hasShears: Boolean
) {
    fun withPickLevel(pickLevel: PickLevel) = PlayerUpgrade(pickLevel, axeLevel, armorLevel, hasShears)
    fun withAxeLevel(axeLevel: AxeLevel) = PlayerUpgrade(pickLevel, axeLevel, armorLevel, hasShears)
    fun withArmorLevel(armorLevel: ArmorLevel) = PlayerUpgrade(pickLevel, axeLevel, armorLevel, hasShears)
    fun withShears() = PlayerUpgrade(pickLevel, axeLevel, armorLevel, true)

    fun getPick(): ItemStack? =
            when (pickLevel) {
                PickLevel.None -> null
                PickLevel.Wood -> woodPick.render()
                PickLevel.Gold -> goldPick.render()
                PickLevel.Iron -> ironPick.render()
                PickLevel.Diamond -> diamondPick.render()
            }

    fun getAxe(): ItemStack? =
            when (axeLevel) {
                AxeLevel.None -> null
                AxeLevel.Wood -> woodAxe.render()
                AxeLevel.Stone -> stoneAxe.render()
                AxeLevel.Iron -> ironAxe.render()
                AxeLevel.Diamond -> diamondAxe.render()
            }

    fun downgradeTools(): PlayerUpgrade =
            PlayerUpgrade(pickLevel.downgrade(), axeLevel.downgrade(), armorLevel, hasShears)

    fun getShears(): ItemStack? =
            if (hasShears) shears.render() else null

    private fun setLeatherColor(itemStack: ItemStack, color: Color) {
        val leatherMeta = itemStack.itemMeta as LeatherArmorMeta
        leatherMeta.color = color
        itemStack.itemMeta = leatherMeta
    }

    fun getArmorSet(color: Color): Array<ItemStack> {
        val helmet = leatherHelmet.render()
        setLeatherColor(helmet, color)
        val chestplate = leatherChestplate.render()
        setLeatherColor(chestplate, color)

        val leggings: ItemStack
        val boots: ItemStack
        when (armorLevel) {
            ArmorLevel.Leather -> {
                leggings = leatherLeggings.render()
                setLeatherColor(leggings, color)
                boots = leatherBoots.render()
                setLeatherColor(boots, color)
            }
            ArmorLevel.Chainmail -> {
                leggings = chainmailLeggings.render()
                boots = chainmailBoots.render()
            }
            ArmorLevel.Iron -> {
                leggings = ironLeggings.render()
                boots = ironBoots.render()
            }
            ArmorLevel.Diamond -> {
                leggings = diamondLeggings.render()
                boots = diamondBoots.render()
            }
        }
        return arrayOf(boots, leggings, chestplate, helmet)
    }

    companion object {
        val DEFAULT = PlayerUpgrade(PickLevel.None, AxeLevel.None, ArmorLevel.Leather, false)
    }
}

// manager class that stores the upgrades
class PlayerUpgrades {
    private var upgradeMap: MutableMap<UUID, PlayerUpgrade> = hashMapOf()

    fun getUpgrade(player: Player): PlayerUpgrade = upgradeMap[player.uniqueId] ?: PlayerUpgrade.DEFAULT

    fun setUpgrade(player: Player, newUpgrade: PlayerUpgrade) {
        upgradeMap[player.uniqueId] = newUpgrade
    }

    fun resetUpgrades() {
        upgradeMap = hashMapOf()
    }
}

// util that updates an inventory with an item
// removing all free items
fun Inventory.addPick(itemStack: ItemStack) {
    remove(woodPick.render())
    addItem(itemStack)
}

fun Inventory.addAxe(itemStack: ItemStack) {
    remove(woodAxe.render())
    addItem(itemStack)
}

fun Inventory.addSword(itemStack: ItemStack) {
    remove(woodSword.render())
    addItem(itemStack)
}
