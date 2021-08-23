package strategieLoadBalancer;

import java.rmi.RemoteException;
import java.util.List;

import entita.Server;

public interface Strategia {

	public Server selecting(List<Server> lista) throws RemoteException; // ritorna server id (sid)
	
	public String getString() throws RemoteException;
	
}
