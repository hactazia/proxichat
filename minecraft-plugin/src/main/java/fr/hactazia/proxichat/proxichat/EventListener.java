package fr.hactazia.proxichat.proxichat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.Channel;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class EventListener implements Listener, PluginMessageListener {
    public ProxiChatPlugin main;

    public EventListener(ProxiChatPlugin main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        main.eventSender.SendChannels(event.getPlayer(), event.getPlayer().getGameMode());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangeGameMode(PlayerGameModeChangeEvent event) {
        main.eventSender.SendChannels(event.getPlayer(), event.getNewGameMode());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getY() == event.getTo().getY() && event.getFrom().getZ() == event.getTo().getZ())
            return;
        main.eventSender.SendPosition(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        main.eventSender.SendPlayerQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {

        main.eventSender.SendPlayerJoin(event.getPlayer());
        main.eventSender.SendChannels(event.getPlayer(), event.getPlayer().getGameMode());
        main.eventSender.SendPosition(event.getPlayer());
    }

    public void onPong() {

    }

    public void onPing() {

    }

    public void onConnect() {
        main.getLogger().info("Connected to ProxiChat server!");
    }

    public void onDisconnect() {
        main.getLogger().warning("Disconnected from ProxiChat server!");
    }

    public void onAuth(JsonObject auth) {
        var success = auth.get("success").getAsBoolean();
        if (!success) {
            var error_message = auth.get("error").getAsString();
            main.getLogger().warning("Authentication exception: " + error_message);
            return;
        }
        main.getLogger().info("Authentication success!");
        for (Player player : main.getServer().getOnlinePlayers()) {
            main.eventSender.SendPlayerJoin(player);
            main.eventSender.SendChannels(player, player.getGameMode());
            main.eventSender.SendPosition(player);
        }
    }

    public void onConnectorLink(JsonObject data) {
        var url = data.get("url").getAsString();
        var player = main.getServer().getPlayer(UUID.fromString(data.get("id").getAsString()));
        if (player == null || url == null) return;
        main.getLogger().info("Connector link: " + url + " for " + data.get("id").getAsString());
        TextComponent message = new TextComponent(main.lang.Format("click_to_link"));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(url)));
        player.spigot().sendMessage(message);
    }

    public void onNoConnector(JsonObject message) {
        main.getLogger().warning("No connector found for " + message.get("id").getAsString());
        var player = main.getServer().getPlayer(UUID.fromString(message.get("id").getAsString()));
        if (player == null) return;
        player.sendMessage(main.lang.Format("no_connector"));
        this.main.eventSender.SendMakeConnectorLink(player);
    }

    public void onPleaseAuth() {
        main.eventSender.SendAuthentification(
                main.getConfig().getString("server_token"),
                main.getConfig().getString("group"),
                main.getConfig().getString("display"),
                main.getConfig().getInt("min_distance"),
                main.getConfig().getInt("max_distance")
        );
    }

    public void onChatterConnect(JsonObject message) {
        var player = main.getServer().getPlayer(UUID.fromString(message.get("id").getAsString()));
        if (player == null) return;
        player.sendMessage(main.lang.Format("connect_to_service"));
        String logMessage = main.getConfig().getString("log_message", "");
        if (logMessage.isEmpty()) return;
        main.eventSender.SendLogMessage(player, TextComponent.fromLegacyText(logMessage));
    }

    public void onChatterDisconnect(JsonObject message) {
        var player = main.getServer().getPlayer(UUID.fromString(message.get("id").getAsString()));
        if (player == null) return;
        player.sendMessage(main.lang.Format("disconnect_from_service"));
    }

    public void onChatterData(JsonObject message) {
        var player = main.getServer().getPlayer(UUID.fromString(message.get("id").getAsString()));
        if (player == null) return;
        var event = message.get("event").getAsString();
        var data = message.get("data").getAsJsonObject();
        switch (event) {
            case "is_mute":
                var mute = data.get("mute").getAsBoolean();
                var by = data.get("by").getAsInt();
                var ir = data.has("state");
                if (by == 1 && !ir)
                    player.sendMessage(main.lang.Format(mute ? "mute_player" : "unmute_player"));
                break;
            default:
                main.getLogger().warning("Unknown chatter data event: " + event);
        }
    }

    public CompletableFuture<Boolean> isMuted(Player target) {
        var future = new CompletableFuture<Boolean>();
        var json = new JsonObject();
        var random = new Random();
        var id = new UUID(random.nextLong(), random.nextLong());
        json.addProperty("state", id.toString());
        main.eventSender.SendData(target, "get_mute", json);
        tasks.add(data -> {
            if (!data.get("type").getAsString().equals("chatter_data")) return false;
            if (!data.get("id").getAsString().equals(target.getUniqueId().toString())) return false;
            if (!data.get("event").getAsString().equals("is_mute")) return false;
            var deq = data.get("data").getAsJsonObject();
            if (!deq.has("state") || !deq.get("state").getAsString().equals(id.toString())) return false;
            future.complete(data.get("data").getAsJsonObject().get("mute").getAsBoolean());
            return true;
        });
        return future;
    }

    public List<Function<JsonObject, Boolean>> tasks = new ArrayList<>();

    public void onEvent(JsonObject data) {
        for (var i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).apply(data)) {
                tasks.remove(i);
                i--;
            }
        }
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (!s.equals("proxichat:bridge")) return;
        try {
            onJsonMessage(JsonParser.parseString(new String(bytes)).getAsJsonObject(), player);
        } catch (Exception e) {
            main.getLogger().severe("Message conversion exception: " + e.getMessage() + "\n" + new String(bytes));
            for (StackTraceElement element : e.getStackTrace()) {
                main.getLogger().severe(element.toString());
            }
        }
    }

    public void onJsonMessage(JsonObject message, Player player) {
        var type = message.get("type").getAsString();
        switch (type) {
            case "ping":
                main.eventSender.EmitPluginPong(player);
                break;
            case "get_mute":
                main.eventListener.isMuted(player).thenAccept(mute -> {
                    main.eventSender.EmitPluginIsMute(player, mute);
                });
                break;
            case "set_mute":
                var mute = message.get("mute").getAsBoolean();
                main.eventSender.SetMute(player, mute).thenAccept(success -> {
                    if (success)
                        main.eventSender.EmitPluginIsMute(player, mute);
                });
                break;
            case "get_info":
                main.eventSender.EmitPluginInfos(
                        player,
                        main.getConfig().getString("group"),
                        main.getConfig().getString("display"),
                        main.getConfig().getInt("min_distance"),
                        main.getConfig().getInt("max_distance")
                );
                break;
        }
    }
}
