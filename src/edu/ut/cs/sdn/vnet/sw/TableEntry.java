package edu.ut.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.Ethernet;
import edu.ut.cs.sdn.vnet.Device;
import edu.ut.cs.sdn.vnet.DumpFile;
import edu.ut.cs.sdn.vnet.Iface;


public class TableEntry {

    private MACAddress destAddr;
    private Iface outIFace;
    int timestamp;

    public TableEntry(MACAddress addr, Iface interface) {
        this.destAddr = addr;
        this.outIFace = interface;
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