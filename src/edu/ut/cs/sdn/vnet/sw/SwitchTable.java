package edu.ut.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.*;
import edu.ut.cs.sdn.vnet.Device;
import edu.ut.cs.sdn.vnet.DumpFile;
import edu.ut.cs.sdn.vnet.Iface;
import java.util.HashMap;
import java.util.Map;

public class SwitchTable {

    private HashMap<MACAddress, TableEntry> routeTable;
	private int numEntries;
    private int timeout;

    public SwitchTable(int timeout) {
        this.routeTable = new HashMap<MACAddress, TableEntry>();
        this.numEntries = 0;
        this.timeout = timeout;
    }

    // adds a new addr-interface entry to the table 
    private void addEntry(MACAddress address, TableEntry entry) {
        this.routeTable.put(address, entry);
        this.numEntries++;
    }

    // updates an entry in the table 
    public void updateEntry(MACAddress address, Iface iface) {
        if (this.routeTable.containsKey(address)) {
            // if the entry already exists, only need to update the timestamp and nothing else 
            Iface currIface = this.routeTable.get(address).getEntryIFace();
            if (currIface.getName().equals(iface.getName())) {
                this.routeTable.get(address).restEntryTimeStamp();
                return;
            }
            
        }
        // handles creating a new entry and updating the interface for a current entry
        this.addEntry(address, new TableEntry(address, iface));
    }

    // returns the interface to send a packet out on for a given destination address. if no entry exists, returns null 
    public Iface getIFaceForAddr(MACAddress address) {
        if (this.routeTable.containsKey(address)) {
            boolean isTimedOut = (this.routeTable.get(address).getEntryTimeStamp() - System.currentTimeMillis() / 1000) > 15;
            if (!isTimedOut) {
                // valid entry
                return this.routeTable.get(address).getEntryIFace();
            } else {
                // entry timed out, so remove it 
                this.routeTable.remove(address);
            }
        } 
        return null;
    }   

}