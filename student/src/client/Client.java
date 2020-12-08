package client;
import common.ServerInterface;
import java.util.Scanner;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ServerInterface stub = (ServerInterface) registry.lookup("Exam");

            Scanner studentInput = new Scanner(System.in);
            System.out.println("Introduce your ID:");
            String id = studentInput.nextLine();
            StudentImplementation client = new StudentImplementation(id);
            stub.register(client);



        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString()); e.printStackTrace();
        }
    }
}
