package googleServices;

import java.io.IOException;
import java.util.List;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

public class SpeechToText {

	public static String fromSpeechToText(byte[] data, String languageCode)
			throws IOException, IndexOutOfBoundsException {

		// Crea il client del servizio

		SpeechClient speechClient = SpeechClient.create();

		// Legge i bit del file audio dalla memoria
		ByteString audioBytes = ByteString.copyFrom(data);

		// Costruisce la richiesta di riconoscimento sincronizzata
		RecognitionConfig config = RecognitionConfig.newBuilder().setLanguageCode(languageCode)
				.setEncoding(AudioEncoding.AMR).setSampleRateHertz(8000).setEnableAutomaticPunctuation(true)
				.setProfanityFilter(false).build();
		RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

		// Effettua la chiamata al servizio Google per la trascrizione
		RecognizeResponse response = speechClient.recognize(config, audio);
		List<SpeechRecognitionResult> results = response.getResultsList();
		SpeechRecognitionAlternative alternative = results.get(0).getAlternativesList().get(0);

		speechClient.close();
		return alternative.getTranscript();

	}
}
