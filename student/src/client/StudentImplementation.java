package client;
import common.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class StudentImplementation extends UnicastRemoteObject implements ClientInterface {
    public StudentImplementation() throws RemoteException{}

    public void notifyStart() throws RemoteException{
        System.out.print("Student received \"Exam started.\" message from server");
    }
}
