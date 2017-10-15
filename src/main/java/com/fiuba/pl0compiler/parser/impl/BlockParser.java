package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(BlockParser.class);

    public BlockParser(Scanner scanner) {
        super(scanner);
    }

    public void parse() {
        LOG.debug("Parsing BLOCK");

        if (this.scanner.getNextTokenType() == TokenType.CONST) {
            this.parseConst();
        }
        if (scanner.getNextTokenType() == TokenType.VAR){
            this.parseVar();
        }
        while (scanner.getNextTokenType() == TokenType.PROCEDURE) {
            this.parseProcedure();
        }

        PL0Parser.parseStatement();
        LOG.debug("Parsing BLOCK END");
    }

    private void parseConst() {
        getNextTokenAndAssertTokenType(TokenType.CONST);
        do {
            getNextTokenAndAssertTokenType(TokenType.IDENT);
            getNextTokenAndAssertTokenType(TokenType.EQUAL);
            getNextTokenAndAssertTokenType(TokenType.NUMBER);
        } while (scanner.readToken().getType() == TokenType.COMMA);

        assertTokenType(TokenType.SEMICOLON);
    }

    private void parseVar() {
        getNextTokenAndAssertTokenType(TokenType.VAR);
        do {
            getNextTokenAndAssertTokenType(TokenType.IDENT);
        } while (scanner.readToken().getType() == TokenType.COMMA);
        assertTokenType(TokenType.SEMICOLON);
    }

    private void parseProcedure() {
        getNextTokenAndAssertTokenType(TokenType.PROCEDURE);

        getNextTokenAndAssertTokenType(TokenType.IDENT);
        getNextTokenAndAssertTokenType(TokenType.SEMICOLON);

        PL0Parser.parseBlock();

        getNextTokenAndAssertTokenType(TokenType.SEMICOLON);
    }

}
