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

import modreq.Comment;
import modreq.CommentType;
import modreq.ModReq;
import modreq.Status;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.korik.Utils;
import modreq.managers.TicketHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReopenCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketHandler tickets;

    public ReopenCommand(ModReq instance) {
        plugin = instance;
    }

    @command
    public void Integer(CommandSender sender, String[] args) {
        tickets = plugin.getTicketHandler();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("modreq.reopen")) {
                if (args.length > 0) {
                    int id;
                    try {
                        id = Integer.parseInt(args[0]);
                    } catch (Exception e) {
                        p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.number"), "", args[0],""));
                        return;
                    }
                    if (tickets.getTicketCount() < id) {
                        p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.ticket.exists"), "", Integer.toString(id),""));
                    } else {
                        String comment = Utils.join(args, " ", 1);
                        Ticket t = tickets.getTicketById(id);

                        Status status = Status.OPEN;
                        String staff = sender.getName();

                        t.addComment(new Comment(sender.getName(), comment, CommentType.REOPEN));
                        t.setStaff(staff);
                        t.setStatus(status);
                        try {
                            t.update();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("staff.executor.ticket.re-opened"), "", "",""));
                        t.sendMessageToSubmitter(ModReq.format(ModReq.getInstance().Messages.getString("player.reopen"), sender.getName(), args[0],""));
                    }
                }
            }
        }
    }
}
