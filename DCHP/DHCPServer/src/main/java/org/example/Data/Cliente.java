package org.example.Data;

import org.onlab.packet.Ip4Address;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Cliente {
    private byte [] hosMac;
    private Ip4Address ipAsiganda;
    private Calendar horaAsignacion;
    private Calendar horaRenovacion;

    private static ArrayList <Cliente> clientesasignados = new ArrayList<>();

    public byte[] getHosMac() {
        return hosMac;
    }

    public void setHosMac(byte[] hosMac) {
        this.hosMac = hosMac;
    }

    public Ip4Address getIpAsiganda() {
        return ipAsiganda;
    }

    public void setIpAsiganda(Ip4Address ipAsiganda) {
        this.ipAsiganda = ipAsiganda;
    }

    public Calendar getHoraAsignacion() {
        return horaAsignacion;
    }
    public String getHoraAsignacionString() {
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return fecha.format(horaAsignacion.getTime());
    }

    public void setHoraAsignacion(Calendar horaAsignacion) {
        this.horaAsignacion = horaAsignacion;
    }

    public Calendar getHoraRenovacion() {
        return horaRenovacion;
    }

    public String getHoraRenovacionString() {
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return fecha.format(horaRenovacion.getTime());
    }

    public void setHoraRenovacion(Calendar horaRenovacion) {
        this.horaRenovacion = horaRenovacion;
    }

    public ArrayList<Cliente> getClientesasignados() {
        return clientesasignados;
    }

    public void setClientesasignados(ArrayList<Cliente> clientesasignados) {
        this.clientesasignados = clientesasignados;
    }

    public static void writeLog (Cliente cliente){
        try (FileWriter fw = new FileWriter("C:\\Users\\santi\\Desktop\\Redes\\DHCP-server\\DHCP\\src\\main\\java\\org\\example\\Data\\File\\log.txt", true)){
            if (existClient(cliente.getHosMac()) == null){
                clientesasignados.add(cliente);
                fw.write("cliente Mac: "+ Arrays.toString(cliente.getHosMac()) + "IP: "+ cliente.getIpAsiganda().toString()+ " Hora Asignacion: "+cliente.getHoraAsignacionString()+ "Hora Revocacion: "+cliente.getHoraRenovacionString()+ "\n" );
            }
        } catch (IOException e) {
            System.out.println("No se pudo crear el archivo");
        }
    }

    public static Cliente existClient (byte [] macClient){
        for (Cliente cl : clientesasignados){
            if (Arrays.equals(cl.getHosMac(), macClient)){
                return cl;
            }
        }
        return null;
    }
}
