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
            File file = new File("C:\\Users\\santi\\OneDrive - Pontificia Universidad Javeriana\\Desktop\\U-SANTI\\Semestre 3\\POO\\02-feb\\src\\entities\\Dir.txt");
            Scanner line = new Scanner(file);
            String content;
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
                    } else {
                        if (tok.equals("G")) {
                            Ipadress.setIpGateway(Ip4Address.valueOf(tokens.nextToken()));
                            break;
                        }
                        Ipadress.getIpsExclude().add(Ip4Address.valueOf(tokens.nextToken()));
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
    public static void poolDirecciones (ArrayList<IpAddress> DireccionesRed, Ip4Address ipServidor){
        int max;
        for (IpAddress ip: DireccionesRed){
            max = (int) Math.pow(2,(32-ip.getPrefijo()));
            max-=2;
            int Inicial=ip.getIpGateway().toInt();
            Inicial ++;
            for(int i=0;i<max;i++){
                IpAddress temp = new IpAddress();
                if (!(Inicial == ipServidor.toInt())){
                    temp.setIpAddress(Ip4Address.valueOf(Inicial));
                    temp.setIpGateway(ip.getIpGateway());
                    temp.setPrefijo(ip.getPrefijo());
                    temp.setStatus(IpAddress.Status.NoAsignada);
                    temp.setIpDNS1(ip.getIpDNS1());
                    temp.setIpDNS2(ip.getIpDNS2());
                    temp.setIpsExclude(ip.getIpsExclude());
                    temp.setIpMask(ip.getIpMask());
                    ip.getDirecciones().add(temp);
                    Inicial ++;
                }else{
                    Inicial ++;
                }
            }
        }
    }

    public static IpAddress pedirDireccion (Ip4Address direccionOrigen, Ip4Address ipServidor, ArrayList<IpAddress> DireccionesRed){

        for (IpAddress red :DireccionesRed){
            if (ipServidor == direccionOrigen){
                IpAddress subRed = existeIp(ipServidor, DireccionesRed);
                for (IpAddress ipsub : subRed.getDirecciones()){
                    if (ipsub.getStatus() == IpAddress.Status.NoAsignada)
                        ipsub.setStatus(IpAddress.Status.Asignada);
                    return ipsub;
                }
            }else{
                IpAddress subRed = existeIp( direccionOrigen, DireccionesRed);
                for (IpAddress ipsub : subRed.getDirecciones()){
                    if (ipsub.getStatus() == IpAddress.Status.NoAsignada)
                        ipsub.setStatus(IpAddress.Status.Asignada);
                    return ipsub;
                }
            }
        }
        return null;
    }

    public static IpAddress existeIp (Ip4Address ip, ArrayList<IpAddress> Direciones){
        for (IpAddress redTem : Direciones) {
            if (redTem.getIpGateway().equals(ip)){
                return redTem;
            }
        }
        return null;
    }
    public static boolean liberarIP (Ip4Address ip, ArrayList<IpAddress> Direciones){
        for (IpAddress redTem : Direciones) {
            if (redTem.getIpGateway().equals(ip)){
                redTem.setStatus(IpAddress.Status.NoAsignada);
                return true;
            }
        }
        return false;
    }
}
