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
import modreq.korik.SubCommandExecutor;
import modreq.korik.Utils;
import modreq.managers.TicketHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoneCommand implements CommandExecutor {

    private ModReq plugin;

    public DoneCommand(ModReq instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        TicketHandler tickets = plugin.getTicketHandler();

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("modreq.close")) {
            Message.sendToPlayer(MessageType.ERROR_PERMISSION, p);
            return true;
        }

        if (args.length == 0) {
            p.sendMessage("/" + label + " <id> (message)");
            return true;
        }

        int id;
        String idString = args[0];
        try {
            id = Integer.parseInt(idString);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p, idString);
            return true;
        }

        Ticket t = tickets.getTicketById(id);
        if (t == null) {
            Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p);
            return true;
        }

        String comment = Utils.join(args, " ", 1);
        String staff = sender.getName();

        String currenstatus = t.getStatus().getStatusString();
        String currentstaff = t.getStaff();

        if (!currenstatus.equals(Status.OPEN.getStatusString())
                && !currentstaff.equals(staff)
                && !sender.hasPermission("modreq.overwrite.close")) {
            sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.ticket.close"), "", "", ""));
            return true;
        }

        t.addComment(new Comment(sender.getName(), comment, CommentType.CLOSE));

        t.setStaff(staff);
        t.setStatus(Status.CLOSED);
        try {
            t.update();
        } catch (SQLException e) {
            Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
            e.printStackTrace();
        }

        Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_CLOSED, p, idString);
        if (comment.equals("")) {
            t.sendMessageToSubmitter(MessageType.PLAYER_CLOSE_WITHOUTCOMMENT.format(p.getName(), idString, ""));
        } else {
            t.sendMessageToSubmitter(MessageType.PLAYER_CLOSE_WITHCOMMENT.format(p.getName(), idString, comment));
        }

        return true;
    }
}
