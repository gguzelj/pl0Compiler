package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.ParserException;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractParser {

    protected final Scanner scanner;

    public AbstractParser(Scanner scanner) {
        this.scanner = scanner;
    }

    public abstract void parse();

    protected void getNextTokenAndAssertTokenType(TokenType tokenType) {
        this.scanner.readToken();
        this.assertTokenType(tokenType);
    }

    protected void assertTokenType(TokenType tokenType) {
        if (scanner.getTokenType() != tokenType) {
            throw new ParserException("Expecting " + tokenType + ": " + this.scanner.getToken());
        }
    }

}
