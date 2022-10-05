package edu.ut.cs.sdn.vnet.rt;

import edu.ut.cs.sdn.vnet.Device;
import edu.ut.cs.sdn.vnet.DumpFile;
import edu.ut.cs.sdn.vnet.Iface;

import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;

/**
 * @author Aaron Gember-Jacobson and Anubhavnidhi Abhashkumar
 */
public class Router extends Device
{	
	/** Routing table for the router */
	private RouteTable routeTable;
	
	/** ARP cache for the router */
	private ArpCache arpCache;
	
	/**
	 * Creates a router for a specific host.
	 * @param host hostname for the router
	 */
	public Router(String host, DumpFile logfile)
	{
		super(host,logfile);
		this.routeTable = new RouteTable();
		this.arpCache = new ArpCache();
	}
	
	/**
	 * @return routing table for the router
	 */
	public RouteTable getRouteTable()
	{ return this.routeTable; }
	
	/**
	 * Load a new routing table from a file.
	 * @param routeTableFile the name of the file containing the routing table
	 */
	public void loadRouteTable(String routeTableFile)
	{
		if (!routeTable.load(routeTableFile, this))
		{
			System.err.println("Error setting up routing table from file "
					+ routeTableFile);
			System.exit(1);
		}
		
		System.out.println("Loaded static route table");
		System.out.println("-------------------------------------------------");
		System.out.print(this.routeTable.toString());
		System.out.println("-------------------------------------------------");
	}
	
	/**
	 * Load a new ARP cache from a file.
	 * @param arpCacheFile the name of the file containing the ARP cache
	 */
	public void loadArpCache(String arpCacheFile)
	{
		if (!arpCache.load(arpCacheFile))
		{
			System.err.println("Error setting up ARP cache from file "
					+ arpCacheFile);
			System.exit(1);
		}
		
		System.out.println("Loaded static ARP cache");
		System.out.println("----------------------------------");
		System.out.print(this.arpCache.toString());
		System.out.println("----------------------------------");
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
		if(etherPacket.getEtherType() == Ethernet.TYPE_IPv4) {

			IPv4 packet = (IPv4)etherPacket.getPayload();

			//Checksum Verification
			short prevChecksum = packet.getChecksum();
			//Checksum -> 0 so seralize recomputes it
			packet.setChecksum(0);
			packet.serialize();
			if (packet.getChecksum() != prevChecksum) {
				//Invalid checksum -> drop packet
				return;
			}

			//TTL Verification
			packet.setTtl((byte)packet.getTtl() - 1);
			if(packet.getTtl() == 0) {
				//Packet cannot travel more hops -> drop packet so not idle in network
				return;
			}

			packet.resetChecksum();

			//Check for network interfaces
			for(String iName: this.interfaces) {
				Iface interface = this.interfaces.get(iName);
				//Invariant: interface != null
				if (packet.getDestinationAddress() == interface.getIpAddress()) {
					//Dest Addr == Interface IP Addr -> Drop packet
					return;
				}
			}

			//Forward IP Packet (as Ethernet packet)
			RouteEntry match = routeTable.lookup(packet.getDestinationAddress());
			if (match == null) {
				//No entry matched with the destination address -> drop packet
				return;
			}
			

			//Get Next-Hop IP Address (if gatewayAddress = 0 then next-hop is destination address)
			int nextHopIP = match.getGatewayAddress() == 0 ? packet.getDestinationAddress() : match.getGatewayAddress();

			//MAC address corresponding to next-hop IP from the ARP cache
			ArpEntry tgt = arpCache.lookup(nextHopIP);
			// assert(tgt != null);
			//set destination MAC address to next-hop's MAC address
			etherPacket.setDestinationMACAddress(tgt.getMac().toBytes());

			//set source MAC address to interface's (outgoing) MAC address
			etherPacket.setSourceAddress(match.getInterface().getMacAddress().toBytes());

			this.sendPacket(etherPacket, match.getInterface());

		}
		
		/********************************************************************/
	}
}
