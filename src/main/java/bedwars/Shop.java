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
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.*;
import org.bukkit.Sound;
import org.bukkit.Location;

public class Shop implements Listener {

  DoubleChest[] pages = new DoubleChest[1];
  //DoubleChest team;
  DoubleChest[] costs = new DoubleChest[1];

  public Shop(Location loc) {
    // team = getDC(loc.clone().add(0,0,2));
    Location loc2 = loc.clone().subtract(0,2,0);
    for (int i=0;i<pages.length;i++) {
      pages[i] = getDC(loc);
      loc.add(1,0,0);
      costs[i] = getDC(loc2);
      loc2.add(1,0,0);
    }
  }

  private DoubleChest getDC(Location loc) {
    Chest chest = (Chest) Bukkit.getWorld("world").getBlockAt(loc).getState();
    return (DoubleChest) chest.getInventory().getHolder();
  }

  private void showItemShop(DoubleChest dc, Player p) {
    Inventory inv = Bukkit.getServer().createInventory(null, 54, "Item Shop");
    inv.setContents(dc.getInventory().getContents());
    p.openInventory(inv);
  }

  public void showUpgradesShop(DoubleChest dc, int[] state, Player p) {
    Inventory inv = Bukkit.getServer().createInventory(null, 54, "Item Shop");
  }

  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
    if (e.getRightClicked().getCustomName().equals("Item Shop")) {
      showItemShop(pages[0], e.getPlayer());
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();
    if (e.getClickedInventory().getName().equals("Item Shop")) {
      if (e.getSlot() < pages.length) {
        showItemShop(pages[e.getSlot()], (Player) p);
        return;
      }
      ItemStack cost = costs[0].getInventory().getItem(e.getSlot());
      if (cost != null && p.getInventory().containsAtLeast(cost, cost.getAmount())) {
        p.getInventory().removeItem(cost);
        p.getInventory().addItem(e.getCurrentItem());
        p.playSound(p.getLocation(), Sound.NOTE_PLING, 100, 2);
      } else {
        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 100, 1);
      }
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