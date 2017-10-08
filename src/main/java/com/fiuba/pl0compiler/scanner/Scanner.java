package com.fiuba.pl0compiler.scanner;

import com.fiuba.pl0compiler.scanner.exception.ScannerException;
import com.fiuba.pl0compiler.scanner.impl.Pl0Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Scanner {

    private static final Logger LOG = LoggerFactory.getLogger(Scanner.class);
    private final Pl0Scanner pl0Scanner; //Jflex implementation

    public Scanner(Pl0Scanner pl0Scanner) {
        this.pl0Scanner = pl0Scanner;
    }

    public Token scan() {
        try {
            this.pl0Scanner.scan();
            return this.getCurrentToken();
        } catch (IOException e) {
            LOG.error("An error occurred while reading file", e);
            throw new ScannerException("Unknown error has occurred");
        }
    }

    public Token getCurrentToken() {
        return new Token(this.pl0Scanner.getS(), this.pl0Scanner.getCad());
    }
}
