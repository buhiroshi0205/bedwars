package bedwars;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

class ResourceSpawner extends BukkitRunnable {

	JavaPlugin plugin;
	ItemStack item;
	Location loc;
	long interval = 40;

	public ResourceSpawner(JavaPlugin plugin, ItemStack item, Location loc) {
		this.plugin = plugin;
		this.item = item;
		this.loc = loc;
	}

	@Override
	public void run() {
		loc.getWorld().dropItem(loc, item);
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public long getInterval() {
		return this.interval;
	}

	public void stop() {
		this.cancel();
	}

	public void start() {
		this.runTaskTimer(plugin, 0, interval);
	}

	public void restart() {
		this.cancel();
		this.runTaskTimer(plugin, 0, interval);
	}

}
