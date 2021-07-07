package Servidor;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class ReplicaControlador extends Replica implements ReplicaCliente_I
{
    private static ArrayList<String> replicas;

    ReplicaControlador(){
        super(0);

        replicas = new ArrayList<String>();
        replicas.add("ReplicaControlador");
    }

//**********************************************//
// Obtener total donado por cliente
//**********************************************//
    public synchronized int consultarTotalDonadoCliente(int idCliente){
        int num = -1;
        try {
            ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
            boolean entidadRegistrada = false;
            int replica = buscaClienteEnEntidad(idCliente, objetosReplicas);

            if (replica != -1){
                int cantidad = objetosReplicas.get(replica).getDonacionesCliente(idCliente);
                num = cantidad;
            }
        }catch (Exception e) {
            System.err.println("Replica: exception:");
            e.printStackTrace();
        }
        return num;
    }

//**********************************************//
// Total donaciones
//**********************************************//
    public synchronized int getTotalDonaciones(int idCliente)
    {
        int num = -1;
        if (consultarTotalDonadoCliente(idCliente) > 0) {
            int totalDonaciones = 0;
            try {
                ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
                for (int i = 0; i < replicas.size(); i++) {
                    totalDonaciones = totalDonaciones + objetosReplicas.get(i).getSubTotalDonado();
                }
            } catch (Exception e) {
                System.err.println("Replica: exception:");
                e.printStackTrace();
            }
            num = totalDonaciones;
        }
        return num;
    }

//**********************************************//
// Registrar cliente
//**********************************************//
    public synchronized String registrarCliente(int idCliente, String nombreCliente){
        String cad = "No se ha podido realizar el registro: Ya existe el cliente identificado por " + idCliente + ".";
        try {
            ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
            boolean entidadRegistrada = false;
            int replica = buscaClienteEnEntidad(idCliente, objetosReplicas);

            if (replica == -1){ //La entidad no esta registrada
                int minEntidades = getEntidades().size();//Se inicializan a los de esta replica
                int idReplicaARegistrar = getIDReplica();
                for (int i=0; i<replicas.size(); i++){
                    if(objetosReplicas.get(i).numEntidadesEnReplica() < minEntidades){
                        minEntidades = objetosReplicas.get(i).numEntidadesEnReplica();
                        idReplicaARegistrar = i;
                    }
                }
                //Se registra en la replica con menor número de entidades
                objetosReplicas.get(idReplicaARegistrar).aniadirCliente(idCliente, nombreCliente);
                cad = "Registro completado con exito";
            }
        } catch (Exception e) {
            System.err.println("Replica: exception:");
            e.printStackTrace();
        }
        return cad;
    }

//**********************************************//
// Donar
//**********************************************//
    public synchronized String donar(int idCliente, int cantidad){
        String cad = "No se ha podido realizar la donacion: Cliente no registrado.";
        if (cantidad > 0) {
            try {
                ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
                boolean entidadRegistrada = false;
                int replica = buscaClienteEnEntidad(idCliente, objetosReplicas);

                if (replica != -1) { //La entidad esta registrada
                    objetosReplicas.get(replica).aniadirDonacion(cantidad, idCliente);
                    cad = "Ha donado una cantidad de " + cantidad;
                }
            } catch (Exception e) {
                System.err.println("Replica: exception:");
                e.printStackTrace();
            }
        } else if (cantidad <= 0){
            cad = "Error: cantidad invalida";
        }
        return cad;
    }

//**********************************************//
// Comprueba si un cliente esta registrado en alguna de las replicas
//**********************************************//
    private synchronized int buscaClienteEnEntidad(int idCliente, ArrayList<ReplicaReplica_I> objetosReplicas) throws Exception{
        boolean entidadRegistrada = false;
        int replica = -1;
        for (int i = 0; i < replicas.size() && !entidadRegistrada; i++) {
            if (objetosReplicas.get(i).contieneEntidad(idCliente)){
                entidadRegistrada = true;
                replica = i;
            }
        }
        return replica;
    }

//**********************************************//
// Obtener objetos Replicas
//**********************************************//
    ArrayList<ReplicaReplica_I> obtenerArrayObjetosReplicas() throws Exception{
        ArrayList<ReplicaReplica_I> objetosReplicas = new ArrayList<ReplicaReplica_I>();
        for (int i=0; i<replicas.size(); i++){
            if (i == getIDReplica()){
                objetosReplicas.add(this); //Esta Replica
            } else{
                objetosReplicas.add (obtenerObjetoReplica((i)));
            }
        }

        return objetosReplicas;
    }

    ReplicaReplica_I obtenerObjetoReplica(int idReplica) throws Exception{
        Registry registry = LocateRegistry.getRegistry("localhost");
        ReplicaReplica_I object = (ReplicaReplica_I) registry.lookup(replicas.get(idReplica));
        return object;
    }

//**********************************************//
// Extra
//**********************************************//
    public int getNumeroDonacionesCliente(int idCliente) {
        int numDonaciones = 0;
        try {
            ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
            boolean entidadRegistrada = false;
            int replica = buscaClienteEnEntidad(idCliente, objetosReplicas);

            if (replica != -1) { //La entidad esta registrada
                numDonaciones = objetosReplicas.get(replica).getNumDonacionesCliente(idCliente);
            }
        } catch (Exception e) {
            System.err.println("Replica: exception:");
            e.printStackTrace();
        }
        return numDonaciones;
    }

    public int getNumeroDonacionesTotales(int idCliente) {
        int num = -1;
        if (consultarTotalDonadoCliente(idCliente) > 0) { //Ha donado al menos una vez
            int numTotalDonaciones = 0;
            try {
                ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
                for (int i = 0; i < replicas.size(); i++) {
                    numTotalDonaciones = numTotalDonaciones + objetosReplicas.get(i).getNumDonaciones();
                }
            } catch (Exception e) {
                System.err.println("Replica: exception:");
                e.printStackTrace();
            }
            num = numTotalDonaciones;
        }
        return num;
    }

    public int getReplicaMasCantidadDonada(int idCliente) {
        int replica = -1;
        int min = 0;
        if (consultarTotalDonadoCliente(idCliente) > 0) { //Ha donado al menos una vez
            try {
                ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
                for (int i = 0; i < replicas.size(); i++) {
                    int totalReplicaActual = objetosReplicas.get(i).getSubTotalDonado();
                    if (totalReplicaActual > min){
                        replica = i;
                        min = totalReplicaActual;
                    }
                }
            } catch (Exception e) {
                System.err.println("Replica: exception:");
                e.printStackTrace();
            }
        }
        return replica;
    }

    public int getReplicaMayorNumeroDonaciones(int idCliente) {
        int replica = -1;
        int min = 0;
        if (consultarTotalDonadoCliente(idCliente) > 0) { //Ha donado al menos una vez
            try {
                ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
                for (int i = 0; i < replicas.size(); i++) {
                    int totalReplicaActual = objetosReplicas.get(i).getNumDonaciones();
                    if (totalReplicaActual > min){
                        replica = i;
                        min = totalReplicaActual;
                    }
                }
            } catch (Exception e) {
                System.err.println("Replica: exception:");
                e.printStackTrace();
            }
        }
        return replica;
    }

    public String modificarNombreCliente(int idCliente, String nombre) {
        String cad = "No se ha podido modificar el nombre: Cliente no registrado";
        try {
            ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
            boolean entidadRegistrada = false;
            int replica = buscaClienteEnEntidad(idCliente, objetosReplicas);

            if (replica != -1) { //La entidad esta registrada
                objetosReplicas.get(replica).setNombreCliente(idCliente, nombre);
                cad = "Se ha modificado correctamente el nombre a " + nombre;
            }
        } catch (Exception e) {
            System.err.println("Replica: exception:");
            e.printStackTrace();
        }
        return cad;
    }

    public String obtenerDatosCliente(int idCliente) {
        String cad = "No se han podido obtener los datos: Cliente no registrado";
        try {
            ArrayList<ReplicaReplica_I> objetosReplicas = obtenerArrayObjetosReplicas();
            boolean entidadRegistrada = false;
            int replica = buscaClienteEnEntidad(idCliente, objetosReplicas);

            if (replica != -1) { //La entidad esta registrada
                cad = objetosReplicas.get(replica).datosCliente(idCliente);
            }
        } catch (Exception e) {
            System.err.println("Replica: exception:");
            e.printStackTrace();
        }
        return cad;
    }

    //MAIN
    public static void main(String[] args)
    {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String remoteObjectName = "ReplicaControlador";
            ReplicaCliente_I object = new ReplicaControlador();
            ReplicaCliente_I stub = (ReplicaCliente_I) UnicastRemoteObject.exportObject(object, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(remoteObjectName, stub);

            System.out.println("ReplicaControlador ready");

            Scanner s = new Scanner(System.in);
            System.out.println("Introduzca numero de replicas extra a lanzar");
            int numReplicas = 0;
            if (s.hasNext()) {
                numReplicas = Integer.parseInt(s.next());
            }
            s.close();

            ArrayList<Thread> replicasN = new ArrayList<Thread>();
            for (int i=0; i<numReplicas; i++){
                Thread replica = new Thread(new ReplicaN(i+1));
                replicasN.add(replica);
                replicas.add("Replica" + (i+1));
            }
            for (int i=0; i<numReplicas; i++){
                replicasN.get(i).start();
            }

        } catch (Exception e) {
            System.err.println("ReplicaControlador exception:");
            e.printStackTrace();
        }
    }
}
