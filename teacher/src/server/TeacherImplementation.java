package server;
import common.ServerInterface;
import common.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class TeacherImplementation extends UnicastRemoteObject implements ServerInterface {
    public TeacherImplementation() throws RemoteException {}

    private ArrayList<ClientInterface> students = new ArrayList<>();

    public void register(ClientInterface student){
        System.out.println("Registering student");
        this.students.add(student);
    }

    public void notify_students(){
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
