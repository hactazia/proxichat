package fr.hactazia.proxichat.proxichat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class EventSender {
    public ProxiChatPlugin main;

    public EventSender(ProxiChatPlugin main) {
        this.main = main;
    }

    public void SendPlayerJoin(Player player) {
        var json = new JsonObject();
        json.addProperty("type", "join");
        json.addProperty("id", player.getUniqueId().toString());
        json.addProperty("name", player.getDisplayName());
        json.addProperty("avatar", "https://cravatar.eu/helmavatar/" + player.getUniqueId() + "/64.png");
        main.udpHandler.send(json);
    }

    public void SendChannels(Player player, GameMode gameMode) {
        var json = new JsonObject();
        json.addProperty("type", "channels");
        json.addProperty("id", player.getUniqueId().toString());
        var channels = new JsonArray();
        channels.add(Objects.requireNonNull(player.getPlayer()).getWorld().getName() + "_" + gameMode.toString());
        json.add("channels", channels);
        main.udpHandler.send(json);
    }

    public void SendAuthentification(String token, String group, String display, int minDistance, int maxDistance) {
        var json = new JsonObject();
        json.addProperty("type", "auth");
        json.addProperty("link", "minecraft");
        json.addProperty("group", group);
        json.addProperty("display", display);
        json.addProperty("min_distance", minDistance);
        json.addProperty("max_distance", maxDistance);
        json.addProperty("token", token);

        var obj = new JsonObject();
        for (World world : main.getServer().getWorlds()) {
            var lc = new JsonArray();
            var ls = new JsonArray();
            var lp = new JsonArray();
            var la = new JsonArray();

            // creative send to creative, survival, spectator and adventure
            lc.add(world.getName() + "_" + GameMode.CREATIVE);
            lc.add(world.getName() + "_" + GameMode.SURVIVAL);
            lc.add(world.getName() + "_" + GameMode.SPECTATOR);
            lc.add(world.getName() + "_" + GameMode.ADVENTURE);

            // survival send to survival, creative, spectator and adventure
            ls.add(world.getName() + "_" + GameMode.SURVIVAL);
            ls.add(world.getName() + "_" + GameMode.CREATIVE);
            ls.add(world.getName() + "_" + GameMode.ADVENTURE);
            ls.add(world.getName() + "_" + GameMode.SPECTATOR);

            // spectator send to spectator, survival, creative and adventure
            lp.add(world.getName() + "_" + GameMode.SPECTATOR);
            lp.add(world.getName() + "_" + GameMode.CREATIVE);

            // adventure send to adventure, survival, spectator and creative
            la.add(world.getName() + "_" + GameMode.ADVENTURE);
            la.add(world.getName() + "_" + GameMode.SURVIVAL);
            la.add(world.getName() + "_" + GameMode.CREATIVE);
            la.add(world.getName() + "_" + GameMode.SPECTATOR);

            obj.add(world.getName() + "_" + GameMode.CREATIVE, lc);
            obj.add(world.getName() + "_" + GameMode.SURVIVAL, ls);
            obj.add(world.getName() + "_" + GameMode.SPECTATOR, lp);
            obj.add(world.getName() + "_" + GameMode.ADVENTURE, la);
        }
        json.add("relations", obj);
        main.udpHandler.send(json);
    }

    public void SendClose() {
        var json = new JsonObject();
        json.addProperty("type", "close");
        main.udpHandler.send(json);
    }


    public void SendPosition(Player player) {
        var x = player.getLocation().getX();
        var y = player.getLocation().getY();
        var z = player.getLocation().getZ();
        var json = new JsonObject();
        json.addProperty("type", "position");
        json.addProperty("id", player.getUniqueId().toString());
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);
        main.udpHandler.send(json);
    }

    public void SendPlayerQuit(Player player) {

        var json = new JsonObject();
        json.addProperty("type", "quit");
        json.addProperty("id", player.getUniqueId().toString());
        main.udpHandler.send(json);
    }

    public void SendMakeConnectorLink(Player player) {
        var json = new JsonObject();
        json.addProperty("type", "make_connector_link");
        json.addProperty("id", player.getUniqueId().toString());
        main.udpHandler.send(json);
    }

    public void SendData(Player player, String event, JsonObject data) {
        var json = new JsonObject();
        json.addProperty("type", "chatter_data");
        json.addProperty("id", player.getUniqueId().toString());
        json.addProperty("event", event);
        json.add("data", data);
        main.udpHandler.send(json);
    }

    public CompletableFuture<Boolean> SetMute(Player player, boolean mute) {
        var future = new CompletableFuture<Boolean>();
        var json = new JsonObject();
        json.addProperty("mute", mute);
        SendData(player, "set_mute", json);
        main.eventListener.isMuted(player).thenAccept(mute1 -> future.complete(mute1 == mute));
        return future;
    }

    public void SendLogMessage(Player player, String message) {
        var json = new JsonObject();
        json.addProperty("type", "log");
        json.addProperty("message", message);
        SendData(player, "log", json);
    }

    public void SendLogMessage(Player player, JsonObject object) {
        var json = new JsonObject();
        json.addProperty("type", "log");
        json.add("message", object);
        SendData(player, "log", json);
    }

    public void SendLogMessage(Player player, JsonArray array) {
        var json = new JsonObject();
        json.addProperty("type", "log");
        json.add("message", array);
        SendData(player, "log", json);
    }

    public void SendLogMessage(Player player, BaseComponent component) {
        var text = new JsonObject();
        text.addProperty("text", component.toPlainText());
        text.addProperty("color", component.getColor().getName());
        text.addProperty("bold", component.isBold());
        text.addProperty("italic", component.isItalic());
        text.addProperty("underlined", component.isUnderlined());
        text.addProperty("strikethrough", component.isStrikethrough());
        text.addProperty("obfuscated", component.isObfuscated());
        SendLogMessage(player, text);
    }

    public void SendLogMessage(Player player, BaseComponent... components) {
        var array = new JsonArray();
        for (BaseComponent component : components) {
            var text = new JsonObject();
            text.addProperty("text", component.toPlainText());
            text.addProperty("color", component.getColor().getName());
            text.addProperty("bold", component.isBold());
            text.addProperty("italic", component.isItalic());
            text.addProperty("underlined", component.isUnderlined());
            text.addProperty("strikethrough", component.isStrikethrough());
            text.addProperty("obfuscated", component.isObfuscated());
            array.add(text);
        }
        SendLogMessage(player, array);
    }

    public void EmitPluginMessage(Player player, JsonObject object) {
        player.sendPluginMessage(main, "proxichat:bridge", object.toString().getBytes());
    }

    public void EmitPluginPong(Player player) {
        var json = new JsonObject();
        json.addProperty("type", "pong");
        EmitPluginMessage(player, json);
    }

    public void EmitPluginIsMute(Player player, boolean mute) {
        var json = new JsonObject();
        json.addProperty("type", "is_mute");
        json.addProperty("mute", mute);
        EmitPluginMessage(player, json);
    }

    public void EmitPluginInfos(Player player, String group, String display, int minDistance, int maxDistance) {
        var json = new JsonObject();
        json.addProperty("type", "info");
        json.addProperty("group", group);
        json.addProperty("display", display);
        json.addProperty("min_distance", minDistance);
        json.addProperty("max_distance", maxDistance);
        EmitPluginMessage(player, json);
    }
}
