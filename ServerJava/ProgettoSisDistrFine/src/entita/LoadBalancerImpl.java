package entita;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import gui.LoadBalancerGUI;
import strategieLoadBalancer.PrimoServer;
import strategieLoadBalancer.Strategia;

public class LoadBalancerImpl extends UnicastRemoteObject implements LoadBalancer, Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<Server> listaServer = new ArrayList<Server>();
	private int sid;
	private Strategia strategia;
	private LoadBalancerGUI gui;
	private ArrayList<Integer> listaSIDRimossi = new ArrayList<Integer>();
	private ThreadSocketLB socket;

	private ReentrantLock lock, lockSelezionaServer;

	public LoadBalancerImpl() throws RemoteException {
		super();
		try {
			LocateRegistry.createRegistry(1099);
			try {
				lock = new ReentrantLock(true);
				lockSelezionaServer = new ReentrantLock(true);
				Naming.rebind("LoadBalancer", this);
				sid = 1;
				gui = new LoadBalancerGUI(this);
				this.strategia = new PrimoServer();
				gui.addLog(getTime() + "Load Balancer pronto.");
				socket = new ThreadSocketLB();
				socket.start();
			} catch (RemoteException | MalformedURLException e) {
				System.out.println("LOAD_BALANCER_INFO: errore nel rebing.");
//				e.printStackTrace();
			}
		} catch (RemoteException e) {
			System.out.println("LOAD_BALANCER_INFO: errore nel creare il registro.");
//			e.printStackTrace();
		}
	}// LoadBalancer Constructor

	// metodo remoto chiamato dal client
	@Override
	public String selezionaServer() throws RemoteException {
		lockSelezionaServer.lock();
		try {
			// deve sfruttare la politica desiderata
			// fornire il nome del server che dovrà usare
			Server s = strategia.selecting(listaServer); // sid selezionato
			int ssid = s.getID();
			gui.addLog(getTime() + "richiesta gestita da Server " + ssid);
			gui.selectedServer(ssid);
			return "Server" + ssid;
		} finally {
			lockSelezionaServer.unlock();
		}
	}// selezionaServer

	// Metodo remoto chiamato dal Server
	@Override
	public int aggiungiServer(Server s) throws RemoteException {
		int sidDaAssegnare;
		if (listaSIDRimossi.size() == 0) {
			sidDaAssegnare = sid++;
		} else {
			sidDaAssegnare = listaSIDRimossi.remove(0);
		}
		listaServer.add(s);
		gui.addLog(getTime() + "aggiunto Server " + sidDaAssegnare);
		gui.changeList(sid, listaSIDRimossi);
		return sidDaAssegnare;
	}// aggiungiServer

	public void rimuoviServer(int sid) {
		Server daRimuovere = null;
		for (Server server : listaServer) {
			try {
				if (server.getID() == sid) {
					daRimuovere = server;
					break;
				}
			} catch (RemoteException e) {
//				e.printStackTrace();
				System.out.println("Server not found.");
			}
		}
		if (daRimuovere != null) {
			listaServer.remove(daRimuovere);
			listaSIDRimossi.add(sid);
			try {
				Naming.unbind("Server" + sid);
				gui.addLog(getTime() + "rimosso Server " + sid);
				gui.changeList(this.sid, listaSIDRimossi);
			} catch (RemoteException | MalformedURLException | NotBoundException e) {
//				e.printStackTrace();
				System.out.println("Server non eliminato.");
			}

		}
	}

	private static String getTime() {
		String nanos = "";
		try {
			nanos = String.valueOf(LocalDateTime.now().getNano()).substring(0, 3);
		} catch (IndexOutOfBoundsException e) {
			nanos = "" + 0;
		}
		return LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
				+ LocalDateTime.now().getSecond() + ":" + nanos + " - ";
	}

	@Override
	public void logFine(int sid) throws RemoteException {
		lock.lock();
		try {
			gui.addLog(getTime() + "Server " + sid + " ha terminato la richiesta.");
		} finally {
			lock.unlock();
		}

	}

//	@Override
	public void cambiaStrategia(Strategia strategia) throws RemoteException {
		this.strategia = strategia;
		gui.addLog(getTime() + "cambia strategia in " + strategia.getString());
	}//

}// Load Balancer
