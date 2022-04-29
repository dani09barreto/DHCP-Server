package org.example.Data;

import org.onlab.packet.Ip4Address;
import org.onosproject.dhcp.IpAssignment;

import java.awt.*;
import java.util.ArrayList;

public class IpAddress {
    private Integer prefijo;
    private Ip4Address ipAddress;
    private Ip4Address ipMask;
    private Ip4Address ipGateway;
    private Ip4Address ipDNS1;
    private Ip4Address ipDNS2;
    private Status status;
    private ArrayList<IpAddress> direcciones = new ArrayList<>();

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(Integer prefijo) {
        this.prefijo = prefijo;
    }
    public ArrayList<IpAddress> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(ArrayList<IpAddress> direcciones) {
        this.direcciones = direcciones;
    }

    public enum Status {
        Asignada,
        NoAsignada,
        Reservada
    }

    private ArrayList<Ip4Address> ipsExclude = new ArrayList<>();

    public Ip4Address getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(Ip4Address ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Ip4Address getIpMask() {
        return ipMask;
    }

    public void setIpMask(Ip4Address ipMask) {
        this.ipMask = ipMask;
    }

    public Ip4Address getIpGateway() {
        return ipGateway;
    }

    public void setIpGateway(Ip4Address ipGateway) {
        this.ipGateway = ipGateway;
    }

    public Ip4Address getIpDNS1() {
        return ipDNS1;
    }

    public void setIpDNS1(Ip4Address ipDNS1) {
        this.ipDNS1 = ipDNS1;
    }

    public Ip4Address getIpDNS2() {
        return ipDNS2;
    }

    public void setIpDNS2(Ip4Address ipDNS2) {
        this.ipDNS2 = ipDNS2;
    }

    public ArrayList<Ip4Address> getIpsExclude() {
        return ipsExclude;
    }

    public void setIpsExclude(ArrayList<Ip4Address> ipsExclude) {
        this.ipsExclude = ipsExclude;
    }

    public IpAddress() {
    }
}
