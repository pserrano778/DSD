// Generated code

import calculadora.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.NoSuchElementException;

import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

public class Cliente {
  public static void main(String [] args) {
    try {
      int numero = 0;
      String cadena = "";
      Boolean primeraIteracion = true;

      ArrayList<Double> numeros = new ArrayList<Double>();
      ArrayList<String> operaciones = new ArrayList<String>();

      Scanner operacionCompleta = new Scanner(System.in);

      System.out.println("Introduzca una operación");
      cadena = operacionCompleta.nextLine(); //Linea con la operación completa
      operacionCompleta.close();

      operacionCompleta = new Scanner(cadena); //Se crea una nueva instancia de scann con la cadena

      while(operacionCompleta.hasNext()){ //Hasta que la cadena esté vacía
        try{ //Excepción por si no se ha introducido un número de elementos correcto

          if (primeraIteracion){ //Primera iteración se lee el primer número
            try{
              cadena = operacionCompleta.next(); //Se lee el siguiente
              numero = Integer.parseInt(cadena);

            } catch (NumberFormatException n) { //Si no se ha introducido un entero
              System.out.println("Introduzca un número entero --> " + cadena);
              System.exit(-1);
            }
            numeros.add((double) numero);
            primeraIteracion = false;
          }

          cadena = operacionCompleta.next(); //Se lee el siguiente

          if (!cadena.equals("+") && !cadena.equals("-") && !cadena.equals("*") && !cadena.equals("/") ){
            System.out.println("Utilice un operador válido (+, -, *, /) --> " + cadena);
            System.exit(-1);
          }
          else{
            operaciones.add(cadena);
          }

          try{
            cadena = operacionCompleta.next(); //Se lee el siguiente
            numero = Integer.parseInt(cadena);

          } catch (NumberFormatException n) { //Si no se ha introducido un entero
            System.out.println("Introduzca un número entero --> " + cadena);
            System.exit(-1);
          }
          numeros.add((double) numero);

        } catch (NoSuchElementException e){ //No se ha introducio un número correcto de elementos
          System.out.println("Debe introducir más elementos para realizar una operación válida");
            System.exit(-1);
        }
      }


      TTransport transport;

      transport = new TSocket("localhost", 9090);
      transport.open();
      
      TProtocol protocol = new  TBinaryProtocol(transport);
      Calculadora.Client cliente = new Calculadora.Client(protocol);

      procesaOperacionCompleta(cliente, numeros, operaciones);

      System.out.println("El resultado final es: " + numeros.get(0));

      transport.close();
    } catch (TException e) {
      e.printStackTrace();
    } 
  }

  private static void procesaOperacionCompleta (Calculadora.Client cliente, ArrayList<Double> numeros, ArrayList<String> operaciones) throws TException{
    procesaMultiplicacionDivision(cliente, numeros, operaciones);
    procesaSumaResta(cliente, numeros, operaciones);
  }

  private static void procesaSumaResta (Calculadora.Client cliente, ArrayList<Double> numeros, ArrayList<String> operaciones) throws TException{
    
    for (int i=0; i<operaciones.size(); i++){
      if (operaciones.get(i).equals("+")){
        numeros.set(0, suma(cliente, numeros.get(0), numeros.get(1)));
        numeros.remove(1);
      }
      else if (operaciones.get(i).equals("-")){
        numeros.set(0, resta(cliente, numeros.get(0), numeros.get(1)));
        numeros.remove(1);
      }
    }
  }

  private static void procesaMultiplicacionDivision (Calculadora.Client cliente, ArrayList<Double> numeros, ArrayList<String> operaciones) throws TException{
    for (int i=0, j=0; i<operaciones.size(); i++){
      if (operaciones.get(i).equals("*")){
        numeros.set(j, multiplica(cliente, numeros.get(j), numeros.get(j+1)));
        numeros.remove(j+1);
      }
      else if (operaciones.get(i).equals("/")){
        numeros.set(j, divide(cliente, numeros.get(j), numeros.get(j+1)));
        numeros.remove(j+1);
      }
      else{
        j++;
      }
    }
  }

  private static double suma(Calculadora.Client cliente, double num1, double num2) throws TException{
    cliente.ping();
    System.out.println("ping()");

    double suma = cliente.suma(num1, num2);
    System.out.println(num1 + "+" + num2 + "=" + suma);

    return suma;
  }

  private static double resta(Calculadora.Client cliente, double num1, double num2) throws TException{
    cliente.ping();
    System.out.println("ping()");

    double resta = cliente.resta(num1, num2);
    System.out.println(num1 + "-" + num2 + "=" + resta);

    return resta;
  }

  private static double multiplica(Calculadora.Client cliente, double num1, double num2) throws TException{
    cliente.ping();
    System.out.println("ping()");

    double multiplicacion = cliente.multiplica(num1, num2);
    System.out.println(num1 + "*" + num2 + "=" + multiplicacion);

    return multiplicacion;
  }

  private static double divide(Calculadora.Client cliente, double num1, double num2) throws TException{
   
    double division = 0.0;
    try{
      cliente.ping();
      division = cliente.divide(num1, num2); 
      System.out.println("ping()");
    }
    catch (DivideEntre0 div0){
      System.out.println(div0.cadena);
      System.exit(div0.codigo);
    }
    System.out.println(num1 + "/" + num2 + "=" + division);

    return division;
  }
}