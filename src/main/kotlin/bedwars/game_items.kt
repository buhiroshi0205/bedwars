package bedwars

import com.dummyc0m.pylon.util.RESET
import com.dummyc0m.pylon.util.itemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag

private fun getWool(color: Short) =
        itemBuilder {
            material = Material.WOOL
            damage = color
            amount = 16
        }

val endStone = itemBuilder {
    material = Material.ENDER_STONE
    amount = 12
}

val leatherHelmet = itemBuilder {
    material = Material.LEATHER_HELMET
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val leatherChestplate = itemBuilder {
    material = Material.LEATHER_CHESTPLATE
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val leatherLeggings = itemBuilder {
    material = Material.LEATHER_LEGGINGS
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val leatherBoots = itemBuilder {
    material = Material.LEATHER_BOOTS
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val woodSword = itemBuilder {
    material = Material.WOOD_SWORD
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

// ARMOR

val chainmailLeggings = itemBuilder {
    material = Material.CHAINMAIL_LEGGINGS
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val chainmailBoots = itemBuilder {
    material = Material.CHAINMAIL_BOOTS
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val ironLeggings = itemBuilder {
    material = Material.IRON_LEGGINGS
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val ironBoots = itemBuilder {
    material = Material.IRON_BOOTS
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val diamondLeggings = itemBuilder {
    material = Material.DIAMOND_LEGGINGS
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val diamondBoots = itemBuilder {
    material = Material.DIAMOND_BOOTS
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

// MELEE

val stoneSword = itemBuilder {
    material = Material.STONE_SWORD
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val ironSword = itemBuilder {
    material = Material.IRON_SWORD
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val diamondSword = itemBuilder {
    material = Material.DIAMOND_SWORD
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val knockbackStick = itemBuilder {
    material = Material.BLAZE_ROD
    displayName = "${RESET}Knockback Stick"
    enchant(Enchantment.KNOCKBACK, 1)
}

// RANGED

val arrows = itemBuilder {
    material = Material.ARROW
    amount = 8
}

val bow1 = itemBuilder {
    material = Material.BOW
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

val bow2 = itemBuilder {
    material = Material.BOW
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.ARROW_DAMAGE, 1)
}

val bow3 = itemBuilder {
    material = Material.BOW
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.ARROW_DAMAGE, 1)
    enchant(Enchantment.ARROW_KNOCKBACK, 1)
}

// TOOLS

val shears = itemBuilder {
    material = Material.SHEARS
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
}

// PICK

val woodPick = itemBuilder {
    material = Material.WOOD_PICKAXE
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.DIG_SPEED, 1)
}

val ironPick = itemBuilder {
    material = Material.IRON_PICKAXE
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.DIG_SPEED, 2)
}

val goldPick = itemBuilder {
    material = Material.GOLD_PICKAXE
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.DIG_SPEED, 2)
    enchant(Enchantment.DAMAGE_ALL, 2)
}

val diamondPick = itemBuilder {
    material = Material.DIAMOND_PICKAXE
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.DIG_SPEED, 3)
}

// AXE

val woodAxe = itemBuilder {
    material = Material.WOOD_AXE
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.DIG_SPEED, 1)
}

val stoneAxe = itemBuilder {
    material = Material.STONE_AXE
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.DIG_SPEED, 1)
}

val ironAxe = itemBuilder {
    material = Material.IRON_AXE
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.DIG_SPEED, 2)
}

val diamondAxe = itemBuilder {
    material = Material.DIAMOND_AXE
    unbreakable = true
    flag(ItemFlag.HIDE_UNBREAKABLE)
    enchant(Enchantment.DIG_SPEED, 3)
}

// MISC TODO incomplete

val goldenApple = itemBuilder {
    material = Material.GOLDEN_APPLE
}

val fireball = itemBuilder {
    material = Material.FIREBALL
}

val tnt = itemBuilder {
    material = Material.TNT
}

val enderPearl = itemBuilder {
    material = Material.ENDER_PEARL
}

val waterBucket = itemBuilder {
    material = Material.WATER_BUCKET
}
