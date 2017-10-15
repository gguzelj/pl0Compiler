package com.fiuba.pl0compiler.scanner;

import com.fiuba.pl0compiler.scanner.exception.ScannerException;
import com.fiuba.pl0compiler.scanner.impl.Pl0Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Scanner {

    private static final Logger LOG = LoggerFactory.getLogger(Scanner.class);

    private Pl0Scanner pl0Scanner;

    public void setFileToScan(String file) {
        try {
            this.pl0Scanner = new Pl0Scanner(new BufferedReader(new FileReader(file)));
        } catch (FileNotFoundException e) {
            throw new ScannerException("File not found " + file);
        }
    }

    public Token readToken() {
        Token token = this.doReadToken();
        LOG.debug("Scanned token: {}", token);
        return token;
    }

    public TokenType getTokenType() {
        return this.pl0Scanner.getS();
    }

    public String getValue() {
        return this.pl0Scanner.getCad();
    }

    public Token getToken() {
        return new Token(pl0Scanner.getS(), pl0Scanner.getCad());
    }

    public TokenType getNextTokenType() {
        Token token = this.doReadToken();
        pl0Scanner.yypushback(pl0Scanner.yylength());
        return token.getType();
    }

    private Token doReadToken() {
        try {
            this.pl0Scanner.scan();
            return new Token(pl0Scanner.getS(), pl0Scanner.getCad());
        } catch (IOException e) {
            LOG.error("An error occurred while reading file", e);
            throw new ScannerException("Unknown error has occurred");
        }
    }
}
