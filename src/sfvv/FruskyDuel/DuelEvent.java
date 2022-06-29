package sfvv.FruskyDuel;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ADMIN on 6/22/2018.
 */
public class DuelEvent implements Listener {


    @EventHandler
    public void changegm(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() != GameMode.CREATIVE) {
            if (Main.range.containsKey(e.getPlayer().getName())) {
                e.setCancelled(true);
                GUI.openSpectatingGUI(e.getPlayer());
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player player = (Player) e.getEntity();
            if (Main.dueling.contains(player.getUniqueId())) {
                Game ga = null;
                if (Main.Games.get(player.getUniqueId()) != null && Main.Games.get(player.getUniqueId()).size() != 0) {

                    ArrayList<Game> gl = Main.Games.get(player.getUniqueId());
                    for (Game gz : gl) {
                        if (gz.getState() != Game.gameState.REQUESTED) {
                            if (gz.getP2() == player && gz.getState() != Game.gameState.REQUESTED) {
                                ga = gz;
                                break;
                            } else if (gz.getP2() == player && gz.getState() != Game.gameState.REQUESTED) {
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
                    if (ga.getState() == Game.gameState.WAITING) e.setCancelled(true);
                    if (ga.getState() == Game.gameState.END) e.setCancelled(true);

                    if (ga.getKit() == Kit.COMBO) {
                        for (UUID u : Main.debug) {
                            Player p = Bukkit.getPlayer(u);
                            if (p.isOnline()) {
                                p.sendMessage(e.getDamager().getName() + " Combo " + e.getEntity().getName());
                            }
                        }

                        Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> {
                            player.setNoDamageTicks(1);
                        }, 1);
                    } else if (ga.getKit() == Kit.SUMO) {
                        e.setDamage(0);
                        if (player.getHealth() != 20)
                            player.setHealth(20);
                    }
                }


            }
        }
    }

    @EventHandler
    public void regen(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (Main.dueling.contains(player.getUniqueId())) {
                Game ga = null;
                if (Main.Games.get(player.getUniqueId()) != null && Main.Games.get(player.getUniqueId()).size() != 0) {

                    ArrayList<Game> gl = Main.Games.get(player.getUniqueId());
                    for (Game gz : gl) {
                        if (gz.getState() != Game.gameState.REQUESTED) {
                            if (gz.getP2() == player && gz.getState() != Game.gameState.REQUESTED) {
                                ga = gz;
                                break;
                            } else if (gz.getP2() == player && gz.getState() != Game.gameState.REQUESTED) {
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
                    if (ga.getKit() == Kit.BUILDUHC) {
                        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                            e.setCancelled(true);
                        }
                    }
                }

            }

        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
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

                if (e.getItemDrop().getItemStack().getType().toString().contains("SWORD") || e.getItemDrop().getItemStack().getType().toString().contains("BOW") || e.getItemDrop().getItemStack().getType() == Material.BLAZE_ROD || e.getItemDrop().getItemStack().getType() == Material.FISHING_ROD) {
                    e.setCancelled(true);
                } else {
                    e.getItemDrop().remove();
                }
            }

        }
    }

    @EventHandler
    public void food(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
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
                if (ga.getKit() == Kit.BUILDUHC) {
                    e.setCancelled(true);
                    if (player.getFoodLevel() < 20) player.setFoodLevel(20);
                } else if (ga.getKit() == Kit.SUMO) {
                    e.setCancelled(true);
                    if (player.getFoodLevel() < 20) player.setFoodLevel(20);
                } else if (ga.getKit() == Kit.GAPPLE) {
                    e.setCancelled(true);
                    if (player.getFoodLevel() < 20) player.setFoodLevel(20);
                } else if (ga.getKit() == Kit.COMBO) {
                    e.setCancelled(true);
                    if (player.getFoodLevel() < 20) player.setFoodLevel(20);
                }
            } else {

                e.setCancelled(true);
                if (player.getFoodLevel() < 20) player.setFoodLevel(20);
            }

        } else {

            e.setCancelled(true);
            if (player.getFoodLevel() < 20) player.setFoodLevel(20);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void enderpearl(PlayerInteractEvent e) {
        Player player = (Player) e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getItemInHand().getType() != Material.ENDER_PEARL && !player.getItemInHand().getType().isBlock()) return;
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
                    if (ga.getState() == Game.gameState.WAITING) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }


}
