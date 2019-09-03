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

import modreq.Message;
import modreq.MessageType;
import modreq.ModReq;
import modreq.Ticket;
import modreq.korik.SubCommandExecutor;
import modreq.repository.TicketRepository;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketRepository tickets;

    public StatusCommand(ModReq instance) {
        plugin = instance;
    }

    @command
    public void Null(CommandSender sender, String[] args) {
        tickets = plugin.getTicketRepository();
        if (!(sender instanceof Player)) {
            return;
        }
        Player p = (Player) sender;
        if (!sender.hasPermission("modreq.status")) {
            Message.sendToPlayer(MessageType.ERROR_PERMISSION, p);
            return;
        }
        try {
            ArrayList<Ticket> t = tickets.getTicketsBySubmitter(p);
            p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("headers-footers.status.header"), "", "", ""));
            for (Ticket ticket : t) {
                ticket.sendStatus(p);
            }
            p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("headers-footers.status.footer"), "", "", ""));
        } catch (SQLException e) {
            e.printStackTrace();
            Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
        }

    }

    @Override
    public void onInvalidCommand(CommandSender sender, String[] args, String ticketNumber) {
        tickets = plugin.getTicketRepository();
        if (!(sender instanceof Player)) {
            return;
        }
        Player p = (Player) sender;
        if (args.length != 0) {
            p.sendMessage("/status <id>");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(ticketNumber);
            Ticket t = tickets.getTicketById(id);
            if (t == null) {
                Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p, ticketNumber);
                return;
            }

            if (t.getSubmitter().equals(p.getName())) {
                t.sendMessageToPlayer(p);
            } else {
                Message.sendToPlayer(MessageType.ERROR_TICKET_YOUR, p, ticketNumber);
            }
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p, ticketNumber);
        }
    }
}
