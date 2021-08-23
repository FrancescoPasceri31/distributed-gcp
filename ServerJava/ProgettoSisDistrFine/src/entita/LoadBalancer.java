package entita;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancer extends Remote {

	public String selezionaServer() throws RemoteException;

	public int aggiungiServer(Server s) throws RemoteException;

	public void logFine(int sid) throws RemoteException;
}
