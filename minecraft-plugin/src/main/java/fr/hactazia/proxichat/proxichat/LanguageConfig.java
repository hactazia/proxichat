package fr.hactazia.proxichat.proxichat;

public class LanguageConfig {
    private final ProxiChatPlugin main;

    // default values for the language config
    public String language = "en";
    public String prefix = "§6§lP§e§lroxi§6§lC§e§lhat§r §7► §r";
    public String noPermission = "§cYou don't have the permission to do this.";
    public String noPlayer = "§cThis player is not online.";
    public String playerOnlyCommand = "§cThis command is only for players.";

    public String noConnector = "No connector found for your account.";
    public String clickToLink = "§e§lClick here§r to link your account!";
    public String connectToService = "Connected to ProxiChat!";
    public String disconnectFromService = "Disconnected from ProxiChat!";

    public String mutePlayer = "You are §lnow muted§r.";
    public String playerMuted = "§oPlayer %s is §lnow muted§r§o.";
    public String unmutePlayer = "You are §lnow unmuted§r.";
    public String playerUnmuted = "§oPlayer %s is §lnow unmuted§r§o.";
    public String muteFailed = "§cFailed to mute player %s.";
    public String unmuteFailed = "§cFailed to unmute player %s.";
    public String playerMutedStatus = "§cPlayer %s is muted.";
    public String playerUnmutedStatus = "§aPlayer %s is not muted.";

    public String deafPlayer = "You are §lnow deafened§r.";
    public String playerDeafened = "§oPlayer %s is §lnow deafened§r§o.";
    public String undeafPlayer = "You are §lnow undeafened§r.";
    public String playerUndeafened = "§oPlayer %s is §lnow undeafened§r§o.";
    public String deafFailed = "§cFailed to deafen player %s.";
    public String undeafFailed = "§cFailed to undeafen player %s.";
    public String playerDeafenedStatus = "§cPlayer %s is deafened.";
    public String playerUndeafenedStatus = "§aPlayer %s is not deafened.";

    public String commandList = "command list:";
    public String commandUsage = "Usage: /%s help";
    public String commandHelp = "/%s help - Show this help message";
    public String commandMute = "/%s mute <player> - Mute a player";
    public String commandUnmute = "/%s unmute <player> - Unmute a player";
    public String commandMuteToggle = "/%s mutetoggle <player> - Toggle mute a player";
    public String commandLink = "/%s link - Link your account";
    public String commandMuteStatus = "/%s mutestatus <player> - Get the mute status of a player";

    public String commandMuteUsage = "Usage: /%s mute <player>";
    public String commandUnmuteUsage = "Usage: /%s unmute <player>";
    public String commandMuteToggleUsage = "Usage: /%s mutetoggle <player>";
    public String commandMuteStatusUsage = "Usage: /%s mutestatus <player>";


    public LanguageConfig(ProxiChatPlugin main) {
        this.main = main;
    }

