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
import java.util.HashMap;

import modreq.Message;
import modreq.MessageType;
import modreq.ModReq;
import modreq.Status;
import modreq.korik.Utils;
import modreq.managers.TicketHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModreqCommand implements CommandExecutor {

    private ModReq plugin;
    private TicketHandler tickets;

    public ModreqCommand(ModReq instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        tickets = plugin.getTicketHandler();

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run as a player");
            return true;
        }
        Player p = (Player) sender;
        if (p.hasPermission("modreq.request")) {
            if (args.length == 0) {
                sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.message"), "", "", ""));
                return true;
            }
            try {
                int ticketsfromplayer = tickets.getTicketsFromPlayer(p, Status.OPEN);
                if (ticketsfromplayer >= plugin.getConfig().getInt("maximum-open-tickets")) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_TOOMANY, p);
                    return true;
                }
                String message = Utils.join(args, " ", 0);
                int id = savereq(message, p);
                final String idString = Integer.toString(id);

                Message.sendToAdmins(MessageType.STAFF_ALL_TICKETSUBMITTED, new HashMap<String, String>() {{
                    put("player", sender.getName());
                    put("number", idString);
                }});

                Message.sendToPlayer(MessageType.PLAYER_SUBMIT, p);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private int savereq(String message, Player sender) {// save
        String time = ModReq.getTimeString();
        Location loc = sender.getLocation();
        String location = loc.getWorld().getName() + " @ "
                + Math.round(loc.getX()) + " " + Math.round(loc.getY()) + " "
                + Math.round(loc.getZ());

        try {
            return tickets.addTicket(sender.getName(), message, time, Status.OPEN, location);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
