package sfvv.FruskyDuel;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;

public enum Kit {
    BUILDUHC(ChatColor.RED + "BuildUHC"),
    SUMO(ChatColor.RED + "Sumo"),
    NODEBUFF(ChatColor.RED + "NoDebuff"),
    GAPPLE(ChatColor.RED + "Gapple"),
    COMBO(ChatColor.RED + "Combo"),
    IRON(ChatColor.RED + "Iron")
    ;
    private String name;
    Kit(String name){
        this.name = name;
    }
    public String getName(){return this.name;}

    public static ArrayList<ItemStack> getItemList(Kit kit){
        ArrayList<ItemStack> list = new ArrayList<>();
        if(kit == Kit.BUILDUHC){
            ItemStack blaze = new ItemStack(Material.BLAZE_ROD);
            list.add(blaze);
        }
        else if (kit == Kit.SUMO){
            ItemStack air = new ItemStack(Material.AIR);
            list.add(air);
        }else if (kit == NODEBUFF){
            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta sm = sword.getItemMeta();
            sm.addEnchant(Enchantment.DAMAGE_ALL , 2 , true);sm.addEnchant(Enchantment.FIRE_ASPECT , 2 , true);
            sm.addEnchant(Enchantment.DURABILITY , 3 , true);
            sword.setItemMeta(sm);
            ItemStack pearl = new ItemStack(Material.ENDER_PEARL , 16);
            ItemStack steak = new ItemStack (Material.COOKED_BEEF , 64);
            list.add(sword);
            list.add(pearl);
            list.add(steak);
            for(int i = 0 ; i < 4 ; i++){
                ItemStack pot = new ItemStack(Material.POTION);
                Potion p = new Potion(1);
                p.setSplash(true);
                p.setType(PotionType.INSTANT_HEAL);
                p.setLevel(2);
                p.apply(pot);
                list.add(pot);
            }
            ItemStack fs = new ItemStack(Material.POTION);
            Potion pfs = new Potion(1);
            pfs.setType(PotionType.FIRE_RESISTANCE);
            pfs.setLevel(1);
            pfs.setHasExtendedDuration(true);
            pfs.apply(fs);
            list.add(fs);
            ItemStack s = new ItemStack(Material.POTION);
            Potion ps = new Potion(1);
            ps.setType(PotionType.SPEED);
            ps.setLevel(2);
            ps.apply(s);
            list.add(s);
            for(int i = 0 ; i < 8 ; i++){
                ItemStack pot = new ItemStack(Material.POTION);
                Potion p = new Potion(1);
                p.setSplash(true);
                p.setType(PotionType.INSTANT_HEAL);
                p.setLevel(2);
                p.apply(pot);
                list.add(pot);
            }
            list.add(s);
            for(int i = 0 ; i < 8 ; i++){
                ItemStack pot = new ItemStack(Material.POTION);
                Potion p = new Potion(1);
                p.setSplash(true);
                p.setType(PotionType.INSTANT_HEAL);
                p.setLevel(2);
                p.apply(pot);
                list.add(pot);
            }
            list.add(s);
            for(int i = 0 ; i < 8 ; i++){
                ItemStack pot = new ItemStack(Material.POTION);
                Potion p = new Potion(1);
                p.setSplash(true);
                p.setType(PotionType.INSTANT_HEAL);
                p.setLevel(2);
                p.apply(pot);
                list.add(pot);
            }
            list.add(s);
            ItemStack h = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta hm = h.getItemMeta();
            hm.addEnchant(Enchantment.DURABILITY , 3 , true);
            hm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 1 , true);
            h.setItemMeta(hm);
            list.add(h);
            ItemStack c = new ItemStack(Material.DIAMOND_CHESTPLATE);
            ItemMeta cm = c.getItemMeta();
            cm.addEnchant(Enchantment.DURABILITY , 3 , true);
            cm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 1 , true);
            c.setItemMeta(cm);
            list.add(c);

