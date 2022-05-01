package org.example.mensajes;

import org.apache.commons.lang3.ArrayUtils;
import org.example.Data.Direccion;
import org.example.Data.IpAddress;
import org.onlab.packet.DHCP;
import org.onlab.packet.DHCPOption;
import org.onlab.packet.DHCPPacketType;
import org.onlab.packet.Ip4Address;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mensaje {

    public static DHCP packetOffer(DHCP mensaje, ArrayList <DHCP> enviados, Ip4Address GateWServer, ArrayList<IpAddress> DireccionesRed, Ip4Address ipServidor) throws UnknownHostException {

        System.out.println("Creando mensaje DHCPOFFER");
        DHCP mensajeOffer = new DHCP();

        IpAddress ipAsignada = new IpAddress();

        for (DHCPOption op : mensaje.getOptions()) {
            if (op.getCode() == DHCP.DHCPOptionCode.OptionCode_RequestedIP.getValue()) {
                if (Direccion.AskedIp(Ip4Address.valueOf(op.getData()), DireccionesRed)) {
                    ipAsignada = Direccion.Exits(Ip4Address.valueOf(op.getData()), DireccionesRed);
                    Direccion.ChangeS(ipAsignada.getIpAddress(), DireccionesRed, IpAddress.Status.Reservada);
                } else {
                    ipAsignada = Direccion.pedirDireccion(GateWServer, DireccionesRed, Ip4Address.valueOf(mensaje.getClientIPAddress()));
                    Direccion.ChangeS(ipAsignada.getIpAddress(), DireccionesRed, IpAddress.Status.Reservada);
                }
				
        mensajeOffer.setOpCode(DHCP.OPCODE_REPLY);
        mensajeOffer.setHardwareType(DHCP.HWTYPE_ETHERNET);
        mensajeOffer.setHardwareAddressLength((byte) 6);
        mensajeOffer.setHops((byte) 0);
        mensajeOffer.setTransactionId(mensaje.getClientIPAddress());
        mensajeOffer.setSeconds((byte) 0);
        mensajeOffer.setFlags((byte) 0);
        mensajeOffer.setClientIPAddress(mensaje.getClientIPAddress());
        mensajeOffer.setYourIPAddress(ipasignada.toInt());
        mensajeOffer.setServerIPAddress(ipServidor.toInt());
        mensajeOffer.setGatewayIPAddress(gateway.toInt());
        mensajeOffer.setClientHardwareAddress(mensaje.getClientHardwareAddress());
        System.out.println(Arrays.toString(mensajeOffer.getClientHardwareAddress()));
        //Opciones:
        List<DHCPOption> opciones = new ArrayList<DHCPOption>();

        DHCPOption opcionMensaje = new DHCPOption();
        opcionMensaje.setCode(DHCP.DHCPOptionCode.OptionCode_MessageType.getValue());
        opcionMensaje.setData(new byte[]{(byte) DHCPPacketType.DHCPOFFER.getValue()});
        opcionMensaje.setLength((byte) new byte[]{(byte) DHCPPacketType.DHCPOFFER.getValue()}.length);
        opciones.add(opcionMensaje);

        DHCPOption opcionServidor = new DHCPOption();
        opcionServidor.setCode(DHCP.DHCPOptionCode.OptionCode_DHCPServerIp.getValue());
        opcionServidor.setData(ipServidor.toOctets());
        opcionServidor.setLength((byte) ipServidor.toOctets().length);
        opciones.add(opcionServidor);

        DHCPOption opcionTiempoArrendado = new DHCPOption();
        opcionTiempoArrendado.setCode(DHCP.DHCPOptionCode.OptionCode_LeaseTime.getValue());
        opcionTiempoArrendado.setData(new byte[]{0, 1, 81, (byte) 128});
        opcionTiempoArrendado.setLength((byte) new byte[]{0, 1, 81, (byte) 128}.length);
        opciones.add(opcionTiempoArrendado);

        DHCPOption opcionMascara = new DHCPOption();
        opcionMascara.setCode(DHCP.DHCPOptionCode.OptionCode_SubnetMask.getValue());
        opcionMascara.setData(mascara.toOctets());
        opcionMascara.setLength((byte) mascara.toOctets().length);
        opciones.add(opcionMascara);

        DHCPOption opcionRouter = new DHCPOption();
        opcionRouter.setCode(DHCP.DHCPOptionCode.OptionCode_RouterAddress.getValue());
        opcionRouter.setData(gateway.toOctets());
        opcionRouter.setLength((byte) gateway.toOctets().length);
        opciones.add(opcionRouter);

        DHCPOption opcionDNS = new DHCPOption();
        opcionDNS.setCode(DHCP.DHCPOptionCode.OptionCode_DomainServer.getValue());

        opcionDNS.setData(ArrayUtils.addAll(ipAsignada.getIpDNS1().toOctets(), ipAsignada.getIpDNS2().toOctets()));
        opcionDNS.setLength((byte) ArrayUtils.addAll(ipAsignada.getIpDNS1().toOctets(), ipAsignada.getIpDNS2().toOctets()).length);

        opciones.add(opcionDNS);

        DHCPOption opcionFinal = new DHCPOption();
        opcionFinal.setCode(DHCP.DHCPOptionCode.OptionCode_END.getValue());
        opcionFinal.setData(new byte[]{(byte) 255});
        opcionFinal.setLength((byte) new byte[]{(byte) 255}.length);
        opciones.add(opcionFinal);

        mensajeOffer.setOptions(opciones);
        System.out.println("mensaje OFFER CREADO");
        enviados.add(mensajeOffer);
        return mensajeOffer;
    }


    public static DHCP packetACK(DHCP mensaje, DHCP Offer, ArrayList<IpAddress> DireccionesRed) {
		
        Direccion.ChangeS(Ip4Address.valueOf(mensaje.getClientIPAddress()),DireccionesRed, IpAddress.Status.Asignada);
		
        DHCP Ackmessage = new DHCP();
        Ip4Address Ipadress = Ip4Address.valueOf(Offer.getClientIPAddress());
        Ip4Address IpServer = Ip4Address.valueOf(Offer.getServerIPAddress());
        Ip4Address GateWay = Ip4Address.valueOf(Offer.getGatewayIPAddress());
        Ip4Address Mask = Ip4Address.valueOf("255.255.255.248");
        Ip4Address DNSPrimario = Ip4Address.valueOf("10.10.1.200");
        Ip4Address DNSSecundario = Ip4Address.valueOf("10.10.1.201");

        Ackmessage.setOpCode(DHCP.OPCODE_REPLY);
        Ackmessage.setHardwareType(DHCP.HWTYPE_ETHERNET);
        Ackmessage.setHardwareAddressLength((byte) 6);
        Ackmessage.setHops((byte) 0);
        Ackmessage.setTransactionId(mensaje.getTransactionId());
        Ackmessage.setSeconds((byte) 0);
        Ackmessage.setFlags((byte) 0);
        Ackmessage.setClientIPAddress(mensaje.getClientIPAddress());
        Ackmessage.setYourIPAddress(Ipadress.toInt());
        Ackmessage.setServerIPAddress(IpServer.toInt());
        Ackmessage.setGatewayIPAddress(GateWay.toInt());
        Ackmessage.setClientHardwareAddress(mensaje.getClientHardwareAddress());

        ArrayList<DHCPOption> options = new ArrayList<DHCPOption>();
        DHCPOption MessageOption = new DHCPOption();
        MessageOption.setCode(DHCP.DHCPOptionCode.OptionCode_MessageType.getValue());
        MessageOption.setLength((byte) new byte[]{(byte) DHCPPacketType.DHCPACK.getValue()}.length);
        MessageOption.setData(new byte[]{(byte) DHCPPacketType.DHCPACK.getValue()});
        options.add(MessageOption);

        DHCPOption ServerOption = new DHCPOption();
        ServerOption.setCode(DHCP.DHCPOptionCode.OptionCode_DHCPServerIp.getValue());
        ServerOption.setData(IpServer.toOctets());
        ServerOption.setLength((byte) IpServer.toOctets().length);
        options.add(ServerOption);

        DHCPOption LeaseOption = new DHCPOption();
        LeaseOption.setCode(DHCP.DHCPOptionCode.OptionCode_LeaseTime.getValue());
        LeaseOption.setData(new byte[]{0,1,81, (byte) 128});
        LeaseOption.setLength((byte) new byte[]{0,1,81, (byte) 128}.length);
        options.add(LeaseOption);

        DHCPOption SubnetOption = new DHCPOption();
        SubnetOption.setCode(DHCP.DHCPOptionCode.OptionCode_SubnetMask.getValue());
        SubnetOption.setData(Mask.toOctets());
        SubnetOption.setLength((byte) Mask.toOctets().length);
        options.add(SubnetOption);

        DHCPOption RouterOption = new DHCPOption();
        RouterOption.setCode(DHCP.DHCPOptionCode.OptionCode_RouterAddress.getValue());
        RouterOption.setData(GateWay.toOctets());
        RouterOption.setLength((byte) GateWay.toOctets().length);
        options.add(RouterOption);

        DHCPOption DomainServerOp = new DHCPOption();
        DomainServerOp.setCode(DHCP.DHCPOptionCode.OptionCode_DomainServer.getValue());
        DomainServerOp.setData(ArrayUtils.addAll(DNSPrimario.toOctets(), DNSSecundario.toOctets()));
        DomainServerOp.setLength((byte)ArrayUtils.addAll(DNSPrimario.toOctets(), DNSSecundario.toOctets()).length);
		
        options.add(DomainServerOp);

        DHCPOption EndOption = new DHCPOption();
        EndOption.setCode(DHCP.DHCPOptionCode.OptionCode_END.getValue());
        EndOption.setData(new byte[]{(byte) 255});
        EndOption.setLength((byte) new byte[]{(byte) 255}.length);
        options.add(EndOption);

        Ackmessage.setOptions(options);

        return Ackmessage;
    }

    //funcion busca en un arreglo de mensajes DHCP si existe uno de ellos con el id
    //esto para evitar contestar un request sin offer
    //o un discover reenviado
    public static DHCP existeMensaje (int idTransaccion, ArrayList <DHCP> mensajes){
        for (DHCP dhcpTemp : mensajes){
            if (dhcpTemp.getTransactionId() == idTransaccion){
                return dhcpTemp;
            }
        }
        return null;
    }
}
