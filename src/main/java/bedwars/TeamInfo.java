package bedwars;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Color;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

class TeamInfo {

  // constant variables
  String name;
  Color color;
  String chatcolor;
  Location spawn, generator, bed;

  // per-game variables
  boolean hasbed = false;
  int[] upgrades;
  int playersalive;

  public TeamInfo(ConfigurationSection config) {
    this.name = config.getString("name");
    this.color = config.getColor("color");
    this.chatcolor = ChatColor.translateAlternateColorCodes('&', config.getString("chatcolor"));
    this.spawn = Main.getLocation(config, "spawn");
    this.generator = Main.getLocation(config, "generator");
    this.bed = Main.getLocation(config, "bed");
  }

  public void newGame(int numplayers) {
    hasbed = true;
    upgrades = new int[7];
    playersalive = numplayers;
  }

}