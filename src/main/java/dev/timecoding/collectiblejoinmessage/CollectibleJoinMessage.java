package dev.timecoding.collectiblejoinmessage;

import dev.timecoding.collectiblejoinmessage.api.Metrics;
import dev.timecoding.collectiblejoinmessage.command.JoinMessageCommand;
import dev.timecoding.collectiblejoinmessage.command.completer.JoinMessageCompleter;
import dev.timecoding.collectiblejoinmessage.data.ConfigHandler;
import dev.timecoding.collectiblejoinmessage.data.TagDataHandler;
import dev.timecoding.collectiblejoinmessage.listener.CJMEventListener;
import dev.timecoding.collectiblejoinmessage.listener.JoinGUIListener;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class CollectibleJoinMessage extends JavaPlugin {

    private ConfigHandler configHandler;
    private TagDataHandler dataHandler;
    private Metrics metrics;

    private HashMap<Player, Integer> messageGUI = new HashMap<>();

    @Override
    public void onEnable() {
        ConsoleCommandSender sender = this.getServer().getConsoleSender();
        this.configHandler = new ConfigHandler(this);
        this.configHandler.init();
        this.dataHandler = new TagDataHandler(this);
        this.dataHandler.init();
        if(this.configHandler.getBoolean("Enabled")) {
            sender.sendMessage("§fCollectibleJoinMessage §cv" + this.getDescription().getVersion() + " §agot enabled!");
            if (configHandler.getBoolean("bStats")) {
                this.metrics = new Metrics(this, 17501);
            }
            registerListeners();
            registerCommands();
        }else{
            sender.sendMessage("§cThe plugin got disabled, because someone disabled the plugin in the config.yml!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void registerListeners(){
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new JoinGUIListener(this), this);
        pluginManager.registerEvents(new CJMEventListener(this), this);
    }

    private void registerCommands(){
        PluginCommand command = this.getCommand("joinmessage");
        command.setExecutor(new JoinMessageCommand(this));
        command.setTabCompleter(new JoinMessageCompleter(this));
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public HashMap<Player, Integer> getMessageGUIMap() {
        return messageGUI;
    }

    public TagDataHandler getDataHandler() {
        return dataHandler;
    }
}
