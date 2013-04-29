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
import modreq.CommentType;

import modreq.ModReq;
import modreq.Status;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.managers.TicketHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TicketCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketHandler tickets;

    public TicketCommand(ModReq instance) {
        plugin = instance;
    }

    @command
    public void Integer(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            if (sender.hasPermission("modreq.check")) {
                if (args.length == 1) {
                    int id;
                    try {
                        id = Integer.parseInt(args[0]);
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED
                                + args[0]
                                + " "
                                + plugin.Messages.getString("no-number",
                                "is not a number"));
                        return;
                    }
                    if (tickets.getTicketCount() < id) {
                        sender.sendMessage(ChatColor.RED
                                + plugin.Messages.getString("no-ticket",
                                "That ticket does not exist"));
                    } else {
                        tickets.getTicketById(id).sendMessageToPlayer(
                                (Player) sender);
                    }
                }
            }
        }
    }

    @command
    public void setpending(CommandSender sender, String[] args) {
        tickets = plugin.getTicketHandler();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("modreq.setpending")) {
                if (args.length == 1) {
                    int id;
                    try {
                        id = Integer.parseInt(args[0]);
                    } catch (Exception e) {
                        p.sendMessage(ChatColor.RED
                                + args[0]
                                + " "
                                + plugin.Messages.getString("no-number",
                                "is not a number"));
                        return;
                    }
                    if (tickets.getTicketCount() < id) {
                        p.sendMessage(ChatColor.RED
                                + plugin.Messages.getString("no-ticket",
                                "That ticket does not exist"));
                    } else {
                        Ticket t = tickets.getTicketById(id);
                        t.setStatus(Status.PENDING);
                        t.addDefaultComment(p, CommentType.PENDING);
                        t.setStaff("no staff member");
                        try {
                            t.update();
                        } catch (SQLException e) {
                        }
                    }
                }
            }
        }
    }
}
