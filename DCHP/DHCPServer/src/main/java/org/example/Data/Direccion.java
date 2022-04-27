package org.example.Data;

import org.onlab.packet.Ip4Address;
import org.onosproject.dhcp.IpAssignment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Direccion {
    private List<IpAddress> direcciones = new ArrayList<>();

    public void ReadFile() {
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
                        System.out.println(1);
                        System.out.println(Ipadress.getIpAddress());
                    } else if (tok.equals("M")) {
                        Ipadress.setIpMask(Ip4Address.valueOf(tokens.nextToken()));
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
    }
}
