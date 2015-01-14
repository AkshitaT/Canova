package org.nd4j.nlp.stopwords;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

/**
 * Loads stop words from the class path
 * @author Adam Gibson
 *
 */
public class StopWords {

	private static List<String> stopWords;

	@SuppressWarnings("unchecked")
	public static List<String> getStopWords() {

		try {
			if(stopWords == null)
				stopWords =  IOUtils.readLines(new ClassPathResource("/stopwords").getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return stopWords;
	}

}
