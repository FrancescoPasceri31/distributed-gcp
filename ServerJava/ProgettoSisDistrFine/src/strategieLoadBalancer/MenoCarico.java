package strategieLoadBalancer;

import java.rmi.RemoteException;
import java.util.List;

import entita.Server;

public class MenoCarico implements Strategia {

	@Override
	public Server selecting(List<Server> lista) throws RemoteException {
		Server serverScelto = lista.get(0);
		for (Server server : lista) {
			if (server.getInServizio() < serverScelto.getInServizio()) {
				serverScelto = server;
			}
		}
		return serverScelto;
	}// selecting
	
	@Override
	public String getString() throws RemoteException {
		return "Meno Carico";
	}

}
