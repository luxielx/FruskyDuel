package sfvv.FruskyDuel;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

/**
 * Created by ADMIN on 6/18/2018.
 */
public class CommandEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void gamemodechange(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() == GameMode.ADVENTURE) {

            Player player = e.getPlayer();
            if(player.getInventory().getItem(0) == null) return;
            if(player.getInventory().getItem(0).getType() != Material.BLAZE_ROD) return;
            Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> {
                ItemStack magma = new ItemStack(Material.MAGMA_CREAM);
                ItemMeta mm = magma.getItemMeta();
                mm.setDisplayName(ChatColor.DARK_GRAY + "»" + ChatColor.RED + " Ranked Queue " + ChatColor.DARK_GRAY + "«");
                magma.setItemMeta(mm);

                ItemStack map = new ItemStack(Material.PAPER);
                ItemMeta mapm = map.getItemMeta();
                mapm.setDisplayName(ChatColor.DARK_GRAY + "»" + ChatColor.RED + " ELO Leaderboards " + ChatColor.DARK_GRAY + "«");
                map.setItemMeta(mapm);
                ItemStack graydye = new ItemStack(Material.INK_SACK, 1, (short) 10);
                ItemMeta gm = graydye.getItemMeta();
                if (Main.disablerequest.contains(player.getUniqueId())) {
                    graydye = new ItemStack(Material.INK_SACK, 1, (short) 8);
                    gm = graydye.getItemMeta();
                    gm.setDisplayName(ChatColor.DARK_GRAY + "»" + ChatColor.RED + " Requests: Off " + ChatColor.DARK_GRAY + "«");
                } else {
                    gm.setDisplayName(ChatColor.DARK_GRAY + "»" + ChatColor.GREEN + " Requests: On " + ChatColor.DARK_GRAY + "«");

                }


                graydye.setItemMeta(gm);
                player.setFoodLevel(20);
                player.getInventory().setItem(1, magma);
                player.getInventory().setItem(7, map);
                player.getInventory().setItem(8, graydye);

            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerJoinEvent e) {
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void respawn(PlayerRespawnEvent e) {
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void commandispatch(ServerCommandEvent e) {
        String command = e.getCommand();
        if (command.contains("duelfixcommand")) {
            Player player = Bukkit.getPlayer(command.replace("duelfixcommand ", ""));
            for(PotionEffect ef : player.getActivePotionEffects()){
                player.removePotionEffect(ef.getType());
            }
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void command(PlayerCommandPreprocessEvent e){
        if(e.isCancelled()) return;
        Player player = e.getPlayer();
        if(e.getMessage().toLowerCase().startsWith("/spawn") || e.getMessage().toLowerCase().startsWith("/toernooi")|| e.getMessage().toLowerCase().startsWith("/warp") || e.getMessage().toLowerCase().contains("/duel")|| e.getMessage().toLowerCase().startsWith("/spec") ) {
            if(Main.range.containsKey(player.getName())){
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't use this command in spectate mode!");
                return;
            }
            if (Main.dueling.contains(player.getUniqueId())) {
                Game ga = null;
                if (Main.Games.get(player.getUniqueId()) != null && Main.Games.get(player.getUniqueId()).size() != 0) {
                    ArrayList<Game> gl = Main.Games.get(player.getUniqueId());
                    for (Game gz : gl) {
                        if (gz.getState() != Game.gameState.REQUESTED) {
                            if (gz.getP2() == player) {
                                ga = gz;
                                break;
                            } else if (gz.getP2() == player) {
                                ga = gz;
                                break;
                            }
                        }
                    }
                } else {
                    boolean break2 = false;
                    for (ArrayList<Game> g : Main.Games.values()) {
                        for (Game gz : g) {
                            if (gz.getState() != Game.gameState.REQUESTED) {
                                if (gz.getP1() == player) {
                                    ga = gz;
                                    break2 = true;
                                    break;
                                } else if (gz.getP1() == player) {
                                    ga = gz;
                                    break2 = true;
                                    break;
                                }
                            }
                        }
                        if (break2) {
                            break;
                        }
                    }
                }
                if (ga != null) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "This command is not allowed in a match.");
                    return;
                }

            }
            if(e.getMessage().startsWith("/spec") && Main.queue.contains(player)){
                Main.queue.remove(player);
            }
            if(e.getMessage().startsWith("/spec") && Main.sumoqueue.contains(player)){
                Main.sumoqueue.remove(player);
            }
        }
    }

}
