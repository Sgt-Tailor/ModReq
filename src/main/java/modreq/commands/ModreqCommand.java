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
import java.time.Instant;
import java.util.HashMap;

import modreq.*;
import modreq.korik.Utils;
import modreq.repository.TicketRepository;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModreqCommand implements CommandExecutor {

    private ModReq plugin;
    private TicketRepository tickets;

    public ModreqCommand(ModReq instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        tickets = plugin.getTicketRepository();

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run as a player");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("modreq.request")) {
            Message.sendToPlayer(MessageType.ERROR_PERMISSION, p);
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.message"), "", "", ""));
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                int ticketsfromplayer = tickets.getTicketCountBySubmitter(p, Status.OPEN);
                if (ticketsfromplayer >= plugin.getConfig().getInt("maximum-open-tickets")) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_TOOMANY, p);
                    return;
                }
                String message = Utils.join(args, " ", 0);
                int id = savereq(message, p);
                final String idString = Integer.toString(id);

                Message.sendToAdmins(MessageType.STAFF_ALL_TICKETSUBMITTED, new HashMap<String, String>() {{
                    put("player", sender.getName());
                    put("number", idString);
                }});

                Message.sendToPlayer(MessageType.PLAYER_SUBMIT, p);
            } catch (SQLException e) {
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
                e.printStackTrace();
            }
        });
        return true;
    }

    private int savereq(String message, Player sender) throws SQLException {
        Location loc = sender.getLocation();
        String location = loc.getWorld().getName() + " @ "
                + Math.round(loc.getX()) + " " + Math.round(loc.getY()) + " "
                + Math.round(loc.getZ());

        Ticket t = new Ticket(0, sender.getName(), sender.getUniqueId(), message, Instant.now(), Status.OPEN, location, "no staff member yet", null);
        return tickets.addTicket(t);
    }
}
