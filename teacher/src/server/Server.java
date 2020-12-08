package server;

import common.ServerInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {


    private static Registry startRegistry(Integer port)
            throws RemoteException {
        if(port == null) {
            port = 1099;
        }
        try {
            Registry registry = LocateRegistry.getRegistry(port);
            registry.list( );
            return registry;
        }
        catch (RemoteException ex) {
            System.out.println("RMI registry cannot be located ");
            Registry registry= LocateRegistry.createRegistry(port);
            System.out.println("RMI registry created at port ");
            return registry;
        }
    }

    public static void main(String args[]) {
        BlockingQueue<String> input = new LinkedBlockingQueue<>();
        InputHandler background = new InputHandler(input);
        background.start();
        try {

            Registry registry = startRegistry(null);
            TeacherImplementation obj = new TeacherImplementation();
            registry.bind("Exam", obj);
            System.out.println("Room ready.");
            while (input.isEmpty() || !input.take().equals("start")) {
                System.out.println("Waiting for more students to register...");
                Thread.sleep(10000);
            }
            obj.start_exam();
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
    }
}

class InputHandler {

    private final BlockingQueue<String> in;


    public InputHandler(BlockingQueue<String> input) {
        in = input;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    Scanner input = new Scanner(System.in);
                    String command = input.nextLine();
                    in.put(command);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }).start();
    }
}
