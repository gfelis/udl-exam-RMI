package client;
import common.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class StudentImplementation extends UnicastRemoteObject implements ClientInterface {

    public String id;

    public StudentImplementation(String id) throws RemoteException{
        this.id = id;
    }

    @Override
    public void notifyStart(){
        System.out.print("Student received \"Exam started.\" message from server");
    }

    @Override
    public String getId(){
        return this.id;
    }
}
