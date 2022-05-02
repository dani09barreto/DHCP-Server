package org.example.main;

import org.example.Data.Cliente;
import org.example.Data.Direccion;
import org.example.Data.IpAddress;
import org.example.mensajes.Mensaje;
import org.onlab.packet.*;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Server {


    public static void main( String[] args ) throws UnknownHostException {
        final int port = 67;
        byte [] data = null;
        Ip4Address broadcast = Ip4Address.valueOf("255.255.255.255");
        InetAddress enviarBroadcast = InetAddress.getByAddress(broadcast.toOctets());
        ArrayList<IpAddress> DireccionesRed =  Direccion.ReadFile();
        //Ip4Address ipServidor = Ip4Address.valueOf(InetAddress.getLocalHost().getAddress());
        Ip4Address ipServidor = Ip4Address.valueOf("10.30.4.11");
        Ip4Address GateWServer = Ip4Address.valueOf("10.30.4.9");
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
                try {
                    byte [] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    System.out.println("Recibiendo mensajes");
                    socketUDP.receive(packet);
                    System.out.println("-----SE RECIBE EL MENSAJE----");
                    System.out.println("-------------------------------");

                    DHCP mensaje = DHCP.deserializer().deserialize(packet.getData(), 0, packet.getLength());
                    System.out.println();

                    for (DHCPOption op: mensaje.getOptions()){
                        //se verifica las opciones existentes
                        if (op.getCode() == DHCP.DHCPOptionCode.OptionCode_MessageType.getValue()){
                            int tipoMensaje = new BigInteger(op.getData()).intValue();

                            if (tipoMensaje == DHCPPacketType.DHCPDISCOVER.getValue()){
                                System.out.println("mensaje Discover");
                                packetType = DHCPPacketType.DHCPDISCOVER;
                            }
                            if (tipoMensaje == DHCPPacketType.DHCPREQUEST.getValue()){
                                System.out.println("mensaje Request");
                                packetType = DHCPPacketType.DHCPREQUEST;
                            }
                            if (tipoMensaje == DHCPPacketType.DHCPRELEASE.getValue()){
                                System.out.println("mensaje release");
                                packetType = DHCPPacketType.DHCPRELEASE;
                            }
                        }
                    }
                    System.out.println("-------- MENSAJE -----------");
                    System.out.println( "MAC: "+ Arrays.toString(mensaje.getClientHardwareAddress()) + " Mensaje: " + packetType.toString());

                    if (packetType == DHCPPacketType.DHCPDISCOVER){
                        mensajeEnviar = Mensaje.existeMensaje(mensaje.getTransactionId(), mensajesEnviados);
                        if (mensajeEnviar == null)
                            mensajeEnviar = Mensaje.packetOffer(mensaje, mensajesEnviados, GateWServer, DireccionesRed, ipServidor, Ip4Address.valueOf(packet.getAddress()));
                        data = mensajeEnviar.serialize();
                    }
                    else if (packetType == DHCPPacketType.DHCPREQUEST){
                        mensajeOffer = Mensaje.existeMensaje(mensaje.getTransactionId(), mensajesEnviados);
                        IpAddress ip = Direccion.Exits(Ip4Address.valueOf(mensaje.getClientIPAddress()), DireccionesRed);

                        if (ip != null){
                            mensajeEnviar = Mensaje.packetACKRelease(mensaje, DireccionesRed);
                            data = mensajeEnviar.serialize();

                        } else if (mensajeOffer == null)
                            throw new RuntimeException();

                        if (ip == null && mensajeOffer != null){
                            mensajeEnviar = Mensaje.packetACK(mensaje, mensajeOffer, DireccionesRed);
                            data = mensajeEnviar.serialize();
                        }
                        System.out.println("Host: " + mensaje.getClientHardwareAddress() + "Ip Asignada "+ Ip4Address.valueOf(mensajeEnviar.getYourIPAddress()).toString());
                        Cliente cliente = new Cliente();
                        Calendar fechaActual = Calendar.getInstance();
                        Calendar fechaRevocacion = (Calendar) fechaActual.clone();
                        cliente.setHosMac(mensajeEnviar.getClientHardwareAddress());
                        cliente.setIpAsiganda(Ip4Address.valueOf(mensajeEnviar.getYourIPAddress()));
                        cliente.setHoraAsignacion(fechaActual);
                        fechaRevocacion.set(Calendar.DATE, fechaRevocacion.get(Calendar.DATE) + 1);
                        cliente.setHoraRenovacion(fechaRevocacion);
                        System.out.println("cliente Mac: "+ Arrays.toString(cliente.getHosMac()) + "IP: "+ cliente.getIpAsiganda().toString()+ " Hora Asignacion: "+cliente.getHoraAsignacionString()+ "Hora Revocacion: "+cliente.getHoraRenovacionString()+ "\n" );
                        Cliente.writeLog(cliente);

                    } else if (packetType == DHCPPacketType.DHCPRELEASE) {
                        Direccion.ChangeS(Ip4Address.valueOf(packet.getAddress()), DireccionesRed, IpAddress.Status.NoAsignada);
                        System.out.println("-----Se libera la direccion Ip-----");
                        System.out.println("Host: " + mensaje.getClientHardwareAddress().toString() + " Ip liberada: "+ packet.getAddress());
                    }
                    if (mensajeEnviar != null){
                        DatagramPacket respuesta;
                        IpAddress ip = Direccion.Exits(Ip4Address.valueOf(mensajeEnviar.getYourIPAddress()), DireccionesRed);
                        InetAddress direccionEnvio = InetAddress.getByAddress(ip.getIpGateway().toOctets());
                        if (ip.getSubRed()){
                            respuesta = new DatagramPacket(data, data.length, direccionEnvio, 67);
                        }else {
                            respuesta = new DatagramPacket(data, data.length, enviarBroadcast, 68);
                        }
                        System.out.println("Enviando respuesta a el cliente");
                        socketUDP.send(respuesta);
                        System.out.println("enviado");
                    }
                } catch (DeserializationException e) {
                    throw new RuntimeException(e);
                } catch (RuntimeException e){
                    System.out.println("No se puede asiganr ip");
                }
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
