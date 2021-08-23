package entita;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

public class ClientRMI extends Thread implements /* ClientRMIInterface, */ Serializable {

	private static final long serialVersionUID = 1L;
	private LoadBalancer lb;
	private Server server;
	private int id;
	private static int gid = 0;

	private Socket socket;
	private Pacchetto file, ritorno;

	public ClientRMI(Socket socket) {
		try {
			this.socket = socket;
			id = gid;
			gid++;
			lb = (LoadBalancer) Naming.lookup("LoadBalancer");

			ObjectInputStream in;
			try {
				in = new ObjectInputStream((socket.getInputStream()));
				file = (Pacchetto) in.readObject();
			} catch (IOException | ClassNotFoundException e) {
//				e.printStackTrace();
				System.out.println("Impossibile ricevere file sulla socket.");
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}// Client constructor

	// @Override
	public byte[] inviaRichiesta() throws RemoteException {
		String nomeServer = lb.selezionaServer();
		if (nomeServer.equals("Server-1")) {
			System.out.println("CLIENT_INFO" + id + ": nessun server ottenuto.");
			return null;
		}
		try {
			server = (Server) Naming.lookup(nomeServer); // mi collego al server
			try {
				byte[] fileAudioBytes = file.getFileAudio();

				String codLingIn = file.getLinguaOriginale(), codLingOut = file.getLinguaTraduzione();

				return server.richiesta(fileAudioBytes, codLingIn, codLingOut); // avvio
																				// servizio
			} catch (IOException e) {
				System.out.println("CLIENT_INFO " + id + "  : file audio non valido");
//				e.printStackTrace();
				return null;
			}
		} catch (MalformedURLException | NotBoundException e1) {
			System.out.println("CLIENT_INFO " + id + " : server non trovato.");
//			e1.printStackTrace();
			return null;
		}
	}// inviaRichiesta

	// @Override
	public void gestisciFileAudio() {
		try {
			byte[] fileAudioDaRiprodurre = inviaRichiesta();
			if (fileAudioDaRiprodurre != null) {
				System.out.println("CLIENT_INFO " + id + " : servizio terminato. @" + LocalDateTime.now().getHour()
						+ ":" + LocalDateTime.now().getMinute() + ":" + LocalDateTime.now().getSecond() + ":"
						+ LocalDateTime.now().getNano());

				ritorno = new Pacchetto();
				ritorno.setIdClient(file.getIdClient());
				ritorno.setFileAudio(fileAudioDaRiprodurre);

			} else { // se dopo 3 tentativi non riesco a risolvere la richiesta
				System.out.println("CLIENT_INFO: servizio non fruibile.");
			}
		} catch (IOException e) {
			System.out.println("CLIENT_INFO: richiesta non valida.");
//			e.printStackTrace();
		}

	}// riceviFileAudio

	@Override
	public void run() {
		gestisciFileAudio();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(ritorno);
			oos.flush();
			oos.close();
			System.out.println("Inviato.");
		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("Impossibile inviare file.");
		}
	}// run
}// Client
