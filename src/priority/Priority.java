package priority;

import modreq.ModReq;
import modreq.managers.PriorityManager;

/**
 * Used to define the priority of a ticket.
 * The name of the priority will define
 * @author Sven Wiltink
 * @since 2.4	
 */
public class Priority {
    private static String name;
    private static int level;
    private PriorityManager Priorities;
    
    public Priority(String name, int level) {
	Priorities = ModReq.getPriorityManager();
	Priority.name = name;
	Priority.level = level;
    }
    public String getName() {
	return name;
    }
    public int getLevel() {
	return level;
    }   
    /**
     * Used to add the priority to the PriorityManager
     * @returns true if it was successfull
     */
    public boolean add() {
	return false;
    }
}
//Tailor was here
