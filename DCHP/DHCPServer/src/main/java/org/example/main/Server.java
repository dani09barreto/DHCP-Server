package org.example.main;

import org.example.Data.Direccion;
import org.example.Data.IpAddress;
import org.example.mensajes.Mensaje;
import org.onlab.packet.*;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {


    public static void main( String[] args ) throws UnknownHostException {
        final int port = 67;
        byte [] data = null;
        Ip4Address broadcast = Ip4Address.valueOf("255.255.255.255");
        InetAddress enviarBroadcast = InetAddress.getByAddress(broadcast.toOctets());
        ArrayList<IpAddress> DireccionesRed =  Direccion.ReadFile();
        //Ip4Address ipServidor = Ip4Address.valueOf(InetAddress.getLocalHost().getAddress());
        Ip4Address ipServidor = Ip4Address.valueOf("10.30.4.11");
        Ip4Address GateWServer = Ip4Address.valueOf("0.0.0.0");
        Direccion.poolDirecciones(DireccionesRed, ipServidor);
        DHCPPacketType packetType = null;
        DHCPOption opcionMensaje = null;
        DHCP mensajeEnviar = new DHCP();
        DHCP mensajeOffer;
        ArrayList <DHCP> mensajesEnviados = new ArrayList<>();

        try {
            //se crea variable para abrir el socket pueto 67
            DatagramSocket socketUDP = new DatagramSocket(port);
            //se  crea variable para recibir el paquete del cliente
            System.out.println("----------SERVIDOR DHCP INICIADO---------------");

            while (true){
                byte [] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                //se espera a recibir el paquete 
                socketUDP.receive(packet);
                System.out.println("-----SE RECIBE EL MENSAJE----");
                System.out.println("-------------------------------");
                //se recibe y se comvierte el arreglo de bytes a objeto DCHP
                DHCP mensaje = DHCP.deserializer().deserialize(packet.getData(), 0, packet.getLength());
                System.out.println();
                //imprimir las opciones
                System.out.println(mensaje.getOptions().toString());
                //se recorre el arreglo de las opciones y se busca cada una de ellas 
                for (DHCPOption op: mensaje.getOptions()){
                    //se verifica las opciones existentes
                    if (op.getCode() == DHCP.DHCPOptionCode.OptionCode_MessageType.getValue()){
                        opcionMensaje = op;
                        int tipoMensaje = new BigInteger(op.getData()).intValue();
                        //se verifica que tipo me mensaje envia el cliente
                        /*  
                        si el cliente manda
                        Discover respondo con Offer
                        Request responde Ack
                        Decline responde Ack
                        */
                        if (tipoMensaje == DHCPPacketType.DHCPDISCOVER.getValue()){
                            System.out.println("mensaje Discover");
                            packetType = DHCPPacketType.DHCPDISCOVER;
                        }
                        if (tipoMensaje == DHCPPacketType.DHCPREQUEST.getValue()){
                            System.out.println("mensaje Request");
                            packetType = DHCPPacketType.DHCPREQUEST;
                        }
                        if (tipoMensaje == DHCPPacketType.DHCPRELEASE.getValue()){
                            System.out.println("mensaje Decline");
                            //crearMensaje.packetACK(mensaje);
                        }
                    }
                }
                System.out.println("-------- MENSAJE -----------");
                System.out.println( "MAC: "+ Arrays.toString(mensaje.getClientHardwareAddress()) + " Mensaje: " + packetType.toString());

                if (packetType == DHCPPacketType.DHCPDISCOVER){
                    mensajeEnviar = Mensaje.existeMensaje(mensaje.getTransactionId(), mensajesEnviados);
                    if (mensajeEnviar == null)
                        mensajeEnviar = Mensaje.packetOffer(mensaje, mensajesEnviados);
                    data = mensajeEnviar.serialize();
                }
                else if (packetType == DHCPPacketType.DHCPREQUEST){
                    mensajeOffer = Mensaje.existeMensaje(mensaje.getTransactionId(), mensajesEnviados);
					
                    if (mensajeOffer == null)
                        throw new RuntimeException();
					
                    mensajeEnviar = Mensaje.packetACK(mensaje, mensajeOffer);

                }
                if (mensajeEnviar != null){
                    Ip4Address ip = Ip4Address.valueOf(mensajeEnviar.getYourIPAddress());
                    InetAddress direccionEnvio = InetAddress.getByAddress(ip.toOctets());
                    DatagramPacket respuesta = new DatagramPacket(data, data.length, direccionEnvio, 68);
                    System.out.println("Enviando respuesta a el cliente");
                    socketUDP.send(respuesta);
                    System.out.println("enviado");
                }
            }

        }
        catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DeserializationException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
