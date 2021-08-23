package entita;

import java.io.Serializable;

public class Pacchetto implements Serializable {

	private static final long serialVersionUID = 1920080160368866157L;

	private int idClient;
	private String linguaOriginale, linguaTraduzione;
	private byte[] fileAudio;

	public int getIdClient() {
		return idClient;
	}

	public String getLinguaOriginale() {
		return linguaOriginale;
	}

	public String getLinguaTraduzione() {
		return linguaTraduzione;
	}

	public byte[] getFileAudio() {
		return fileAudio;
	}

	public void setFileAudio(byte[] fileAudio) {
		this.fileAudio = fileAudio;
	}

	public void setIdClient(int idClient) {
		this.idClient = idClient;
	}

	public void setLinguaOriginale(String linguaOriginale) {
		this.linguaOriginale = linguaOriginale;
	}

	public void setLinguaTraduzione(String linguaTraduzione) {
		this.linguaTraduzione = linguaTraduzione;
	}
	
	
}
