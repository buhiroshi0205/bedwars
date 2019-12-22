package bedwars;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.Location;

class Team {

  ArrayList<Player> players = new ArrayList<Player>();
  boolean hasbed = false;
  int upgrades[] = {0,0,0,0,0,0,0};
  Location spawn, generator, bed;

}