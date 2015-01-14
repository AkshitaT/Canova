package org.canova.nlp.tokenization.tokenizer.preprocessor;


import org.nd4j.nlp.tokenization.tokenizer.TokenPreProcess;

/**
 * Gets rid of endings:
 *
 *    ed,ing, ly, s, .
 * @author Adam Gibson
 */
public class EndingPreProcessor implements TokenPreProcess {
    @Override
    public String preProcess(String token) {
        if(token.endsWith("s") && !token.endsWith("ss"))
            token = token.substring(0,token.length() - 1);
        if(token.endsWith("."))
            token = token.substring(0,token.length() - 1);
        if(token.endsWith("ed"))
            token = token.substring(0,token.length() - 2);
         if(token.endsWith("ing"))
            token = token.substring(0,token.length() - 3);
         if(token.endsWith("ly"))
            token = token.substring(0,token.length() - 2);
         return token;
    }
}
