package sfvv.FruskyDuel;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ADMIN on 6/1/2018.
 */
public class Main extends JavaPlugin implements Listener {
    public static ArrayList<Arena> Arenas;
    public static Main main;
    public static ConcurrentHashMap<UUID, ArrayList<Game>> Games = new ConcurrentHashMap<>();
    public static ArrayList<UUID> dueling = new ArrayList<>();
    public static Location spawn;
    public static HashMap<UUID, Integer> diff = new HashMap<UUID, Integer>();
    public static ArrayList<UUID> debug = new ArrayList<>();
    public static HashMap sorted;
    public static HashMap sorted2;

    public static Plugin getMain() {
        return main;
    }

    public final static String ARROW_LEFT = "\u00AB";
    public final static String ARROW_RIGHT = "\u00BB";
    public static HashMap<String, UUID> range = new HashMap<>();

    //    public static Essentials ess;
    public static ArrayList<UUID> queue = new ArrayList<>();
    public static ArrayList<UUID> sumoqueue = new ArrayList<>();

//    public static Essentials getEss() {
//        return ess;
//    }


    public static ArrayList<UUID> disablerequest = new ArrayList<>();

    public static boolean percentRoll(int percent) {
        return (ThreadLocalRandom.current().nextInt(0, 100) < percent);
    }

