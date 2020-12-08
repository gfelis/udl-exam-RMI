package common;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote{

    public void notifyStart() throws RemoteException;
    public String getId() throws RemoteException;
}
