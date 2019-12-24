package bedwars;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

class TeamInfo {

  // constant variables
  String name;
  Color color;
  String chatcolor;
  Location spawn, generator, bed;
  ResourceSpawner iron, gold, emerald;

  // per-game variables
  boolean hasbed = false;
  int[] upgrades;
  int playersalive;

  public TeamInfo(ConfigurationSection config, JavaPlugin plugin) {
    name = config.getString("name");
    color = config.getColor("color");
    chatcolor = ChatColor.translateAlternateColorCodes('&', config.getString("chatcolor"));
    spawn = Main.getLocation(config, "spawn");
    generator = Main.getLocation(config, "generator");
    bed = Main.getLocation(config, "bed");

    iron = new ResourceSpawner(plugin, new ItemStack(Material.IRON_INGOT), generator);
    gold = new ResourceSpawner(plugin, new ItemStack(Material.GOLD_INGOT), generator);
    emerald = new ResourceSpawner(plugin, new ItemStack(Material.EMERALD), generator);
  }

  public void newGame(int numplayers) {
    hasbed = true;
    upgrades = new int[7];
    playersalive = numplayers;
    iron.setInterval(10);
    gold.setInterval(100);
    emerald.setInterval(1000);
    iron.start();
    gold.start();
  }

}