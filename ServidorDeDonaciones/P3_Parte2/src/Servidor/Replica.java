package Servidor;

import java.util.ArrayList;

public abstract class Replica implements ReplicaReplica_I {
    private ArrayList<Entidad> entidades;
    private int idReplica;
    int subTotalDonado;
    int numeroDonacionesReplica;

    Replica (int id){
        entidades = new ArrayList<Entidad>();
        subTotalDonado = 0;
        idReplica = id;
        numeroDonacionesReplica = 0;
    }

    ArrayList<Entidad> getEntidades(){
        return entidades;
    }

    public synchronized int getIDReplica(){
        return idReplica;
    }

    public boolean contieneEntidad(int idEntidad){
        boolean contieneEntidad = false;
        for (int i=0; i<entidades.size() && !contieneEntidad; i++){
            if (entidades.get(i).getIdCliente() == idEntidad){
                contieneEntidad = true;
            }
        }
        return contieneEntidad;
    }

    public synchronized void aniadirCliente(int id, String nombre){
        Entidad entidad = new Entidad(id, nombre);
        entidades.add(entidad);

        System.out.println("(Replica " + idReplica + ") Registrando cliente --> [idCliente: " + Integer.toString(id) + " || Nombre: " + nombre + "]" );
    }

    public int numEntidadesEnReplica(){
        return entidades.size();
    }

    public int getSubTotalDonado(){
        return subTotalDonado;
    }

    public synchronized void aniadirDonacion(int cantidad, int idCliente){
        Entidad entidad = getEntidad(idCliente);
        if(entidad != null){
            entidad.nuevaDonacion(cantidad);
            subTotalDonado = subTotalDonado + cantidad;
            numeroDonacionesReplica ++;
            System.out.println("(Replica " + idReplica + ") Se ha registrado donacion (Replica " + idReplica + ") de: " + cantidad);
        }
    }

    public int getDonacionesCliente(int idCliente){
        Entidad entidad = getEntidad(idCliente);
        int cantidad = 0;
        if(entidad != null){
            cantidad = entidad.getTotalDonado();
        }
        return cantidad;
    }

    public int getNumDonaciones(){
        return numeroDonacionesReplica;
    }

    public void setNombreCliente(int idCliente, String nombre){
        getEntidad(idCliente).setNombre(nombre);
    }

    public String datosCliente(int idCliente){
        String nombre = getEntidad(idCliente).getNombreCliente();
        int totalDonado = getEntidad(idCliente).getTotalDonado();
        int numDonaciones = getEntidad(idCliente).getNumDonaciones();
        String cad = nombre + ", cliente con identificador " + idCliente + " ha donado una cantidad de " + totalDonado + " en " + numDonaciones + " donaciones";
        return cad;
    }

    public int getNumDonacionesCliente(int idCliente){
        return getEntidad(idCliente).getNumDonaciones();
    }
//**********************************************//
// Devuelve una entidad identificada por idCliente
//**********************************************//
    private Entidad getEntidad(int idCliente){
        boolean encontrado = false;
        Entidad entidad = null;
        for (int i=0; i<entidades.size() && !encontrado; i++) {
            if (entidades.get(i).getIdCliente() == idCliente) {
                encontrado = true;
                entidad = entidades.get(i);
            }
        }
        return entidad;
    }
}

