package Servidor;

public class Entidad {
    private int idCliente;
    private String nombreCliente;
    private int totalDonado;
    private int numDonaciones;

    Entidad(int id, String nombre){
        idCliente = id;
        nombreCliente = nombre;
        totalDonado = 0;
        numDonaciones = 0;
    }

    void nuevaDonacion (int cantidad){
        if (cantidad > 0) {
            totalDonado = totalDonado + cantidad;
            numDonaciones ++;
        }
    }

    int getIdCliente(){
        return idCliente;
    }

    String getNombreCliente(){
        return nombreCliente;
    }

    int getTotalDonado(){
        return totalDonado;
    }

    void setNombre(String nombre){
        nombreCliente = nombre;
    }

    int getNumDonaciones(){
        return numDonaciones;
    }
}
