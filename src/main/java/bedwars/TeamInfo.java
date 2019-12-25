package bedwars;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Color;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.IllegalStateException;

class TeamInfo {
public class TeamInfo {

  // constant variables
  String name;
  Short woolColor;
  Color color;
  String chatcolor;
  Location spawn, generator, bed, shop;
  ResourceSpawner iron, gold, emerald;

  // per-game variables
  boolean hasbed = false;
  int[] upgrades;
  int playersalive;

  public TeamInfo(ConfigurationSection config, JavaPlugin plugin) {
    name = config.getString("name");
    woolColor = (short) config.getInt("woolColor");
    color = config.getColor("color");
    chatcolor = ChatColor.translateAlternateColorCodes('&', config.getString("chatcolor"));
    spawn = Main.getLocation(config, "spawn");
    generator = Main.getLocation(config, "generator");
    bed = Main.getLocation(config, "bed");
    shop = Main.getLocation(config, "shop");

    iron = new ResourceSpawner(plugin, new ItemStack(Material.IRON_INGOT), generator);
    gold = new ResourceSpawner(plugin, new ItemStack(Material.GOLD_INGOT), generator);
    emerald = new ResourceSpawner(plugin, new ItemStack(Material.EMERALD), generator);
  }

  public void newGame(int numplayers) {
    hasbed = true;
    upgrades = new int[7];
    playersalive = numplayers;
    iron.setInterval(20);
    gold.setInterval(200);
    emerald.setInterval(1000);
    iron.start();
    gold.start();

    Entity shopvillager = Bukkit.getWorld("world").spawnEntity(shop, EntityType.VILLAGER);
    shopvillager.setCustomName("Item Shop");
    shopvillager.setCustomNameVisible(true);
  }

  public void stopGenerators() {
    iron.stop();
    gold.stop();
    emerald.stop();
  }

  public String getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  public String getChatcolor() {
    return chatcolor;
  }

  public Location getSpawn() {
    return spawn;
  }

  public Location getGenerator() {
    return generator;
  }

  public Location getBed() {
    return bed;
  }

  public ResourceSpawner getIron() {
    return iron;
  }

  public ResourceSpawner getGold() {
    return gold;
  }

  public ResourceSpawner getEmerald() {
    return emerald;
  }

  public boolean isHasbed() {
    return hasbed;
  }

  public int[] getUpgrades() {
    return upgrades;
  }

  public int getPlayersalive() {
    return playersalive;
  }
}