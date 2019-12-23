package bedwars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.Location;
import org.bukkit.Color;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.GameMode;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public final class Main extends JavaPlugin implements Listener {

	/* GLOBAL VARIABLES */

	int gamephase = 1; // 1 = lobby, 2 = ingame
	FileConfiguration config;
	Hashtable<String, TeamInfo> teaminfos = new Hashtable<String, TeamInfo>();
	ArrayList<ResourceSpawner> resourcegens;
	Location playloclow, playlochigh, structureloclow, structurelochigh, spectatespawn, lobby;
	Scoreboard sb;


	/* COMMAND LISTENER */

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (gamephase == 1) {

			if (label.equalsIgnoreCase("setloc")) {
				if (!(sender instanceof Player)) return true;
				if (getLocation(config, args[0]) == null) return true;
				saveLocation(config, args[0], ((Player)sender).getLocation());
				saveConfig();
				config = getConfig();
				initializeLocations();
				sender.sendMessage("Location " + args[0] + " successfully updated!");

			} else if (label.equalsIgnoreCase("jointeam")) {
				if (!(sender instanceof Player)) return true;
				Player p = (Player) sender;
				if (sb.getTeam(args[0]) != null) {
					sb.getTeam(args[0]).addPlayer(p);
					p.sendMessage("Joined team " + args[0]);
				} else {
					p.sendMessage("Please enter a valid team color (in lower case)!");
				}

			} else if (label.equalsIgnoreCase("startgame")) {
				if (!(sender instanceof Player)) return true;
				startGame(sender);
			}
		}

		return true;
  }


  /* GAME MANAGER */

	@Override
	public void onEnable() {
		// initialize shit
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		config = getConfig();
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		initializeLocations();

		// initialize the teams and team infos
		ConfigurationSection allteamconfigs = config.getConfigurationSection("teams");
		for (String teamid : allteamconfigs.getKeys(false)) {
			ConfigurationSection teamconfig = allteamconfigs.getConfigurationSection(teamid);
			TeamInfo info = new TeamInfo(teamconfig);
			teaminfos.put(teamid, info);

			Team newteam = sb.registerNewTeam(teamid);
			newteam.setPrefix(info.chatcolor + "[" + info.name + "]");
			newteam.setCanSeeFriendlyInvisibles(true);
			newteam.setAllowFriendlyFire(false);
		}
	}

	/*
	private void initGame(CommandSender sender) {
		resourcegens = new ArrayList<ResourceSpawner>();
	}
	*/

	private void startGame(CommandSender sender) {
		if (gamephase != 1) return;
		Bukkit.dispatchCommand(sender, String.format("clone %d %d %d %d %d %d %d %d %d",
																								 structureloclow.getBlockX(),
																								 structureloclow.getBlockY(),
																								 structureloclow.getBlockZ(),
																								 structurelochigh.getBlockX(),
																								 structurelochigh.getBlockY(),
																								 structurelochigh.getBlockZ(),
																								 playloclow.getBlockX(),
																								 playloclow.getBlockY(),
																								 playloclow.getBlockZ()));

		for (Team t : sb.getTeams()) {
			// update team info for new game
			Set<OfflinePlayer> players = t.getPlayers();
			TeamInfo info = getInfo(t);
			info.newGame(players.size());

			// teleport players
			for (OfflinePlayer op : players) {
				if (!op.isOnline()) continue;
				Player p = (Player) op;
				p.teleport(info.spawn);
				p.setGameMode(GameMode.SURVIVAL);
			}

			/*
			ItemStack is = new ItemStack(Material.IRON_INGOT);
			ResourceSpawner spawner = new ResourceSpawner(is, team.generator);
			resourcegens.add(spawner);
			spawner.runTaskTimer(this, 10, 10);
			*/
		}

		// teleport non-participating players as spectators
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (sb.getPlayerTeam(p) == null) {
				p.setGameMode(GameMode.SPECTATOR);
				p.teleport(spectatespawn);
			}
		}

		Bukkit.broadcastMessage("Game started!");
		gamephase = 2;
	}

	private void endGame() {
		// clean up and refresh
		/*
		for (ResourceSpawner rs : resourcegens) {
			rs.cancel();
		}
		*/
		for (Team t : sb.getTeams()) {
			for (OfflinePlayer p : t.getPlayers()) {
				t.removePlayer(p);
			}
		}

		// teleport everyone to lobby
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setGameMode(GameMode.ADVENTURE);
			p.teleport(lobby);
		}

		Bukkit.broadcastMessage("Game ended!");
		gamephase = 0;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		// if in lobby phase, remove player from any team
		if (gamephase == 1) {
			if (sb.getPlayerTeam(p) != null) {
				sb.getPlayerTeam(p).removePlayer(p);
			}

		// if in game phase, mark him as dead
		} else if (gamephase == 2) {
			getInfo(sb.getPlayerTeam(p)).playersalive--;
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();

		// if in lobby phase, tp to lobby
		if (gamephase == 1) {
			p.setGameMode(GameMode.ADVENTURE);
			p.teleport(lobby);

		// in game
		} else if (gamephase == 2) {
			// make him spectate first
			p.teleport(spectatespawn);
			p.setGameMode(GameMode.SPECTATOR);
			Team t = sb.getPlayerTeam(p);
			final TeamInfo info = getInfo(t);

			// if he was in a team and team still has bed then bring him back
			if (t != null && info.hasbed) {
				info.playersalive++;
				new BukkitRunnable() {
					@Override
					public void run() {
						p.teleport(info.spawn);
						p.setGameMode(GameMode.SURVIVAL);
					}
				}.runTaskLater(this, 100);
			}
		}
	}


	/* IN-GAME EVENT LISTENERS */

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (gamephase != 2) return;
		Material blocktype = e.getBlock().getType();
		Location loc = e.getBlock().getLocation();

		// bed destroyed logic
		if (blocktype == Material.BED_BLOCK) {
			for (Team t : sb.getTeams()) {
				TeamInfo info = getInfo(t);
				if (loc.distance(info.bed) < 3) {
					info.hasbed = false;
					for (OfflinePlayer op : t.getPlayers()) {
						if (op.isOnline()) {
							((Player) op).sendTitle(ChatColor.DARK_RED + "BED DESTROYED",
																			"You will no longer respawn!");
						}
					}
				}
			}

		// only allow breaking player-placed blocks
		} else {
			if (loc.getWorld().getBlockAt(loc.subtract(playloclow).add(structureloclow))
					.getType() != Material.AIR) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		if (gamephase != 2) return;

		// don't drop the bed
		if (e.getEntity().getItemStack().getType() == Material.BED) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (gamephase != 2) return;
		final Player p = e.getEntity();

		// don't drop loot
		e.getDrops().clear();

		// bypass respawn screen
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
		final Player p = e.getPlayer();
		final Team t = sb.getPlayerTeam(p); //assert not nul
		assert t != null : " A non-player respawned???";
		final TeamInfo info = getInfo(t);

		// respawn as spectator first at least for 5 sec
		e.setRespawnLocation(spectatespawn);
		p.setGameMode(GameMode.SPECTATOR);

		// if team hasbed, put him back to warzone after 5 sec
		if (info.hasbed) {
			new BukkitRunnable() {
				@Override
				public void run() {
					p.teleport(info.spawn);
					p.setGameMode(GameMode.SURVIVAL);
				}
			}.runTaskLater(this, 100);

		// else no bed!
		} else {
			// final kill!
			info.playersalive--;

			// test for game end condition
			int numaliveteams = 0;
			Team aliveteam = t;
			for (Team temp : sb.getTeams()) {
				if (getInfo(temp).playersalive > 0) {
					aliveteam = temp;
					numaliveteams++;
				}
			}
			if (numaliveteams == 1) {
				// game over!
				getServer().broadcastMessage("    " + getInfo(aliveteam).chatcolor +
																		 getInfo(aliveteam).name + ChatColor.YELLOW +
																		 " team wins!!!");
				// clean up game 10 seconds later
				new BukkitRunnable() {
					@Override
					public void run() {
						endGame();
					}
				}.runTaskLater(this, 200);
			}
		}

		/*
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		((LeatherArmorMeta)helmet.getItemMeta()).setColor(team.color);
		p.getInventory().setHelmet(helmet);
		*/
	}


	/* HELPER FUNCTIONS */

	private TeamInfo getInfo(Team t) {
		return teaminfos.get(t.getName());
	}

	private void initializeLocations() {
		playloclow = getLocation(config, "playregion.low");
		playlochigh = getLocation(config, "playregion.high");
		structureloclow = getLocation(config, "structureregion.low");
		structurelochigh = getLocation(config, "structureregion.high");
		spectatespawn = getLocation(config, "spectatespawn");
		lobby = getLocation(config, "lobby");
	}

	static Location getLocation(ConfigurationSection config, String path) {
		if (!config.contains(path)) return null;
		World world = Bukkit.getWorld("world");
		float x = (float) config.getDouble(path + ".x");
		float y = (float) config.getDouble(path + ".y");
		float z = (float) config.getDouble(path + ".z");
		float pitch = (float) config.getDouble(path + ".pitch");
		float yaw = (float) config.getDouble(path + ".yaw");
		return new Location(world, x, y, z, yaw, pitch);
	}

	void saveLocation(ConfigurationSection config, String path, Location loc) {
		config.set(path + ".x", loc.getX());
		config.set(path + ".y", loc.getY());
		config.set(path + ".z", loc.getZ());
		config.set(path + ".pitch", loc.getPitch());
		config.set(path + ".yaw", loc.getYaw());
		saveConfig();
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