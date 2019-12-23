package bedwars;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Color;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

class Team {

  String name;
  Color color;
  String chatcolor;
  Location spawn, generator, bed;
  int playersalive;

  ArrayList<Player> players = new ArrayList<Player>();
  boolean hasbed = true;
  int upgrades[] = {0,0,0,0,0,0,0};

  public Team(ConfigurationSection config) {
    this.name = config.getString("name");
    this.color = config.getColor("color");
    this.chatcolor = ChatColor.translateAlternateColorCodes('&', config.getString("chatcolor"));
    this.spawn = Main.getLocation(config, "spawn");
    this.generator = Main.getLocation(config, "generator");
    this.bed = Main.getLocation(config, "bed");
  }

  public void addPlayer(Player p) {
    players.add(p);
  }

  public boolean removePlayer(Player p) {
    for (int i=0;i<players.size();i++) {
      if (players.get(i).getUniqueId().equals(p.getUniqueId())) {
        players.remove(i);
        return true;
      }
    }
    return false;
  }

}