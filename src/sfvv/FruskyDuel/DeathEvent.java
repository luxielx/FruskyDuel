package sfvv.FruskyDuel;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ADMIN on 6/18/2018.
 */
public class DeathEvent implements Listener {

    @EventHandler
    public void deathevent(PlayerDeathEvent e) {
        Player player = e.getEntity();
        UUID u = player.getUniqueId();
        boolean ingame = false;
        Game game = null;
        if (Main.Games.containsKey(player)) {
            ingame = true;
            ArrayList<Game> gl = Main.Games.get(player.getUniqueId());
            for (Game gz : gl) {
                if(gz.getState() == Game.gameState.REQUESTED) continue;
                if (gz.getP1() == player) {
                    game = gz;
                    break;
                } else if (gz.getP2() == player) {
                    game = gz;
                    break;
                }
            }
        }
        if (!ingame) {
            for (ArrayList<Game> gl : Main.Games.values()) {
                for (Game g : gl) {
                    if(g.getState() == Game.gameState.REQUESTED) continue;
                    if (g.getP1() == player) {
                        ingame = true;
                        game = g;
                    } else if (g.getP2() == player) {
                        ingame = true;
                        game = g;
                    }
                }
            }
        }
        if (ingame) {
            Player p2;
            if (player.getUniqueId().toString().equals(game.getP1().getUniqueId().toString())) {
                p2 = game.getP2();
            } else {
                p2 = game.getP1();
            }
            game.setState(Game.gameState.END);
            game.setWinner(p2);
            game.setLoser(player);
            e.getDrops().clear();
            ItemStack pot = new ItemStack(Material.POTION);
            Potion p = new Potion(1);
            p.setSplash(true);
            p.setType(PotionType.INSTANT_HEAL);
            p.setLevel(2);
            p.apply(pot);
            game.setPotCountLoser(player.getInventory().all(pot).size());


        }
        if (Main.sumoqueue.contains(u)) {
            Main.sumoqueue.remove(u);
        }
        if (Main.queue.contains(u)) {
            Main.queue.remove(u);
        }
    }

    @EventHandler
    public void Damageevent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
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
                    if (ga.getState() == Game.gameState.END) {
                        event.setCancelled(true);
                        player.setHealth(20);
                        ga.setWinnerAtSapwn(true);
                        player.teleport(Main.spawn , PlayerTeleportEvent.TeleportCause.PLUGIN);

                    } else {
                        Player p = (Player) event.getEntity();
                        p.setHealth(0.1D);
                    }
                } else {
                    Player p = (Player) event.getEntity();
                    p.setHealth(0.1D);
                    return;
                }

            }
            return;
        }
    }


}
