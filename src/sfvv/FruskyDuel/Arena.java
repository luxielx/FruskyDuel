package sfvv.FruskyDuel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by ADMIN on 6/1/2018.
 */
public class Arena {
    String name;
    Location loc1;
    Location loc2;
    Main.ArenaType type;
    Material icon;
    boolean using;

    public Arena(String name) {
        this.name = name;
        this.loc1 = null;
        this.loc2 = null;
        this.type = null;
        this.icon = null;
        this.using = false;
    }
    public boolean isUsing(){ return this.using; }
    public void setUsing(boolean a){  this.using = a ;}

    public String getName() {
        return this.name;
    }

    public Location getLoc1() {
        return this.loc1;
    }

    public Location getLoc2() {
        return this.loc2;
    }

    public Main.ArenaType getType() {
        return this.type;
    }

    public Material getIcon() {
        return this.icon;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setLoc1(Location loc) {
        this.loc1 = loc;
    }

    public void setLoc2(Location loc) {
        this.loc2 = loc;
    }

    public void setType(Main.ArenaType type) {
        this.type = type;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }


    public boolean ready() {
        return (loc1 != null && loc2 != null);
    }

    public File getDataFile() {
        return new File(Main.getMain().getDataFolder() +"/Arena/" +this.name + ".yml");

    }

    public boolean isExist() {
        return this.getDataFile().exists();
    }

    public void saveData() {

        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(this.getDataFile());
        dataFile.set("Arena.Name", this.name);

        dataFile.set("Arena.first.x", this.loc1.getX());
        dataFile.set("Arena.first.y", this.loc1.getY());
        dataFile.set("Arena.first.z", this.loc1.getZ());
        dataFile.set("Arena.first.yaw", this.loc1.getYaw());
        dataFile.set("Arena.first.pitch", this.loc1.getPitch());

        dataFile.set("Arena.first.world", this.loc1.getWorld().getName());

        dataFile.set("Arena.second.x", this.loc2.getX());
        dataFile.set("Arena.second.y", this.loc2.getY());
        dataFile.set("Arena.second.z", this.loc2.getZ());
        dataFile.set("Arena.second.yaw", this.loc2.getYaw());
        dataFile.set("Arena.second.pitch", this.loc2.getPitch());
        dataFile.set("Arena.second.world", this.loc2.getWorld().getName());

        dataFile.set("Arena.Type", this.type.toString());
        dataFile.set("Arena.Icon", this.icon.toString());
        try {
            dataFile.save(getDataFile());
        } catch (IOException e) {
        }


    }

    public void loadData() {
        FileConfiguration dataFile = YamlConfiguration.loadConfiguration(this.getDataFile());
        this.name = dataFile.getString("Arena.Name");
        this.loc1 = new Location(Bukkit.getWorld(dataFile.getString("Arena.first.world")), dataFile.getDouble("Arena.first.x"), dataFile.getDouble("Arena.first.y"),
                dataFile.getDouble("Arena.first.z"),(float)dataFile.getDouble("Arena.first.yaw"),(float)dataFile.getDouble("Arena.first.pitch"));
        this.loc2 = new Location(Bukkit.getWorld(dataFile.getString("Arena.second.world")), dataFile.getDouble("Arena.second.x"), dataFile.getDouble("Arena.second.y"),
                dataFile.getDouble("Arena.second.z"),(float)dataFile.getDouble("Arena.second.yaw"),(float)dataFile.getDouble("Arena.second.pitch"));

        this.type = Main.ArenaType.valueOf(  dataFile.getString("Arena.Type"));
        this.icon = Material.valueOf(  dataFile.getString("Arena.Icon"));
        Main.getMain().getLogger().info("Arena " + this.name + " loaded");
    }
    public void remove(){
         getDataFile().delete();


    }

}
