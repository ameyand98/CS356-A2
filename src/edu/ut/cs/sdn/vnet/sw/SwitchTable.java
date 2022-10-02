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

    public void addEntry(MACAddress address, TableEntry entry) {
        this.routeTable.put(address, entry);
        this.numEntries++;
    }

    public void updateEntry(MACAddress address, Iface iface) {
        // might not need to create a new entry every time
        this.routeTable.put(address, new TableEntry(address, iface));
    }

    public Iface getIFaceForAddr(MACAddress address) {
        if (this.routeTable.containsKey(address)) {
            boolean isTimedOut = (this.routeTable.get(address).getEntryTimeStamp() - System.currentTimeMillis() / 1000) > 15;
            if (!isTimedOut) {
                return this.routeTable.get(address).getEntryIFace();
            }
            // might want to delete the entry here
        } 
        return null;
    }   

}