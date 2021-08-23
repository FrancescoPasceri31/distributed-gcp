package entita;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {

	public byte[] richiesta(byte[] fileAudio, String codiceLinguaOrig, String codiceLinguaFin) throws RemoteException;

	public int getID() throws RemoteException;

	public int getInServizio() throws RemoteException;

}
