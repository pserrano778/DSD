package Servidor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReplicaReplica_I extends Remote{
    boolean contieneEntidad(int idEntidad) throws RemoteException;
    int getSubTotalDonado() throws RemoteException;
    void aniadirCliente(int id, String nombre) throws RemoteException;
    int numEntidadesEnReplica() throws RemoteException;
    int getIDReplica() throws RemoteException;
    void aniadirDonacion(int cantidad, int idCliente) throws RemoteException;
    int getDonacionesCliente(int idCliente) throws RemoteException;
    int getNumDonaciones() throws RemoteException;
    void setNombreCliente(int idCliente, String nombre) throws RemoteException;
    String datosCliente(int idCliente) throws RemoteException;
    int getNumDonacionesCliente(int idCliente) throws RemoteException;
}
