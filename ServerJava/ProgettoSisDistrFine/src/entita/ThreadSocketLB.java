package entita;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadSocketLB extends Thread implements Serializable {

	private static final long serialVersionUID = 1L;

	private ServerSocket ss;
	private Socket s;
	private int portReq = 8080;

	public ThreadSocketLB() {
		try {
			ss = new ServerSocket(portReq);
			System.out.println("ThreadSocketLB pronto all'indirizzo : " + InetAddress.getLocalHost().getHostAddress()
					+ " port " + portReq + ".");

		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("Errore in creazione socket.");
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				s = ss.accept();
				(new ClientRMI(s)).start();
			} catch (IOException e) {
//				e.printStackTrace();
				System.out.println("Connessione rifiutata by socket.");
			}
		}
	}
}