    @Override
    public void onEnable() {
        Arenas = new ArrayList<>();
        this.main = this;
//        ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        if (!ELOManager.isExist()) {
            ELOManager.createFile();
        }
        if (getConfig().get("spawn") != null)
            spawn = new Location(Bukkit.getWorld(getConfig().getString("spawn.world")), getConfig().getDouble("spawn.x"), getConfig().getDouble("spawn.y"), getConfig().getDouble("spawn.z"), (float) getConfig().getDouble("spawn.yaw"), (float) getConfig().getDouble("spawn.pitch"));
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> ELOManager.loadFile());
        getConfig().options().copyDefaults(true);
        saveConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        saveConfig();
        reloadConfig();
        loadArena();
        Bukkit.getPluginManager().registerEvents(new GUI(), this);
        Bukkit.getPluginManager().registerEvents(new DeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new CommandEvent(), this);
        Bukkit.getPluginManager().registerEvents(new JoinQuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DuelEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ItemUsageEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ChatFormat(), this);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {

            @Override
            public void run() {
                for (String s : range.keySet()) {
                    Player player = Bukkit.getPlayer(s);
                    Player p2 = Bukkit.getPlayer(range.get(s));
                    int playerDistance = 0;
                    if (p2 != null && p2.getWorld() == player.getWorld())
                        playerDistance = (int) player.getLocation().distance(p2.getLocation());
                    if (!player.isOnline()) {
                        range.remove(s);
                    }
                    if (playerDistance > 50) {
                        player.teleport(p2);
                    }
//                    if (player.getInventory().getItem(4) == null) {
//                        GUI.openSpectatingGUI(player);
//                    }
//                    if (player.getInventory().getItem(4).getType() == Material.AIR) {
//                        GUI.openSpectatingGUI(player);
//
//                    }

                }
                for (Player z : Bukkit.getOnlinePlayers()) {
                    if (range.keySet().contains(z.getName())) {
                        for (Player s : Bukkit.getOnlinePlayers()) {
                            if (z.getUniqueId() == s.getUniqueId()) continue;
                            if (range.keySet().contains(s.getName())) {
                                if (z.spigot().getHiddenPlayers().contains(s)) {

                                    z.showPlayer(s);
                                }
                            } else {
                                if (!z.spigot().getHiddenPlayers().contains(s)) {

                                    z.showPlayer(s);
                                }
                            }

                        }
                    } else {
                        for (Player s : Bukkit.getOnlinePlayers()) {
                            if (z.getUniqueId() == s.getUniqueId()) continue;

                            if (range.keySet().contains(s.getName())) {
                                if (!z.spigot().getHiddenPlayers().contains(s)) {
                                    z.hidePlayer(s);
                                }
                            } else {
                                if (z.spigot().getHiddenPlayers().contains(s)) {
                                    z.showPlayer(s);
                                }
                            }

                        }
                    }
                }
            }
        }, 5, 60);

        // &cBlaZerCooper &8[&c2122&8] &7fights against &cElwins &8[&c2122&8] &cSUMO &9&lClick to spectate
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            int time = 2;
            int ct = 1;

            @Override
            public void run() {
                int count = Bukkit.getServer().getOnlinePlayers().size();
                if (count <= 10) {
                    time = 2;
                } else if (count <= 20) {
                    time = 3;
                } else if (count <= 35) {
                    time = 4;
                } else if (count <= 50) {
                    time = 5;
                } else {
                    time = 6;
                }
                if (ct >= time) {
                    ct = 1;


                    ArrayList<UUID> rev = new ArrayList<UUID>();
                    for (UUID u : queue) {
                        if (diff.keySet().contains(u)) {
                            diff.put(u, diff.get(u) + 75);
                            if (diff.get(u) >= 2000) {
                                diff.put(u, 100);
                            }
                        } else {
                            diff.put(u, 100);
                        }
                        Player player = Bukkit.getPlayer(u);
                        player.sendMessage(ChatColor.GREEN + "Searching for ELO difference " + diff.get(u) + "...");
                        if (dueling.contains(u)) {
                            continue;
                        }
                        Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                            if (player.getInventory().getItem(4) != null) {
                                ItemStack rose = new ItemStack(Material.INK_SACK, 1, (short) 1);
                                ItemMeta bm = rose.getItemMeta();
                                bm.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Leave Queue " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Currently " + ChatColor.RESET + "" + Main.queue.size() + ChatColor.GRAY + " in queue.");


                                rose.setItemMeta(bm);
                                player.getInventory().setItem(4, rose);
                            }
                        });

                        ArrayList<UUID> queuelist = new ArrayList<UUID>(queue);
                        if (queuelist.contains(u)) {
                            queuelist.remove(u);
                        }
                        ArrayList<UUID> possiblelist = new ArrayList<UUID>();
                        queuelist.sort((o1, o2) -> Integer.valueOf(ELOManager.getElo(o1, ArenaType.NORMAL)).compareTo(ELOManager.getElo(o2, ArenaType.NORMAL)));
                        int id = queuelist.indexOf(u);
                        if (queuelist.size() >= 1) {
                            int range = 1;
                            while (possiblelist.size() <= 1) {
                                try {
                                    possiblelist.add(queuelist.get(id - range));
                                    possiblelist.add(queuelist.get(id + range));
                                } catch (Exception e) {

                                }
                                if (range >= 5) {
                                    possiblelist.addAll(queuelist);
                                }
                                range++;
                            }
                        } else {
                            continue;
                        }

                        if (possiblelist.size() >= 1) {

                            Player p2 = Bukkit.getPlayer(possiblelist.get(ThreadLocalRandom.current().nextInt(possiblelist.size() - 1)));

                            if (dueling.contains(p2.getUniqueId())) {
                                if (!rev.contains(p2.getUniqueId())) rev.add(p2.getUniqueId());
                                continue;
                            }
                            int elop = ELOManager.getElo(player.getUniqueId(), ArenaType.NORMAL);
                            int elopp = ELOManager.getElo(p2.getUniqueId(), ArenaType.NORMAL);
                            if (Math.abs(elopp - elop) >= diff.get(u)) {
                                continue;
                            }
                            if (Math.abs(elopp - elopp) >= diff.get(p2.getUniqueId())) {
                                continue;
                            }

                            boolean havearena = false;
                            ArrayList<Arena> al = new ArrayList<>();
                            for (Arena a : Main.Arenas) {
                                if (!a.isUsing() && a.getType() == Main.ArenaType.NORMAL) {
                                    al.add(a);
                                }

                            }
                            if (al.size() >= 0) {
                                Arena a = al.get(ThreadLocalRandom.current().nextInt(0, al.size()));
                                a.setUsing(true);

                                Game g = new Game(player, p2, Kit.BUILDUHC, a, true);
                                if (Main.Games.containsKey(p2.getUniqueId())) {
                                    Main.Games.get(p2.getUniqueId()).add(g);
                                } else {
                                    ArrayList<Game> gl = new ArrayList<>();
                                    gl.add(g);
                                    Main.Games.put(p2.getUniqueId(), gl);
                                }

                                g.setState(Game.gameState.WAITING);
                                g.getP1().teleport(g.getArena().getLoc1());
                                g.getP2().teleport(g.getArena().getLoc2());
                                dueling.add(g.getP1().getUniqueId());
                                dueling.add(g.getP2().getUniqueId());
                                Main.sendMessage(g.getP1(), ChatColor.RED + g.getP2().getName() + ChatColor.GRAY + " has an ELO rating of " + ChatColor.UNDERLINE + ChatColor.WHITE + ELOManager.getElo(g.getP2().getUniqueId(), ArenaType.NORMAL));
                                Main.sendMessage(g.getP2(), ChatColor.RED + g.getP1().getName() + ChatColor.GRAY + " has an ELO rating of " + ChatColor.UNDERLINE + ChatColor.WHITE + ELOManager.getElo(g.getP1().getUniqueId(), ArenaType.NORMAL));
                                if (elop >= 1650 && elopp >= 1650) {
                                    String msg = ChatColor.RED + player.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.RED + elop + "" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " fights against " + ChatColor.RED + p2.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.RED + elop + "" + ChatColor.DARK_GRAY + "] "
                                            + ChatColor.RED + "BuildUHC";
                                    String lol = ChatColor.AQUA + ChatColor.BOLD.toString() + "Click to spectate";
                                    TextComponent z = new TextComponent(msg);
                                    TextComponent pz = new TextComponent(lol);
                                    HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to spectate").create());
                                    pz.setHoverEvent(hover);

                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (percentRoll(50)) {
                                            pz.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spec " + player.getName() + " " + p2.getName()));
                                        } else {
                                            pz.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spec " + p2.getName() + " " + player.getName()));
                                        }
                                        p.spigot().sendMessage(z);
                                        p.spigot().sendMessage(pz);
                                    }


                                }


                                g.getP1().getInventory().clear();
                                g.getP2().getInventory().clear();
                                if (g.getKit() != Kit.SUMO) {
                                    if (g.getKit() == Kit.BUILDUHC) {
                                        for (ItemStack is : Kit.getItemList(g.getKit())) {
                                            g.getP1().getInventory().addItem(is);
                                            g.getP2().getInventory().addItem(is);
                                        }
                                    }
                                }
                                rev.add(g.getP1().getUniqueId());
                                rev.add(g.getP2().getUniqueId());

                                diff.remove(u);
                                havearena = true;

                            }
                            if (!havearena) {
                                break;
                            }

                        }

                    }
                    for (UUID u : rev) {
                        if (queue.contains(u)) {
                            queue.remove(u);
                        }
                    }
                    rev = new ArrayList<UUID>();
                    for (UUID y : sumoqueue) {
                        Player player = Bukkit.getPlayer(y);
                        if (diff.keySet().contains(y)) {
                            diff.put(y, diff.get(y) + 75);
                            if (diff.get(y) >= 2000) {
                                diff.put(y, 100);
                            }
                        } else {
                            diff.put(y, 100);
                        }
                        player.sendMessage(ChatColor.GREEN + "Searching for ELO difference " + diff.get(y) + "...");
                        if (dueling.contains(y)) continue;
                        Bukkit.getScheduler().runTask(Main.getMain(), () -> {
                            if (player.getInventory().getItem(4) != null) {
                                ItemStack rose = new ItemStack(Material.INK_SACK, 1, (short) 1);
                                ItemMeta bm = rose.getItemMeta();
                                bm.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Leave Queue " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + "Currently " + ChatColor.RESET + "" + Main.sumoqueue.size() + ChatColor.GRAY + " in queue.");

                                rose.setItemMeta(bm);
                                player.getInventory().setItem(4, rose);
                            }
                        });
                        ArrayList<UUID> queuelist = new ArrayList<UUID>(sumoqueue);
                        if (queuelist.contains(y)) {
                            queuelist.remove(y);
                        }
                        ArrayList<UUID> possiblelist = new ArrayList<UUID>();
                        queuelist.sort((o1, o2) -> Integer.valueOf(ELOManager.getElo(o1, ArenaType.SUMO)).compareTo(ELOManager.getElo(o2, ArenaType.SUMO)));
                        int id = queuelist.indexOf(y);
                        if (queuelist.size() >= 1) {
                            int range = 1;
                            while (possiblelist.size() <= 1) {
                                try {
                                    possiblelist.add(queuelist.get(id - range));
                                    possiblelist.add(queuelist.get(id + range));
                                } catch (Exception e) {

                                }
                                if (range >= 5) {
                                    possiblelist.addAll(queuelist);
                                }
                                range++;
                            }
                        } else {
                            continue;
                        }

                        if (possiblelist.size() >= 1) {
                            Player p2 = Bukkit.getPlayer(possiblelist.get(ThreadLocalRandom.current().nextInt(possiblelist.size() - 1)));
                            if (dueling.contains(p2.getUniqueId())) {
                                if (!rev.contains(p2.getUniqueId())) rev.add(p2.getUniqueId());
                                continue;
                            }

                            int elop = ELOManager.getElo(player.getUniqueId(), ArenaType.SUMO);
                            int elopp = ELOManager.getElo(p2.getUniqueId(), ArenaType.SUMO);
                            if (Math.abs(elopp - elop) >= diff.get(y)) {
                                continue;
                            }
                            if (Math.abs(elopp - elopp) >= diff.get(p2.getUniqueId())) {
                                continue;
                            }
                            boolean havearena = false;
                            ArrayList<Arena> al = new ArrayList<>();
                            for (Arena a : Main.Arenas) {
                                if (!a.isUsing() && a.getType() == Main.ArenaType.SUMO) {
                                    al.add(a);
                                }

                            }
                            if (al.size() >= 0) {
                                Arena a = al.get(ThreadLocalRandom.current().nextInt(0, al.size()));
                                a.setUsing(true);

                                Game g = new Game(player, p2, Kit.SUMO, a, true);
                                if (Main.Games.containsKey(p2.getUniqueId())) {
                                    Main.Games.get(p2.getUniqueId()).add(g);
                                } else {
                                    ArrayList<Game> gl = new ArrayList<>();
                                    gl.add(g);
                                    Main.Games.put(p2.getUniqueId(), gl);
                                }

                                g.setState(Game.gameState.WAITING);
                                g.getP1().teleport(g.getArena().getLoc1());
                                g.getP2().teleport(g.getArena().getLoc2());
                                dueling.add(g.getP1().getUniqueId());
                                dueling.add(g.getP2().getUniqueId());
                                Main.sendMessage(g.getP1(), ChatColor.RED + g.getP2().getName() + ChatColor.GRAY + " has an ELO rating of " + ChatColor.UNDERLINE + ChatColor.WHITE + ELOManager.getElo(g.getP2().getUniqueId(), ArenaType.SUMO));
                                Main.sendMessage(g.getP2(), ChatColor.RED + g.getP1().getName() + ChatColor.GRAY + " has an ELO rating of " + ChatColor.UNDERLINE + ChatColor.WHITE + ELOManager.getElo(g.getP1().getUniqueId(), ArenaType.SUMO));

                                if (elop >= 1650 && elopp >= 1650) {
                                    String msg = ChatColor.RED + player.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.RED + elop + "" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " fights against " + ChatColor.RED + p2.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.RED + elop + "" + ChatColor.DARK_GRAY + "] "
                                            + ChatColor.RED + "Sumo";
                                    String lol = ChatColor.AQUA + ChatColor.BOLD.toString() + "Click to spectate";
                                    TextComponent z = new TextComponent(msg);
                                    TextComponent pz = new TextComponent(lol);
                                    HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to spectate").create());
                                    pz.setHoverEvent(hover);

                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (percentRoll(50)) {
                                            pz.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spec " + player.getName() + " " + p2.getName()));
                                        } else {
                                            pz.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spec " + p2.getName() + " " + player.getName()));
                                        }
                                        p.spigot().sendMessage(z);
                                        p.spigot().sendMessage(pz);
                                    }


                                }
                                g.getP1().getInventory().clear();
                                g.getP2().getInventory().clear();
                                g.getP1().getInventory().setHelmet(new ItemStack(Material.AIR));
                                g.getP1().getInventory().setChestplate(new ItemStack(Material.AIR));
                                g.getP1().getInventory().setLeggings(new ItemStack(Material.AIR));
                                g.getP1().getInventory().setBoots(new ItemStack(Material.AIR));
                                g.getP2().getInventory().setHelmet(new ItemStack(Material.AIR));
                                g.getP2().getInventory().setChestplate(new ItemStack(Material.AIR));
                                g.getP2().getInventory().setLeggings(new ItemStack(Material.AIR));
                                g.getP2().getInventory().setBoots(new ItemStack(Material.AIR));

                                rev.add(g.getP1().getUniqueId());
                                rev.add(g.getP2().getUniqueId());
                                havearena = true;
                                diff.remove(y);
                            }
                            if (!havearena) {
                                continue;
                            }

                        }

                    }
                    for (UUID u : rev) {
                        if (sumoqueue.contains(u)) {
                            sumoqueue.remove(u);
                        }
                    }
                } else

                {
                    ct++;
                }

            }
        }, 20, 20);
        Bukkit.getScheduler().

                runTaskTimerAsynchronously(this, new Runnable() {
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            ELOManager.saveData(player.getUniqueId());
                        }
                        sorted = Main.sortByValues(ELOManager.PlayerEloBUILDHUC);
                        sorted2 = Main.sortByValues(ELOManager.PlayerEloSUMO);
//                updateTopList();

                    }
                }, 60 * 20 * 5, 60 * 20 * 5);


    }

