package bedwars;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Location;
import java.util.ArrayList;

public final class Main extends JavaPlugin {

	boolean ingame = false;
	FileConfiguration config;
	ArrayList<Team> teams = new ArrayList<Team>();

	Location loclow;
	Location lochigh;


	@Override
	public void onEnable() {
		saveDefaultConfig();

		config = getConfig();
		ConfigurationSection allteamconfigs = config.getConfigurationSection("teams");
		for (String teamname : allteamconfigs.getKeys(false)) {
			ConfigurationSection teamconfig = allteamconfigs.getConfigurationSection(teamname);
			
		}

	}

}