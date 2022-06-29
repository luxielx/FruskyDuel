package sfvv.FruskyDuel;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ADMIN on 6/2/2018.
 */
public class GUI implements Listener {
    public static void openKitGUI(Player player, Player player2) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.RED + "Choose the Kit! ");
        int index = 1;
        for (Kit kit : Kit.values()) {
            inv.setItem(index, kit.getIcon(kit));
            index++;
            if (index == 4) index++;
        }
        ItemStack gl = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta gm = gl.getItemMeta();
        gm.setDisplayName(Main.hideS(player2.getName()));
        gl.setItemMeta(gm);
        for (int i = 0; i <= 8; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, gl);
            }
        }
        inv.setItem(8, gl);
        player.openInventory(inv);
    }


    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getClickedInventory().getTitle().contains(ChatColor.RED + "Choose the Kit! ")) {
            String name = Main.unhideS(e.getClickedInventory().getItem(8).getItemMeta().getDisplayName());
            Player player = (Player) e.getWhoClicked();
            Player player2 = Bukkit.getPlayer(name);
            e.setCancelled(true);
            ItemStack click = e.getCurrentItem();
            for (Kit kit : Kit.values()) {
                if (kit.getName().equalsIgnoreCase(click.getItemMeta().getDisplayName())) {
                    if (player.hasPermission("practice.donor.map")) {
                        if (kit == Kit.SUMO) {
                            openArenaGUI(player, player2, true, kit);
                        } else {
                            openArenaGUI(player, player2, false, kit);
                        }
                    } else {
                        ArrayList<Arena> al = new ArrayList<>();
                        for (Arena a : Main.Arenas) {
                            if (!a.isUsing() && a.getType() == Main.ArenaType.NORMAL && kit != Kit.SUMO) {
                                al.add(a);
                            }
                            if (!a.isUsing() && a.getType() == Main.ArenaType.SUMO && kit == Kit.SUMO) {
                                al.add(a);
                            }
                        }
                        if (al.size() >= 0) {
                            Arena a = al.get(ThreadLocalRandom.current().nextInt(0, al.size()));
                            Main.sendMessageDuel(player, ChatColor.RED + "Sent your duel request to " + player2.getName(), player2);
                            String msg = ChatColor.RED + player.getName() + ChatColor.RESET + " duel request " + ChatColor.RED + kit.getName() + ChatColor.RESET + " in " + ChatColor.RED + a.getName();
                            String clickable = ChatColor.GREEN + " ✔";
                            TextComponent f = new TextComponent(msg);
                            TextComponent z = new TextComponent(clickable);
                            HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to accept duel request").create());
                            z.setHoverEvent(hover);
                            z.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + player.getName()));
                            f.addExtra(z);
                            player2.spigot().sendMessage(f);
                            Game g = new Game(player, player2, kit, a, false);

                            if (Main.Games.containsKey(player2.getUniqueId())) {
                                ArrayList<Game> gl = new ArrayList<>(Main.Games.get(player2.getUniqueId()));
                                gl.add(g);
                                Main.Games.put(player2.getUniqueId(), gl);
                            } else {
                                ArrayList<Game> gl = new ArrayList<>();
                                gl.add(g);
                                Main.Games.put(player2.getUniqueId(), gl);
                            }
                        }
                        player.closeInventory();
                    }
                }
            }
        } else if (e.getClickedInventory().getTitle().contains(ChatColor.RED + "Choose the Arena! ")) {
            String name = Main.unhideS(e.getClickedInventory().getItem(53).getItemMeta().getDisplayName());
            Player player = (Player) e.getWhoClicked();
            Player player2 = Bukkit.getPlayer(name);
            e.setCancelled(true);
            if (e.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)) return;
            ItemStack click = e.getCurrentItem();
            Kit kit = Kit.valueOf(Main.unhideS(click.getItemMeta().getLore().get(1)));
            String arenaname = ChatColor.stripColor(click.getItemMeta().getDisplayName());
            if (click.getItemMeta().getLore().get(0).contains("No")) {
                e.setCancelled(true);
                return;
            }
            for (Arena a : Main.Arenas) {
                if (a.getName().equalsIgnoreCase(arenaname)) {
                    Main.sendMessageDuel(player, ChatColor.RED + "Sent your duel request to " + player2.getName(), player2);
                    String msg = ChatColor.RED + player.getName() + ChatColor.RESET + " duel request " + ChatColor.RED + kit.getName() + ChatColor.RESET + " in " + ChatColor.RED + a.getName();
                    String clickable = ChatColor.GREEN + " ✔";
                    TextComponent f = new TextComponent(msg);
                    TextComponent z = new TextComponent(clickable);
                    HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to accept duel request").create());
                    z.setHoverEvent(hover);
                    z.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + player.getName()));
                    f.addExtra(z);
                    player2.spigot().sendMessage(f);
                    Game g = new Game(player, player2, kit, a, false);

                    if (Main.Games.containsKey(player2.getUniqueId())) {
                        ArrayList<Game> gl = new ArrayList<>(Main.Games.get(player2.getUniqueId()));
                        gl.add(g);
                        Main.Games.put(player2.getUniqueId(), gl);
                    } else {
                        ArrayList<Game> gl = new ArrayList<>();
                        gl.add(g);
                        Main.Games.put(player2.getUniqueId(), gl);
                    }
                }
            }

            player.closeInventory();


        } else if (e.getClickedInventory().getTitle().contains(ChatColor.RED + "Ranked Queue")) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            ItemStack click = e.getCurrentItem();
            if (click.getType() == Material.LAVA_BUCKET) {
                openWaitingInQueueGui(player, Main.ArenaType.NORMAL);
            } else if (click.getType() == Material.LEASH) {
                openWaitingInQueueGui(player, Main.ArenaType.SUMO);
            }

            player.closeInventory();
        } else if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
            Player player = (Player) e.getWhoClicked();
            ItemStack is = e.getCurrentItem();
            if (is.getType() == Material.INK_SACK) {
                if (is.getDurability() == 1) {
                    Main.leaveQueue(player.getUniqueId());
                    player.getInventory().clear();
                    player.sendMessage(ChatColor.RED + "Left ranking queue");
                    player.setGameMode(GameMode.SURVIVAL);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "duelfixcommand " + player.getName());
                    player.closeInventory();
                }

            }
            if (is.getType() == Material.REDSTONE) {
                Main.range.remove(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                player.setFlying(false);
                player.setAllowFlight(false);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "duelfixcommand " + player.getName());
                player.teleport(Main.spawn);
                player.closeInventory();

            }

        } else if (e.getClickedInventory().getTitle().contains(ChatColor.RED + "ELO Leaderboards")) {
            Player player = (Player) e.getWhoClicked();
            if (e.getCurrentItem().getType() == Material.LAVA_BUCKET) {
                openTOPGui(player, Main.ArenaType.NORMAL);
            } else {
                openTOPGui(player, Main.ArenaType.SUMO);
            }

        } else if (e.getClickedInventory().getTitle().contains(ChatColor.RED + "Top ELO ")) {
            e.setCancelled(true);

        }

    }

    @EventHandler
    public void inte(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            ItemStack is = player.getItemInHand();
            if (is.getType() == Material.INK_SACK) {
                if (is.getDurability() == 1) {
                    Main.leaveQueue(player.getUniqueId());
                    player.getInventory().clear();
                    player.sendMessage(ChatColor.RED + "Left ranking queue");
                    player.setGameMode(GameMode.SURVIVAL);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "duelfixcommand " + player.getName());
                }

            }
            if (is.getType() == Material.REDSTONE) {
                Main.range.remove(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                player.setFlying(false);
                player.setAllowFlight(false);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "duelfixcommand " + player.getName());
                player.teleport(Main.spawn);


            }
        }
    }


    private void openWaitingInQueueGui(Player player, Main.ArenaType t) {
        String type = "BuildUHC";
        if (t == Main.ArenaType.SUMO) {
            type = "Sumo";
        }
        Main.sendMessage(player, ChatColor.RED + "You're now queuing for " + type + " ranked");
        Main.addtoQueue(player.getUniqueId(), t);
        player.getInventory().clear();
        ItemStack rose = new ItemStack(Material.INK_SACK, 1, (short) 1);
        ItemMeta bm = rose.getItemMeta();

        if (t == Main.ArenaType.SUMO) {
            bm.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Leave Queue " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Currently " + ChatColor.RESET + "" + Main.sumoqueue.size() + ChatColor.GRAY + " in queue.");

        } else {
            bm.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Leave Queue " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Currently " + ChatColor.RESET + "" + Main.queue.size() + ChatColor.GRAY + " in queue.");
        }

        rose.setItemMeta(bm);
        player.getInventory().setItem(4, rose);
        Main.diff.put(player.getUniqueId(), 25);

    }

    private void openArenaGUI(Player player, Player player2, boolean isSumo, Kit kit) {
        int arena = 0;
        for (Arena a : Main.Arenas) {
            if (a.type == Main.ArenaType.NORMAL && !isSumo) {
                arena++;
            } else if (a.type == Main.ArenaType.SUMO && isSumo) {
                arena++;
            }
        }
        int slot = 54;
        Inventory inv = Bukkit.createInventory(null, slot, ChatColor.RED + "Choose the Arena! ");
        int index = 0;
        ArrayList<Arena> alist = new ArrayList<>(Main.Arenas);
        alist.sort(new Comparator<Arena>() {
            @Override
            public int compare(Arena o1, Arena o2) {
                return o1.name.compareToIgnoreCase(o2.getName());
            }
        });
        for (Arena a : alist) {
            if (a.type == Main.ArenaType.NORMAL && !isSumo) {
                inv.setItem(index, arenaicon(a, kit));
                index++;
            } else if (a.type == Main.ArenaType.SUMO && isSumo) {
                inv.setItem(index, arenaicon(a, kit));
                index++;
            }
            if (index == 54) break;


        }
        ItemStack gl = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta gm = gl.getItemMeta();
        gm.setDisplayName(Main.hideS(player2.getName()));
        gl.setItemMeta(gm);
        for (int i = 0; i <= 53; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, gl);
            }
        }
        inv.setItem(53, gl);
        player.openInventory(inv);


    }

    public static void openQueueGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.RED + "Ranked Queue");
        ItemStack bucket = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta bm = bucket.getItemMeta();
        bm.setDisplayName(ChatColor.RED + "BuildUHC");
        bm.setLore(Arrays.asList(new String[]{}));

        bm.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "Currently " + ChatColor.RESET + Main.queue.size() + "" + ChatColor.GRAY + " in queue", "", ChatColor.GRAY + "Click to join this queue"}));
        bucket.setItemMeta(bm);

        ItemStack lead = new ItemStack(Material.LEASH);
        ItemMeta lm = lead.getItemMeta();
        lm.setDisplayName(ChatColor.RED + "Sumo");
        lm.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "Currently " + ChatColor.RESET + Main.sumoqueue.size() + "" + ChatColor.GRAY + " in queue", "", ChatColor.GRAY + "Click to join this queue"}));

        lead.setItemMeta(lm);
        inv.setItem(3, bucket);
        inv.setItem(5, lead);
        player.openInventory(inv);

    }

    public static void openSpectatingGUI(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.14f);
        player.getInventory().clear();
        ItemStack rose = new ItemStack(Material.REDSTONE);
        ItemMeta bm = rose.getItemMeta();
        bm.setDisplayName(ChatColor.RED + "Quit Spectate.");
        rose.setItemMeta(bm);
        player.getInventory().setItem(4, rose);
        player.setGameMode(GameMode.CREATIVE);

    }

    private ItemStack arenaicon(Arena a, Kit kit) {
        ItemStack is = new ItemStack(a.getIcon());
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.RED + a.getName());
        String avai = ChatColor.GREEN + "Yes";
        if (a.isUsing()) {
            avai = ChatColor.RED + "No";
        }
        im.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "Available: " + avai, Main.hideS(kit.toString())}));
        is.setItemMeta(im);
        return is;
    }

    public static void openSelectTopGui(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.RED + "ELO Leaderboards");
        ItemStack bucket = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta bm = bucket.getItemMeta();
        bm.setDisplayName(ChatColor.RED + "Top ELO BuildUHC");
        bucket.setItemMeta(bm);

        ItemStack lead = new ItemStack(Material.LEASH);
        ItemMeta lm = lead.getItemMeta();
        lm.setDisplayName(ChatColor.RED + "Top ELO Sumo");
        lead.setItemMeta(lm);
        inv.setItem(3, bucket);
        inv.setItem(5, lead);
        player.openInventory(inv);
    }

    public static void openTOPGui(Player player, Main.ArenaType t) {

        String type = "BuildUHC";
        if (t == Main.ArenaType.SUMO) {
            type = "Sumo";
        }
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.RED + "Top ELO " + type);

        if (t == Main.ArenaType.NORMAL) {

            Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> {
                int top = 1;
                for (Object z : Main.sorted.keySet()) {
                    UUID u = (UUID) z;
                    OfflinePlayer p = Bukkit.getOfflinePlayer(u);
                    ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                    SkullMeta skullmeta = (SkullMeta) item.getItemMeta();
                    skullmeta.setOwner(p.getName());
                    skullmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8&m----&8< &c{rank} &8>&m----".replace("{rank}", top + "")));
                    skullmeta.setLore(Arrays.asList(new String[]{ChatColor.translateAlternateColorCodes('&', "&8&m------------"),
                            ChatColor.RED + p.getName(), ChatColor.WHITE + "" + ELOManager.getElo(u, t) + ChatColor.RED + " ELO",
                            ChatColor.translateAlternateColorCodes('&', "&8&m------------")}));
                    item.setItemMeta(skullmeta);
                    inv.setItem(top - 1, item);
                    if (top == 18) break;
                    top++;
                }

                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta skullmeta = (SkullMeta) item.getItemMeta();
                skullmeta.setOwner(player.getName());
                skullmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8&m----&8< &c{rank} &8>&m----".replace("{rank}",
                        Arrays.asList(Main.sorted.keySet().toArray()).indexOf(player.getUniqueId()) + 1 + "")));
                skullmeta.setLore(Arrays.asList(new String[]{ChatColor.translateAlternateColorCodes('&', "&8&m------------"),
                        ChatColor.RED + player.getName(), ChatColor.WHITE + "" + ELOManager.getElo(player.getUniqueId(), t) + ChatColor.RED + " ELO",
                        ChatColor.translateAlternateColorCodes('&', "&8&m------------")}));
                item.setItemMeta(skullmeta);
                inv.setItem(22, item);
            });

        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> {
                int top = 1;
                for (Object z : Main.sorted2.keySet()) {
                    UUID u = (UUID) z;
                    OfflinePlayer p = Bukkit.getOfflinePlayer(u);
                    ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                    SkullMeta skullmeta = (SkullMeta) item.getItemMeta();
                    skullmeta.setOwner(p.getName());
                    skullmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8&m----&8< &c{rank} &8>&m----".replace("{rank}", top + "")));
                    skullmeta.setLore(Arrays.asList(new String[]{ChatColor.translateAlternateColorCodes('&', "&8&m------------"),
                            ChatColor.RED + p.getName(), ChatColor.WHITE + "" + ELOManager.getElo(u, t) + ChatColor.RED + " ELO",
                            ChatColor.translateAlternateColorCodes('&', "&8&m------------")}));
                    item.setItemMeta(skullmeta);
                    inv.setItem(top - 1, item);
                    if (top == 18) break;
                    top++;
                }
                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta skullmeta = (SkullMeta) item.getItemMeta();
                skullmeta.setOwner(player.getName());
                skullmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8&m----&8< &c{rank} &8>&m----".replace("{rank}",
                        Arrays.asList(Main.sorted2.keySet().toArray()).indexOf(player.getUniqueId()) + 1 + "")));
                skullmeta.setLore(Arrays.asList(new String[]{ChatColor.translateAlternateColorCodes('&', "&8&m------------"),
                        ChatColor.RED + player.getName(), ChatColor.WHITE + "" + ELOManager.getElo(player.getUniqueId(), t) + ChatColor.RED + " ELO",
                        ChatColor.translateAlternateColorCodes('&', "&8&m------------")}));
                item.setItemMeta(skullmeta);
                inv.setItem(22, item);
            });

        }
        player.openInventory(inv);


    }

}
