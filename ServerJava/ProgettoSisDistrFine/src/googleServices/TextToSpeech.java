package googleServices;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

public class TextToSpeech {

	public static ByteString fromTextToSpeech(String inputText, String languageCodeOut)
			throws FileNotFoundException, IOException {

		// Crea il servizio client per Google TextToSpeech
		try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {

			// Crea la sintesi del testo da cui creare l'audio
			SynthesisInput input = SynthesisInput.newBuilder().setText(inputText).build();

			// Crea la richiesta dell'audio, selezione la lingua mediante il codice
			// ("en-US") e il genere sessuale della voce nell'audio ("neutral")
			VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode(languageCodeOut)
					.setSsmlGender(SsmlVoiceGender.NEUTRAL).build();

			// Selezione il formato dell'audio da ritornare

			AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

			// Esegue la richiesta del servizio Google specificando la richiesta di voce, di
			// input e
			// il formato audio da ricevere
			SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

			// Acquisisce l'audio dal servizio Google
			ByteString audioContents = response.getAudioContent();

			textToSpeechClient.close();
			
			return audioContents;
		}
	}

	/*
	 * public static void main(String[] args) { try { String trad =
	 * Translator.doTranslation("Ciao mi chiamo Francesco", "it", "en"); ByteString
	 * bs = fromTextToSpeech(trad, "en-US"); byte[] in = bs.toByteArray();
	 * System.out.println("Sizeof(in) = " + in.length); OutputStream os = new
	 * FileOutputStream("/home/ciccio/FileAudio/output.mp3"); os.write(in); } catch
	 * (IOException e) { e.printStackTrace(); } }
	 */
}
