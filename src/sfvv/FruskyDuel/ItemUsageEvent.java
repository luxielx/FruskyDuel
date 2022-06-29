package sfvv.FruskyDuel;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by ADMIN on 6/23/2018.
 */
public class ItemUsageEvent implements Listener {
    @EventHandler
    public void click(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            ItemStack is = player.getItemInHand();
            if (is == null) return;
            if (is.getType() == Material.AIR) return;
            if (is.getType() == Material.INK_SACK) {
                if (is.getDurability() == 10) {
                    ItemStack dye = new ItemStack(Material.INK_SACK, 1, (short) 8);
                    ItemMeta meta = dye.getItemMeta();
                    meta.setDisplayName(ChatColor.DARK_GRAY + "»" +ChatColor.RED + " Requests: Off "+ChatColor.DARK_GRAY + "«");
                    dye.setItemMeta(meta);
                    player.setItemInHand(dye);
                    if (!Main.disablerequest.contains(player.getUniqueId())) {
                        Main.disablerequest.add(player.getUniqueId());
                    }
                }
                if (is.getDurability() == 8) {
                    ItemStack dye = new ItemStack(Material.INK_SACK, 1, (short) 10);
                    ItemMeta meta = dye.getItemMeta();
                    meta.setDisplayName(ChatColor.DARK_GRAY + "»" +ChatColor.GREEN + " Requests: On "+ChatColor.DARK_GRAY + "«");
                    dye.setItemMeta(meta);
                    player.setItemInHand(dye);
                    if (Main.disablerequest.contains(player.getUniqueId())) {
                        Main.disablerequest.remove(player.getUniqueId());
                    }
                }
            } else if (is.getType() == Material.MAGMA_CREAM) {
                GUI.openQueueGUI(player);
            }else if(is.getType() == Material.PAPER){
                GUI.openSelectTopGui(player);
            }


        }
    }

}
