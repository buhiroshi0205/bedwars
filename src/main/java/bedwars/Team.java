package bedwars;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Color;

class Team {

  String name;
  Color color;
  Location spawn, generator, bed;
  int maxplayers;
  int playersalive;

  ArrayList<Player> players = new ArrayList<Player>();
  boolean hasbed = true;
  int upgrades[] = {0,0,0,0,0,0,0};

  public Team(String name, Color color, Location spawn, Location gen, Location bed, int maxplayers) {
    this.name = name;
    this.color = color;
    this.spawn = spawn;
    this.generator = gen;
    this.bed = bed;
    this.maxplayers = maxplayers;
  }

}