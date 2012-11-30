package modreq;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModReqCommandExecutor implements CommandExecutor {

	public static modreq plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public ModReqCommandExecutor(modreq instance) {
	plugin = instance;
	}
	public File configFile;
	public TicketHandler tickets;
	
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String arg2, final String[] arg) {
		if(cmd.getName().equalsIgnoreCase("mods")) {
			if(sender.hasPermission("modreq.mods")) {
				
				sender.sendMessage(ChatColor.GOLD+"-------List-of-Online-Mods-------");
				
				Player[] op = Bukkit.getOnlinePlayers();
				String online = "";
				for(int i=0; i<op.length;i++) {
					if(op[i].hasPermission("modreq.check")) {
						if(i==0) {
							online = op[i].getDisplayName();
						}
						else {
							online = online + " " + op[i].getDisplayName();
						}
					}
				}
				if(online.equals("")) {
					sender.sendMessage(ChatColor.GRAY + "There are no mods online");
					return true;
				}
				sender.sendMessage(online);
				return true;
			}
			else {
				sender.sendMessage(ChatColor.RED + "You don't have permissions to do this");
				return true;
			}
		}
		tickets = new TicketHandler();
		if(cmd.getName().equalsIgnoreCase("modreq")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(p.hasPermission("modreq.request")){
					if(arg.length == 0){
						p.sendMessage(ChatColor.RED + "You have not typed any message. Please do so");
						return true;
					}
					else{
						int ticketfromplayer;
						try {
							ticketfromplayer = tickets.getTicketsFromPlayer(p, sender.getName(), "open");
							if(plugin.getConfig().getInt("maximum-open-tickets") > ticketfromplayer) {
								String message = argToString(arg);
								savereq(message, sender, ((Player) sender).getLocation());
								sendMessageToAdmins(ChatColor.GREEN + sender.getName() + ChatColor.AQUA + " submitted a moderator request");
								p.sendMessage(ChatColor.GREEN + "You successfully submitted a moderator request");
								return true;
							}
							else {
								p.sendMessage(ChatColor.RED + "You have too many open requests");
								return true;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						
						
					}
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("status")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(arg.length == 0) {
					if(sender.hasPermission("modreq.status")){
						try {
							ArrayList<Ticket> t = tickets.getTicketsByPlayer(p, sender.getName());//get last tickets (max 5, but not always)
							p.sendMessage(ChatColor.GOLD + "-----List-of-Your-Last-5-Requests-----");
							for(int i=0;i<t.size(); i++) {//for each ticket, send status
								t.get(i).sendStatus(p);
							}
							return true;
						} catch (SQLException e) {//never happens
							e.printStackTrace();
						}
					}
				}
				if(arg.length ==1) {//the command must me /status <id>
					int id;
					try{//check if arg[0] is an Integer
						id = Integer.parseInt(arg[0]);
						if(id > tickets.getTicketCount()) {//check if that ticket exists
							p.sendMessage(ChatColor.RED + "That Ticket does not exist");
							return true;
						}
						Ticket t = tickets.getTicket(id);
						if(t.getSubmitter().equals(p.getName())) {//check if the ticket is from the sender
							t.sendMessageToPlayer(p);
						}
						else {
							p.sendMessage(ChatColor.RED + "That is not your ticket");
						}
						return true;
					}
					catch(Exception e) {
						p.sendMessage(ChatColor.RED + arg[0] + " is not a number");
					}
				}
				
			}
		}
		if(cmd.getName().equalsIgnoreCase("check")){//the command can be /check <page/id/closed/claimed> <id>
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(p.hasPermission("modreq.check")){
					if(arg.length == 0){//command = /check
						tickets.sendPlayerPage(1, "open", p);
						return true;
						
					}
					if(arg.length == 1){
						if(arg[0].equalsIgnoreCase("claimed")){//command = /check claimed
							tickets.sendPlayerPage(1, "claimed", p);
							return true;
						}
						if(arg[0].equalsIgnoreCase("closed")){//command = /check closed
							tickets.sendPlayerPage(1, "closed", p);
							return true;
							}
						else {
							int page = Integer.parseInt(arg[0]);//command must be /check <id>
							tickets.sendPlayerPage(page, "open", p);
							return true;
						}
					}
					if(arg.length == 2) {//command must be /check <closed,claimed,id> <page,id>
						if(arg[0].equals("claimed")) {
							int id;
							try{id = Integer.parseInt(arg[1]);
							}
							catch(Exception e) {
								p.sendMessage(ChatColor.RED + arg[1] + " is not a number");
								return true;
							}
							tickets.sendPlayerPage(id, "claimed", p);
							return true;
						}
						if(arg[0].equals("closed")) {
							int id;
							try{id = Integer.parseInt(arg[1]);
							}
							catch(Exception e) {
								p.sendMessage(ChatColor.RED + arg[1] + " is not a number");
								return true;
							}
							tickets.sendPlayerPage(id, "closed", p);
							return true;
							
						}
						if(arg[0].equals("id")) {
							int id;
							try{id = Integer.parseInt(arg[1]);
							}
							catch(Exception e) {
								p.sendMessage(ChatColor.RED + arg[1] + " is not a number");
								return true;
							}
							if(tickets.getTicketCount() < id) {
								p.sendMessage(ChatColor.RED + "That ticket does not exist");
								return true;
							}
							else {
								tickets.getTicket(id).sendMessageToPlayer(p);
								return true;
							}
						}
					}
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("done")){//closes a ticket
			if(sender instanceof Player){
				Player p = (Player)sender;
				if(p.hasPermission("modreq.close")){
					if(arg.length>0){
						int id;
						try{id = Integer.parseInt(arg[0]);
						}
						catch(Exception e) {
							p.sendMessage(ChatColor.RED + arg[1] + " is not a number");
							return true;
						}
						if(tickets.getTicketCount() < id) {
							p.sendMessage(ChatColor.RED + "That ticket does not exist");
							return true;
						}
						else {
							String comment = "";
							if(arg.length > 1) {
								for(int i=1; i <arg.length; i++) {
									if(i==1) {
										comment = arg[i];
									}
									else {
										comment = comment + " " + arg[i];	
									}
								}
							}
							Ticket t = tickets.getTicket(id);
							
							String status = "closed";
							String staff = sender.getName();
							
							String currenstatus = t.getStatus();
							String currentstaff = t.getStaff();
							
							if(!currenstatus.equals("open")) {
								if(!currentstaff.equals(staff)) {
									p.sendMessage(ChatColor.RED + "You can not close that ticket");
									return true;
								}
							}
							
							
							t.setComment(comment);
							t.setStaff(staff);
							t.setStatus(status);
							try {
								t.update();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							p.sendMessage(ChatColor.GREEN + "Ticket closed");
							if(comment.equals("")) {
								sendMessageToSubmitter(t.getSubmitter(), ChatColor.GREEN + p.getName() + " just closed your ModReq");
							}
							else {
								sendMessageToSubmitter(t.getSubmitter(), ChatColor.GREEN + p.getName() + " just closed your ModReq with the comment: "+ChatColor.GRAY + comment);
							}
							return true;
						}
					}
						
				}
			}
			
		}
		if(cmd.getName().equalsIgnoreCase("re-open")){//sets the status of a ticket back to open
			if(sender instanceof Player){
				Player p = (Player)sender;
				if(p.hasPermission("modreq.reopen")){
					if(arg.length>0){
						int id;
						try{id = Integer.parseInt(arg[0]);
						}
						catch(Exception e) {
							p.sendMessage(ChatColor.RED + arg[1] + " is not a number");
							return true;
						}
						if(tickets.getTicketCount() < id) {
							p.sendMessage(ChatColor.RED + "That ticket does not exist");
							return true;
						}
						else {
							String comment = "";
							if(arg.length > 1) {
								for(int i=1; i <arg.length; i++) {
									if(i==1) {
										comment = arg[i];
									}
									else {
										comment = comment + " " + arg[i];	
									}
								}
							}
							Ticket t = tickets.getTicket(id);
							
							String status = "open";
							String staff = sender.getName();
							
							t.setComment(comment);
							t.setStaff(staff);
							t.setStatus(status);
							try {
								t.update();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							p.sendMessage(ChatColor.GREEN + "Ticket re-open");
							if(comment.equals("")) {
								sendMessageToSubmitter(t.getSubmitter(), ChatColor.GREEN + p.getName() + " just re-opened your ModReq");
							}
							else {
								sendMessageToSubmitter(t.getSubmitter(), ChatColor.GREEN + p.getName() + " just re-opened your ModReq with the comment: "+ChatColor.GRAY + comment);
							}
							return true;
						}
					}
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("tp-id")){
			if(sender instanceof Player){
				Player p = (Player)sender;
				if(p.hasPermission("modreq.tp-id")){
					if(arg.length == 1){
						int id;
						try {
							id = Integer.parseInt(arg[0]);
						}
						catch(Exception e) {
							p.sendMessage(ChatColor.RED + arg[0] + " is not a number");
							return true;
						}
						if(tickets.getTicketCount() < id) {
							p.sendMessage(ChatColor.RED + "That ticket does not exist");
							return true;
						}
						else {
							Ticket t = tickets.getTicket(id);
							Location loc = t.getLocation();
							p.teleport(loc);
							p.sendMessage(ChatColor.GREEN + "You have been teleported");
							sendMessageToSubmitter(t.getSubmitter(), ChatColor.GREEN + p.getName() + " just teleported to your ModReq");
							return true;
						}
					}
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("claim")){//claims a ticket
			if(sender instanceof Player){
				Player p = (Player)sender;
				if(p.hasPermission("modreq.claim")){
					if(arg.length>0){
						int id;
						try{id = Integer.parseInt(arg[0]);
						}
						catch(Exception e) {
							p.sendMessage(ChatColor.RED + arg[1] + " is not a number");
							return true;
						}
						if(tickets.getTicketCount() < id) {
							p.sendMessage(ChatColor.RED + "That ticket does not exist");
							return true;
						}
						else {
							Ticket t = tickets.getTicket(id);
							
							String currentstatus = t.getStatus();
														
							String status = "claimed";
							String staff = sender.getName();
							if(!currentstatus.equalsIgnoreCase("open")) {
								p.sendMessage(ChatColor.RED + "You can not claim that ticket");
								return true;
							}
							
							t.setStaff(staff);
							t.setStatus(status);
							try {
								t.update();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							p.sendMessage(ChatColor.GREEN + "Ticket claimed");
							sendMessageToSubmitter(t.getSubmitter(), ChatColor.GREEN + p.getName() + " just claimed your ModReq");
							return true;
						}
					}
				}
			}	
		}	
		return false;
	}
	public void savereq(String message, CommandSender sender, Location loc) {//save a ticket to the database
		TicketHandler tickets = new TicketHandler();
		String cal = Calendar.getInstance().getTime().toString();
		logger.info(cal);
		cal = cal.split(" ")[0] + " "+ cal.split(" ")[1] + " " + cal.split(" ")[2];
		String location =  loc.getWorld().getName()+" "+Math.round(loc.getX()) + " "+Math.round(loc.getY())+" "+Math.round(loc.getZ());
		try {
			tickets.addTicket( sender.getName(), message, cal, "open", location);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		plugin.saveYaml();
	}
	public void sendMessageToAdmins(String message) {//sends a message to all online players with the modreq.check permission
		Player[] list = Bukkit.getServer().getOnlinePlayers();
		int l = list.length;
		int n = 0;
		while(n<l){
			Player op = list[n];
			if(op.hasPermission("modreq.check")){
				op.sendMessage(message);
			}
			n++;
		}
		
	}
	public String argToString(String[] arg) {//converts arg[] to a string
		int a=0;
		String message = "";
		while( (a <= arg.length)){
			if(!(a == arg.length)){
				message = message + " " + arg[a] + " ";
			}
			
			a++;
		}
		return message;
	}
	public void sendMessageToSubmitter(String player, String message) {//sends a message to the submitter of a ticket if he is online
		Player[] op = Bukkit.getOnlinePlayers();
		for(int i = 0; i<op.length; i++) {
			if(op[i].getName().equals(player)) {
				if(op[i].isOnline()) {
					op[i].sendMessage(message);
					return;
				}
			}
		}
		return;
	}

}
