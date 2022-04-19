package org.example;

import com.google.common.net.InetAddresses;
import org.onlab.packet.DHCP;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main( String[] args ) {
        final int port = 65000;
        byte [] buffer = new byte[1024];

        try {
            System.out.println("servidor inciado");
            DatagramSocket socketUDP = new DatagramSocket(port);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(packet);
            System.out.println("se recibe la info del cliete");
            String mensaje = new String(packet.getData());
            System.out.println(mensaje);

            int clientPort = packet.getPort();
            InetAddress direccion = packet.getAddress();
            mensaje = "Servidor responde";
            buffer = mensaje.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packetRequest = new DatagramPacket(buffer, buffer.length, direccion, clientPort);
            System.out.println("se envia el mensaje");
            socketUDP.send(packetRequest);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
