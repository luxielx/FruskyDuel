package sfvv.FruskyDuel;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ADMIN on 6/22/2018.
 */
public class ELOManager {

    public static HashMap<UUID, Integer> PlayerEloBUILDHUC = new HashMap<>();
    public static HashMap<UUID, Integer> PlayerEloSUMO = new HashMap<>();

    static int elodefaultvalue = 1000;


    public static void loadFile() {

        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(getDataFile());

        for (String s : dataFile.getKeys(false)) {
            for (String z : dataFile.getConfigurationSection(s).getKeys(false)) {
                PlayerEloBUILDHUC.put(UUID.fromString(z), dataFile.getInt(s + "." + z + ".BUILDUHC"));
                PlayerEloSUMO.put(UUID.fromString(z), dataFile.getInt(s + "." + z + ".SUMO"));
            }
        }
       Main.sorted = Main.sortByValues(ELOManager.PlayerEloBUILDHUC);
       Main.sorted2 = Main.sortByValues(ELOManager.PlayerEloSUMO);
    }

    public static File getDataFile() {
        return new File(Main.getMain().getDataFolder() + "/ELOData.yml");

    }

    public static boolean isExist() {
        return getDataFile().exists();
    }

    public static void saveData(UUID player) {
        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(getDataFile());
        String playerName = Bukkit.getOfflinePlayer(player).getName();
        if (PlayerEloBUILDHUC.containsKey(player)) {
            dataFile.set(playerName + "." + player.toString() + ".BUILDUHC", PlayerEloBUILDHUC.get(player));
        } else {
            dataFile.set(playerName + "." + player.toString() + ".BUILDUHC", elodefaultvalue);
        }

        if (PlayerEloSUMO.containsKey(player)) {
            dataFile.set(playerName + "." + player.toString() + ".SUMO", PlayerEloSUMO.get(player));
        } else {
            dataFile.set(playerName + "." + player.toString() + ".SUMO", elodefaultvalue);
        }
        try {

            dataFile.save(getDataFile());
        } catch (IOException e) {
        }


    }

    public static void createFile() {
        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(getDataFile());
        try {

            dataFile.save(getDataFile());
        } catch (IOException e) {
        }
    }

    public static void loadData(UUID player) {
        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(getDataFile());
        String playerName = Bukkit.getOfflinePlayer(player).getName();
        boolean override = false;
        String nn = playerName;
        if (dataFile.get(playerName + "." + player.toString() + ".BUILDUHC") == null) {
            for (String s : dataFile.getKeys(false)) {
                for (String z : dataFile.getConfigurationSection(s).getKeys(false)) {
                    if (z.equalsIgnoreCase(player.toString())) {
                        nn = s;
                        override = true;
                    }
                }
            }
            if (override) {
                PlayerEloBUILDHUC.put(player, dataFile.getInt(nn + "." + player.toString() + ".BUILDUHC"));
            } else {
                PlayerEloBUILDHUC.put(player, elodefaultvalue);
            }
        } else {
            PlayerEloBUILDHUC.put(player, dataFile.getInt(playerName + "." + player.toString() + ".BUILDUHC"));
        }

        if (dataFile.get(playerName + "." + player.toString() + ".SUMO") == null) {
            if (override) {
                PlayerEloSUMO.put(player, dataFile.getInt(nn + "." + player.toString() + ".SUMO"));
            } else {
                PlayerEloSUMO.put(player, elodefaultvalue);
            }
        } else {
            PlayerEloSUMO.put(player, dataFile.getInt(playerName + "." + player.toString() + ".SUMO"));
        }
        if (override) {

            dataFile.set(nn, null);
            try {

                dataFile.save(getDataFile());
            } catch (IOException e) {
            }
        }
    }

