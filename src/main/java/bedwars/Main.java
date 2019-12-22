package bedwars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.Location;
import org.bukkit.Color;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.GameMode;

import java.util.ArrayList;

public final class Main extends JavaPlugin implements Listener {

	/* GLOBAL VARIABLES */

	int gamephase = 0; // 0 = none, 1 = lobby, 2 = ingame
	FileConfiguration config;
	ArrayList<Team> teams;
	ArrayList<ResourceSpawner> resourcegens;
	Location playloclow, playlochigh, structureloclow, structurelochigh, spectatespawn, lobby;


	/* COMMAND LISTENER */

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (gamephase == 0) {
			if (label.equalsIgnoreCase("initgame")) {
				initGame(sender);
			}
		} else if (gamephase == 1) {
			if (label.equalsIgnoreCase("jointeam")) {
				if (!(sender instanceof Player)) return false;
				Player p = (Player) sender;
				Team prevteam = getTeam(p);
				for (Team team : teams) {
					if (team.name.equalsIgnoreCase(args[0])) {
						if (prevteam != null) {
							prevteam.removePlayer(p);
						}
						team.addPlayer(p);
						p.sendMessage("Joined team " + team.name);
						break;
					}
				}
			} else if (label.equalsIgnoreCase("startgame")) {
				startGame();
			}
		}

		return true;
  }


  /* GAME MANAGER */

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		config = getConfig();
	}

	private void initGame(CommandSender sender) {
		teams = new ArrayList<Team>();
		resourcegens = new ArrayList<ResourceSpawner>();

		playloclow = getLocFromConfig(config, "playregion.low", false);
		playlochigh = getLocFromConfig(config, "playregion.high", false);
		structureloclow = getLocFromConfig(config, "structureregion.low", false);
		structurelochigh = getLocFromConfig(config, "structureregion.high", false);
		spectatespawn = getLocFromConfig(config, "spectatespawn", false);
		lobby = getLocFromConfig(config, "lobby", false);

		ConfigurationSection allteamconfigs = config.getConfigurationSection("teams");
		for (String teamid : allteamconfigs.getKeys(false)) {
			ConfigurationSection teamconfig = allteamconfigs.getConfigurationSection(teamid);
			teams.add(new Team(teamconfig));
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.teleport(lobby);
		}

		Bukkit.dispatchCommand(sender, String.format("clone %d %d %d %d %d %d %d %d %d",
																								 (int)structureloclow.getX(),
																								 (int)structureloclow.getY(),
																								 (int)structureloclow.getZ(),
																								 (int)structurelochigh.getX(),
																								 (int)structurelochigh.getY(),
																								 (int)structurelochigh.getZ(),
																								 (int)playloclow.getX(),
																								 (int)playloclow.getY(),
																								 (int)playloclow.getZ()));

		Bukkit.broadcastMessage("Game initialized!");
		gamephase = 1;
	}

	private void startGame() {
		for (Team team : teams) {
			team.playersalive = team.players.size();
			for (Player p : team.players) {
				p.teleport(team.spawn);
			}
			ItemStack is = new ItemStack(Material.IRON_INGOT);
			ResourceSpawner spawner = new ResourceSpawner(is, team.generator);
			resourcegens.add(spawner);
			spawner.runTaskTimer(this, 10, 10);
		}

		Bukkit.broadcastMessage("Game started!");
		gamephase = 2;
	}

	private void endGame() {
		for (ResourceSpawner rs : resourcegens) {
			rs.cancel();
		}
		for (Team team : teams) {
			for (Player p : team.players) {
				p.setGameMode(GameMode.SURVIVAL);
				p.teleport(lobby);
			}
		}

		Bukkit.broadcastMessage("Game ended!");
		gamephase = 0;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		for (Team team : teams) {
			for (int i=0;i<team.players.size();i++) {
				if (team.players.get(i).getUniqueId().equals(e.getPlayer().getUniqueId())) {
					team.players.remove(i);
					break;
				}
			}
		}
	}


	/* IN-GAME EVENT LISTENERS */

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (gamephase != 2) return;
		Material blocktype = e.getBlock().getType();
		Location loc = e.getBlock().getLocation();
		if (blocktype == Material.BED_BLOCK) {
			for (Team team : teams) {
				if (loc.distance(team.bed) < 3) {
					team.hasbed = false;
					for (Player p : team.players) {
						p.sendTitle(ChatColor.DARK_RED + "BED DESTROYED", "");
					}
				}
			}
		} else {
			if (loc.getWorld().getBlockAt(loc.subtract(playloclow).add(structureloclow)).getType() != Material.AIR) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (gamephase != 2) return;
		final Player p = e.getEntity();
		new BukkitRunnable() {
			@Override
			public void run() {
				p.spigot().respawn();
			}
		}.runTaskLater(this, 1);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (gamephase != 2) return;
		e.setRespawnLocation(spectatespawn);
		final Player p = e.getPlayer();
		p.setGameMode(GameMode.SPECTATOR);
		final Team team = getTeam(p);
		if (team.hasbed) {
			new BukkitRunnable() {
				@Override
				public void run() {
					p.teleport(team.spawn);
					p.setGameMode(GameMode.SURVIVAL);
				}
			}.runTaskLater(this, 100);
		} else {
			team.playersalive--;
			int numaliveteams = 0;
			Team aliveteam = teams.get(0);
			for (Team t : teams) {
				if (team.playersalive > 0) {
					aliveteam = t;
					numaliveteams++;
				}
			}
			if (numaliveteams == 0) {
				getServer().broadcastMessage("        " + aliveteam.chatcolor + aliveteam.name + ChatColor.YELLOW + " team wins!!!");
				new BukkitRunnable() {
					@Override
					public void run() {
						endGame();
					}
				}.runTaskLater(this, 200);
			}
		}
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		((LeatherArmorMeta)helmet.getItemMeta()).setColor(team.color);
		p.getInventory().setHelmet(helmet);
	}


	/* HELPER FUNCTIONS */

	static Location getLocFromConfig(ConfigurationSection configsec, String base, boolean pitchyaw) {
		if (pitchyaw) {
			return new Location(Bukkit.getWorld("world"),
													configsec.getInt(base + ".x"),
													configsec.getInt(base + ".y"),
													configsec.getInt(base + ".z"),
													configsec.getInt(base + ".yaw"),
													configsec.getInt(base + ".pitch"));
		} else {
			return new Location(Bukkit.getWorld("world"),
													configsec.getInt(base + ".x"),
													configsec.getInt(base + ".y"),
													configsec.getInt(base + ".z"));
		}

	}

	Team getTeam(Player player) {
		for (Team team : teams) {
			for (Player p : team.players) {
				if (player.getUniqueId().equals(p.getUniqueId())) {
					return team;
				}
			}
		}
		return null;
	}

}

class ResourceSpawner extends BukkitRunnable {

	ItemStack item;
	Location loc;

	public ResourceSpawner(ItemStack item, Location loc) {
		this.item = item;
		this.loc = loc;
	}

	@Override
	public void run() {
		loc.getWorld().dropItem(loc, item);
	}

}