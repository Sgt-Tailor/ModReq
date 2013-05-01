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
import java.util.ArrayList;

import modreq.ModReq;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.managers.TicketHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketHandler tickets;

    public StatusCommand(ModReq instance) {
        plugin = instance;
    }

    @command
    public void Null(CommandSender sender, String[] args) {
        tickets = plugin.getTicketHandler();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                if (sender.hasPermission("modreq.status")) {
                    try {
                        ArrayList<Ticket> t = tickets.getTicketsByPlayer(sender.getName());
                        p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("headers-footers.status.header"), "","",""));
                        for (int i = 0; i < t.size(); i++) {
                            t.get(i).sendStatus(p);
                        }
                        p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("headers-footers.status.footer"), "","",""));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @command
    public void Integer(CommandSender sender, String[] args) {
        tickets = plugin.getTicketHandler();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                int id;
                try {
                    id = Integer.parseInt(args[0]);
                    if (id > tickets.getTicketCount()) {
                        p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.ticket.exist"), "","",""));
                        return;
                    }
                    Ticket t = tickets.getTicketById(id);
                    if (t.getSubmitter().equals(p.getName())) {
                        t.sendMessageToPlayer(p);
                    } else {
                        p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.ticket.your"), "","",""));
                    }
                } catch (Exception e) {
                    p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.number"), "","",""));
                }
            }
        }
    }
}
