package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.internal.SymbolTable;
import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.Token;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class StatementParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(StatementParser.class);

    private static final Map<TokenType, BiConsumer<Integer, Integer>> OPERATIONS = new HashMap<>();

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

    @Override
    public void parse() {
        this.parse(0,0);
    }


    public void parse(Integer base, Integer offset) {
        LOG.debug("Parsing STATEMENT");
        TokenType type = scanner.getNextTokenType();
        if (OPERATIONS.containsKey(type)) {
            OPERATIONS.get(type).accept(base, offset);
        }
        LOG.debug("Parsing STATEMENT END");
    }

    private void parseWriteln(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.WRITELN);
        if (scanner.getNextTokenType() == TokenType.OPEN_PARENTHESIS) {
            this.doWrite(base, offset);
        }
    }

    private void parseWrite(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.WRITE);
        doWrite(base, offset);
    }

    private void parseReadln(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.READLN);
        getNextTokenAndAssertTokenType(TokenType.OPEN_PARENTHESIS);
        Token ident = getNextTokenAndAssertTokenType(TokenType.IDENT);
        SymbolTable.checkVarExistence(ident.getValue(), base, offset);


        while (scanner.getNextTokenType() == TokenType.COMMA) {
            scanner.readToken();
            getNextTokenAndAssertTokenType(TokenType.IDENT);
        }
        getNextTokenAndAssertTokenType(TokenType.CLOSE_PARENTHESIS);
    }

    private void parseAssign(Integer base, Integer offset) {
        Token ident = getNextTokenAndAssertTokenType(TokenType.IDENT);
        SymbolTable.checkVarExistence(ident.getValue(), base, offset);
        getNextTokenAndAssertTokenType(TokenType.ASSIGN);
        PL0Parser.parseExpression(base, offset);
    }

    private void parseIf(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.IF);
        PL0Parser.parseCondition(base, offset);
        getNextTokenAndAssertTokenType(TokenType.THEN);
        PL0Parser.parseStatement(base, offset);
    }

    private void parseWhile(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.WHILE);
        PL0Parser.parseCondition(base, offset);
        getNextTokenAndAssertTokenType(TokenType.DO);
        PL0Parser.parseStatement(base, offset);
    }

    private void parseBegin(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.BEGIN);
        do {
            PL0Parser.parseStatement(base, offset);
            scanner.readToken();
        } while (scanner.getTokenType() == TokenType.SEMICOLON);
        assertTokenType(TokenType.END);
    }

    private void parseCall(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.CALL);
        Token ident = getNextTokenAndAssertTokenType(TokenType.IDENT);
        SymbolTable.checkProcedureExistence(ident.getValue(), base, offset);
    }

    private void doWrite(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.OPEN_PARENTHESIS);
        if (scanner.getNextTokenType() == TokenType.STRING) {
            getNextTokenAndAssertTokenType(TokenType.STRING);
        } else {
            PL0Parser.parseExpression(base, offset);
        }
        while (scanner.getNextTokenType() == TokenType.COMMA) {
            scanner.readToken();
            if (scanner.getNextTokenType() == TokenType.STRING) {
                getNextTokenAndAssertTokenType(TokenType.STRING);
            } else {
                PL0Parser.parseExpression(base, offset);
            }
        }
        getNextTokenAndAssertTokenType(TokenType.CLOSE_PARENTHESIS);
    }

}
