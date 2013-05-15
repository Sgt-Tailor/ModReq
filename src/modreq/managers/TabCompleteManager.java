package modreq.managers;

import java.util.ArrayList;
import java.util.List;

import modreq.ModReq;
import modreq.Status;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TabCompleteManager {
    
    public static List<String> getPossibilities(CommandSender sender, Command cmd, String[] args){
	if(cmd.getName().equalsIgnoreCase("check")) {
	    if(sender.hasPermission("modreq.check")) {
		List<String> a = new ArrayList<String>();
        	if(args.length == 0) {// check <last page> <pending> <claimed> <closed>
        	   a.add("id");
        	   if(sender.hasPermission("modreq.claim.pending")) {a.add("pending");}
        	   a.add("claimed");
        	   a.add("closed");
        	    int b = viewablePages(sender);
        	   for(int i=1; i<= b; i++) {
        	       a.add(Integer.toString(i));
        	   }
        	   return a;
        	}
	    }
	}
	return null;
    }
    private static int viewablePages(CommandSender sender) {
	TicketHandler tickets = ModReq.getInstance().getTicketHandler();
	    int Openamount = tickets.getTicketAmount(Status.OPEN);
	    if (ModReq.getInstance().getConfig().getBoolean("show-claimed-tickets-in-open-list")) {
		Openamount = Openamount + tickets.getTicketAmount(Status.CLAIMED);
	    }
	    if (ModReq.getInstance().getConfig().getBoolean("show-pending-tickets-in-open-list") && sender.hasPermission("modreq.claim.pending")) {
		Openamount += tickets.getTicketAmount(Status.PENDING);
	    }
	    int pages = (int) Math.ceil(Openamount / 10.0);
	    return pages;
    }

}
