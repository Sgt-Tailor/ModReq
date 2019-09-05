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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketRepository ticketRepository;

    public StatusCommand(ModReq instance) {
        plugin = instance;
    }

    @command
    public void Null(CommandSender sender, String[] args) {
        ticketRepository = plugin.getTicketRepository();
        if (!(sender instanceof Player)) {
            return;
        }
        Player player = (Player) sender;
        if (!sender.hasPermission("modreq.status")) {
            Message.sendToPlayer(MessageType.ERROR_PERMISSION, player);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ArrayList<Ticket> tickets = this.ticketRepository.getTicketsBySubmitter(player);
                Message.sendToPlayer(MessageType.STATUS_HEADER, player);
                for (Ticket ticket : tickets) {
                    ticket.sendStatus(player);
                }
                Message.sendToPlayer(MessageType.STATUS_FOOTER, player);
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, player);
            }
        });
    }

    @Override
    public void onInvalidCommand(CommandSender sender, String[] args, String ticketNumber) {
        ticketRepository = plugin.getTicketRepository();
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
        } catch (NumberFormatException e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p, ticketNumber);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Ticket ticket = null;
            SQLException exception = null;

            try {
                ticket = ticketRepository.getTicketById(id);
            } catch (SQLException e) {
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
                e.printStackTrace();
                return;
            }

            if (ticket == null) {
                Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p, ticketNumber);
                return;
            }

            if (ticket.getSubmitter().equals(p.getName())) {
                ticket.sendMessageToPlayer(p);
            } else {
                Message.sendToPlayer(MessageType.ERROR_TICKET_YOUR, p, ticketNumber);
            }
        });
    }
}
