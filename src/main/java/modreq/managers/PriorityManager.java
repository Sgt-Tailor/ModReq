package modreq.managers;

import java.util.ArrayList;

import priority.Priority;

public class PriorityManager {
    
    private ArrayList<Priority> priorities = new ArrayList<Priority>();
    public PriorityManager() {
	loadFromYML();
    }
    public void loadFromYML() {
	//
    }
    /**
     * 
     * @param p
     * @return
     */
    public boolean addPriority(Priority p) {
	for(Priority pr : priorities) {
	    if(pr.getName().equalsIgnoreCase(p.getName())) {
		return false;
	    }
	    if(pr.getLevel() == p.getLevel()) {
		return false;
	    }
	}
	if(priorities.size() >0) {
	    Priority latest = priorities.get(priorities.size() -1);
	    
	}
	return false;
    }
}