            ItemStack l = new ItemStack(Material.DIAMOND_LEGGINGS);
            ItemMeta lm = h.getItemMeta();
            lm.addEnchant(Enchantment.DURABILITY , 3 , true);
            lm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 1 , true);
            l.setItemMeta(lm);
            list.add(l);

            ItemStack b = new ItemStack(Material.DIAMOND_BOOTS);
            ItemMeta bm = h.getItemMeta();
            bm.addEnchant(Enchantment.DURABILITY , 3 , true);
            bm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 1 , true);
            bm.addEnchant(Enchantment.PROTECTION_FALL , 4 , true);
            b.setItemMeta(bm);
            list.add(b);

        }else if (kit == GAPPLE){
            ItemStack s = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta sm = s.getItemMeta();
            sm.addEnchant(Enchantment.DAMAGE_ALL ,6 , true);
            sm.addEnchant(Enchantment.DURABILITY ,3 , true);
            sm.addEnchant(Enchantment.FIRE_ASPECT ,2 , true);
            s.setItemMeta(sm);
            list.add(s);

            ItemStack apple = new ItemStack(Material.GOLDEN_APPLE,64 ,(short) 1);
            list.add(apple);


            ItemStack h = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta hm = h.getItemMeta();
            hm.addEnchant(Enchantment.DURABILITY , 3 , true);
            hm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 4 , true);
            h.setItemMeta(hm);
            list.add(h);
            ItemStack c = new ItemStack(Material.DIAMOND_CHESTPLATE);
            ItemMeta cm = c.getItemMeta();
            cm.addEnchant(Enchantment.DURABILITY , 3 , true);
            cm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 4 , true);
            c.setItemMeta(cm);
            list.add(c);

            ItemStack l = new ItemStack(Material.DIAMOND_LEGGINGS);
            ItemMeta lm = h.getItemMeta();
            lm.addEnchant(Enchantment.DURABILITY , 3 , true);
            lm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 4 , true);
            l.setItemMeta(lm);
            list.add(l);

            ItemStack b = new ItemStack(Material.DIAMOND_BOOTS);
            ItemMeta bm = h.getItemMeta();
            bm.addEnchant(Enchantment.DURABILITY , 3 , true);
            bm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 4 , true);
            b.setItemMeta(bm);
            list.add(b);

            list.add(h); list.add(c); list.add(l); list.add(b);
        }else if (kit == COMBO){
            ItemStack s = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta sm = s.getItemMeta();
            sm.addEnchant(Enchantment.DAMAGE_ALL ,2 , true);
            sm.addEnchant(Enchantment.DURABILITY ,10 , true);
            sm.addEnchant(Enchantment.FIRE_ASPECT ,2 , true);
            s.setItemMeta(sm);
            list.add(s);

            ItemStack apple = new ItemStack(Material.GOLDEN_APPLE,64 ,(short) 1);
            list.add(apple);


            ItemStack h = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta hm = h.getItemMeta();
            hm.addEnchant(Enchantment.DURABILITY , 10 , true);
            hm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 4 , true);
            h.setItemMeta(hm);
            list.add(h);

            ItemStack c = new ItemStack(Material.DIAMOND_CHESTPLATE);
            ItemMeta cm = c.getItemMeta();
            cm.addEnchant(Enchantment.DURABILITY , 10 , true);
            cm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 4 , true);
            c.setItemMeta(cm);
            list.add(c);

            ItemStack l = new ItemStack(Material.DIAMOND_LEGGINGS);
            ItemMeta lm = h.getItemMeta();
            lm.addEnchant(Enchantment.DURABILITY , 10 , true);
            lm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 4 , true);
            l.setItemMeta(lm);
            list.add(l);

            ItemStack b = new ItemStack(Material.DIAMOND_BOOTS);
            ItemMeta bm = h.getItemMeta();
            bm.addEnchant(Enchantment.DURABILITY , 10 , true);
            bm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL , 4 , true);
            b.setItemMeta(bm);
            list.add(b);

            list.add(h); list.add(c); list.add(l); list.add(b);

        }else if (kit == IRON){
             ItemStack s = new ItemStack(Material.IRON_SWORD);
            ItemStack bo = new ItemStack(Material.BOW);
            ItemStack meat=  new ItemStack(Material.COOKED_BEEF,64);
            ItemStack arrow = new ItemStack(Material.ARROW ,32 );
            list.add(s);list.add(bo);list.add(meat);list.add(arrow);

            ItemStack h = new ItemStack(Material.IRON_HELMET);
            list.add(h);
            ItemStack c = new ItemStack(Material.IRON_CHESTPLATE);
            list.add(c);

            ItemStack l = new ItemStack(Material.IRON_LEGGINGS);
            list.add(l);

            ItemStack b = new ItemStack(Material.IRON_BOOTS);
            list.add(b);

        }



        return list;
    }
    public static ItemStack getIcon(Kit kit){
        Material mat;
        ItemStack is = null;
        ItemMeta im = null;
        if(kit == BUILDUHC){
            mat = Material.LAVA_BUCKET;
            is = new ItemStack(mat);
            im = is.getItemMeta();
            im.setDisplayName(kit.getName());
        }else if(kit == SUMO){
            mat = Material.LEASH;
            is = new ItemStack(mat);
            im = is.getItemMeta();
            im.setDisplayName(kit.getName());
        }else if (kit ==  GAPPLE){
            mat = Material.GOLDEN_APPLE;
            is = new ItemStack(mat, 1 , (short) 1);
            im = is.getItemMeta();
            im.setDisplayName(kit.getName());
        }else if (kit == COMBO){
            mat = Material.RAW_FISH;
            is = new ItemStack(mat ,1,(short) 3);
            im = is.getItemMeta();
            im.setDisplayName(kit.getName());


        }else if (kit == IRON){
            mat = Material.IRON_HELMET;
            is = new ItemStack(mat);
            im = is.getItemMeta();
            im.setDisplayName(kit.getName());
        }else if (kit == NODEBUFF){
            mat = Material.POTION;
            is = new ItemStack(mat);
            Potion pot = new Potion(1);
            pot.setType(PotionType.INSTANT_HEAL);
            pot.setSplash(true);
            pot.apply(is);
            im = is.getItemMeta();
            im.setDisplayName(kit.getName());
        }
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        is.setItemMeta(im);



        return is;
    }
}
