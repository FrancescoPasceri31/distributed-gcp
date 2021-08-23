package entita;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.protobuf.ByteString;

import googleServices.SpeechToText;
import googleServices.TextToSpeech;
import googleServices.Translator;

public class ServerImpl extends UnicastRemoteObject implements Server, Serializable {

	private static final long serialVersionUID = 1L;

	private LoadBalancer lb;
	private int sid;
	private Lock lock;
	private int inServizio;

	public ServerImpl() throws RemoteException {
		super();
		try {
			lb = (LoadBalancer) Naming.lookup("LoadBalancer"); // cerco il load balancer
			this.sid = lb.aggiungiServer(this); // mi aggiungo alla sua lista di server
			Naming.rebind("Server" + sid, this);
			lock = new ReentrantLock(true);
			inServizio = 0;
			// ora sono pronto a ricevere richieste dei client

		} catch (MalformedURLException | RemoteException | NotBoundException e) {

			System.out.println("SERVER" + sid + "_INFO: load balancer || rmiregistry non trovato.");

//			e.printStackTrace();
		}
	}// Server Constructor

	@Override
	public byte[] richiesta(byte[] fileAudio, String codiceLinguaOrig, String codiceLinguaFin) throws RemoteException {
		inServizio++;
		lock.lock();
		try {
			return iniziaServizio(fileAudio, codiceLinguaOrig, codiceLinguaFin);
		} finally {
			inServizio--;
			lb.logFine(sid);
			lock.unlock();
		}
	}// richiesta

//	@Override
	public byte[] iniziaServizio(byte[] fileAudio, String codiceLinguaOrig, String codiceLinguaFin)
			throws RemoteException {
		try {
			// richiamo speech-to-text
			String trascrizioneAudio = SpeechToText.fromSpeechToText(fileAudio, codiceLinguaOrig);
			// richiamo traduttore che mi ritorna una stringa tradotta
			String trascrizioneAudioTradotta = Translator.doTranslation(trascrizioneAudio, codiceLinguaOrig,
					codiceLinguaFin);
			// richiamo text to speech per convertire stringa in file audio (byte[])
			ByteString audioRicevuto = TextToSpeech.fromTextToSpeech(trascrizioneAudioTradotta, codiceLinguaFin);
			return audioRicevuto.toByteArray();
		} catch (IOException | IndexOutOfBoundsException e) {
//			e.printStackTrace();
			System.out.println("Servizi Google non disponibili.");
			return null;
		}
	}// iniziaServizio

	public int getInServizio() throws RemoteException {
		return inServizio;
	} // getInServizio

	@Override
	public int getID() throws RemoteException {
		return sid;
	}

	@Override
	public String toString() {
		return "Server " + sid;
	}
}// Server
