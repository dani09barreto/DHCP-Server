package org.example;

import com.google.common.net.InetAddresses;
import org.onlab.packet.DHCP;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main( String[] args ) {
        final int port = 68;
        byte [] buffer = new byte[1024];

        try {
            System.out.println("servidor inciado");
            DatagramSocket socketUDP = new DatagramSocket(port);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(packet);
            System.out.println("se recibe la info del cliete");
            System.out.println("se convierte el mensaje");
            DHCP mensaje = new DHCP();
            mensaje.deserialize(packet.getData(), 4, packet.getData().length);


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
