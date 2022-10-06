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
        SwitchTable table = this.routeTable; 
        if (table.containsKey(address)) {
            Iface currIface = table.get(address).getEntryIFace();
            if (currIface.getName().equals(iface.getName())) {
                TableEntry entry = table.get(address).restEntryTimeStamp();
                return;
            }
            
        }
        // this handles new address and interfaces
        this.addEntry(address, new TableEntry(address, iface));
    }

    // returns the interface to send a packet out on for a given destination address. if no entry exists, returns null 
    public Iface getIFaceForAddr(MACAddress address) {
        if (this.routeTable.containsKey(address)) {
            SwitchTable table = this.routeTable;
            boolean isTimedOut = (table.get(address).getEntryTimeStamp() - System.currentTimeMillis() / 1000) > 15;
            if (!isTimedOut) {
                return table.get(address).getEntryIFace();
            } else {
                table.remove(address);
            }
        } 
        return null;
    }   

}