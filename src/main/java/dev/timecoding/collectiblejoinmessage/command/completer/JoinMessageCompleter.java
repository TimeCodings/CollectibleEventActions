package dev.timecoding.collectiblejoinmessage.command.completer;

import dev.timecoding.collectiblejoinmessage.CollectibleJoinMessage;
import dev.timecoding.collectiblejoinmessage.data.ConfigHandler;
import dev.timecoding.collectiblejoinmessage.data.TagDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JoinMessageCompleter implements TabCompleter {

    private CollectibleJoinMessage plugin;
    private ConfigHandler configHandler;
    private TagDataHandler dataHandler;

    public JoinMessageCompleter(CollectibleJoinMessage plugin){
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
        this.dataHandler = this.plugin.getDataHandler();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completerList = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("joinmessage") || command.getName().equalsIgnoreCase("joinmsg")){
            String first = "";
            if(args.length >= 1){
                first = args[0];
            }
            if(sender.hasPermission(getPermission("create"))){
                if(args.length == 1){
                    completerList.add("create");
                }
                if(first.equalsIgnoreCase("create")) {
                    if (args.length == 2) {
                        completerList.add("TAGNAME");
                    } else if (args.length == 3) {
                        completerList.add("MESSAGE");
                    }
                }
            }
            if(sender.hasPermission(getPermission("delete"))){
                if(args.length == 1){
                    completerList.add("delete");
                }else if(args.length == 2 && first.equalsIgnoreCase("delete")){
                    for(String tags : dataHandler.getAllTags()){
                        completerList.add(tags);
                    }
                }
            }
            if(sender.hasPermission(getPermission("add"))){
                if(args.length == 1){
                    completerList.add("add");
                }
                if(first.equalsIgnoreCase("add")) {
                    if (args.length == 2) {
                        for (String tags : dataHandler.getAllTags()) {
                            completerList.add(tags);
                        }
                    } else if (args.length == 3) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            completerList.add(player.getName());
                        }
                    }
                }
            }
            if(sender.hasPermission(getPermission("remove"))){
                if(args.length == 1){
                    completerList.add("remove");
                }
                if(first.equalsIgnoreCase("remove")) {
                    if (args.length == 2) {
                        for (String tags : dataHandler.getAllTags()) {
                            completerList.add(tags);
                        }
                    } else if (args.length == 3) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            completerList.add(player.getName());
                        }
                    }
                }
            }
            boolean applyPermissionEnabled = configHandler.getBoolean("Command.apply.Permission.Enabled");
            String applyPermission = getPermission("apply.Permission");
            if(applyPermissionEnabled && sender.hasPermission(applyPermission) || !applyPermissionEnabled){
                if(args.length == 1){
                    completerList.add("apply");
                }
                if(first.equalsIgnoreCase("apply")) {
                    if (args.length == 2) {
                        if (sender instanceof Player) {
                            for (String tags : dataHandler.getPlayerTags(((Player) sender).getUniqueId().toString())) {
                                completerList.add(tags);
                            }
                        } else {
                            for (String tags : dataHandler.getAllTags()) {
                                completerList.add(tags);
                            }
                        }
                    } else if (args.length == 3 && sender.hasPermission("apply.Others")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            completerList.add(player.getName());
                        }
                    }
                }
            }

            boolean guiPermissionEnabled = configHandler.getBoolean("Command.apply.Permission.Enabled");
            String guiPermission = getPermission("apply.Permission");
            if(guiPermissionEnabled && sender.hasPermission(applyPermission) || !guiPermissionEnabled){
                if(args.length == 1) {
                    completerList.add("gui");
                }
            }
        }
        return completerList;
    }

    public String getPermission(String subCommand){
        String commandBase = "Command."+subCommand+".Permission";
        if(configHandler.keyExists(commandBase)){
            return configHandler.getString(commandBase);
        }
        return null;
    }

}
