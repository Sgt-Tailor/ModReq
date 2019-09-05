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
import modreq.repository.TicketRepository;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TicketCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketRepository tickets;

    public TicketCommand(ModReq instance) {
        plugin = instance;
    }

    @Override
    public void onInvalidCommand(CommandSender sender, String[] args, String command) {
        tickets = plugin.getTicketRepository();
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can only run this command as a player");
            return;
        }
        Player player = (Player) sender;
        if (sender.hasPermission("modreq.check")) {
            Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
            return;
        }

        if (args.length != 1) {
            sender.sendMessage("/ticket <id>");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.number"), "", "", ""));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Ticket ticket;
            try {
                ticket = tickets.getTicketById(id);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
                return;
            }

            if (ticket == null) {
                Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, player);
                return;
            }

            ticket.sendMessageToPlayer(player);
        });
    }

    @command(
            permissions = "modreq.setpending",
            maximumArgsLength = 1,
            minimumArgsLength = 1,
            playerOnly = true,
            usage = "/ticket setpending <id>"
    )
    public void setpending(CommandSender sender, String[] args) {
        tickets = plugin.getTicketRepository();
        Player p = (Player) sender;
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.number"), "", args[0], ""));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Ticket t = tickets.getTicketById(id);
                if (t == null) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p);
                    return;
                }

                t.setStatus(Status.PENDING);
                t.addDefaultComment(p, CommentType.PENDING);
                t.setStaff("no staff member");
                t.sendMessageToSubmitter(ModReq.format(ModReq.getInstance().Messages.getString("player.pending"), sender.getName(), Integer.toString(id), ""));

                Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_PENDING, p);

                t.update();
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
            }
        });
    }
}

