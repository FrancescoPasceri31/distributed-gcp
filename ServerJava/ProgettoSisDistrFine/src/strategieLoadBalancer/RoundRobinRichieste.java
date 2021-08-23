package strategieLoadBalancer;

import java.rmi.RemoteException;
import java.util.List;

import entita.Server;

public class RoundRobinRichieste implements Strategia {

	private int serverCorrente=0;
	
	@	Override
	public Server selecting(List<Server> lista) {
		Server server = lista.get(serverCorrente%lista.size());
		serverCorrente++;
		return server;
	}	// selecting
	
	@Override
	public String getString() throws RemoteException {
		return "Round Robin Richieste";
	}
}
