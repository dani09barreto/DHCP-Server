package org.example.mensajes;

import org.apache.commons.lang3.ArrayUtils;
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

    public byte [] packetOffer(DHCP mensaje) throws UnknownHostException {
        System.out.println("Creando mensaje DHCPOFFER");
        DHCP mensajeOffer = new DHCP();
        Ip4Address gateway = Ip4Address.valueOf("10.30.4.9");
        Ip4Address ipasignada = Ip4Address.valueOf("10.30.4.10");
        Ip4Address mascara = Ip4Address.valueOf("255.255.255.248");
        Ip4Address DNSPrimario = Ip4Address.valueOf("10.10.1.200");
        Ip4Address DNSSecundario = Ip4Address.valueOf("10.10.1.201");
        Ip4Address ip0 = Ip4Address.valueOf("0.0.0.0");
        boolean flagIp = false;
        Ip4Address ipServidor = Ip4Address.valueOf(InetAddress.getLocalHost().getAddress());
        for (DHCPOption op: mensaje.getOptions()){
            if (op.getCode() == DHCP.DHCPOptionCode.OptionCode_RequestedIP.getValue()){
                flagIp = true;
                ipasignada = Ip4Address.valueOf(op.getData());
            }
        }
        mensajeOffer.setOpCode(DHCP.OPCODE_REPLY);
        mensajeOffer.setHardwareType(DHCP.HWTYPE_ETHERNET);
        mensajeOffer.setHardwareAddressLength((byte) 6);
        mensajeOffer.setHops((byte) 0);
        mensajeOffer.setTransactionId(randomId());
        mensajeOffer.setSeconds((byte) 0);
        mensajeOffer.setFlags((byte) 0);
        mensajeOffer.setClientIPAddress(mensaje.getClientIPAddress());
        mensajeOffer.setYourIPAddress(ipasignada.toInt());
        mensajeOffer.setServerIPAddress(ip0.toInt());
        mensajeOffer.setGatewayIPAddress(ip0.toInt());
        mensajeOffer.setClientHardwareAddress(mensaje.getClientHardwareAddress());
        System.out.println(Arrays.toString(mensajeOffer.getClientHardwareAddress()));
        //Opciones:
        List<DHCPOption> opciones = new ArrayList<DHCPOption>();

        DHCPOption opcionMensaje = new DHCPOption();
        opcionMensaje.setCode(DHCP.DHCPOptionCode.OptionCode_MessageType.getValue());
        opcionMensaje.setData(new byte[]{(byte) DHCPPacketType.DHCPOFFER.getValue()});
        opcionMensaje.setLength((byte) new byte[]{(byte) DHCPPacketType.DHCPOFFER.getValue()}.length);
        opciones.add(opcionMensaje);

        DHCPOption opcionServidor =  new DHCPOption();
        opcionServidor.setCode(DHCP.DHCPOptionCode.OptionCode_DHCPServerIp.getValue());
        opcionServidor.setData(ipServidor.toOctets());
        opcionServidor.setLength( (byte) ipServidor.toOctets().length);
        opciones.add(opcionServidor);

        DHCPOption opcionTiempoArrendado = new DHCPOption();
        opcionTiempoArrendado.setCode(DHCP.DHCPOptionCode.OptionCode_LeaseTime.getValue());
        opcionTiempoArrendado.setData(new byte[]{0,1,81, (byte) 128});
        opcionTiempoArrendado.setLength((byte) new byte[]{0,1,81, (byte) 128}.length);
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
        opcionDNS.setData(ArrayUtils.addAll(DNSPrimario.toOctets(), DNSSecundario.toOctets()));
        opcionDNS.setLength((byte)ArrayUtils.addAll(DNSPrimario.toOctets(), DNSSecundario.toOctets()).length);
        opciones.add(opcionDNS);
        mensajeOffer.setOptions(opciones);

        DHCPOption opcionFinal = new DHCPOption();
        opcionFinal.setCode(DHCP.DHCPOptionCode.OptionCode_END.getValue());
        opcionFinal.setData(new byte[]{(byte) 255});
        opcionFinal.setLength((byte)new byte[]{(byte) 255}.length);
        opciones.add(opcionFinal);

        System.out.println(mensajeOffer.toString());
        System.out.println(mensajeOffer.getOptions().toString());
        System.out.println("mensaje OFFER CREADO");
        return mensajeOffer.serialize();
    }
    public byte [] packetACK(DHCP mensaje){
        byte [] data = null;
        return data;
    }
    public int randomId(){
        return (int) Math.floor((Math.random()*100));
    }
}
