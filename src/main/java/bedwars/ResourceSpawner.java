package bedwars;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

class ResourceSpawner {

	JavaPlugin plugin;
	ItemStack item;
	Location loc;
	long interval = 40;
	BukkitRunnable br;

	public ResourceSpawner(JavaPlugin plugin, ItemStack item, Location loc) {
		this.plugin = plugin;
		this.item = item;
		this.loc = loc;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public long getInterval() {
		return this.interval;
	}

	public void stop() {
		if (br != null) {
			br.cancel();
			br = null;
		}
	}

	public void start() {
		br = new BukkitRunnable() {
			@Override
			public void run() {
				loc.getWorld().dropItem(loc, item);
			}
		};
		br.runTaskTimer(plugin, 0, interval);
	}

	public void restart() {
		stop();
		start();
	}

}
