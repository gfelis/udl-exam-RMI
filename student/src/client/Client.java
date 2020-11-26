package client;
import common.ServerInterface;
import common.ClientInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            StudentImplementation client = new StudentImplementation();
            ServerInterface stub = (ServerInterface) registry.lookup("Exam");
            stub.register(client);
            System.out.println("Student registered, waiting for exam to start.");

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString()); e.printStackTrace();
        }
    }
}
