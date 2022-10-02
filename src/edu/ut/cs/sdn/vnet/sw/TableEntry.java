package edu.ut.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.*;
import edu.ut.cs.sdn.vnet.Device;
import edu.ut.cs.sdn.vnet.DumpFile;
import edu.ut.cs.sdn.vnet.Iface;


public class TableEntry {

    private MACAddress destAddr;
    private Iface outIFace;
    long timestep;

    public TableEntry(MACAddress addr, Iface iface) {
        this.destAddr = addr;
        this.outIFace = iface;
        this.timestep = System.currentTimeMillis() / 1000;
    }

    public MACAddress getEntryMACAddr() {
        return this.destAddr;
    }

    public Iface getEntryIFace() {
        return this.outIFace;
    }

    public int getEntryTimeStamp() {
        return this.timestamp;
    }

    public void restEntryTimeStamp() {
        this.timestamp = System.currentTimeMillis() / 1000;
    }



}