package common;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote{

    void sendMessage(String message) throws RemoteException;
    void denyConnection(String error) throws RemoteException;
    void notifyStart() throws  RemoteException;
    void give_mark(int correct, int total) throws RemoteException;
}
