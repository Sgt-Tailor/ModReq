package commands;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import managers.TicketHandler;
import modreq.Ticket;
import modreq.modreq;
import korik.SubCommandExecutor;

public class StatusCommand extends SubCommandExecutor{
    
    private modreq plugin;
    private TicketHandler tickets;
    public StatusCommand (modreq instance) {
	plugin = instance;
    }
    
    @command
    public void Null(CommandSender sender, String[] args) {
	tickets = plugin.getTicketHandler();
	if(sender instanceof Player){
		Player p = (Player) sender;
		if(args.length == 0) {
			if(sender.hasPermission("modreq.status")){
				try {
					ArrayList<Ticket> t = tickets.getTicketsByPlayer(p, sender.getName());//get last tickets (max 5, but not always)
					p.sendMessage(ChatColor.GOLD + plugin.Messages.getString("status-header", "-----List-of-Your-Last-5-Requests-----"));
					for(int i=0;i<t.size(); i++) {//for each ticket, send status
						t.get(i).sendStatus(p);
					}
					p.sendMessage(ChatColor.GOLD + plugin.Messages.getString("status-footer", "do /status <id> for more info"));
					return;
				} catch (SQLException e) {//never happens
					e.printStackTrace();
				}
			}
		}	
	}
    }
    @command
    public void Integer(CommandSender sender, String[] args) {
	if(sender instanceof Player) {
	    Player p = (Player) sender;
	    if(args.length ==1) {//the command must me /status <id>
    		int id;
    		try{//check if arg[0] is an Integer
    			id = Integer.parseInt(args[0]);
    			if(id > tickets.getTicketCount()) {//check if that ticket exists
    				p.sendMessage(ChatColor.RED + plugin.Messages.getString("no-ticket","That Ticket does not exist"));
    				return;
    			}
    			Ticket t = tickets.getTicketById(id);
    			if(t.getSubmitter().equals(p.getName())) {//check if the ticket is from the sender
    				t.sendMessageToPlayer(p);
    			}
    			else {
    				p.sendMessage(ChatColor.RED + plugin.Messages.getString("not-your","That is not your ticket"));
    			}
    			return;
    		}
    		catch(Exception e) {
    			p.sendMessage(ChatColor.RED + args[0] + " "+ plugin.Messages.getString("no-number", "is not a number"));
    		}
	    }
        }
    }

}
