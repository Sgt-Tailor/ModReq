/*
 Modreq Minecraft/Bukkit server ticket system
 Copyright (C) 2013 Sven Wiltink

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package modreq.commands;

import java.sql.SQLException;

import modreq.*;
import modreq.managers.TicketHandler;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpIdCommand implements CommandExecutor {

    private ModReq plugin;

    public TpIdCommand(ModReq instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TicketHandler tickets = plugin.getTicketHandler();
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can only run this command as a player");
            return true;
        }

        Player p = (Player) sender;
        if (!p.hasPermission("modreq.tp-id")) {
            Message.sendToPlayer(MessageType.ERROR_PERMISSION, p);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage("/" + label + " <id>");
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p, args[0]);
            return true;
        }

        Ticket t = tickets.getTicketById(id);
        if (t == null) {
            Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p);
            return true;
        }

        t.addDefaultComment(p, CommentType.TP);
        try {
            t.update();
        } catch (SQLException e) {
            e.printStackTrace();
            Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
        }

        Location loc = t.getLocation();
        p.teleport(loc);

        Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_TELEPORT, p);
        t.sendMessageToSubmitter(ModReq.format(ModReq.getInstance().Messages.getString("player.teleport"), sender.getName(), args[0], ""));
        return true;
    }
}
