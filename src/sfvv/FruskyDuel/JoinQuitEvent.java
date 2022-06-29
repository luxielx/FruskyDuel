package sfvv.FruskyDuel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ADMIN on 6/22/2018.
 */
public class JoinQuitEvent implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        for (PotionEffect ef : e.getPlayer().getActivePotionEffects()) {
            e.getPlayer().removePotionEffect(ef.getType());
        }
        e.getPlayer().setFoodLevel(20);
        Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> ELOManager.loadData(u));

    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        UUID u = e.getPlayer().getUniqueId();

        if (Main.sumoqueue.contains(u)) {
            Main.sumoqueue.remove(u);
        }
        if (Main.queue.contains(u)) {
            Main.queue.remove(u);
        }


        Player player = e.getPlayer();
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
                Player p2;
                if (player.getUniqueId().toString().equals(ga.getP1().getUniqueId().toString())) {
                    p2 = ga.getP2();
                } else {
                    p2 = ga.getP1();
                }
                ga.setState(Game.gameState.END);
                ga.setWinner(p2);
                ga.setLoser(player);
                ga.setState(Game.gameState.END);
            }
            Main.dueling.remove(player.getUniqueId());

        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
            @Override
            public void run() {
                for (String s : Main.range.keySet()) {
                    Player player = Bukkit.getPlayer(s);
                    if (Main.range.get(s) == u) {
                        Main.kickOutOfSpec(player);
                        player.sendMessage(ChatColor.RED + "The player you are spectating left!");
                    }
                }
                ELOManager.saveData(u);
            }
        });


    }


}
