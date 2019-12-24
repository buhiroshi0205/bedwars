package bedwars;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.block.*;
import org.bukkit.Location;

public class Shop implements Listener {

  DoubleChest pg1, team;

  public Shop(Location firstchest) {
    Chest chest = (Chest) Bukkit.getWorld("world").getBlockAt(firstchest).getState();
    pg1 = (DoubleChest) chest.getInventory().getHolder();
  }


  private void showItemShop(DoubleChest dc, Player p) {
    Inventory inv = Bukkit.getServer().createInventory(null, 54, "Item Shop");
    inv.setContents(dc.getInventory().getContents());
    p.openInventory(inv);
  }

  public void showUpgradesShop(DoubleChest dc, int[] state, Player p) {

  }

  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
    if (e.getRightClicked().getCustomName().equals("Item Shop")) {
      showItemShop(pg1, e.getPlayer());
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    if (e.getClickedInventory().getName().equals("Item Shop")) {
      e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent e) {
    if (e.getEntityType() == EntityType.VILLAGER) {
      e.setCancelled(true);
    }
  }

}