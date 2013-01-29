package managers;

import commands.CheckCommand;
import commands.ClearticketsCommand;
import commands.DoneCommand;
import commands.ModhelpCommand;
import commands.ModreqCommand;
import commands.ModsCommand;
import commands.ReloadCommand;
import commands.ReopenCommand;
import commands.StatusCommand;
import commands.TicketCommand;
import commands.TpIdCommand;
import commands.UpdatemodreqCommand;
import commands.claimCommand;

import modreq.modreq;

public class CommandManager {
	private modreq plugin;
	
	private CheckCommand check;
	private claimCommand claim;
	private DoneCommand done;
	private StatusCommand status;
	private ModreqCommand modreq;
	private TpIdCommand tpid;
	private ReopenCommand reopen;
	private ModsCommand mods;
	private ModhelpCommand modhelp;
	private TicketCommand ticket;
	private UpdatemodreqCommand update;
	private ClearticketsCommand clear;
	private ReloadCommand reload;
	
	public CommandManager(modreq instance) {
		plugin = instance;
		check = new CheckCommand(plugin);
		claim = new claimCommand(plugin);
		done = new DoneCommand(plugin);
		status = new StatusCommand(plugin);
		modreq = new ModreqCommand(plugin);
		tpid = new TpIdCommand(plugin);
		reopen = new ReopenCommand(plugin);
		mods = new ModsCommand(plugin);
		modhelp = new ModhelpCommand(plugin);
		ticket = new TicketCommand(plugin);
		update = new UpdatemodreqCommand(plugin);
		clear = new ClearticketsCommand(plugin);
		reload = new ReloadCommand(plugin);
		
	}
	
	
	public void initCommands() {
		plugin.getCommand("check").setExecutor(check);
		plugin.getCommand("claim").setExecutor(claim);
		plugin.getCommand("done").setExecutor(done);
		plugin.getCommand("status").setExecutor(status);
		plugin.getCommand("modreq").setExecutor(modreq);
		plugin.getCommand("tp-id").setExecutor(tpid);
		plugin.getCommand("re-open").setExecutor(reopen);
		plugin.getCommand("mods").setExecutor(mods);
		plugin.getCommand("modhelp").setExecutor(modhelp);
		plugin.getCommand("ticket").setExecutor(ticket);
		plugin.getCommand("updatemodreq").setExecutor(update);
		plugin.getCommand("cleartickets").setExecutor(clear);
		plugin.getCommand("modreload").setExecutor(reload);
	}
	

}
