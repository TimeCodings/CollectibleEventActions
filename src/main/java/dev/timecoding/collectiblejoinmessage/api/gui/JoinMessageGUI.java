package dev.timecoding.collectiblejoinmessage.api.gui;

import dev.timecoding.collectiblejoinmessage.CollectibleJoinMessage;
import dev.timecoding.collectiblejoinmessage.data.ConfigHandler;
import dev.timecoding.collectiblejoinmessage.data.TagDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JoinMessageGUI {

    private Player player;
    private CollectibleJoinMessage plugin;
    private TagDataHandler dataHandler;
    private HashMap<Player, Integer> messageMap = new HashMap<>();
    private ConfigHandler configHandler;

    private @NotNull List<Integer> sIN = new ArrayList<>();

    public JoinMessageGUI(Player player, CollectibleJoinMessage plugin){
        this.player = player;
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
        this.dataHandler = this.plugin.getDataHandler();
        this.messageMap = this.plugin.getMessageGUIMap();
        this.sIN = this.configHandler.getConfig().getIntegerList("GUI.Slots");
    }

    public void nextJoinTagSite(){
        setMessageSite((getTagSite()+1));
    }

    public void previousTagSite(){
        setMessageSite(getTagSite()-1);
    }

    public Integer getLastTagSite(){
        List<String> list = this.dataHandler.getPlayerTags(player.getUniqueId().toString());
        int general = 0;
        int site = 1;
        for(int i = list.size(); i > 0; i--){
            general++;
            if(general > this.sIN.size()){
                general = 0;
                site++;
            }
        }
        return site;
    }

    public void setMessageSite(Integer site) {
        messageMap.remove(this.player);
        messageMap.put(this.player, site);
    }

    public Integer getTagSite() {
        if (messageMap.containsKey(this.player)) {
            return messageMap.get(this.player);
        }
        return 1;
    }

    public void buildSelectedTagSite() {
        Inventory inv = Bukkit.createInventory(null, configHandler.getInteger("GUI.Size"), configHandler.getString("GUI.Title").replace("%site%", getTagSite().toString()));
        fillInventory(inv);
        List<String> list = this.dataHandler.getPlayerTags(player.getUniqueId().toString());
        List<Integer> got = new ArrayList<>();
        if(configHandler.getBoolean("GUI.SlotsEmpty")) {
            for (Integer sin : sIN) {
                inv.setItem(sin, new ItemStack(Material.AIR));
            }
        }
        if (list.size() > sIN.size() && getTagSite() > 1) {
            Integer getI = (sIN.size() * (getTagSite() - 1));
            for (int i = getI; i < list.size(); i++) {
                boolean gotb = false;
                for (Integer sin : sIN) {
                    if (!got.contains(sin) && !gotb) {
                        inv.setItem(sin, getItemToShow(list.get(i)));
                        gotb = true;
                        got.add(sin);
                    }
                }
            }
        }else if(list.size() != 0){
            for (int i = 0; i < list.size(); i++) {
                boolean gotb = false;
                for (Integer sin : sIN) {
                    if (!got.contains(sin) && !gotb) {
                        inv.setItem(sin, getItemToShow(list.get(i)));
                        gotb = true;
                        got.add(sin);
                    }
                }
            }
        }
        if(getLastTagSite() <= getTagSite()){
            inv.setItem(configHandler.getItemSlot("NoNextSite"), configHandler.readItem("Item.NoNextSite", ""));
        }else{
            inv.setItem(configHandler.getItemSlot("NextSite"), configHandler.readItem("Item.NextSite", ""));
        }
        if(getTagSite() <= 1){
            inv.setItem(configHandler.getItemSlot("NoPreviousSite"), configHandler.readItem("Item.NoPreviousSite", ""));
        }else{
            inv.setItem(configHandler.getItemSlot("PreviousSite"), configHandler.readItem("Item.PreviousSite", ""));
        }

        this.player.openInventory(inv);
    }

    private ItemStack getPlaceholder(){
        return configHandler.readItem("Item.Placeholder", "");
    }

    private void fillInventory(Inventory inv){
        for(int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, getPlaceholder());
        }
    }

    private ItemStack getItemToShow(String tag) {
        ItemStack item = dataHandler.readTagItem(tag);
        String uuid = this.player.getUniqueId().toString();
        if(dataHandler.isSelected(uuid, tag)){
            item = configHandler.getModifiedSelectedItem(item, tag);
        }
        return item;
    }

}