    public void load() {
        language = main.getConfig().getString("language", language);
        prefix = main.getConfig().getString("prefix", prefix);
        noPermission = main.getConfig().getString("no_permission", noPermission);
        noPlayer = main.getConfig().getString("no_player", noPlayer);
        noConnector = main.getConfig().getString("no_connector", noConnector);
        connectToService = main.getConfig().getString("connect_to_service", connectToService);
        disconnectFromService = main.getConfig().getString("disconnect_from_service", disconnectFromService);
        mutePlayer = main.getConfig().getString("mute_player", mutePlayer);
        playerMuted = main.getConfig().getString("player_muted", playerMuted);
        unmutePlayer = main.getConfig().getString("unmute_player", unmutePlayer);
        playerUnmuted = main.getConfig().getString("player_unmuted", playerUnmuted);
        clickToLink = main.getConfig().getString("click_to_link", clickToLink);
        commandHelp = main.getConfig().getString("command_help", commandHelp);
        commandUsage = main.getConfig().getString("command_usage", commandUsage);
        commandMute = main.getConfig().getString("command_mute", commandMute);
        commandUnmute = main.getConfig().getString("command_unmute", commandUnmute);
        commandMuteToggle = main.getConfig().getString("command_mute_toggle", commandMuteToggle);
        commandLink = main.getConfig().getString("command_link", commandLink);
        muteFailed = main.getConfig().getString("mute_failed", muteFailed);
        unmuteFailed = main.getConfig().getString("unmute_failed", unmuteFailed);
        commandList = main.getConfig().getString("command_list", commandList);
        commandMuteStatus = main.getConfig().getString("command_mute_status", commandMuteStatus);
        commandMuteUsage = main.getConfig().getString("command_mute_usage", commandMuteUsage);
        commandUnmuteUsage = main.getConfig().getString("command_unmute_usage", commandUnmuteUsage);
        commandMuteToggleUsage = main.getConfig().getString("command_mute_toggle_usage", commandMuteToggleUsage);
        commandMuteStatusUsage = main.getConfig().getString("command_mute_status_usage", commandMuteStatusUsage);
        playerMutedStatus = main.getConfig().getString("player_muted_status", playerMutedStatus);
        playerUnmutedStatus = main.getConfig().getString("player_unmuted_status", playerUnmutedStatus);
        playerOnlyCommand = main.getConfig().getString("player_only_command", playerOnlyCommand);
        deafPlayer = main.getConfig().getString("deaf_player", deafPlayer);
        playerDeafened = main.getConfig().getString("player_deafened", playerDeafened);
        undeafPlayer = main.getConfig().getString("undeaf_player", undeafPlayer);
        playerUndeafened = main.getConfig().getString("player_undeafened", playerUndeafened);
        deafFailed = main.getConfig().getString("deaf_failed", deafFailed);
        undeafFailed = main.getConfig().getString("undeaf_failed", undeafFailed);
    }

    public String Format(String key, Object... args) {
        return String.format(prefix + Get(key), args);
    }

    public String Format(String key) {
        return String.format(prefix + Get(key));
    }

    public String FormatWithOutPrefix(String key, Object... args) {
        return String.format(Get(key), args);
    }

    public String FormatWithOutPrefix(String key) {
        return String.format(Get(key));
    }

    public String Get(String key) {
        switch (key) {
            case "no_permission":
                return noPermission;
            case "no_player":
                return noPlayer;
            case "no_connector":
                return noConnector;
            case "click_to_link":
                return clickToLink;
            case "connect_to_service":
                return connectToService;
            case "disconnect_from_service":
                return disconnectFromService;
            case "mute_player":
                return mutePlayer;
            case "player_muted":
                return playerMuted;
            case "unmute_player":
                return unmutePlayer;
            case "player_unmuted":
                return playerUnmuted;
            case "command_list":
                return commandList;
            case "command_help":
                return commandHelp;
            case "command_usage":
                return commandUsage;
            case "command_mute":
                return commandMute;
            case "command_unmute":
                return commandUnmute;
            case "command_mute_toggle":
                return commandMuteToggle;
            case "command_link":
                return commandLink;
            case "mute_failed":
                return muteFailed;
            case "unmute_failed":
                return unmuteFailed;
            case "command_mute_status":
                return commandMuteStatus;
            case "command_mute_usage":
                return commandMuteUsage;
            case "command_unmute_usage":
                return commandUnmuteUsage;
            case "command_mute_toggle_usage":
                return commandMuteToggleUsage;
            case "command_mute_status_usage":
                return commandMuteStatusUsage;
            case "player_muted_status":
                return playerMutedStatus;
            case "player_unmuted_status":
                return playerUnmutedStatus;
            case "player_only_command":
                return playerOnlyCommand;
            case "deaf_player":
                return deafPlayer;
            case "player_deafened":
                return playerDeafened;
            case "undeaf_player":
                return undeafPlayer;
            case "player_undeafened":
                return playerUndeafened;
            case "deaf_failed":
                return deafFailed;
            case "undeaf_failed":
                return undeafFailed;
            default:
                return String.format("[%s].", key);
        }
    }
}
