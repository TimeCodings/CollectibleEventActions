package dev.timecoding.collectiblejoinmessage.listener;

import dev.timecoding.collectiblejoinmessage.CollectibleJoinMessage;
import dev.timecoding.collectiblejoinmessage.api.gui.JoinMessageGUI;
import dev.timecoding.collectiblejoinmessage.data.ConfigHandler;
import dev.timecoding.collectiblejoinmessage.data.TagDataHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class JoinGUIListener implements Listener {

    private CollectibleJoinMessage plugin;
    private ConfigHandler configHandler;
    private TagDataHandler dataHandler;

    public JoinGUIListener(CollectibleJoinMessage plugin){
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
        this.dataHandler = this.plugin.getDataHandler();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory() != null && e.getCurrentItem() != null){
            Player player = (Player) e.getWhoClicked();
            String title = e.getView().getTitle();
            JoinMessageGUI messageGUI = new JoinMessageGUI(player, this.plugin);
            if(title.equalsIgnoreCase(this.configHandler.getString("GUI.Title").replace("%site%", messageGUI.getTagSite().toString()))){
                e.setCancelled(true);
                ItemStack currentItem = e.getCurrentItem();
                if(isSameItem(currentItem, "NextSite")){
                    messageGUI.nextJoinTagSite();
                    messageGUI.buildSelectedTagSite();
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 2);
                }else if(isSameItem(currentItem, "PreviousSite")){
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 2);
                    messageGUI.previousTagSite();
                    messageGUI.buildSelectedTagSite();
                }else{
                    String uuid = player.getUniqueId().toString();
                    String foundTag = null;
                    for(String tag : this.dataHandler.getPlayerTags(uuid)){
                        ItemStack item = this.dataHandler.readTagItem(tag);
                        if(item.equals(currentItem)){
                            foundTag = tag;
                        }
                    }
                    if(foundTag != null){
                        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 2, 2);
                        this.dataHandler.setSelectedTag(uuid, foundTag);
                        messageGUI.buildSelectedTagSite();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        Player player = (Player) e.getPlayer();
        if(e.getReason() != InventoryCloseEvent.Reason.OPEN_NEW){
            if(configHandler.getBoolean("ResetGUISiteOnClose")){
                JoinMessageGUI gui = new JoinMessageGUI(player, plugin);
                gui.setMessageSite(1);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 2, 2);
            }
        }
    }

    private boolean isSameItem(ItemStack itemStack, String key){
        key = "Item."+key;
        ItemStack finalIs = configHandler.readItem(key, "");
        if(finalIs.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName()) && finalIs.getType().equals(itemStack.getType())){
            return true;
        }
        return false;
    }

}
