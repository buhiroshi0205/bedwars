package bedwars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.*;
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
	ArrayList<ResourceSpawner> diamondgens = new ArrayList<ResourceSpawner>();
	ArrayList<ResourceSpawner> emeraldgens = new ArrayList<ResourceSpawner>();
	Location playloclow, playlochigh, structureloclow, structurelochigh, spectatespawn, lobby;
	Scoreboard sb;


	/* COMMAND LISTENER */

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("update")) updateDisplay();
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
				updateDisplay();
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
			TeamInfo info = new TeamInfo(teamconfig, this);
			teaminfos.put(teamid, info);

			Team newteam = sb.registerNewTeam(teamid);
			newteam.setPrefix(info.chatcolor + "[" + info.name + "] ");
			newteam.setCanSeeFriendlyInvisibles(true);
			newteam.setAllowFriendlyFire(false);
		}

		// initialize resourcespawners
		// diamonds
		ConfigurationSection diamonds = config.getConfigurationSection("diamondgens");
		ItemStack is = new ItemStack(Material.DIAMOND);
		for (String genid : diamonds.getKeys(false)) {
			ResourceSpawner gen = new ResourceSpawner(this, is, getLocation(diamonds, genid));
			gen.setInterval(600);
			diamondgens.add(gen);
		}
		// emeralds
		ConfigurationSection emeralds = config.getConfigurationSection("emeraldgens");
		is = new ItemStack(Material.EMERALD);
		for (String genid : emeralds.getKeys(false)) {
			ResourceSpawner gen = new ResourceSpawner(this, is, getLocation(emeralds, genid));
			gen.setInterval(1200);
			emeraldgens.add(gen);
		}

		// initialize player health/kills display
		Objective health = sb.registerNewObjective("health", "health");
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		health.setDisplayName(ChatColor.RED + "❤");
		Objective kills = sb.registerNewObjective("kills", "playerKillCount");
		kills.setDisplaySlot(DisplaySlot.PLAYER_LIST);

		// initialize scoreboard display
		Objective display = sb.registerNewObjective("display", "dummy");
		display.setDisplaySlot(DisplaySlot.SIDEBAR);
		display.setDisplayName("BED WARS");

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(sb);
		}
	}

	private void startGame(CommandSender sender) {
		if (gamephase != 1) return;

		clone(structureloclow, structurelochigh, playloclow);

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
				giveLeatherArmor(p, info.color);
			}
		}

		// teleport non-participating players as spectators
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (sb.getPlayerTeam(p) == null) {
				p.setGameMode(GameMode.SPECTATOR);
				p.teleport(spectatespawn);
			}
		}

		// start resource generators
		for (ResourceSpawner rs : diamondgens) {
			rs.start();
		}
		for (ResourceSpawner rs : emeraldgens) {
			rs.start();
		}

		Bukkit.broadcastMessage("Game started!");
		gamephase = 2;
	}

	private void endGame() {
		// clean up and refresh
		for (ResourceSpawner rs : diamondgens) {
			rs.stop();
		}
		for (ResourceSpawner rs : emeraldgens) {
			rs.stop();
		}
		for (Team t : sb.getTeams()) {
			for (OfflinePlayer p : t.getPlayers()) {
				t.removePlayer(p);
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			sb.resetScores((OfflinePlayer) p);
		}

		// teleport everyone to lobby
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setGameMode(GameMode.ADVENTURE);
			p.teleport(lobby);
		}

		Bukkit.broadcastMessage("Game ended!");
		gamephase = 1;
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
		updateDisplay();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		p.setScoreboard(sb);

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
	public void onInventoryClick(InventoryClickEvent e) {
		switch (e.getSlotType()) {
			case ARMOR:
			case CRAFTING:
			case FUEL:
			case RESULT:
				e.setCancelled(true);
				break;
			default:
				break;
		}
	}

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
			updateDisplay();

		// only allow breaking player-placed blocks
		} else {
			if (loc.getWorld().getBlockAt(loc.subtract(playloclow).add(structureloclow))
					.getType() != Material.AIR) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!isBetween(playloclow, playlochigh, e.getBlockPlaced().getLocation())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		// don't drop the bed
		if (e.getEntity().getItemStack().getType() == Material.BED) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getTo().getY() < 0) {
			e.getPlayer().setHealth(0.0);
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

		final TeamInfo info = getInfo(sb.getPlayerTeam(p));

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
			updateDisplay();

			// test for game end condition
			int numaliveteams = 0;
			Team aliveteam = null;
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

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (gamephase == 1) {
			e.setRespawnLocation(lobby);
			e.getPlayer().setGameMode(GameMode.ADVENTURE);
		} else if (gamephase == 2) {
			Player p = e.getPlayer();
			Team t = sb.getPlayerTeam(p);
			assert t != null : " A non-player respawned???";
			TeamInfo info = getInfo(t);

			// respawn as spectator first at least for 5 sec
			e.setRespawnLocation(spectatespawn);
			p.setGameMode(GameMode.SPECTATOR);
			
			giveLeatherArmor(p, info.color);
		}
	}


	/* HELPER FUNCTIONS */

	private void giveLeatherArmor(Player p, Color color) {
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta meta = (LeatherArmorMeta)helmet.getItemMeta();
		meta.setColor(color);
		helmet.setItemMeta(meta);
		chestplate.setItemMeta(meta);
		leggings.setItemMeta(meta);
		boots.setItemMeta(meta);
		p.getInventory().setHelmet(helmet);
		p.getInventory().setChestplate(chestplate);
		p.getInventory().setLeggings(leggings);
		p.getInventory().setBoots(boots);
	}

	private TeamInfo getInfo(Team t) {
		return teaminfos.get(t.getName());
	}

	private void updateDisplay() {
		Objective display = sb.getObjective("display");
		for (OfflinePlayer op : sb.getPlayers()) {
			if (!(op.isOnline())) {
				sb.resetScores(op);
			}
		}
		display.getScore(".").setScore(1);
		int i = 2;
		for (TeamInfo info : teaminfos.values()) {
			String base = info.chatcolor + info.name + ChatColor.RESET + ": ";
			if (info.hasbed) {
				display.getScore(base + "✓").setScore(i++);
			} else {
				display.getScore(base + String.valueOf(info.playersalive)).setScore(i++);
			}
		}
		display.getScore(". ").setScore(i);
	}

	private void initializeLocations() {
		playloclow = getLocation(config, "playregion.low");
		playlochigh = getLocation(config, "playregion.high");
		structureloclow = getLocation(config, "structureregion.low");
		structurelochigh = getLocation(config, "structureregion.high");
		spectatespawn = getLocation(config, "spectatespawn");
		lobby = getLocation(config, "lobby");
	}

	private void clone(Location loc1, Location loc2, Location loc3) {
		int xmin = loc1.getBlockX();
		int ymin = loc1.getBlockY();
		int zmin = loc1.getBlockZ();
		int xmax = loc2.getBlockX();
		int ymax = loc2.getBlockY();
		int zmax = loc2.getBlockZ();
		int xdest = loc3.getBlockX();
		int ydest = loc3.getBlockY();
		int zdest = loc3.getBlockZ();

		for (int x=xmin;x<=xmax;x++) {
			for (int y=ymin;y<=ymax;y++) {
				for (int z=zmin;z<=zmax;z++) {
					Block fromblock = Bukkit.getWorld("world").getBlockAt(x,y,z);
					Block toblock = Bukkit.getWorld("world").getBlockAt(x-xmin+xdest, y-ymin+ydest, z-zmin+zdest);

					toblock.setBiome(fromblock.getBiome());
					toblock.setType(fromblock.getType());

					BlockState fromstate = fromblock.getState();
					BlockState tostate = toblock.getState();

					tostate.setData(fromstate.getData());
					tostate.update();
				}
			}
		}
	}

	private boolean isBetween(Location loc1, Location loc2, Location test) {
		return loc1.getX() <= test.getX() && test.getX() <= loc2.getX() &&
					 loc1.getY() <= test.getY() && test.getY() <= loc2.getY() &&
					 loc1.getZ() <= test.getZ() && test.getZ() <= loc2.getZ();
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
