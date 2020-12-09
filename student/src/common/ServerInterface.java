package common;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote{
    void register(ClientInterface client) throws RemoteException;
    void receiveAnswer(ClientInterface client, String answer) throws RemoteException;
}
