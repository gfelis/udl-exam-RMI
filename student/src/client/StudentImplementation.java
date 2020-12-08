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
    public void sendMessage(String message){
        System.out.print(message);
    }

    @Override
    public String getId(){
        return this.id;
    }

    @Override
    public void denyConnection(String error){
        System.err.println("Connection denied. " + error);
        System.exit(-1);
    }
}
