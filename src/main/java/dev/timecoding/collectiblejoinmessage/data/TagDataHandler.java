package dev.timecoding.collectiblejoinmessage.data;

import dev.timecoding.collectiblejoinmessage.CollectibleJoinMessage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TagDataHandler {

    private CollectibleJoinMessage plugin;
    private ConfigHandler configHandler;

    public TagDataHandler(CollectibleJoinMessage plugin) {
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
    }

    private File file = null;
    public YamlConfiguration cfg = null;

    public void init() {
        file = new File(plugin.getDataFolder(), "datas.yml");
        if(!file.exists()){
            plugin.saveResource("datas.yml", false);
        }
        cfg = YamlConfiguration.loadConfiguration(file);
        cfg.options().copyDefaults(true);
    }

    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration getConfig(){
        return cfg;
    }

    public boolean tagExists(String name){
        if(keyExists("Tags."+name.toUpperCase())){
            return true;
        }
        return false;
    }

    public void addTag(String name, String message){
        if(!tagExists(name)) {
            setString("Tags." + name.toUpperCase(), message);
            for (String key : cfg.getValues(true).keySet()) {
                if (key.startsWith("Item.DefaultTagItem")) {
                    cfg.set("Item.TagBlocks."+name+key.replace("Item.DefaultTagItem", ""), cfg.getValues(true).get(key));
                    save();
                }
            }
        }
    }

    public String getTagMessage(String tagName){
        return getString("Tags."+tagName.toUpperCase()).replace("&", "§");
    }

    public void updateTag(String name, String message){
        addTag(name, message);
    }

    public void deleteTag(String name){
        setString("Tag."+name.toUpperCase(), null);
        for(String playerUUID : getPlayersWithTags()){
            if(getPlayerTags(playerUUID).contains(name.toUpperCase())){
                removeTagFromPlayer(playerUUID, name.toUpperCase());
                if(hasSelectedTag(playerUUID) && getSelectedTag(playerUUID).equalsIgnoreCase(name.toUpperCase())){
                    forceDeleteSelectedTag(playerUUID);
                }
            }
        }
    }

    private void forceDeleteSelectedTag(String uuid){
        cfg.set("Selected."+uuid, null);
        save();
    }

    public void setSelectedTag(String uuid, String tag){
        setString("Selected."+uuid, tag);
    }

    public boolean hasSelectedTag(String uuid){
        if(keyExists("Selected."+uuid)){
            return true;
        }
        return false;
    }

    public String getSelectedTag(String uuid){
        if(hasSelectedTag(uuid)){
            return getString("Selected."+uuid);
        }
        return null;
    }

    public ItemStack readTagItem(String tag){
        ItemStack defaultItem = this.configHandler.readItem("Item.DefaultTagItem", tag);
        if(configHandler.keyExists("Item.TagBlocks."+tag)){
            return this.configHandler.readItem("Item.TagBlocks."+tag, tag);
        }
        return defaultItem;
    }

    public List<String> getPlayersWithTags(){
        List<String> playerList = new ArrayList<>();
        for(String base : cfg.getValues(true).keySet()){
            if(base.startsWith("Players.")){
                playerList.add(base.replace("Players.", ""));
            }
        }
        return playerList;
    }

    public boolean isSelected(String uuid, String tag){
        if(tag.equalsIgnoreCase(getSelectedTag(uuid))){
            return true;
        }
        return false;
    }

    public List<String> getAllTags(){
        List<String> playerList = new ArrayList<>();
        for(String base : cfg.getValues(true).keySet()){
            if(base.startsWith("Tags.")){
                playerList.add(base.replace("Tags.", ""));
            }
        }
        return playerList;
    }

    public List<String> getPlayerTags(String uuid){
        if(keyExists("Players."+uuid)){
            return cfg.getStringList("Players."+uuid);
        }
        return new ArrayList<>();
    }

    public void addTagToPlayer(String uuid, String tagName){
        List<String> tagList = getPlayerTags(uuid);
        if(tagExists(tagName) && !hasTag(uuid, tagName)){
            tagList.add(tagName.toUpperCase());
            cfg.set("Players."+uuid, tagList);
            save();
        }
    }

    public boolean hasTag(String uuid, String tagName){
        List<String> tagList = getPlayerTags(uuid);
        if(tagList.contains(tagName.toUpperCase())){
            return true;
        }
        return false;
    }

    public void removeTagFromPlayer(String uuid, String tagName){
        List<String> tagList = getPlayerTags(uuid);
        if(tagExists(tagName) && hasTag(uuid, tagName)){
            tagList.remove(tagName.toUpperCase());
            cfg.set("Players."+uuid, tagList);
            save();
        }
    }

    public void setString(String key, String value){
        cfg.set(key, value);
        save();
    }

    public Integer getInteger(String key){
        if(keyExists(key)){
            return cfg.getInt(key);
        }
        return null;
    }

    public String getString(String key){
        if(keyExists(key)){
            return ChatColor.translateAlternateColorCodes('&', cfg.getString(key));
        }
        return null;
    }

    public Boolean getBoolean(String key){
        if(keyExists(key)){
            return cfg.getBoolean(key);
        }
        return false;
    }

    public boolean keyExists(String key){
        if(cfg.get(key) != null){
            return true;
        }
        return false;
    }
}
