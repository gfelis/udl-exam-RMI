package server;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;


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
        InputHandler background = new InputHandler();
        background.start();

        try {
            Registry registry = startRegistry(null);
            TeacherImplementation teacher = new TeacherImplementation();
            registry.bind("Exam", teacher);
            System.out.println("Room ready.");
            while (!background.start) {
                System.out.println("Waiting for more students to register...");
                synchronized (Server.class){
                    Server.class.wait();
                }
            }
            teacher.start_exam();
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
    }
}

class InputHandler {

    public boolean start;


    public InputHandler() {
        this.start = false;
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                Scanner input = new Scanner(System.in);
                String command = input.nextLine();
                if(command.equals("start")){
                    synchronized (Server.class) {
                        start = true;
                        Server.class.notify();
                    }
                }
            }
        }).start();
    }
}