//    public static void updateTopList() {
//            sortByComparator(ELOManager.PlayerEloBUILDHUC);
//            sortByComparator(ELOManager.PlayerEloSUMO);
//    }

    public static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    @Override
    public void onDisable() {

        for (Arena a : Arenas) {
            a.saveData();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            ELOManager.saveData(player.getUniqueId());
        }

    }

    // /duel Player
    public void loadArena() {
        for (File f : new File(Main.getMain().getDataFolder() + "/Arena/").listFiles()) {
            String z = f.getName().replace(".yml", "");
            Arena a = new Arena(z);
            if (a.isExist()) {
                a.loadData();
                Arenas.add(a);
            } else {
                a.saveData();
            }

        }
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (cmd.getName().equalsIgnoreCase("duel") && args.length <= 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            }
            if (cmd.getName().equalsIgnoreCase("duel") && args.length > 0 && sender instanceof Player) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("accept") && args.length > 1) {
                    if (range.containsKey(player.getName())) return false;
                    Player player2 = Bukkit.getPlayer(args[1]);
                    if (range.containsKey(player2.getName())) {
                        player.sendMessage(ChatColor.RED + "This player is now in spectate mode.");
                        return false;
                    }
                    if (dueling.contains(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You are already in a duel.");
                        return false;
                    }

                    if (dueling.contains(player2.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "Player is already in a duel.");
                        return false;
                    }
                    if (Games.containsKey(player.getUniqueId())) {

                        ArrayList<Game> gl = Games.get(player.getUniqueId());
                        Game g = null;
                        for (Game gz : gl) {
                            if (gz.getP1() == player2) {
                                g = gz;
                                break;
                            }
                        }
                        if (g == null) {
                            player.sendMessage(ChatColor.RED + "This request expired!");
                            return false;
                        }
                        if (g.iscancel) {
                            player.sendMessage(ChatColor.RED + "This request expired!");
                            return false;
                        }
                        if (g.getState() != Game.gameState.REQUESTED) {
                            player.sendMessage(ChatColor.RED + "Can't accept request while dueling ");
                            return true;
                        }

                        if (g.getArena().isUsing()) {
                            if (Main.Games.containsKey(player.getUniqueId())) {
                                Main.Games.get(player.getUniqueId()).remove(g);
                            }
                            player.sendMessage(ChatColor.RED + "This arena is now unavailable");
                            return false;
                        }
                        if (queue.contains(g.getP1().getUniqueId())) queue.remove(g.getP1().getUniqueId());
                        if (queue.contains(g.getP2().getUniqueId())) queue.remove(g.getP2().getUniqueId());
                        if (sumoqueue.contains(g.getP1().getUniqueId())) sumoqueue.remove(g.getP1().getUniqueId());
                        if (sumoqueue.contains(g.getP2().getUniqueId())) sumoqueue.remove(g.getP2().getUniqueId());
                        g.getP1().setItemOnCursor(new ItemStack(Material.AIR));
                        g.getP2().setItemOnCursor(new ItemStack(Material.AIR));
                        g.getP1().getOpenInventory().getTopInventory().clear();
                        g.getP2().getOpenInventory().getTopInventory().clear();
                        g.getP1().teleport(g.getArena().getLoc1());
                        g.getP2().teleport(g.getArena().getLoc2());
                        dueling.add(g.getP1().getUniqueId());
                        dueling.add(g.getP2().getUniqueId());


                        g.getP1().sendMessage(ChatColor.GRAY + "Opponent is " + ChatColor.RED + g.getP2().getName() + ChatColor.GRAY + " with kit " + ChatColor.RESET + g.getKit().getName());


                        g.getP2().sendMessage(ChatColor.GRAY + "Opponent is " + ChatColor.RED + g.getP1().getName() + ChatColor.GRAY + " with kit " + ChatColor.RESET + g.getKit().getName());


                        g.getP1().getInventory().clear();
                        g.getP2().getInventory().clear();
                        if (g.getKit() != Kit.SUMO) {
                            if (g.getKit() == Kit.BUILDUHC) {
                                for (ItemStack is : Kit.getItemList(g.getKit())) {
                                    g.getP1().getInventory().addItem(is);
                                    g.getP2().getInventory().addItem(is);
                                }
                            } else {
                                int index = 0;
                                for (ItemStack is : Kit.getItemList(g.getKit())) {
                                    g.getP1().getInventory().addItem(is);
                                    g.getP2().getInventory().addItem(is);
                                    if (index >= Kit.getItemList(g.getKit()).size() - 5) {
                                        break;
                                    }
                                    index++;
                                }

                                g.getP1().getInventory().setHelmet(Kit.getItemList(g.getKit()).get(Kit.getItemList(g.getKit()).size() - 4));
                                g.getP1().getInventory().setChestplate(Kit.getItemList(g.getKit()).get(Kit.getItemList(g.getKit()).size() - 3));
                                g.getP1().getInventory().setLeggings(Kit.getItemList(g.getKit()).get(Kit.getItemList(g.getKit()).size() - 2));
                                g.getP1().getInventory().setBoots(Kit.getItemList(g.getKit()).get(Kit.getItemList(g.getKit()).size() - 1));

                                g.getP2().getInventory().setHelmet(Kit.getItemList(g.getKit()).get(Kit.getItemList(g.getKit()).size() - 4));
                                g.getP2().getInventory().setChestplate(Kit.getItemList(g.getKit()).get(Kit.getItemList(g.getKit()).size() - 3));
                                g.getP2().getInventory().setLeggings(Kit.getItemList(g.getKit()).get(Kit.getItemList(g.getKit()).size() - 2));
                                g.getP2().getInventory().setBoots(Kit.getItemList(g.getKit()).get(Kit.getItemList(g.getKit()).size() - 1));
                            }
                        } else {
                            g.getP1().getInventory().setHelmet(new ItemStack(Material.AIR));
                            g.getP1().getInventory().setChestplate(new ItemStack(Material.AIR));
                            g.getP1().getInventory().setLeggings(new ItemStack(Material.AIR));
                            g.getP1().getInventory().setBoots(new ItemStack(Material.AIR));
                            g.getP2().getInventory().setHelmet(new ItemStack(Material.AIR));
                            g.getP2().getInventory().setChestplate(new ItemStack(Material.AIR));
                            g.getP2().getInventory().setLeggings(new ItemStack(Material.AIR));
                            g.getP2().getInventory().setBoots(new ItemStack(Material.AIR));
                        }
                        g.setState(Game.gameState.WAITING);
                    } else {
                        player.sendMessage(ChatColor.RED + "This request expired!");
                    }
                } else {
                    Player specify = Bukkit.getPlayer(args[0]);
                    if (specify.getName().equals(player.getName())) {
                        player.sendMessage(ChatColor.RED + "You can't duel yourself.");
                        return false;
                    } else if (Main.disablerequest.contains(specify.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "This player has duel requests disabled.");
                        return false;

                    } else if (range.keySet().contains(player.getName())) {
                        player.sendMessage(ChatColor.RED + "This player is in spectate mode.");
                        return false;
                    } else {
                        sendRequest(player, specify);
                    }
                }
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Usage: /duel <player>");
        }
        if (cmd.getName().equalsIgnoreCase("spawn")) {
            if (sender instanceof Player && args.length == 0) {

                Player player = (Player) sender;
                UUID u = player.getUniqueId();
                if (Main.sumoqueue.contains(u)) {
                    Main.sumoqueue.remove(u);
                }
                if (Main.queue.contains(u)) {
                    Main.queue.remove(u);
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(Main.spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
                Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "duelfixcommand " + player.getName()), 3);
            } else if (args.length > 0 && sender.hasPermission("dueladmin.admin")) {

                Player player = Bukkit.getPlayer(args[0]);
                UUID u = player.getUniqueId();
                if (Main.sumoqueue.contains(u)) {
                    Main.sumoqueue.remove(u);
                }
                if (Main.queue.contains(u)) {
                    Main.queue.remove(u);
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(Main.spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
                Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "duelfixcommand " + player.getName()), 3);
            }
        }

        if (cmd.getName().equalsIgnoreCase("elo")) {
            if (args.length > 0) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if (!player.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.RED + args[0] + " doesn't exist");
                    return false;
                }
                sender.sendMessage(ChatColor.GRAY + "ELO Statistics of " + ChatColor.RESET + args[0] + ChatColor.GRAY + ".");
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "BuildFFA " + ChatColor.GRAY + "» " + ChatColor.RESET + ELOManager.getElo(player.getUniqueId(), ArenaType.NORMAL));
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "SUMO " + ChatColor.GRAY + "» " + ChatColor.RESET + ELOManager.getElo(player.getUniqueId(), ArenaType.SUMO));
            } else if (sender instanceof Player) {
                Player player = (Player) sender;
                sender.sendMessage(ChatColor.GRAY + "ELO Statistics of " + ChatColor.RESET + player.getName() + ChatColor.GRAY + ".");
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "BuildFFA " + ChatColor.GRAY + "» " + ChatColor.RESET + ELOManager.getElo(player.getUniqueId(), ArenaType.NORMAL));
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "SUMO " + ChatColor.GRAY + "» " + ChatColor.RESET + ELOManager.getElo(player.getUniqueId(), ArenaType.SUMO));
            }
        }
        if (cmd.getName().equalsIgnoreCase("adminduel") && sender.hasPermission("dueladmin.admin")) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.GREEN + "/adminduel setelo <Player> <Normal/Sumo> <ELO> ");
                sender.sendMessage(ChatColor.GREEN + "/adminduel create <Normal/Sumo> <ArenaName> ");
                sender.sendMessage(ChatColor.GREEN + "/adminduel set <ArenaName> <1/2> ");
                sender.sendMessage(ChatColor.GREEN + "/adminduel seticon <ArenaName> <id> ");
                sender.sendMessage(ChatColor.GREEN + "/adminduel remove <ArenaName> ");
                sender.sendMessage(ChatColor.GREEN + "/adminduel ulb // update the leaderboard");
            }

            // /adminduel checkelo [player] [type]
            if (args[0].equalsIgnoreCase("checkelo") && args.length > 2) {
                Player specify = Bukkit.getPlayer(args[1]);
                ArenaType type = ArenaType.valueOf(args[2].toUpperCase());
                sender.sendMessage("Elo of " + specify.getName() + " is " + ELOManager.getElo(specify.getUniqueId(), type));

            }
            if (args[0].equalsIgnoreCase("setspawn") && sender instanceof Player) {
                Player player = (Player) sender;
                getConfig().set("spawn.x", player.getLocation().getX());
                getConfig().set("spawn.y", player.getLocation().getY());
                getConfig().set("spawn.z", player.getLocation().getZ());
                getConfig().set("spawn.pitch", player.getLocation().getPitch());
                getConfig().set("spawn.yaw", player.getLocation().getYaw());
                getConfig().set("spawn.world", player.getLocation().getWorld().getName());
                saveConfig();
                spawn = player.getLocation();
                sender.sendMessage("Spawn set!");

            }
            if (args[0].equalsIgnoreCase("setelo") && args.length > 3) {
                OfflinePlayer specify = Bukkit.getOfflinePlayer(args[1]);
                ArenaType type = ArenaType.valueOf(args[2].toUpperCase());
                Integer elo = Integer.valueOf(args[3]);
                ELOManager.updateElo(specify.getUniqueId(), elo, type);
                sender.sendMessage(ChatColor.GREEN + specify.getName() + "'s " + type.toString() + " ELO" + " is " + elo + " now");

            }
            if (args[0].equalsIgnoreCase("ulb")) {
                Bukkit.getScheduler().

                        runTaskAsynchronously(this, new Runnable() {
                            @Override
                            public void run() {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    ELOManager.saveData(player.getUniqueId());
                                }
                                sorted = Main.sortByValues(ELOManager.PlayerEloBUILDHUC);
                                sorted2 = Main.sortByValues(ELOManager.PlayerEloSUMO);
//                updateTopList();

                            }
                        });
                sender.sendMessage("Leaderboard updated");

            }
            if (args[0].equalsIgnoreCase("create") && args.length > 2) {
                for (Arena a : Arenas) {
                    if (a.getName().equalsIgnoreCase(args[2])) {
                        sender.sendMessage(ChatColor.RED + "Arena already exist");
                        return false;
                    }
                }
                if (EnumUtils.isValidEnum(ArenaType.class, args[1].toUpperCase())) {

                    createArenas(ArenaType.valueOf(args[1].toUpperCase()), args[2]);
                    sender.sendMessage(ChatColor.GREEN + "Created arena " + args[2] + " Arena Type : " + args[1].toUpperCase());
                }
            }
            if (args[0].equalsIgnoreCase("remove") && args.length > 1) {
                Arena arena = getArena(args[1]);
                Main.Arenas.remove(arena);
                arena.remove();
                sender.sendMessage(ChatColor.RED + "Arena " + args[1] + " removed");

            }
            if (args[0].equalsIgnoreCase("set") && args.length > 2 && sender instanceof Player) {
                try {
                    Arena arena = getArena(args[1]);
                    Player player = (Player) sender;
                    if (args[2].equals("1")) {
                        arena.setLoc1(player.getLocation());
                        sender.sendMessage(ChatColor.RED + "Arena " + arena.getName() + " Location 1 set");
                    }
                    if (args[2].equals("2")) {
                        arena.setLoc2(player.getLocation());
                        sender.sendMessage(ChatColor.RED + "Arena " + arena.getName() + " Location 2 set");

                    }
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "This Arena doesn't exist");
                }


            }
            if (args[0].equalsIgnoreCase("seticon") && args.length > 2) {
                try {
                    Arena arena = getArena(args[1]);
                    Material maz = null;
                    if (StringUtils.isNumeric(args[2])) {
                        for (Material ma : Material.values()) {
                            //noinspection deprecation
                            if (ma.getId() == Integer.valueOf(args[2])) {
                                maz = ma;
                                break;
                            }
                        }
                    } else if (EnumUtils.isValidEnum(Material.class, args[2])) {
                        maz = Material.valueOf(args[2]);
                    }
                    if (maz == null) {
                        sender.sendMessage(ChatColor.RED + "Material with ID " + args[2] + " dont exist");
                    } else {
                        arena.setIcon(maz);
                        sender.sendMessage(ChatColor.RED + "Arena " + arena.getName() + "'s icon set to " + maz.toString());
                    }

                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "This Arena doesn't exist");
                }


            }
            if (args[0].equalsIgnoreCase("debug") && sender instanceof Player) {
                Player player = (Player) sender;
                if (debug.contains(player.getUniqueId())) {
                    debug.remove(player.getUniqueId());
                } else {
                    debug.add(player.getUniqueId());
                }


            }


            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("spec")) {
            if (args.length == 0) {
                player.sendMessage("§8[§cFruskySpectate§8]: §fGebruik: /spec <player>");
                return true;
            } else if (args.length >= 1) {
                String targetName = args[0];
                Player target = Bukkit.getPlayer(targetName);
                if (target == null) {
                    player.sendMessage("§8[§cFruskySpectate§8]: " + targetName + "§f is momenteel niet online!");
                    return true;
                } else if (range.containsKey(target.getName())) {
                    player.sendMessage(ChatColor.RED + "This is a spectator, You can't spectate this player");
                    return true;
                } else if (target == player) {
                    player.sendMessage("§8[§cFruskySpectate§8]: §fJe kan niet jezelf spectate");
                    return true;
                } else if (range.containsKey(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You are already spectating someone!");
                    return false;
                }
                if (Main.queue.contains(player.getUniqueId())) {
                    queue.remove(player.getUniqueId());
                }
                if (Main.sumoqueue.contains(player.getUniqueId())) {
                    sumoqueue.remove(player.getUniqueId());
                }


                GUI.openSpectatingGUI(player);
                player.teleport(target);
                target.sendMessage("&8[&cFruskySpectate&8]: ".replace("&", ChatColor.COLOR_CHAR + "") + ChatColor.RED + player.getName() + " &fis now spectating!".replace("&", ChatColor.COLOR_CHAR + ""));
                player.sendMessage(ChatColor.RESET + "You're now spectating " + ChatColor.RED + target.getName() + ChatColor.RESET + "!");
                if (args.length == 2) {
                    Player target2 = Bukkit.getPlayer(args[1]);
                    target2.sendMessage("&8[&cFruskySpectate&8]: ".replace("&", ChatColor.COLOR_CHAR + "") + ChatColor.RED + player.getName() + " &fis now spectating!".replace("&", ChatColor.COLOR_CHAR + ""));

                }
                range.put(player.getName(), target.getUniqueId());
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);
                for (Player z : Bukkit.getOnlinePlayers()) {
                    if (range.keySet().contains(z.getName())) {
                        for (Player s : Bukkit.getOnlinePlayers()) {
                            if (z.getUniqueId() == s.getUniqueId()) continue;
                            if (range.keySet().contains(s.getName())) {
                                if (z.spigot().getHiddenPlayers().contains(s)) {

                                    z.showPlayer(s);
                                }
                            } else {
                                if (!z.spigot().getHiddenPlayers().contains(s)) {

                                    z.showPlayer(s);
                                }
                            }

                        }
                    } else {
                        for (Player s : Bukkit.getOnlinePlayers()) {
                            if (z.getUniqueId() == s.getUniqueId()) continue;

                            if (range.keySet().contains(s.getName())) {
                                if (!z.spigot().getHiddenPlayers().contains(s)) {
                                    z.hidePlayer(s);
                                }
                            } else {
                                if (z.spigot().getHiddenPlayers().contains(s)) {
                                    z.showPlayer(s);
                                }
                            }

                        }
                    }
                }


                return true;

            }
        }


        if (label.equalsIgnoreCase("spectate")) {
            player.sendMessage("§8[§cFruskySpectate§8]: §cGebruik /spec [naam] om iemand te spectate!");
        }


        return true;
    }

    private void sendRequest(Player sender, Player specify) {
        if (dueling.contains(specify.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player is already in a match");
            return;
        }
        if (Main.Games.containsKey(specify.getUniqueId())) {
            ArrayList<Game> gl = Main.Games.get(specify.getUniqueId());
            Game game = null;
            boolean sent = false;
            for (Game gz : gl) {
                if (gz.getP1() == sender) {
                    sent = true;
                    game = gz;
                    break;
                }
                if (gz.getP1() == sender) {
                    sent = true;
                    game = gz;
                    break;
                }
            }
            if (!sent) {
                GUI.openKitGUI(sender, specify);
            } else {
                int time = game.timerr;
                if (game.iscancel) {
                    ArrayList<Game> gla = new ArrayList<>(Main.Games.get(specify.getUniqueId()));
                    gla.remove(game);
                    Main.Games.put(specify.getUniqueId(), gla);
                    GUI.openKitGUI(sender, specify);
                } else {
                    sender.sendMessage(ChatColor.RED + "Wait " + (40 - time) / 2 + " seconds before sending again");
                }

            }
        } else {
            GUI.openKitGUI(sender, specify);
        }
    }

    private void createArenas(ArenaType type, String name) {
        Arena a = new Arena(name);
        a.setType(type);
        Arenas.add(a);

    }

    public Arena getArena(String name) {
        for (Arena a : Arenas) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;

    }

    public void openArenaList(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "");
        int index = 0;
        for (Arena a : Arenas) {
            if (!a.ready()) continue;
            ItemStack is = new ItemStack(a.getIcon());
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(a.getName());
            im.setLore(Arrays.asList(new String[]{a.getType() + "", a.getLoc1() + "", a.getLoc2() + ""}));
            is.setItemMeta(im);
            inv.setItem(index, is);
            index++;
        }
        player.openInventory(inv);
    }


    public enum ArenaType {
        NORMAL, SUMO;
    }

    public static String hideS(String s) {
        String hidden = "";
        for (char c : s.toCharArray())
            hidden += ChatColor.COLOR_CHAR + "" + c;
        return hidden;
    }

    public static String unhideS(String s) {
        String r = s.replaceAll("§", "");
        return r;
    }

    public static void sendTitle(Player player, String tit, String sub) {
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + tit + "\",color:" + ChatColor.GOLD.name().toLowerCase() + "}");
        IChatBaseComponent subtitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + sub + "\",color:" + ChatColor.GOLD.name().toLowerCase() + "}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle subt = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(5, 20, 5);


        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subt);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    public static void addtoQueue(UUID player, ArenaType type) {
        if (type == ArenaType.NORMAL) {
            queue.add(player);
        } else {
            sumoqueue.add(player);

        }
    }

    public static void leaveQueue(UUID player) {
        if (queue.contains(player))
            queue.remove(queue.indexOf(player));
        if (sumoqueue.contains(player))
            sumoqueue.remove(sumoqueue.indexOf(player));
    }


    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getWhoClicked().getGameMode() == GameMode.CREATIVE && range.containsKey(e.getWhoClicked().getName())) {
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void onDamageEvent(EntityDamageEvent event) {
        Entity player = event.getEntity();
        if (range.containsKey(player.getName())) {
            event.setCancelled(true);
        }
        return;
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (range.containsKey(player.getName())) {
            event.setCancelled(true);
        }
        return;
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (range.containsKey(player.getName())) {
            event.setCancelled(true);
        }
        return;
    }

    @EventHandler
    public void onAttackEvent(EntityDamageByEntityEvent event) {
        Entity player = event.getDamager();
        if (range.containsKey(player.getName())) {
            event.setCancelled(true);
        }
        return;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (range.containsKey(p.getName())) {
            range.remove(event.getPlayer().getName());
        }
    }


    @EventHandler
    public void breakblock(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (range.keySet().contains(player.getName())) {
            e.setCancelled(true);
        }
    }

    public static void sendMessage(Player player, String mess) {
        player.sendMessage(mess);
        for (String s : range.keySet()) {
            if (range.get(s) == player.getUniqueId()) {
                Bukkit.getPlayer(s).sendMessage(mess.replace("you're", player.getName() + "'s").replace("your", player.getName() + "'s").replace("you", player.getName()));
            }
        }
    }

    public static void sendMessageDuel(Player player, String mess, Player receiver) {
        player.sendMessage(mess);
        for (String s : range.keySet()) {
            if (range.get(s) == player.getUniqueId()) {
                Bukkit.getPlayer(s).sendMessage(ChatColor.RED + player.getName() + ChatColor.RESET + " has send a duel request to " + ChatColor.RED + receiver.getName());
            }
        }
    }
//{SENDER} has send a duel request to {RECEIVER}

    public static void kickOutOfSpec(Player player) {
        Main.range.remove(player.getName());
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "duelfixcommand " + player.getName());
        player.teleport(Main.spawn);
    }
}
