package org.example;

import org.onlab.packet.DHCP;
import org.onlab.packet.DHCPOption;
import org.onlab.packet.DHCPPacketType;
import org.onlab.packet.DeserializationException;
import org.onosproject.dhcp.DhcpService;
import org.onosproject.dhcp.DhcpStore;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;

public class Server {
    public static void main( String[] args ) {
        final int port = 67;
        byte [] buffer = new byte[1024];
        try {
            //se crea variable para abrir el socket pueto 67
            DatagramSocket socketUDP = new DatagramSocket(port);
            //se  crea variable para recibir el paquete del cliente
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("servidor inciado");
            for (;;){
                //se espera a recibir el paquete 
                socketUDP.receive(packet);
                System.out.println("se recibe la info del cliete");
                System.out.println("se convierte el mensaje");
                //se recibe y se comvierte el arreglo de bytes a objeto DCHP
                DHCP mensaje = DHCP.deserializer().deserialize(packet.getData(), 0, packet.getLength());
                //imprimir mensaje
                System.out.println(mensaje.toString());
                //imprimir las opciones
                System.out.println(mensaje.getOptions().toString());
                //se recorre el arreglo de las opciones y se busca cada una de ellas 
                for (DHCPOption op: mensaje.getOptions()){
                    //se verifica las opciones existentes
                    if (op.getCode() == DHCP.DHCPOptionCode.OptionCode_MessageType.getValue()){
                        int tipoMensaje = new BigInteger(op.getData()).intValue();
                        //se verifica que tipo me mensaje envia el cliente
                        /*  
                        si el cliente manda
                        Discover respondo con Offer
                        Request responde Ack
                        Decline responde Ack */       
                        if (tipoMensaje == DHCPPacketType.DHCPDISCOVER.getValue()){
                            System.out.println("mensaje Discover");
                        }
                        if (tipoMensaje == DHCPPacketType.DHCPREQUEST.getValue()){
                            System.out.println("mensaje Request");
                        }
                        if (tipoMensaje == DHCPPacketType.DHCPDECLINE.getValue()){
                            System.out.println("mensaje Decline");
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DeserializationException e) {
            throw new RuntimeException(e);
        }
    }
}
