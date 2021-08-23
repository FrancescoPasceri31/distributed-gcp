package strategieLoadBalancer;

import java.rmi.RemoteException;
import java.util.List;

import entita.Server;

public class PrimoServer implements Strategia {

	@Override
	public Server selecting(List<Server> lista) {
		return lista.get(0);
	}// selecting
	
	@Override
	public String getString() throws RemoteException {
		return "Primo Server";
	}

}
