package modreq;

import org.bukkit.entity.Player;

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
}
