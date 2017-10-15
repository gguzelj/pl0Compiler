package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class StatementParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(StatementParser.class);

    private static final Map<TokenType, Runnable> OPERATIONS = new HashMap<>();

    public StatementParser(Scanner scanner) {
        super(scanner);
        OPERATIONS.put(TokenType.IDENT, this::parseAssign);
        OPERATIONS.put(TokenType.CALL, this::parseCall);
        OPERATIONS.put(TokenType.BEGIN, this::parseBegin);
        OPERATIONS.put(TokenType.IF, this::parseIf);
        OPERATIONS.put(TokenType.WHILE, this::parseWhile);
        OPERATIONS.put(TokenType.WRITE, this::parseWrite);
        OPERATIONS.put(TokenType.WRITELN, this::parseWriteln);
        OPERATIONS.put(TokenType.READLN, this::parseReadln);
    }

    public void parse() {
        LOG.debug("Parsing STATEMENT");
        TokenType type = scanner.getNextTokenType();
        if (OPERATIONS.containsKey(type)) {
            OPERATIONS.get(type).run();
        }
        LOG.debug("Parsing STATEMENT END");
    }

    private void parseWriteln() {
        getNextTokenAndAssertTokenType(TokenType.WRITELN);
        if (scanner.getNextTokenType() == TokenType.OPEN_PARENTHESIS) {
            this.doWrite();
        }
    }

    private void parseWrite() {
        getNextTokenAndAssertTokenType(TokenType.WRITE);
        doWrite();
    }

    private void parseReadln() {
        getNextTokenAndAssertTokenType(TokenType.READLN);
        getNextTokenAndAssertTokenType(TokenType.OPEN_PARENTHESIS);
        getNextTokenAndAssertTokenType(TokenType.IDENT);
        while (scanner.getNextTokenType() == TokenType.COMMA) {
            scanner.readToken();
            getNextTokenAndAssertTokenType(TokenType.IDENT);
        }
        getNextTokenAndAssertTokenType(TokenType.CLOSE_PARENTHESIS);
    }

    private void parseAssign() {
        getNextTokenAndAssertTokenType(TokenType.IDENT);
        getNextTokenAndAssertTokenType(TokenType.ASSIGN);
        PL0Parser.parseExpression();
    }

    private void parseIf() {
        getNextTokenAndAssertTokenType(TokenType.IF);
        PL0Parser.parseCondition();
        getNextTokenAndAssertTokenType(TokenType.THEN);
        PL0Parser.parseStatement();
    }

    private void parseWhile() {
        getNextTokenAndAssertTokenType(TokenType.WHILE);
        PL0Parser.parseCondition();
        getNextTokenAndAssertTokenType(TokenType.DO);
        PL0Parser.parseStatement();
    }

    private void parseBegin() {
        getNextTokenAndAssertTokenType(TokenType.BEGIN);
        do {
            PL0Parser.parseStatement();
            scanner.readToken();
        } while (scanner.getTokenType() == TokenType.SEMICOLON);
        assertTokenType(TokenType.END);
    }

    private void parseCall() {
        getNextTokenAndAssertTokenType(TokenType.CALL);
        getNextTokenAndAssertTokenType(TokenType.IDENT);
    }

    private void doWrite() {
        getNextTokenAndAssertTokenType(TokenType.OPEN_PARENTHESIS);
        if (scanner.getNextTokenType() == TokenType.STRING) {
            getNextTokenAndAssertTokenType(TokenType.STRING);
        } else {
            PL0Parser.parseExpression();
        }
        while (scanner.getNextTokenType() == TokenType.COMMA) {
            scanner.readToken();
            if (scanner.getNextTokenType() == TokenType.STRING) {
                getNextTokenAndAssertTokenType(TokenType.STRING);
            } else {
                PL0Parser.parseExpression();
            }
        }
        getNextTokenAndAssertTokenType(TokenType.CLOSE_PARENTHESIS);
    }

}
