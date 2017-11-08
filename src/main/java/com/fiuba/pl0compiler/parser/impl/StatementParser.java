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
        getAndAssertToken(TokenType.WRITELN);
        if (scanner.getNextTokenType() == TokenType.OPEN_PARENTHESIS) {
            this.doWrite(base, offset);
        }
        PL0Parser.writer.call(0x180);
    }

    private void parseWrite(Integer base, Integer offset) {
        getAndAssertToken(TokenType.WRITE);
        doWrite(base, offset);
    }

    private void parseReadln(Integer base, Integer offset) {
        getAndAssertToken(TokenType.READLN);
        getAndAssertToken(TokenType.OPEN_PARENTHESIS);
        Token ident = getAndAssertToken(TokenType.IDENT);
        SymbolTable.checkVarExistence(ident.getValue(), base, offset);

        Integer var = SymbolTable.getVar(ident.getValue(), base, offset);
        PL0Parser.writer.call(0x310);
        PL0Parser.writer.ediOffsetEax(var);

        while (scanner.getNextTokenType() == TokenType.COMMA) {
            scanner.readToken();
            ident = getAndAssertToken(TokenType.IDENT);

            SymbolTable.checkVarExistence(ident.getValue(), base, offset);
            var = SymbolTable.getVar(ident.getValue(), base, offset);
            PL0Parser.writer.call(0x310);
            PL0Parser.writer.ediOffsetEax(var);
        }
        getAndAssertToken(TokenType.CLOSE_PARENTHESIS);
    }

    private void parseAssign(Integer base, Integer offset) {
        Token ident = getAndAssertToken(TokenType.IDENT);
        SymbolTable.checkVarExistence(ident.getValue(), base, offset);
        getAndAssertToken(TokenType.ASSIGN);
        PL0Parser.parseExpression(base, offset);

        Integer var = SymbolTable.getVar(ident.getValue(), base, offset);
        PL0Parser.writer.popEax();
        PL0Parser.writer.ediOffsetEax(var);
    }

    private void parseIf(Integer base, Integer offset) {
        getAndAssertToken(TokenType.IF);


        PL0Parser.parseCondition(base, offset);

        Integer positionBefore = PL0Parser.writer.getPosition();

        getAndAssertToken(TokenType.THEN);
        PL0Parser.parseStatement(base, offset);

        Integer positionAfter = PL0Parser.writer.getPosition();

        Integer dist = positionAfter - positionBefore;

        PL0Parser.writer.fixUp(positionBefore - 4, dist);
    }

    private void parseWhile(Integer base, Integer offset) {
        getAndAssertToken(TokenType.WHILE);

        Integer positionBefore = PL0Parser.writer.getPosition();

        PL0Parser.parseCondition(base, offset);

        Integer positionAfter = PL0Parser.writer.getPosition();

        getAndAssertToken(TokenType.DO);

        PL0Parser.parseStatement(base, offset);

        PL0Parser.writer.inconditionalJump(positionBefore - (PL0Parser.writer.getPosition() + 5));

        Integer positionAfterJump = PL0Parser.writer.getPosition();

        //FixUp
        Integer dist = positionAfterJump - positionAfter;
        PL0Parser.writer.fixUp(positionAfter - 4, dist);
    }

    private void parseBegin(Integer base, Integer offset) {
        getAndAssertToken(TokenType.BEGIN);
        do {
            PL0Parser.parseStatement(base, offset);
            scanner.readToken();
        } while (scanner.getTokenType() == TokenType.SEMICOLON);
        assertTokenType(TokenType.END);
    }

    private void parseCall(Integer base, Integer offset) {
        getAndAssertToken(TokenType.CALL);
        Token ident = getAndAssertToken(TokenType.IDENT);
        SymbolTable.checkProcedureExistence(ident.getValue(), base, offset);

        Integer address = SymbolTable.getProcedure(ident.getValue(), base, offset);
        PL0Parser.writer.call(address);
    }

    private void doWrite(Integer base, Integer offset) {
        getAndAssertToken(TokenType.OPEN_PARENTHESIS);
        this.doWriteString(base, offset);

        while (scanner.getNextTokenType() == TokenType.COMMA) {
            scanner.readToken();
            this.doWriteString(base, offset);
        }
        getAndAssertToken(TokenType.CLOSE_PARENTHESIS);
    }

    private void doWriteString(Integer base, Integer offset) {
        if (scanner.getNextTokenType() == TokenType.STRING) {
            Token token = getAndAssertToken(TokenType.STRING);

            Integer from = PL0Parser.writer.getIntFromAddress(193);
            Integer to = PL0Parser.writer.getIntFromAddress(197);

            int absolutBase = from - to;

            PL0Parser.writer.movEcxOffset(absolutBase + 20 + PL0Parser.writer.getPosition());
            PL0Parser.writer.movEdx(token.getValue().length() - 2);
            PL0Parser.writer.call(0x170);
            PL0Parser.writer.inconditionalJump(token.getValue().length() - 2);
            PL0Parser.writer.string(token.getValue());

        } else {
            PL0Parser.parseExpression(base, offset);
            PL0Parser.writer.popEax();
            PL0Parser.writer.call(0x190);
        }
    }

}
