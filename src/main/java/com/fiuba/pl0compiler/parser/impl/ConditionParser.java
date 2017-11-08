package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.parser.ParserException;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConditionParser extends AbstractParser{

    private static final Logger LOG = LoggerFactory.getLogger(ConditionParser.class);
    private static final Map<TokenType, Runnable> OPERATIONS = new HashMap<>();

    public ConditionParser(Scanner scanner) {
        super(scanner);
        OPERATIONS.put(TokenType.EQUAL, this::parseEqual);
        OPERATIONS.put(TokenType.DISTINCT, this::parseDistinct);
        OPERATIONS.put(TokenType.LESS_THAN, this::parseLessThan);
        OPERATIONS.put(TokenType.LESS_OR_EQUAL, this::parseLessOrEqual);
        OPERATIONS.put(TokenType.GREATER_THAN, this::parseGreaterThan);
        OPERATIONS.put(TokenType.GREATER_OR_EQUAL, this::parseGreaterOrEqual);
    }

    public void parse() {
        this.parse(0,0);
    }

    public void parse(Integer base, Integer offset) {
        LOG.debug("Parsing CONDITION");
        if (scanner.getNextTokenType() == TokenType.ODD) {
            scanner.readToken();
            PL0Parser.parseExpression(base, offset);

            PL0Parser.writer.popEax();
            PL0Parser.writer.testAl(0x01);
            PL0Parser.writer.oddJump();
            PL0Parser.writer.inconditionalJump();
            return;
        }

        PL0Parser.parseExpression(base, offset);
        TokenType tokenType;
        if (OPERATIONS.containsKey(scanner.getNextTokenType())) {
            tokenType = scanner.getNextTokenType();
            OPERATIONS.get(tokenType).run();
        } else {
            throw new ParserException("Unknown token " + scanner.readToken());
        }

        PL0Parser.parseExpression(base, offset);

        PL0Parser.writer.popEax();
        PL0Parser.writer.popEbx();
        PL0Parser.writer.cmpEbxEax();
        PL0Parser.writer.conditionalJump(tokenType);
        PL0Parser.writer.inconditionalJump();




        LOG.debug("Parsing CONDITION END");
    }

    private void parseEqual() {
        getAndAssertToken(TokenType.EQUAL);
    }
    private void parseDistinct() {
        getAndAssertToken(TokenType.DISTINCT);
    }
    private void parseLessThan() {
        getAndAssertToken(TokenType.LESS_THAN);
    }
    private void parseLessOrEqual() {
        getAndAssertToken(TokenType.LESS_OR_EQUAL);
    }
    private void parseGreaterThan() {
        getAndAssertToken(TokenType.GREATER_THAN);
    }
    private void parseGreaterOrEqual() {
        getAndAssertToken(TokenType.GREATER_OR_EQUAL);
    }

}
