package org.example;

import org.onlab.packet.DHCP;
import org.onlab.packet.DHCPOption;
import org.onlab.packet.DHCPPacketType;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException, SocketException {
        InetAddress localHost = InetAddress.getLocalHost();
        NetworkInterface ni =  NetworkInterface.getByInetAddress(localHost);
        List<DHCPOption> options = new ArrayList<>();
        System.out.println( "Hello World!" );
        DHCP Discover = new DHCP();
        Discover.setOpCode(DHCP.OPCODE_REQUEST);
        Discover.setHardwareType(DHCP.HWTYPE_ETHERNET);
        Discover.setHardwareAddressLength((byte) 6);
        Discover.setHops((byte)0);
        Discover.setTransactionId(1);
        Discover.setSeconds((short) 0);
        Discover.setFlags((short) 1);
        Discover.setClientIPAddress(0);
        Discover.setYourIPAddress(0);
        Discover.setGatewayIPAddress(0);
        Discover.setClientHardwareAddress(ni.getHardwareAddress());
        Discover.setServerName(null);
        Discover.setBootFileName(null);



        System.out.println(Discover.toString());
    }
}
