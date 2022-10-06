package edu.ut.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.*;
import edu.ut.cs.sdn.vnet.Device;
import edu.ut.cs.sdn.vnet.DumpFile;
import edu.ut.cs.sdn.vnet.Iface;


public class TableEntry {

    private MACAddress destAddr;
    private Iface outIFace;
    long timestamp;

    public TableEntry(MACAddress addr, Iface iface) {
        this.destAddr = addr;
        this.outIFace = iface;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    // return the mac address of the table entry 
    public MACAddress getEntryMACAddr() {
        return this.destAddr;
    }

    // return the Interface of the table entry 
    public Iface getEntryIFace() {
        return this.outIFace;
    }

    //  get the current timestamp of the given entry 
    public long getEntryTimeStamp() {
        return this.timestamp;
    }

    // reset the timestamp of the given entry 
    public void restEntryTimeStamp() {
        this.timestamp = System.currentTimeMillis() / 1000;
    }



}