package server;


import common.ClientInterface;

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
            System.out.println("Room ready. Write \"start\" and hit enter to start the exam and \"finish\" to end it.");
            System.out.println("Waiting for more students to register...");

            while (!background.start) {
                synchronized (Server.class){
                    Server.class.wait();
                }
            }
            synchronized (teacher) {
                teacher.start_exam();
                teacher.notifyStart();
                for (ClientInterface student : teacher.marks.keySet()) {
                    teacher.sendQuestion(student);
                }

                while (!background.finish) {
                    synchronized (Server.class) {
                        Server.class.wait();
                    }
                }

                teacher.write_results();

                for (ClientInterface student : teacher.marks.keySet()) {
                    try{
                        teacher.give_mark(student);
                    }catch (Exception e){}
                }
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
    }

    private static class InputHandler {

        public boolean start;
        public boolean finish;

        public InputHandler() {
            this.start = false;
            this.finish = false;
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
                    if(command.equals("finish")){
                        synchronized (Server.class) {
                            if(start) finish = true;
                            Server.class.notify();
                        }
                    }
                }
            }).start();
        }
    }
}
