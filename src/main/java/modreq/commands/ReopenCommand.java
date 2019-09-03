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
import modreq.korik.Utils;
import modreq.repository.TicketRepository;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReopenCommand implements CommandExecutor {

    private ModReq plugin;
    private TicketRepository tickets;

    public ReopenCommand(ModReq instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        tickets = plugin.getTicketRepository();
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can only run this command as a Player");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("modreq.reopen")) {
            Message.sendToPlayer(MessageType.ERROR_PERMISSION, p);
            return true;
        }

        if (args.length == 0) {
            p.sendMessage("/" + label + " <id> (reason)");
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
            Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p, args[0]);
            return true;
        }

        String comment = Utils.join(args, " ", 1);
        Status status = Status.OPEN;
        String staff = sender.getName();

        t.addComment(new Comment(sender.getName(), p.getUniqueId().toString(), comment, CommentType.REOPEN));
        t.setStaff(staff);
        t.setStatus(status);
        try {
            t.update();
        } catch (SQLException e) {
            Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
            e.printStackTrace();
            return true;
        }

        Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_REOPENED, p, id, comment);
        t.sendMessageToSubmitter(ModReq.format(ModReq.getInstance().Messages.getString("player.reopen"), sender.getName(), args[0], ""));
        return true;
    }
}
