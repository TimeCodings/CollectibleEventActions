package dev.timecoding.collectiblejoinmessage.listener;

import dev.timecoding.collectiblejoinmessage.CollectibleJoinMessage;
import dev.timecoding.collectiblejoinmessage.data.ConfigHandler;
import dev.timecoding.collectiblejoinmessage.data.TagDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CJMEventListener implements Listener {

    private CollectibleJoinMessage plugin;
    private TagDataHandler tagDataHandler;
    private ConfigHandler configHandler;

    public CJMEventListener(CollectibleJoinMessage plugin){
        this.plugin = plugin;
        this.tagDataHandler = this.plugin.getDataHandler();
        this.configHandler = this.plugin.getConfigHandler();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        String uuid = player.getUniqueId().toString();
        if(configHandler.getBoolean("BlockAllDefaultJoinMessages")){
            e.setJoinMessage("");
        }
        if(tagDataHandler.hasSelectedTag(uuid)){
            String selectedTag = tagDataHandler.getSelectedTag(uuid);
            String playerName = player.getName();
            e.setJoinMessage(tagDataHandler.getTagMessage(selectedTag).replace("%player%", playerName).replace("%uuid%", uuid));
        }
    }

}
