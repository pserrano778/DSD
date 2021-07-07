package Servidor;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ReplicaN extends Replica implements Runnable
{
    ReplicaN(int num){
        super(num);
    }

    public void run(){
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String remoteObjectName = "Replica" + getIDReplica();
            ReplicaReplica_I object = this;
            ReplicaReplica_I stub = (ReplicaReplica_I) UnicastRemoteObject.exportObject(object, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(remoteObjectName, stub);

            System.out.println("Replica " + getIDReplica() + " ready");

        } catch (Exception e) {
            System.err.println("Replica " + getIDReplica() + " exception:");
            e.printStackTrace();
        }
    }

}
