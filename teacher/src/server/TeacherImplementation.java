package server;
import common.ServerInterface;
import common.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class TeacherImplementation extends UnicastRemoteObject implements ServerInterface {
    public TeacherImplementation() throws RemoteException {}

    private final ArrayList<ClientInterface> students = new ArrayList<>();

    public synchronized void register(ClientInterface student) throws RemoteException{
        System.out.println("Student with id: " + student.getId() + " has been registered.");
        this.students.add(student);
    }

    public void start_exam(){
        System.out.println("Registering time is over, the exam is starting.");
        for (ClientInterface s: this.students){
            try{
                System.out.println("Notifying the student");
                s.notifyStart();
            } catch(RemoteException e){
                System.out.println("Error in notify"); e.printStackTrace();
            }
        }
    }
}
