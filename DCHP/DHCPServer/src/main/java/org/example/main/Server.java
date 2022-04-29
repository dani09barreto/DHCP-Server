package org.example.main;

import org.example.Data.Direccion;
import org.example.Data.IpAddress;
import org.example.mensajes.Mensaje;
import org.onlab.packet.*;
import org.onosproject.dhcp.DhcpService;
import org.onosproject.dhcp.DhcpStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Server {
    public static void main( String[] args ) throws UnknownHostException {
        final int port = 67;
        Mensaje crearMensaje =  new Mensaje();
        byte [] data = null;
        Ip4Address broadcast = Ip4Address.valueOf("255.255.255.255");
        InetAddress enviarBroadcast = InetAddress.getByAddress(broadcast.toOctets());
        ArrayList<IpAddress> DireccionesRed =  Direccion.ReadFile();
        int max;
        for (IpAddress ip: DireccionesRed){

            max = (int) Math.pow(2,(32-ip.getPrefijo()));
            max-=2;
            System.out.println(max);
            int Inicial=ip.getIpGateway().toInt();
            Inicial++;
            for(int i=0;i<max;i++){
                IpAddress temp = new IpAddress();
                temp.setIpAddress(Ip4Address.valueOf(Inicial));
                temp.setIpGateway(ip.getIpGateway());
                temp.setPrefijo(ip.getPrefijo());
                temp.setStatus(IpAddress.Status.NoAsignada);
                temp.setIpDNS1(ip.getIpDNS1());
                temp.setIpDNS2(ip.getIpDNS2());
                temp.setIpsExclude(ip.getIpsExclude());
                temp.setIpMask(ip.getIpMask());
                
                Inicial++;
                System.out.println(temp.getIpAddress());
            }
            System.out.println("Sub red");
        }
        try {
            //se crea variable para abrir el socket pueto 67
            DatagramSocket socketUDP = new DatagramSocket(port);
            //se  crea variable para recibir el paquete del cliente
            System.out.println("servidor inciado");

            while (true){
                byte [] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
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
                            data = crearMensaje.packetOffer(mensaje);
                        }
                        if (tipoMensaje == DHCPPacketType.DHCPREQUEST.getValue()){
                            System.out.println("mensaje Request");
                            crearMensaje.packetACK(mensaje,DHCP.deserializer().deserialize(data, 0, data.length));
                        }
                        if (tipoMensaje == DHCPPacketType.DHCPDECLINE.getValue()){
                            System.out.println("mensaje Decline");
                            //crearMensaje.packetACK(mensaje);
                        }
                        if (tipoMensaje == DHCPPacketType.DHCPRELEASE.getValue()){
                            System.out.println("mensaje Decline");
                            //crearMensaje.packetACK(mensaje);
                        }
                    }
                }
                System.out.println(enviarBroadcast);
                DatagramPacket respuesta = new DatagramPacket(data, data.length, enviarBroadcast, 68);
                System.out.println("Enviando respuesta a el cliente");
                socketUDP.send(respuesta);
                System.out.println("enviado");
            }

        }
        catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DeserializationException e) {
            throw new RuntimeException(e);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
