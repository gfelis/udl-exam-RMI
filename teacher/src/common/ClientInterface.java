package common;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote{

    void sendMessage(String message) throws RemoteException;
    String getId() throws RemoteException;
    void denyConnection(String error) throws RemoteException;
}
