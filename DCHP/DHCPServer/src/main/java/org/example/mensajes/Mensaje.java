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

    public static DHCP packetOffer(DHCP mensaje, ArrayList<DHCP> enviados, Ip4Address GateWServer, ArrayList<IpAddress> DireccionesRed, Ip4Address ipServidor, Ip4Address GateMensaje) throws UnknownHostException {

        System.out.println("Creando mensaje DHCPOFFER");
        DHCP mensajeOffer = new DHCP();

        IpAddress ipAsignada = new IpAddress();
        boolean asignado = true;
        for (DHCPOption op : mensaje.getOptions()) {
            if (op.getCode() == DHCP.DHCPOptionCode.OptionCode_RequestedIP.getValue()) {
                if (Direccion.AskedIp(Ip4Address.valueOf(op.getData()), DireccionesRed,GateMensaje)) {
                    asignado = false;
                    ipAsignada = Direccion.Exits(Ip4Address.valueOf(op.getData()), DireccionesRed);
                    Direccion.ChangeS(ipAsignada.getIpAddress(), DireccionesRed, IpAddress.Status.Reservada);
                }
            }
        }
        if (asignado){
            ipAsignada = Direccion.pedirDireccion(GateWServer, DireccionesRed, GateMensaje);
            Direccion.ChangeS(ipAsignada.getIpAddress(), DireccionesRed, IpAddress.Status.Reservada);
        }
        mensajeOffer.setOpCode(DHCP.OPCODE_REPLY);
        mensajeOffer.setHardwareType(DHCP.HWTYPE_ETHERNET);
        mensajeOffer.setHardwareAddressLength((byte) 6);
        mensajeOffer.setHops((byte) 0);
        mensajeOffer.setTransactionId(mensaje.getTransactionId());
        mensajeOffer.setSeconds((byte) 0);
        if (ipAsignada.getSubRed()){
            mensajeOffer.setFlags((byte) 0x8000);
        }else {
            mensajeOffer.setFlags((byte) 0);
        }
        mensajeOffer.setClientIPAddress(mensaje.getClientIPAddress());
        mensajeOffer.setYourIPAddress(ipAsignada.getIpAddress().toInt());
        mensajeOffer.setServerIPAddress(ipServidor.toInt());
        mensajeOffer.setGatewayIPAddress(ipAsignada.getIpGateway().toInt());
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
        opcionMascara.setData(ipAsignada.getIpMask().toOctets());
        opcionMascara.setLength((byte) ipAsignada.getIpMask().toOctets().length);
        opciones.add(opcionMascara);

        DHCPOption opcionRouter = new DHCPOption();
        opcionRouter.setCode(DHCP.DHCPOptionCode.OptionCode_RouterAddress.getValue());
        opcionRouter.setData(ipAsignada.getIpGateway().toOctets());
        opcionRouter.setLength((byte) ipAsignada.getIpGateway().toOctets().length);
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

    public static DHCP packetACKRelease(DHCP mensaje, ArrayList<IpAddress> DireccionesRed){
        System.out.println("Creando mensaje DHCPACK R");
        IpAddress ipAddress = Direccion.Exits(Ip4Address.valueOf(mensaje.getClientIPAddress()), DireccionesRed);
        DHCP Ackmessage = new DHCP();
        Ackmessage.setOpCode(DHCP.OPCODE_REPLY);
        Ackmessage.setHardwareType(DHCP.HWTYPE_ETHERNET);
        Ackmessage.setHardwareAddressLength((byte) 6);
        Ackmessage.setHops((byte) 0);
        Ackmessage.setTransactionId(mensaje.getTransactionId());
        Ackmessage.setSeconds((byte) 0);
        if (ipAddress.getSubRed()){
            Ackmessage.setFlags((byte) 0x8000);
        }else {
            Ackmessage.setFlags((byte) 0);
        }
        Ackmessage.setClientIPAddress(Ip4Address.valueOf("0.0.0.0").toInt());
        Ackmessage.setYourIPAddress(mensaje.getClientIPAddress());
        Ackmessage.setServerIPAddress(Ip4Address.valueOf("10.30.4.11").toInt());
        Ackmessage.setGatewayIPAddress(ipAddress.getIpGateway().toInt());
        Ackmessage.setClientHardwareAddress(mensaje.getClientHardwareAddress());

        ArrayList<DHCPOption> options = new ArrayList<DHCPOption>();
        DHCPOption MessageOption = new DHCPOption();
        MessageOption.setCode(DHCP.DHCPOptionCode.OptionCode_MessageType.getValue());
        MessageOption.setLength((byte) new byte[]{(byte) DHCPPacketType.DHCPACK.getValue()}.length);
        MessageOption.setData(new byte[]{(byte) DHCPPacketType.DHCPACK.getValue()});
        options.add(MessageOption);

        DHCPOption ServerOption = new DHCPOption();
        ServerOption.setCode(DHCP.DHCPOptionCode.OptionCode_DHCPServerIp.getValue());
        ServerOption.setData(Ip4Address.valueOf("10.30.4.11").toOctets());
        ServerOption.setLength((byte) Ip4Address.valueOf("10.30.4.11").toOctets().length);
        options.add(ServerOption);

        DHCPOption LeaseOption = new DHCPOption();
        LeaseOption.setCode(DHCP.DHCPOptionCode.OptionCode_LeaseTime.getValue());
        LeaseOption.setData(new byte[]{0, 1, 81, (byte) 128});
        LeaseOption.setLength((byte) new byte[]{0, 1, 81, (byte) 128}.length);
        options.add(LeaseOption);

        DHCPOption SubnetOption = new DHCPOption();
        SubnetOption.setCode(DHCP.DHCPOptionCode.OptionCode_SubnetMask.getValue());
        SubnetOption.setData(ipAddress.getIpMask().toOctets());
        SubnetOption.setLength((byte) ipAddress.getIpMask().toOctets().length);
        options.add(SubnetOption);

        DHCPOption RouterOption = new DHCPOption();
        RouterOption.setCode(DHCP.DHCPOptionCode.OptionCode_RouterAddress.getValue());
        RouterOption.setData(ipAddress.getIpGateway().toOctets());
        RouterOption.setLength((byte) ipAddress.getIpGateway().toOctets().length);
        options.add(RouterOption);

        DHCPOption DomainServerOp = new DHCPOption();
        DomainServerOp.setCode(DHCP.DHCPOptionCode.OptionCode_DomainServer.getValue());
        DomainServerOp.setData(ArrayUtils.addAll(ipAddress.getIpDNS1().toOctets(),ipAddress.getIpDNS2().toOctets()));
        DomainServerOp.setLength((byte) ArrayUtils.addAll(ipAddress.getIpDNS1().toOctets(),ipAddress.getIpDNS2().toOctets()).length);

        options.add(DomainServerOp);

        DHCPOption EndOption = new DHCPOption();
        EndOption.setCode(DHCP.DHCPOptionCode.OptionCode_END.getValue());
        EndOption.setData(new byte[]{(byte) 255});
        EndOption.setLength((byte) new byte[]{(byte) 255}.length);
        options.add(EndOption);

        Ackmessage.setOptions(options);
        return Ackmessage;
    }
    public static DHCP packetACK(DHCP mensaje, DHCP Offer, ArrayList<IpAddress> DireccionesRed) {
        System.out.println("Creando mensaje DHCPACK");
        Direccion.ChangeS(Ip4Address.valueOf(mensaje.getClientIPAddress()), DireccionesRed, IpAddress.Status.Asignada);
        IpAddress ipAddress = Direccion.Exits(Ip4Address.valueOf(Offer.getYourIPAddress()), DireccionesRed);
        DHCP Ackmessage = new DHCP();

        Ackmessage.setOpCode(DHCP.OPCODE_REPLY);
        Ackmessage.setHardwareType(DHCP.HWTYPE_ETHERNET);
        Ackmessage.setHardwareAddressLength((byte) 6);
        Ackmessage.setHops((byte) 0);
        Ackmessage.setTransactionId(mensaje.getTransactionId());
        Ackmessage.setSeconds((byte) 0);
        if (ipAddress.getSubRed()){
            Ackmessage.setFlags((byte) 0x8000);
        }else {
            Ackmessage.setFlags((byte) 0);
        }
        Ackmessage.setClientIPAddress(mensaje.getClientIPAddress());
        Ackmessage.setYourIPAddress(Offer.getYourIPAddress());
        Ackmessage.setServerIPAddress(Offer.getServerIPAddress());
        Ackmessage.setGatewayIPAddress(Offer.getGatewayIPAddress());
        Ackmessage.setClientHardwareAddress(mensaje.getClientHardwareAddress());

        ArrayList<DHCPOption> options = new ArrayList<DHCPOption>();
        DHCPOption MessageOption = new DHCPOption();
        MessageOption.setCode(DHCP.DHCPOptionCode.OptionCode_MessageType.getValue());
        MessageOption.setLength((byte) new byte[]{(byte) DHCPPacketType.DHCPACK.getValue()}.length);
        MessageOption.setData(new byte[]{(byte) DHCPPacketType.DHCPACK.getValue()});
        options.add(MessageOption);

        DHCPOption ServerOption = new DHCPOption();
        ServerOption.setCode(DHCP.DHCPOptionCode.OptionCode_DHCPServerIp.getValue());
        ServerOption.setData(Ip4Address.valueOf(Offer.getServerIPAddress()).toOctets());
        ServerOption.setLength((byte) Ip4Address.valueOf(Offer.getServerIPAddress()).toOctets().length);
        options.add(ServerOption);

        DHCPOption LeaseOption = new DHCPOption();
        LeaseOption.setCode(DHCP.DHCPOptionCode.OptionCode_LeaseTime.getValue());
        LeaseOption.setData(new byte[]{0, 1, 81, (byte) 128});
        LeaseOption.setLength((byte) new byte[]{0, 1, 81, (byte) 128}.length);
        options.add(LeaseOption);

        DHCPOption SubnetOption = new DHCPOption();
        SubnetOption.setCode(DHCP.DHCPOptionCode.OptionCode_SubnetMask.getValue());
        SubnetOption.setData(ipAddress.getIpMask().toOctets());
        SubnetOption.setLength((byte) ipAddress.getIpMask().toOctets().length);
        options.add(SubnetOption);

        DHCPOption RouterOption = new DHCPOption();
        RouterOption.setCode(DHCP.DHCPOptionCode.OptionCode_RouterAddress.getValue());
        RouterOption.setData(ipAddress.getIpGateway().toOctets());
        RouterOption.setLength((byte) ipAddress.getIpGateway().toOctets().length);
        options.add(RouterOption);

        DHCPOption DomainServerOp = new DHCPOption();
        DomainServerOp.setCode(DHCP.DHCPOptionCode.OptionCode_DomainServer.getValue());
        DomainServerOp.setData(ArrayUtils.addAll(ipAddress.getIpDNS1().toOctets(),ipAddress.getIpDNS2().toOctets()));
        DomainServerOp.setLength((byte) ArrayUtils.addAll(ipAddress.getIpDNS1().toOctets(),ipAddress.getIpDNS2().toOctets()).length);

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
    public static DHCP existeMensaje(int idTransaccion, ArrayList<DHCP> mensajes) {
        for (DHCP dhcpTemp : mensajes) {
            if (dhcpTemp.getTransactionId() == idTransaccion) {
                return dhcpTemp;
            }
        }
        return null;
    }
}