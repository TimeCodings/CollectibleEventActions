package dev.timecoding.collectiblejoinmessage.command;

import dev.timecoding.collectiblejoinmessage.CollectibleJoinMessage;
import dev.timecoding.collectiblejoinmessage.api.gui.JoinMessageGUI;
import dev.timecoding.collectiblejoinmessage.data.ConfigHandler;
import dev.timecoding.collectiblejoinmessage.data.TagDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class JoinMessageCommand implements CommandExecutor {

    private CollectibleJoinMessage plugin;
    private ConfigHandler configHandler;
    private TagDataHandler dataHandler;

    public JoinMessageCommand(CollectibleJoinMessage plugin){
        this.plugin = plugin;
        this.configHandler = this.plugin.getConfigHandler();
        this.dataHandler = this.plugin.getDataHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String noPermission = configHandler.getString("Messages.NoPermission");
        String wrongSyntax = configHandler.getString("Messages.WrongSyntax");
        String onlyForPlayers = configHandler.getString("Messages.OnlyPlayers");
        if(args.length >= 1) {
            String first = args[0];
            if (first.equalsIgnoreCase("apply")) {
                if (args.length == 2) {
                    String second = args[1];
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        String playerUUID = player.getUniqueId().toString();
                        boolean permissionEnabled = configHandler.getBoolean("Command.apply.Permission.Enabled");
                        String permission = getPermission("apply.Permission");
                        if (permissionEnabled && sender.hasPermission(permission) || !permissionEnabled) {
                            if (dataHandler.hasTag(playerUUID, second)) {
                                String tagMessage = dataHandler.getTagMessage(second);
                                dataHandler.setSelectedTag(playerUUID, second);
                                sender.sendMessage(replace(getMessage("apply", "MessageSuccess"), second, tagMessage));
                            } else {
                                sender.sendMessage(getMessage("apply", "MessageError"));
                            }
                        }
                    } else {
                        sender.sendMessage(onlyForPlayers);
                    }
                } else if (args.length == 3) {
                    if (sender.hasPermission(getPermission("apply.Others"))) {
                        String second = args[1];
                        String third = args[2];
                        if (playerExists(third)) {
                            String uuid = getUUIDbyName(third);
                            if (dataHandler.hasTag(uuid, second)) {
                                String tagMessage = dataHandler.getTagMessage(second);
                                dataHandler.setSelectedTag(uuid, second);
                                sender.sendMessage(replace(getMessage("apply.Others", "MessageSuccess"), second, tagMessage).replace("%player%", third));
                                if (dataHandler.getBoolean("Command.apply.Others.Player.Enabled")) {
                                    OfflinePlayer player = Bukkit.getOfflinePlayer(third);
                                    if (player.isOnline()) {
                                        player.getPlayer().sendMessage(replace(dataHandler.getString("Command.apply.Others.Player.Message"), second, tagMessage));
                                    }
                                }
                            } else {
                                sender.sendMessage(getMessage("apply.Others", "MessageError"));
                            }
                        } else {
                            sender.sendMessage(getMessage("apply.Others", "MessageError2"));
                        }
                    }
                } else {
                    sender.sendMessage(wrongSyntax);
                }

            } else if (first.equalsIgnoreCase("add") && args.length == 3) {
                if (sender.hasPermission(getPermission("add"))) {
                    String second = args[1];
                    String third = args[2];
                    if (dataHandler.tagExists(second)) {
                        if (playerExists(third)) {
                            String uuid = getUUIDbyName(third);
                            if (!dataHandler.hasTag(uuid, second)) {
                                String tagMessage = dataHandler.getTagMessage(second);
                                dataHandler.addTagToPlayer(uuid, second);
                                sender.sendMessage(replace(getMessage("add", "MessageSuccess"), second, tagMessage).replace("%player%", third));
                                if (dataHandler.getBoolean("Command.add.Player.Enabled")) {
                                    OfflinePlayer player = Bukkit.getOfflinePlayer(third);
                                    if (player.isOnline()) {
                                        player.getPlayer().sendMessage(replace(dataHandler.getString("Command.add.Player.Message"), second, tagMessage));
                                    }
                                }
                            } else {
                                sender.sendMessage(getMessage("add", "MessageError3"));
                            }
                        } else {
                            sender.sendMessage(getMessage("add", "MessageError2"));
                        }
                    } else {
                        sender.sendMessage(getMessage("add", "MessageError1"));
                    }
                } else {
                    sender.sendMessage(noPermission);
                }
            } else if (args.length == 2) {
                String second = args[1];
                if (first.equalsIgnoreCase("delete")) {
                    if (sender.hasPermission(getPermission("delete"))) {
                        if (dataHandler.tagExists(second)) {
                            String tagMessage = dataHandler.getTagMessage(second);
                            dataHandler.deleteTag(second);
                            sender.sendMessage(replace(getMessage("create", "MessageSuccess"), second, tagMessage));
                        } else {
                            sender.sendMessage(getMessage("create", "MessageError"));
                        }
                    } else {
                        sender.sendMessage(noPermission);
                    }
                } else {
                    sender.sendMessage(wrongSyntax);
                }
            } else if (first.equalsIgnoreCase("remove") && args.length == 3) {
                String second = args[1];
                String third = args[2];
                if (sender.hasPermission(getPermission("remove"))) {
                    if (dataHandler.tagExists(second)) {
                        if (playerExists(third)) {
                            String uuid = getUUIDbyName(third);
                            if (dataHandler.hasTag(uuid, second)) {
                                String tagMessage = dataHandler.getTagMessage(second);
                                dataHandler.removeTagFromPlayer(uuid, second);
                                sender.sendMessage(replace(getMessage("remove", "MessageSuccess"), second, tagMessage).replace("%player%", third));
                                if (dataHandler.getBoolean("Command.remove.Player.Enabled")) {
                                    OfflinePlayer player = Bukkit.getOfflinePlayer(third);
                                    if (player.isOnline()) {
                                        player.getPlayer().sendMessage(replace(dataHandler.getString("Command.remove.Player.Message"), second, tagMessage));
                                    }
                                }
                            } else {
                                sender.sendMessage(getMessage("remove", "MessageError3"));
                            }
                        } else {
                            sender.sendMessage(getMessage("remove", "MessageError2"));
                        }
                    } else {
                        sender.sendMessage(getMessage("remove", "MessageError1"));
                    }
                } else {
                    sender.sendMessage(noPermission);
                }
            } else if (args.length >= 3) {
                ArrayList<String> values = new ArrayList<>(Arrays.asList(args));
                values.remove(0);
                values.remove(0);
                String tagMessage = "";
                for (int i = 0; i < values.size(); i++) {
                    tagMessage = tagMessage + values.get(i) + " ";
                }
                tagMessage = tagMessage.substring(0, tagMessage.length()-1);
                first = args[0];
                String second = args[1];
                if (first.equalsIgnoreCase("create")) {
                    if (sender.hasPermission(getPermission("create"))) {
                        if (!dataHandler.tagExists(second)) {
                            dataHandler.addTag(second, tagMessage);
                            sender.sendMessage(replace(getMessage("create", "MessageSuccess"), second, tagMessage));
                        } else {
                            sender.sendMessage(getMessage("create", "MessageError"));
                        }
                    } else {
                        sender.sendMessage(noPermission);
                    }
                } else {
                    sender.sendMessage(wrongSyntax);
                }
            } else if(args.length == 1 && first.equalsIgnoreCase("gui")){
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    JoinMessageGUI guiBuilder = new JoinMessageGUI(player, plugin);
                    guiBuilder.buildSelectedTagSite();
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 2, 2);
                }else{
                    sender.sendMessage(onlyForPlayers);
                }
            }else{
                sender.sendMessage(wrongSyntax);
            }
        }else {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                JoinMessageGUI guiBuilder = new JoinMessageGUI((Player) sender, plugin);
                if (configHandler.getBoolean("OpenGUIWhenOnlyBase")) {
                    guiBuilder.buildSelectedTagSite();
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 2, 2);
                } else {
                    sender.sendMessage(wrongSyntax);
                }
            }else {
                sender.sendMessage(wrongSyntax);
            }
        }
        return false;
    }

    private String replace(String message, String tagName, String tagMessage){
        return message.replace("%tag%", tagName).replace("%message%", tagMessage);
    }

    public String getMessage(String subCommand, String option){
        String commandBase = "Command."+subCommand+"."+option;
        if(configHandler.keyExists(commandBase)){
            return configHandler.getString(commandBase);
        }
        return null;
    }

    public String getPermission(String subCommand){
        String commandBase = "Command."+subCommand+".Permission";
        if(configHandler.keyExists(commandBase)){
            return configHandler.getString(commandBase);
        }
        return null;
    }

    public boolean playerExists(String name){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if(offlinePlayer == null || !offlinePlayer.hasPlayedBefore()){
            return false;
        }
        return true;
    }

    public String getUUIDbyName(String name){
        if(playerExists(name)){
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            return offlinePlayer.getUniqueId().toString();
        }
        return null;
    }
}
