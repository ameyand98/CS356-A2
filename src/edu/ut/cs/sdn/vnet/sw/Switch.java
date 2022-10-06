package edu.ut.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.*;
import edu.ut.cs.sdn.vnet.Device;
import edu.ut.cs.sdn.vnet.DumpFile;
import edu.ut.cs.sdn.vnet.Iface;

/**
 * @author Aaron Gember-Jacobson
 */
public class Switch extends Device
{	

	private SwitchTable routingTable;
	private static final int TIMEOUT = 15;

	/**
	 * Creates a router for a specific host.
	 * @param host hostname for the router
	 */
	public Switch(String host, DumpFile logfile)
	{
		super(host,logfile);
		this.routingTable = new SwitchTable(TIMEOUT);
	}

	/**
	 * Handle an Ethernet packet received on a specific interface.
	 * @param etherPacket the Ethernet packet that was received
	 * @param inIface the interface on which the packet was received
	 */
	public void handlePacket(Ethernet etherPacket, Iface inIface)
	{
		System.out.println("*** -> Received packet: " +
                etherPacket.toString().replace("\n", "\n\t"));
		
		// get src and dest mac addr of the paket
		MACAddress srcMAC = etherPacket.getSourceMAC();
		MACAddress destMAC = etherPacket.getDestinationMAC();

		// switch learning part - add the src addr interface to our table if it doesn't already exist
		updateRouteTable(srcMAC, inIface);

		// get the interface to send this packet out of. if no interface, flood the packet  
		Iface outIface = this.routingTable.getIFaceForAddr(destMAC);
		if (outIface != null) {
			sendPacket(etherPacket, outIface);
		} else {
			floodMessage(etherPacket, inIface);
		}
		

	}

	// adds switch table entry for the src address if it doesn't already exist
	private void updateRouteTable(MACAddress address, Iface iface) {
		SwitchTable table = this.routingTable;
		table.updateEntry(address, iface);
	}

	// floods the packet on all interfaces 
	private void floodMessage(Ethernet etherPacket, Iface srcIface) {
		for (String name : this.interfaces.keySet()) {
			Iface currIface = this.interfaces.get(name);
			if (!currIface.equals(srcIface)) {
				sendPacket(etherPacket, currIface);
			}
		}
	}
}
