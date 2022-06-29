package sfvv.FruskyDuel;


import com.ngxdev.knockback.KnockbackProfile;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Created by ADMIN on 6/3/2018.
 */
public class Game {
    Player p1;
    Player p2;
    Kit kit;
    Arena arena;
    gameState state;
    Player winner;
    Player loser;
    boolean isranked;
    static int timerr;
    boolean iscancel;
    boolean winneratspawn;
    int potloser;

    public Game(Player p1, Player p2, Kit kit, Arena arena, boolean isranked) {
        this.p1 = p1;
        this.p2 = p2;
        this.kit = kit;
        this.arena = arena;
        this.state = gameState.REQUESTED;
        this.winner = p1;
        this.loser = p2;
        this.isranked = isranked;
        this.iscancel = false;
        this.winneratspawn = false;
        this.timerr = 0;
        potloser = 0;
        new BukkitRunnable() {
            int timerw = 0;
            int timer = 0;
            int timere = 0;
            Location loz1;
            Location loz2;

            @Override
            public void run() {
                if (state == gameState.REQUESTED) {
                    if (timerr >= 20 * 2) {
                        iscancel = true;
                        if (Main.Games.containsKey(p1.getUniqueId())) {
                            ArrayList<Game> rev = new ArrayList<>();
                            for (Game games : Main.Games.get(p1.getUniqueId())) {
                                if (games.getState() == gameState.REQUESTED) {
                                    rev.add(games);
                                }
                            }
                            Main.Games.get(p1.getUniqueId()).removeAll(rev);
                        }
                        if (Main.Games.containsKey(p2.getUniqueId())) {
                            ArrayList<Game> rev = new ArrayList<>();
                            for (Game games : Main.Games.get(p2.getUniqueId())) {
                                if (games.getState() == gameState.REQUESTED) {
                                    rev.add(games);
                                }
                            }
                            Main.Games.get(p2.getUniqueId()).removeAll(rev);
                        }
                        this.cancel();
                    } else {
                        timerr++;
                    }

                }
                if (state == gameState.WAITING) {
                    p1.setItemOnCursor(new ItemStack(Material.AIR));
                    p2.setItemOnCursor(new ItemStack(Material.AIR));
                    if (Main.Games.containsKey(p1.getUniqueId())) {
                        ArrayList<Game> rev = new ArrayList<>();
                        for (Game games : Main.Games.get(p1.getUniqueId())) {
                            if (games.getState() == gameState.REQUESTED) {
                                rev.add(games);
                            }
                        }
                        Main.Games.get(p1.getUniqueId()).removeAll(rev);
                    }
                    if (Main.Games.containsKey(p2.getUniqueId())) {
                        ArrayList<Game> rev = new ArrayList<>();
                        for (Game games : Main.Games.get(p2.getUniqueId())) {
                            if (games.getState() == gameState.REQUESTED) {
                                rev.add(games);
                            }
                        }
                        Main.Games.get(p2.getUniqueId()).removeAll(rev);
                    }

                    if (timerw == 0) {
                        p1.teleport(arena.getLoc1());
                        p2.teleport(arena.getLoc2());
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (loz1 == null) {
                                    loz1 = p1.getLocation();
                                }
                                if (loz2 == null) {
                                    loz2 = p2.getLocation();
                                }
                                loz1.setY(arena.getLoc1().getY());
                                loz2.setY(arena.getLoc2().getY());
                                if (loz1.distance(arena.getLoc1()) >= 0.1) {
                                    p1.teleport(arena.getLoc1());
                                }
                                if (loz2.distance(arena.getLoc2()) >= 0.1) {
                                    p2.teleport(arena.getLoc2());
                                }
                                loz1 = p1.getLocation();
                                loz2 = p2.getLocation();
                                if (getState() != gameState.WAITING) this.cancel();
                            }
                        }.runTaskTimer(Main.getMain(), 1, 1);
                        Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                            for (PotionEffect e : p1.getActivePotionEffects()) {
                                p1.removePotionEffect(e.getType());
                            }
                            for (PotionEffect e : p2.getActivePotionEffects()) {
                                p2.removePotionEffect(e.getType());
                            }
                        });


                    }


