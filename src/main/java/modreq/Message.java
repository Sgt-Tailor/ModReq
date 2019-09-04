package modreq;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class Message {

    public static void sendToPlayer(MessageType MessageType, Player player, int ticket, String comment) {
        player.sendMessage(MessageType.format(player.getName(), Integer.toString(ticket), comment));
    }

    public static void sendToPlayer(MessageType MessageType, Player player) {
        player.sendMessage(MessageType.format(player.getName(), "", ""));
    }

    public static void sendToPlayer(MessageType MessageType, Player player, String number) {
        player.sendMessage(MessageType.format(player.getName(), number, ""));
    }

    public static void sendToPlayer(MessageType MessageType, Player player, Map<String,String>parameters) {
        player.sendMessage(MessageType.formatWithParameters(parameters));
    }
    public static void sendToAdmins(MessageType MessageType, Map<String,String>parameters) {
        String message = MessageType.formatWithParameters(parameters);
        for (Player op : Bukkit.getOnlinePlayers()) {
            if (op.hasPermission("modreq.check")) {
                op.sendMessage(message);
            }
        }
    }
}