    public static void updateElo(UUID player, int elo, Main.ArenaType type) {
        if (Bukkit.getOfflinePlayer(player).isOnline()) {
            if (type == Main.ArenaType.NORMAL) {
                PlayerEloBUILDHUC.put(player, elo);
            } else {
                PlayerEloSUMO.put(player, elo);
            }

        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(player);
            FileConfiguration dataFile = YamlConfiguration.loadConfiguration(getDataFile());
            Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
                @Override
                public void run() {
                    if (type == Main.ArenaType.NORMAL) {
                        dataFile.set(p.getName() + "." + player.toString() + ".BUILDUHC", elo);PlayerEloBUILDHUC.put(player, elo);
                    } else {
                        dataFile.set(p.getName() + "." + player.toString() + ".SUMO", elo);PlayerEloSUMO.put(player, elo);
                    }
                    try {
                        dataFile.save(getDataFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });



        }
    }

    public static int getElo(UUID player, Main.ArenaType type) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op.isOnline()) {
            if (type == Main.ArenaType.NORMAL) {
                if (PlayerEloBUILDHUC.containsKey(player)) {
                    return PlayerEloBUILDHUC.get(player);
                } else {
                    PlayerEloBUILDHUC.put(player, elodefaultvalue);
                    return elodefaultvalue;
                }
            } else {
                if (PlayerEloSUMO.containsKey(player)) {
                    return PlayerEloSUMO.get(player);
                } else {
                    PlayerEloSUMO.put(player, elodefaultvalue);
                    return elodefaultvalue;
                }
            }
        } else {
            FileConfiguration dataFile = YamlConfiguration.loadConfiguration(getDataFile());
            String z = "BUILDUHC";
            if (type == Main.ArenaType.SUMO) {
                z = type.toString();
            }
            return dataFile.getInt(op.getName() + "." + player.toString() + "." + z);
        }
    }


    public static int calculateEloAdd(UUID winneru, UUID loseru, Main.ArenaType type) {
        int winner = getElo(winneru, type);
        int loser = getElo(loseru, type);
        if (winner < loser) {
            int distance = loser - winner;
            if (distance <= 65) {
                return 7;

            } else if (distance <= 155) {
                return 10;
            } else if (distance <= 250) {
                return 13;
            } else if (distance <= 370) {
                return 15;

            } else if (distance <= 450) {
                return 22;

            } else if (distance <= 550) {
                return 25;
            } else if (distance <= 680) {
                return 30;
            } else if (distance <= 820) {
                return 32;
            } else if (distance <= 1050) {
                return 35;
            } else if (distance <= 1200) {
                return 38;
            } else if (distance <= 1400) {
                return 42;
            } else {
                return 46;
            }
        } else {
            int distance = winner - loser;
            if (distance <= 65) {
                return 7;

            } else if (distance <= 155) {
                return 6;
            } else if (distance <= 250) {
                return 5;
            } else if (distance <= 370) {
                return 4;

            } else if (distance <= 450) {
                return 3;

            } else if (distance <= 550) {
                return 2;
            } else {
                return 1;
            }
        }

    }

    public static int calculateEloSubtract(UUID winneru, UUID loseru, Main.ArenaType type) {
        int winner = getElo(winneru, type);
        int loser = getElo(loseru, type);
        if (loser < winner) {
            int distance = winner - loser;
            if (distance <= 65) {
                return 5;

            } else if (distance <= 155) {
                return 4;
            } else if (distance <= 370) {
                return 3;
            } else if (distance <= 450) {
                return 2;

            } else {
                return 1;
            }
        } else {
            int distance = loser - winner;
            if (distance <= 65) {
                return 7;

            } else if (distance <= 155) {
                return 12;
            } else if (distance <= 250) {
                return 15;
            } else if (distance <= 370) {
                return 19;

            } else if (distance <= 450) {
                return 24;

            } else if (distance <= 550) {
                return 29;
            } else if (distance <= 680) {
                return 30;
            } else {
                return 32;
            }
        }

    }
}
