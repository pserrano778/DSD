package Servidor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReplicaCliente_I extends Remote {

    int getTotalDonaciones(int idCliente) throws RemoteException;
    String registrarCliente(int idCliente, String nombre) throws RemoteException;
    String donar(int idCliente, int cantidad) throws RemoteException;
    int consultarTotalDonadoCliente(int idCliente) throws RemoteException;
    int getNumeroDonacionesCliente(int idCliente) throws RemoteException;
    int getNumeroDonacionesTotales(int idCliente) throws RemoteException;
    int getReplicaMasCantidadDonada(int idCliente) throws RemoteException;
    int getReplicaMayorNumeroDonaciones(int idCliente) throws RemoteException;
    String modificarNombreCliente(int idCliente, String nombre) throws RemoteException;
    String obtenerDatosCliente(int idCliente) throws RemoteException;
}
