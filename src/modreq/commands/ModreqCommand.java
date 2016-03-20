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
    public boolean onCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        tickets = plugin.getTicketHandler();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("modreq.request")) {
                if (args.length == 0) {
                    sender.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.message"), "", "",""));
                    return true;
                } else {
                    int ticketsfromplayer;
                    try {
                        ticketsfromplayer = tickets.getTicketsFromPlayer(p,
                                sender.getName(), Status.OPEN);
                        if (plugin.getConfig().getInt("maximum-open-tickets") > ticketsfromplayer) {
                            String message = Utils.join(args, " ", 0);
                            int id = savereq(message, sender,((Player) sender).getLocation());
                            sendMessageToAdmins(ModReq.format(ModReq.getInstance().Messages.getString("staff.all.ticket-submitted"), sender.getName(), Integer.toString(id),""));
                            p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("player.submit"), "", "",""));
                            return true;
                        } else {
                            p.sendMessage(ModReq.format(ModReq.getInstance().Messages.getString("error.ticket.toomany"), "", "",""));
                            return true;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return false;
    }

    public void sendMessageToAdmins(String message) {// sends a message to all
        // online players with the
        // modreq.check permission
        Player[] list = Bukkit.getServer().getOnlinePlayers().toArray(new Player[Bukkit.getServer().getOnlinePlayers().size()]);
        int l = list.length;
        int n = 0;
        while (n < l) {
            Player op = list[n];
            if (op.hasPermission("modreq.check")) {
                op.sendMessage(message);
            }
            n++;
        }

    }

    private int savereq(String message, CommandSender sender, Location loc) {// save
        String time = ModReq.getTimeString();
        String location = loc.getWorld().getName() + " @ "
                + Math.round(loc.getX()) + " " + Math.round(loc.getY()) + " "
                + Math.round(loc.getZ());

        try {
            int id = tickets.addTicket(sender.getName(), message, time, Status.OPEN, location);
            return id;
        } catch (SQLException e) {
        }
        return 0;
    }
}
