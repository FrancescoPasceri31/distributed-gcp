package googleServices;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class Translator {

	public static String doTranslation(String text, String linguaDa, String linguaA) {

		linguaDa = linguaDa.substring(0, 2);
		linguaA = linguaA.substring(0, 2);

		// avvia il client
		Translate translate = (Translate) TranslateOptions.getDefaultInstance().getService();

		// traduce il testo ricevuto nella lingua desiderata
		Translation translation = translate.translate(text, TranslateOption.sourceLanguage(linguaDa),
				TranslateOption.targetLanguage(linguaA));

		System.out.printf("Text: %s%n", text);
		System.out.printf("Translation: %s%n", translation.getTranslatedText());

		return translation.getTranslatedText();
	}
}
