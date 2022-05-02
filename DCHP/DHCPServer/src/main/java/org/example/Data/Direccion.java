package org.example.Data;

import org.jboss.netty.handler.ipfilter.IpSubnet;
import org.onlab.packet.DHCP;
import org.onlab.packet.Ip4Address;
import org.onosproject.dhcp.IpAssignment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Direccion {

    private static ArrayList<IpAddress> direcciones = new ArrayList<>();

    public static ArrayList<IpAddress> ReadFile() {
        try {
            boolean Exepcion = false;
            File file = new File("C:\\Users\\santi\\Desktop\\Redes\\DHCP-server\\DHCP\\src\\main\\java\\org\\example\\Data\\File\\Dir.txt");

            Scanner line = new Scanner(file);
            String content;
            //actualizar token
            while (line.hasNextLine()) { // lineas
                IpAddress Ipadress = new IpAddress();
                content = line.nextLine();
                String tok;
                StringTokenizer tokens = new StringTokenizer(content, ",");
                while (tokens.hasMoreTokens()) {// token de la linea
                    tok = tokens.nextToken();
                    if (tok.equals("R")) {
                        Ipadress.setIpAddress(Ip4Address.valueOf(tokens.nextToken()));
                    } else if (tok.equals("M")) {
                        Ipadress.setPrefijo(Integer.parseInt(tokens.nextToken()));
                        Ipadress.setIpMask(Ip4Address.makeMaskPrefix(Ipadress.getPrefijo()));
                    } else if (tok.equals("D1")) {
                        Ipadress.setIpDNS1(Ip4Address.valueOf(tokens.nextToken()));
                    } else if (tok.equals("D2")) {
                        Ipadress.setIpDNS2(Ip4Address.valueOf(tokens.nextToken()));
                    } else if (tok.equals("E")) {
                        Exepcion = true;
                    } else if (tok.equals("G")) {
                        Exepcion = false;
                        Ipadress.setIpGateway(Ip4Address.valueOf(tokens.nextToken()));
                        break;
                    } else if (Exepcion) {
                        Ipadress.getIpsExclude().add(Ip4Address.valueOf(tok));
                    }
                }
                direcciones.add(Ipadress);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No se encuentra!");
        }
        return direcciones;
    }

    public static void poolDirecciones(ArrayList<IpAddress> DireccionesRed, Ip4Address ipServidor) {
        int max;
        for (IpAddress ip : DireccionesRed) {
            max = (int) Math.pow(2, (32 - ip.getPrefijo()));
            max -= 2;
            int Inicial = ip.getIpGateway().toInt();
            Inicial++;
            for (int i = 0; i < max; i++) {
                boolean Valida = true;
                for (Ip4Address escluida : ip.getIpsExclude()) {
                    if (Ip4Address.valueOf(Inicial).equals(escluida))
                        Valida = false;
                }
                IpAddress temp = new IpAddress();
                if (Valida) {
                    temp.setIpAddress(Ip4Address.valueOf(Inicial));
                    temp.setIpGateway(ip.getIpGateway());
                    temp.setPrefijo(ip.getPrefijo());
                    temp.setStatus(IpAddress.Status.NoAsignada);
                    temp.setIpDNS1(ip.getIpDNS1());
                    temp.setIpDNS2(ip.getIpDNS2());
                    temp.setIpsExclude(ip.getIpsExclude());
                    temp.setIpMask(ip.getIpMask());
                    ip.getDirecciones().add(temp);
                    Inicial++;
                } else {
                    Inicial++;
                }
            }
        }
    }

    public static IpAddress pedirDireccion(Ip4Address GateServer, ArrayList<IpAddress> DireccionesRed, Ip4Address GateWayMessage) {
        if (GateWayMessage.equals(Ip4Address.valueOf("0.0.0.0"))) {
            for (IpAddress ip : DireccionesRed) {
                if (ip.getIpGateway().equals(GateServer)) {
                    for (IpAddress host : ip.getDirecciones()) {
                        if (host.getStatus().equals(IpAddress.Status.NoAsignada)) {
                            System.out.println(host.getIpAddress());
                            return host;
                        }
                    }
                }
            }
        } else {
            for (IpAddress ip : DireccionesRed) {
                if (ip.getIpGateway().equals(GateWayMessage)) {
                    for (IpAddress host : ip.getDirecciones()) {
                        if (host.getStatus().equals(IpAddress.Status.NoAsignada)) {
                            System.out.println(host.getIpAddress());
                            return host;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean AskedIp(Ip4Address asked, ArrayList<IpAddress> DireccionesRed, Ip4Address gateWay) {
        for (IpAddress ip : DireccionesRed) {
            if (gateWay.equals(ip.getIpGateway())) {
                for (IpAddress host : ip.getDirecciones()) {
                    if (host.getIpAddress().equals(asked) && host.getStatus().equals(IpAddress.Status.NoAsignada)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static IpAddress Exits(Ip4Address asked, ArrayList<IpAddress> DireccionesRed) {
        for (IpAddress ip : DireccionesRed) {
            for (IpAddress host : ip.getDirecciones()) {
                if (host.getIpAddress().equals(asked)) {
                    return host;
                }
            }
        }
        return null;
    }

    public static void ChangeS(Ip4Address asked, ArrayList<IpAddress> DireccionesRed, IpAddress.Status state) {
        for (IpAddress ip : DireccionesRed) {
            for (IpAddress host : ip.getDirecciones()) {
                if (host.getIpAddress().equals(asked)) {
                    host.setStatus(state);
                }
            }
        }
    }
}
