package fr.hactazia.proxichat.proxichat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class ProxiChatPlugin extends JavaPlugin implements TabCompleter {

    UdpHandler udpHandler;
    UdpServer udpServer;
    EventSender eventSender;
    EventListener eventListener;
    LanguageConfig lang;
    org.bukkit.plugin.messaging.PluginMessageListenerRegistration messagelistener;
    int[] tasks;

    @Override
    public void onEnable() {
        getLogger().info("ProxiChat is starting...");
        var config = getConfig();

        config.addDefault("group", "default");
        config.addDefault("display", "MC ProxiChat Server");
        config.addDefault("min_distance", 10); // 100% volume at less than 10 blocks
        config.addDefault("max_distance", 100); // 0% volume at more than 100 blocks
        config.addDefault("server_ip", "127.0.0.1");
        config.addDefault("server_port", 3000);
        config.addDefault("server_ping", 1); // 1 second
        config.addDefault("server_timeout", 5); // 5 seconds
        config.options().copyDefaults(true);
        saveConfig();

        lang = new LanguageConfig(this);
        udpHandler = new UdpHandler(this);
        udpServer = new UdpServer(this);
        eventSender = new EventSender(this);
        eventListener = new EventListener(this);
        getServer().getPluginManager().registerEvents(eventListener, this);

        tasks = new int[2];
        tasks[0] = getServer().getScheduler().scheduleSyncRepeatingTask(this, this.udpHandler::onTick, 0L, 1L);
        tasks[1] = getServer().getScheduler().scheduleSyncRepeatingTask(this, this.udpServer::onTick, 0L, 1L);

        getLogger().info("ProxiChat configuration:");
        getLogger().info("Server Display: " + config.getString("display"));
        getLogger().info("Server Group: " + config.getString("group"));
        getLogger().info("Server Address: " + getConfig().getString("server_ip"));
        getLogger().info("Server Port: " + getConfig().getInt("server_port"));
        getLogger().info("Server Ping Interval: " + getConfig().getInt("server_ping") + "s");
        getLogger().info("Server Timeout: " + getConfig().getInt("server_timeout") + "s");
        getLogger().info("Server Min Distance: " + config.getInt("min_distance") + " blocks");
        getLogger().info("Server Max Distance: " + config.getInt("max_distance") + " blocks");

        getLogger().info("ProxiChat is ready!");

        messagelistener = this.getServer().getMessenger().registerIncomingPluginChannel(this, "proxichat:bridge", eventListener);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "proxichat:bridge");
    }

    @Override
    public void onDisable() {
        getLogger().info("ProxiChat is stopping...");
        for (var task : tasks) getServer().getScheduler().cancelTask(task);
        for (Player player : getServer().getOnlinePlayers())
            eventSender.SendPlayerQuit(player);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "proxichat:bridge");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "proxichat:bridge", eventListener);
        messagelistener = null;
        eventSender.SendClose();
        udpHandler.close();
        udpHandler = null;
        udpServer.close();
        udpServer = null;
        eventListener.tasks.clear();
        eventListener = null;
        eventSender = null;
        lang = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("pc") && !command.getName().equalsIgnoreCase("proxichat"))
            return false;

        // disable command for non-players
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.Format("player_only_command"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(lang.Format("command_usage", command.getName()));
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(lang.Format("command_list"));
            sender.sendMessage(lang.FormatWithOutPrefix("command_help", command.getName()));
            sender.sendMessage(lang.FormatWithOutPrefix("command_mute", command.getName()));
            sender.sendMessage(lang.FormatWithOutPrefix("command_unmute", command.getName()));
            sender.sendMessage(lang.FormatWithOutPrefix("command_mute_toggle", command.getName()));
            sender.sendMessage(lang.FormatWithOutPrefix("command_link", command.getName()));
            sender.sendMessage(lang.FormatWithOutPrefix("command_mute_status", command.getName()));
            return true;
        }

        if (args[0].equalsIgnoreCase("mute")) {
            Player target;
            if (args.length < 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(lang.Format("command_mute_usage", command.getName()));
                    return true;
                } else target = (Player) sender;
            } else {
                if (!sender.isOp()) {
                    sender.sendMessage(lang.Format("no_permission"));
                    return true;
                }
                target = getServer().getPlayer(args[1]);
            }
            if (target == null) {
                sender.sendMessage(lang.Format("no_player"));
                return true;
            }

            eventSender.SetMute(target, true).thenAccept(success -> {
                if (success) {
                    if (sender != target)
                        sender.sendMessage(lang.Format("player_muted", target.getName()));
                    target.sendMessage(lang.Format("mute_player"));
                } else {
                    sender.sendMessage(lang.Format("mute_failed", target.getName()));
                }
            });
            return true;
        } else if (args[0].equalsIgnoreCase("unmute")) {
            Player target;
            if (args.length < 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(lang.Format("command_unmute_usage", command.getName()));
                    return true;
                } else target = (Player) sender;
            } else {
                if (!sender.isOp()) {
                    sender.sendMessage(lang.Format("no_permission"));
                    return true;
                }
                target = getServer().getPlayer(args[1]);
            }
            if (target == null) {
                sender.sendMessage(lang.Format("no_player"));
                return true;
            }

            eventSender.SetMute(target, false).thenAccept(success -> {
                if (success) {
                    if (sender != target)
                        sender.sendMessage(lang.Format("player_unmuted", target.getName()));
                    target.sendMessage(lang.Format("unmute_player"));
                } else {
                    sender.sendMessage(lang.Format("unmute_failed", target.getName()));
                }
            });
            return true;
        } else if (args[0].equalsIgnoreCase("mutetoggle")) {
            Player target;
            if (args.length < 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(lang.Format("command_mute_toggle_usage", command.getName()));
                    return true;
                } else target = (Player) sender;
            } else {
                if (!sender.isOp()) {
                    sender.sendMessage(lang.Format("no_permission"));
                    return true;
                }
                target = getServer().getPlayer(args[1]);
            }
            if (target == null) {
                sender.sendMessage(lang.Format("no_player"));
                return true;
            }

            eventListener.isMuted(target).thenAccept(mute -> {
                eventSender.SetMute(target, !mute).thenAccept(success -> {
                    if (success) {
                        if (sender != target)
                            sender.sendMessage(lang.Format(mute ? "player_unmuted" : "player_muted", target.getName()));
                        target.sendMessage(lang.Format(mute ? "unmute_player" : "mute_player"));
                    } else {
                        sender.sendMessage(lang.Format(mute ? "unmute_failed" : "mute_failed", target.getName()));
                    }
                });
            });

            return true;
        } else if (args[0].equalsIgnoreCase("link")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(lang.Format("no_player"));
                return true;
            }
            eventSender.SendMakeConnectorLink((Player) sender);
            return true;
        } else if (args[0].equalsIgnoreCase("mutestatus")) {
            if (args.length < 2) {
                sender.sendMessage(lang.Format("command_mute_status_usage", command.getName()));
                return true;
            }
            Player target = getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(lang.Format("no_player"));
                return true;
            }
            eventListener.isMuted(target).thenAccept(mute -> {
                sender.sendMessage(lang.Format(mute ? "player_muted_status" : "player_unmuted_status", target.getName()));
            });
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            var list = new ArrayList<String>();
            list.add("help");
            list.add("mute");
            list.add("unmute");
            list.add("mutetoggle");
            list.add("link");
            list.add("mutestatus");
            return list;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("mute")
                    || args[0].equalsIgnoreCase("unmute")
                    || args[0].equalsIgnoreCase("mutetoggle")
                    || args[0].equalsIgnoreCase("mutestatus")
            ) {
                var list = new ArrayList<String>();
                for (var player : getServer().getOnlinePlayers()) {
                    list.add(player.getName());
                }
                return list;
            }
        }
        return null;
    }


}
