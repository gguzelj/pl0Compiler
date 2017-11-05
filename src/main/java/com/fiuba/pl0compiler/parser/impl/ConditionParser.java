package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.parser.ParserException;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
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
            return;
        }

        PL0Parser.parseExpression(base, offset);
        if (OPERATIONS.containsKey(scanner.getNextTokenType())) {
            OPERATIONS.get(scanner.getNextTokenType()).run();
        } else {
            throw new ParserException("Unknown token " + scanner.readToken());
        }

        PL0Parser.parseExpression(base, offset);
        LOG.debug("Parsing CONDITION END");
    }

    private void parseEqual() {
        getNextTokenAndAssertTokenType(TokenType.EQUAL);
    }
    private void parseDistinct() {
        getNextTokenAndAssertTokenType(TokenType.DISTINCT);
    }
    private void parseLessThan() {
        getNextTokenAndAssertTokenType(TokenType.LESS_THAN);
    }
    private void parseLessOrEqual() {
        getNextTokenAndAssertTokenType(TokenType.LESS_OR_EQUAL);
    }
    private void parseGreaterThan() {
        getNextTokenAndAssertTokenType(TokenType.GREATER_THAN);
    }
    private void parseGreaterOrEqual() {
        getNextTokenAndAssertTokenType(TokenType.GREATER_OR_EQUAL);
    }

}
