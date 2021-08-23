package strategieLoadBalancer;

import java.rmi.RemoteException;
import java.util.List;

import entita.Server;

public class RoundRobinTime implements Strategia {

	private final int TIME_SLICE_SECONDS = 2; // 10s
	private int indiceServer;
	private ThreadTimer tt;

	public RoundRobinTime() {
		indiceServer = 0;
		tt = new ThreadTimer(this, TIME_SLICE_SECONDS);
		tt.start();
	}

	@Override
	public Server selecting(List<Server> lista) throws RemoteException {
		return lista.get(indiceServer % lista.size());
	}

	@Override
	public String getString() throws RemoteException {
		return "Round Robin Time";
	}

	public void incr() {
		indiceServer++;
	}

}
