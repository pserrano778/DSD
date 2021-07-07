package Cliente;

import Servidor.ReplicaCliente_I;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            int idCliente = -1;
            Scanner s = new Scanner(System.in);
            System.out.println("Introduzca su idCliente");
            if (s.hasNext()) {
                idCliente = Integer.parseInt(s.next());
            }

            String remoteObjectName = "ReplicaControlador";

            Registry registry = LocateRegistry.getRegistry("localhost");
            ReplicaCliente_I localObject = (ReplicaCliente_I) registry.lookup(remoteObjectName);

            boolean conectado = true;
            while(conectado){
                System.out.println("Operaciones:");
                System.out.println("[1] Registrarse");
                System.out.println("[2] Donar");
                System.out.println("[3] Consultar cantidad donada");
                System.out.println("[4] Consultar numero de donaciones");
                System.out.println("[5] Consultar cantidad total donada");
                System.out.println("[6] Consultar numero de donaciones total");
                System.out.println("[7] Consultar la replica que mas ha recaudado");
                System.out.println("[8] Consultar la replica con mayor numero de donaciones");
                System.out.println("[9] Consultar datos cliente");
                System.out.println("[10] Modificar nombre cliente");
                System.out.println("[11] Desconectarse");
                int operacion = Integer.parseInt(s.next());
                int num;
                if (operacion == 1){
                    System.out.println("Introduzca un nombre de usuario");
                    String nombre = s.next();
                    System.out.println(localObject.registrarCliente(idCliente, nombre));
                } else if (operacion == 2){
                    System.out.println("Introduzca la cantidad a donar");
                    int cantidad = Integer.parseInt(s.next());
                    System.out.println(localObject.donar(idCliente, cantidad));
                } else if (operacion == 3){
                    num = localObject.consultarTotalDonadoCliente(idCliente);
                    if (num == -1){
                        System.out.println("Error: Cliente no registrado.");
                    } else{
                        System.out.println("Ha donado una cantidad total de " + num);
                    }
                } else if (operacion == 4){
                    num = localObject.getNumeroDonacionesCliente(idCliente);
                    if (num == -1){
                        System.out.println("Error: Cliente no registrado.");
                    } else{
                        System.out.println("Ha donado " + num + " veces");
                    }
                } else if (operacion == 5){
                    num = localObject.getTotalDonaciones(idCliente);
                    if (num == -1){
                        System.out.println("Necesita realizar al menos 1 donacion para ver el total recaudado");
                    } else{
                        System.out.println("El total recaudado es de: " + num);
                    }
                } else if (operacion == 6){
                    num = localObject.getNumeroDonacionesTotales(idCliente);
                    if (num == -1){
                        System.out.println("Necesita realizar al menos 1 donacion para ver el numero total de donaciones");
                    } else{
                        System.out.println("El numero total de donaciones registradas es de: " + num);
                    }
                } else if (operacion == 7){
                    num = localObject.getReplicaMasCantidadDonada(idCliente);
                    if (num == -1){
                        System.out.println("Necesita realizar al menos 1 donacion para ver la replica que mas ha recaudado");
                    } else{
                        String cadena = Integer.toString(num);
                        if (num == 0){
                            cadena = "Controlador";
                        }
                        System.out.println("La replica que mas ha recaudado es la: " + cadena);
                    }
                } else if (operacion == 8){
                    num = localObject.getReplicaMayorNumeroDonaciones(idCliente);
                    if (num == -1){
                        System.out.println("Necesita realizar al menos 1 donacion para ver la replica con mayor numero de donaciones");
                    } else{
                        String cadena = Integer.toString(num);
                        if (num == 0){
                            cadena = "Controlador";
                        }
                        System.out.println("La replica que tiene mas donaciones es la: " + cadena);
                    }
                } else if (operacion == 9){
                    System.out.println(localObject.obtenerDatosCliente(idCliente));
                } else if (operacion == 10){
                    System.out.println("Introduzca un nombre de usuario");
                    String nombre = s.next();
                    System.out.println(localObject.modificarNombreCliente(idCliente, nombre));
                } else if (operacion == 11){
                    System.out.println("Desconectando...");
                    conectado = false;
                }
            }
            s.close();

        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }
    }
}
