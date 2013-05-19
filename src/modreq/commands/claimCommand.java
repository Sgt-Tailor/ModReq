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
import modreq.korik.SubCommandExecutor;
import modreq.managers.TicketHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class claimCommand extends SubCommandExecutor {

    private ModReq plugin;
    private TicketHandler tickets;

    public claimCommand(ModReq instance) {
        plugin = instance;
    }

    @command
    public void Null(CommandSender sender, String[] args) {
        sender.sendMessage("/claim <id>");
    }

    public void onInvalidCommand(CommandSender sender, String[] args) {
        tickets = plugin.getTicketHandler();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("modreq.claim.normal")) {
                if (args.length > 0) {
                    int id;
                    try {
                        id = Integer.parseInt(args[0]);
                    } catch (Exception e) {
                	Message.sendToPlayer(MessageType.ERROR_NUMBER, (Player) sender, args[0]);
                        return;
                    }
                    if (tickets.getTicketCount() < id) {
                	Message.sendToPlayer(MessageType.ERROR_TICKET_EXIST, (Player) sender, args[0]);
                    } else {
                        Ticket t = tickets.getTicketById(id);
                        Status currentstatus = t.getStatus();
                        Status status = Status.CLAIMED;
                        String staff = sender.getName();
                        if (!currentstatus.equals(Status.OPEN)) {
                            if(!currentstatus.equals(Status.PENDING) && !sender.hasPermission("modreq.overwrite.claim")) {
                        	Message.sendToPlayer(MessageType.ERROR_TICKET_CLAIM, (Player) sender, args[0]);
                        	return;
                            }
                            else {
                        	if(!sender.hasPermission("modreq.claim.pending")) {
                        	    Message.sendToPlayer(MessageType.ERROR_TICKET_CLAIM, (Player) sender, args[0]);
                        	    return;
                        	}
                            }
                        }
                        if (plugin.getConfig().getBoolean("may-claim-multiple",false) == false) {
                            if (tickets.hasClaimed((Player) sender)) {
                        	Message.sendToPlayer(MessageType.ERROR_TICKET_CLAIM, (Player) sender, args[0]);
                                return;
                            }
                        }

                        t.setStaff(staff);
                        t.setStatus(status);
                        t.addDefaultComment(p, CommentType.CLAIM);
                        try {
                            t.update();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Message.sendToPlayer(MessageType.STAFF_EXECUTOR_TICKET_CLAIMED, (Player) sender, args[0]);
                        t.sendMessageToSubmitter(MessageType.PLAYER_CLAIM.format(p.getName(), args[0], ""));
                    }
                }
            }
        } else {
            sender.sendMessage("This command can only be ran as a player");
        }

    }
}