                    arena.setUsing(true);
                    if (timerw <= 6 * 2) {
                        Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                            p1.setGameMode(GameMode.SURVIVAL);
                            p2.setGameMode(GameMode.SURVIVAL);
                        });
                        switch (timerw) {
                            case (0):

                                Main.sendTitle(p1, "", ChatColor.RED + "3");
                                Main.sendTitle(p2, "", ChatColor.RED + "3");
                                Main.sendMessage(p1, ChatColor.GRAY + "Match starting over " + ChatColor.RED + "3" + ChatColor.GRAY + "...");
                                Main.sendMessage(p2, ChatColor.GRAY + "Match starting over " + ChatColor.RED + "3" + ChatColor.GRAY + "...");

                                Bukkit.getScheduler().runTask(Main.getMain(), new Runnable() {
                                    @Override
                                    public void run() {
                                        p1.setHealth(20);
                                        p2.setHealth(20);
                                        p1.setFoodLevel(20);
                                        p2.setFoodLevel(20);
                                    }
                                });

                                break;
                            case (2):
                                Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                                    p1.hidePlayer(p2);
                                    p1.showPlayer(p2);
                                    p2.hidePlayer(p1);
                                    p2.showPlayer(p1);
                                });

                                Main.sendTitle(p1, "", ChatColor.YELLOW + "2");
                                Main.sendTitle(p2, "", ChatColor.YELLOW + "2");
                                Main.sendMessage(p1, ChatColor.GRAY + "Match starting over " + ChatColor.YELLOW + "2" + ChatColor.GRAY + "...");
                                Main.sendMessage(p2, ChatColor.GRAY + "Match starting over " + ChatColor.YELLOW + "2" + ChatColor.GRAY + "...");
                                break;
                            case (4):
                                Main.sendTitle(p1, "", ChatColor.GREEN + "1");
                                Main.sendTitle(p2, "", ChatColor.GREEN + "1");
                                Main.sendMessage(p1, ChatColor.GRAY + "Match starting over " + ChatColor.GREEN + "1" + ChatColor.GRAY + "...");
                                Main.sendMessage(p2, ChatColor.GRAY + "Match starting over " + ChatColor.GREEN + "1" + ChatColor.GRAY + "...");

                                break;
                            case (6):
                                Main.sendTitle(p1, "", ChatColor.GREEN + "GL!");
                                Main.sendTitle(p2, "", ChatColor.GREEN + "GL!");
                                Main.sendMessage(p1, ChatColor.GRAY + "Good luck!");
                                Main.sendMessage(p2, ChatColor.GRAY + "Good luck!");
                                state = gameState.FIGHTING;
                                if (kit == Kit.COMBO) {

                                    Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                                        p1.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                                        p2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                                        ((CraftPlayer)p1).getHandle().setKnockback(new KnockbackProfile("combo"));
                                        ((CraftPlayer)p2).getHandle().setKnockback(new KnockbackProfile("combo"));
                                    });

                                }
                                if (kit == Kit.SUMO) {
                                    Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                                        ((CraftPlayer)p1).getHandle().setKnockback(new KnockbackProfile("sumo"));
                                        ((CraftPlayer)p2).getHandle().setKnockback(new KnockbackProfile("sumo"));
                                    });

                                }
                                if (kit == Kit.GAPPLE) {
                                    Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                                        p1.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                                        p2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                                        p1.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                                        p2.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));

                                    });
                                }
                                break;
                        }

                    } else {
                        state = gameState.FIGHTING;
                    }
                    timerw++;
                }
                if (state == gameState.END) {
                    if (winneratspawn) timere = 8;
                    if (timere == 0) {
                        if (isranked) {
                            int winnerelo = ELOManager.getElo(winner.getUniqueId(), arena.getType());
                            int loserelo = ELOManager.getElo(loser.getUniqueId(), arena.getType());
                            int add = ELOManager.calculateEloAdd(winner.getUniqueId(), loser.getUniqueId(), arena.getType());
                            int sub = ELOManager.calculateEloSubtract(winner.getUniqueId(), loser.getUniqueId(), arena.getType());
                            ELOManager.updateElo(winner.getUniqueId(), winnerelo + add, arena.getType());
                            ELOManager.updateElo(loser.getUniqueId(), loserelo - sub, arena.getType());
                            Main.sendMessage(winner, ChatColor.GREEN + winner.getName() + " (" + winnerelo + ")" + " (+" + add + ")" + ChatColor.GRAY + " has beaten ");
                            Main.sendMessage(winner, ChatColor.RED + loser.getName() + " (" + loserelo + ")" + " (-" + sub + ")" + ChatColor.GRAY + " with kit " + ChatColor.WHITE + kit.getName());
                            Main.sendMessage(loser, ChatColor.GREEN + winner.getName() + " (" + winnerelo + ")" + " (+" + add + ")" + ChatColor.GRAY + " has beaten ");
                            Main.sendMessage(loser, ChatColor.RED + loser.getName() + " (" + loserelo + ")" + " (-" + sub + ")" + ChatColor.GRAY + " with kit " + ChatColor.WHITE + kit.getName());


                        } else {
                            if (kit != Kit.NODEBUFF) {
                                Main.sendMessage(p1, ChatColor.GREEN + winner.getName() + ChatColor.GRAY + " has won from " + ChatColor.RED + loser.getName() + ChatColor.GRAY + " with kit " + ChatColor.RESET + kit.getName());
                                Main.sendMessage(p2, ChatColor.GREEN + winner.getName() + ChatColor.GRAY + " has won from " + ChatColor.RED + loser.getName() + ChatColor.GRAY + " with kit " + ChatColor.RESET + kit.getName());
                            } else {
                                ItemStack pot = new ItemStack(Material.POTION);
                                Potion p = new Potion(1);
                                p.setSplash(true);
                                p.setType(PotionType.INSTANT_HEAL);
                                p.setLevel(2);
                                p.apply(pot);
                                int potion1 = winner.getInventory().all(pot).size();
                                Main.sendMessage(p2, ChatColor.GREEN + winner.getName() + "[" + potion1 + "]" + ChatColor.GRAY + " has won from " + ChatColor.RED + loser.getName() + "[" + potloser + "]" + ChatColor.GRAY + " with kit " + ChatColor.RESET + kit.getName());

                                Main.sendMessage(p1, ChatColor.GREEN + winner.getName() + "[" + potion1 + "]" + ChatColor.GRAY + " has won from " + ChatColor.RED + loser.getName() + "[" + potloser + "]" + ChatColor.GRAY + " with kit " + ChatColor.RESET + kit.getName());
                            }
                        }
                        Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                            winner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 7 * 20, 100));
                        });


                    }
                    if (timere >= 8) {
                        Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                            if (!winneratspawn)
                                winner.teleport(Main.spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            winner.setItemOnCursor(new ItemStack(Material.AIR));
                            winner.getOpenInventory().getTopInventory().clear();
                            for (PotionEffect e : p1.getActivePotionEffects()) {
                                p1.removePotionEffect(e.getType());
                            }
                            for (PotionEffect e : p2.getActivePotionEffects()) {
                                p2.removePotionEffect(e.getType());
                            }
                            ((CraftPlayer)p1).getHandle().setKnockback(new KnockbackProfile("default"));
                            ((CraftPlayer)p2).getHandle().setKnockback(new KnockbackProfile("default"));
                            p1.setFireTicks(0);
                            p2.setFireTicks(0);

                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "duelfixcommand " + winner.getName());
                            winner.setHealth(20);
                            winner.setFoodLevel(20);



                        });
                        Main.dueling.remove(p1.getUniqueId());
                        Main.dueling.remove(p2.getUniqueId());

                        if (Main.Games.containsKey(p1.getUniqueId())) {
                            Main.Games.remove(p1.getUniqueId());
                        }
                        if (Main.Games.containsKey(p2.getUniqueId())) {
                            Main.Games.remove(p2.getUniqueId());
                        }
                        arena.setUsing(false);
                        this.cancel();
                    }
                    timere++;
                }

                timer++;
                if (timer >= 60 * 25 * 2 && state != gameState.END) {// 25 min
                    setLoser(p1);
                    setWinner(p2);
                    state = gameState.END;
                }
            }
        }.runTaskTimerAsynchronously(Main.getMain(), 0, 10);


    }

    public Player getWinner() {
        return this.winner;
    }

    public Player setWinner(Player player) {
        return this.winner = player;
    }

    public Player getLoser() {
        return this.loser;
    }

    public Player setLoser(Player player) {
        return this.loser = player;
    }

    public Player getP1() {
        return this.p1;
    }

    public Player getP2() {
        return this.p2;
    }

    public Kit getKit() {
        return this.kit;
    }

    public gameState getState() {
        return this.state;
    }

    public Arena getArena() {
        return this.arena;
    }

    public void setState(gameState state) {
        this.state = state;
    }

    public void setWinnerAtSapwn(boolean b) {
        this.winneratspawn = b;
    }

    public void setPotCountLoser(int b) {
        this.potloser = b;
    }

    public enum gameState {
        REQUESTED, WAITING, FIGHTING, END;
    }
}
