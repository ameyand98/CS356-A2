package edu.ut.cs.sdn.vnet.sw;

import net.floodlightcontroller.packet.Ethernet;
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
		
		/********************************************************************/
		/* TODO: Handle packets                                             */
		
		/********************************************************************/

		MACAddress srcMAC = etherPacket.getSourceMAC();
		MACAddress destMAC = etherPacket.getDestinationMAC();

		updateRouteTable(srcMAC, inIface);

		Iface outIface = this.routingTable.getIFaceForAddr(destMAC);
		if (outIface != null) {
			sendPacket(etherPacket, outIface);
		} else {
			floodMessage(etherPacket, inIface);
		}
		

	}

	private void updateRouteTable(MACAddress address, Iface interface) {
		this.table.updateEntry(address, interface);
	}

	private void floodMessage(Ethernet etherPacket, Iface srcIface) {
		for (String name : this.interfaces.keySet()) {
			Iface currIface = this.interfaces.get(name);
			if (!currIface.equals(srcIface)) {
				sendPacket(etherPacket, currIface);
			}
		}
	}
}
