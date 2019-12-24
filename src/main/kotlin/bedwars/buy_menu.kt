package bedwars

import com.dummyc0m.pylon.app.component.AppRoot
import com.dummyc0m.pylon.app.component.Component
import com.dummyc0m.pylon.app.component.RootComponent
import com.dummyc0m.pylon.app.component.bootstrapApp
import com.dummyc0m.pylon.app.view.RootElement
import com.dummyc0m.pylon.app.view.ViewElement
import com.dummyc0m.pylon.app.view.container
import com.dummyc0m.pylon.app.view.root
import com.dummyc0m.pylon.util.BLUE
import com.dummyc0m.pylon.util.GOLD
import com.dummyc0m.pylon.util.RESET
import com.dummyc0m.pylon.util.itemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

fun launchBuyMenu(
        javaPlugin: JavaPlugin,
        player: Player
) {
    bootstrapApp(javaPlugin, player) {
        BuyNav(it, player)
    }
}

sealed class BuyRoute

private object HomeRoute : BuyRoute()
private object BlockRoute : BuyRoute()
private object ToolRoute : BuyRoute()

class NavBar(app: AppRoot, val routeCallback: (BuyRoute) -> Unit) : Component(app) {
    fun render(currentRoute: BuyRoute): ViewElement {
        return container {
            i(0, 0) {
                material = Material.WOOD_DOOR
                displayName = "${RESET}Home"
                if (currentRoute == HomeRoute) {
                    enchant(Enchantment.SILK_TOUCH, 1)
                } else {
                    onClick { _, _ -> routeCallback(HomeRoute) }
                }
            }
            i(1, 0) {
                material = Material.WOOL
                displayName = "${RESET}Blocks"
                if (currentRoute == BlockRoute) {
                    enchant(Enchantment.SILK_TOUCH, 1)
                } else {
                    onClick { _, _ -> routeCallback(BlockRoute) }
                }
            }
            i(2, 0) {
                material = Material.STONE_PICKAXE
                displayName = "${RESET}Tools"
                if (currentRoute == ToolRoute) {
                    enchant(Enchantment.SILK_TOUCH, 1)
                } else {
                    onClick { _, _ -> routeCallback(ToolRoute) }
                }
            }
        }
    }
}

class BuyNav(app: AppRoot, player: Player): RootComponent(app) {
    var route: BuyRoute by prop(HomeRoute)

    val nav = NavBar(app) { route = it }

    val buyHome = BuyHome(app, player)
    val buyBlock = BuyBlock(app, player)
    val buyTool = BuyTool(app, player)

    override fun render(): RootElement {
        return root(container {
            c(1, 1) {
                h(nav.render(route))
            }

            c(1, 3) {
                h(when (route) {
                    HomeRoute -> buyHome.render()
                    BlockRoute -> buyBlock.render()
                    ToolRoute -> buyTool.render()
                })
            }
        }) {
            title = "Buy"
            topSize = 6
            enableBottom = true
        }
    }
}

class BuyHome(app: AppRoot, private val player: Player) : Component(app) {
    fun render(): ViewElement {
        return container {
            i(0, 0) {
                material = Material.WOOL
                damage = 10
                amount = 16
                lore("${RESET}${BLUE}CLICK TO BUY")
                onClick { _, _ ->
                    player.inventory.addItem(itemBuilder {
                        material = Material.WOOL
                        damage = 10
                        amount = 16
                    }.render())
                }
            }
        }
    }
}

class BuyBlock(app: AppRoot, private val player: Player) : Component(app) {
    fun render(): ViewElement {
        return container {
            i(0, 0) {
                material = Material.WOOL
                damage = 11
                amount = 16
                lore("${RESET}${BLUE}CLICK TO BUY")
                onClick { _, _ ->
                    player.inventory.addItem(itemBuilder {
                        material = Material.WOOL
                        damage = 11
                        amount = 16
                    }.render())
                }
            }
        }
    }
}

class BuyTool(app: AppRoot, private val player: Player) : Component(app) {
    var hasItem by prop(
            player.inventory.contains(Material.STONE_PICKAXE)
    )

    fun render(): ViewElement {
        return container {
            i(0, 0) {
                material = Material.STONE_PICKAXE
                amount = 1
                if (hasItem) {
                    lore("${RESET}${GOLD}BOUGHT")
                } else {
                    lore("${RESET}${BLUE}CLICK TO BUY")
                    enchant(Enchantment.SILK_TOUCH, 1)
                    onClick { _, _ ->
                        hasItem = true
                        if (!player.inventory.contains(Material.STONE_PICKAXE)) {
                            player.inventory.addItem(itemBuilder {
                                material = Material.STONE_PICKAXE
                                enchant(Enchantment.SILK_TOUCH, 1)
                                amount = 1
                            }.render())
                        }
                    }
                }
            }
        }
    }
}
