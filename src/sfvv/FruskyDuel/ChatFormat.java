package sfvv.FruskyDuel;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by ADMIN on 7/1/2018.
 */

public class ChatFormat implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        int belo = ELOManager.getElo(player.getUniqueId(), Main.ArenaType.NORMAL);
        int selo = ELOManager.getElo(player.getUniqueId(), Main.ArenaType.SUMO);
        String mss = e.getFormat().replace("{builduhcelo}",""+belo).replace("{sumoelo}",""+selo).replace("{elo}" , "" + ((belo +selo) /2));
        e.setFormat(mss);
    }

}
