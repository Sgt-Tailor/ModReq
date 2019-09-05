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
import modreq.Message;
import modreq.MessageType;
import modreq.ModReq;
import modreq.Status;
import modreq.Ticket;
import modreq.repository.TicketRepository;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimCommand implements CommandExecutor {

    private ModReq plugin;

    public ClaimCommand(ModReq instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        TicketRepository tickets = plugin.getTicketRepository();
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran as a player");
            return true;
        }

        Player p = (Player) sender;
        if (!p.hasPermission("modreq.claim.normal")) {
            Message.sendToPlayer(MessageType.ERROR_PERMISSION, p);
            return true;
        }

        if (args.length == 0) {
            p.sendMessage("/" + label + " <id>");
            return true;
        }

        int ticketId;
        try {
            ticketId = Integer.parseInt(args[0]);
        } catch (Exception e) {
            Message.sendToPlayer(MessageType.ERROR_NUMBER, p, args[0]);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Ticket t = tickets.getTicketById(ticketId);
                if (t == null) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, p);
                    return;
                }

                Status currentstatus = t.getStatus();
                Status status = Status.CLAIMED;
                String staff = sender.getName();

                if (currentstatus.equals(Status.CLAIMED) && !sender.hasPermission("modreq.overwrite.claim")) {
                    Message.sendToPlayer(MessageType.ERROR_TICKET_CLAIM, (Player) sender, args[0]);
                    return;
                }

                if (currentstatus.equals(Status.PENDING) && !sender.hasPermission("modreq.claim.pending")) {
                    Message.sendToPlayer(MessageType.ERROR_CLAIM_PENDING, (Player) sender, args[0]);
                    return;
                }

                if (!plugin.getConfig().getBoolean("may-claim-multiple", false)) {
                    if (tickets.playerHasClaimedTicket(p)) {
                        Message.sendToPlayer(MessageType.ERROR_CLAIM_MULTIPLE, (Player) sender, args[0]);
                        return;
                    }

                }

                t.setStaff(staff);
                t.setStaffUUID(p.getUniqueId());
                t.setStatus(status);
                t.addDefaultComment(p, CommentType.CLAIM);
                t.update();

                Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_CLAIMED, (Player) sender, args[0]);
                t.sendMessageToSubmitter(MessageType.PLAYER_CLAIM.format(p.getName(), args[0], ""));
            } catch (SQLException e) {
                e.printStackTrace();
                Message.sendToPlayer(MessageType.ERROR_GENERIC, p);
            }
        });

        return true;
    }
}
