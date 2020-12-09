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
        System.out.println("Client received: \"" + message + "\" from server.");
    }

    @Override
    public void notifyStart(){
        System.out.println("The exam is going to start.");
        synchronized (this) {
            this.notify();
        }
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

    @Override
    public void give_mark(int correct, int total){
        float final_mark = (float) correct/total * 10;
        System.out.println("Your mark is: " + correct + " out of " + total +". Final mark = " + final_mark);
        System.exit(-1);
    }
}
