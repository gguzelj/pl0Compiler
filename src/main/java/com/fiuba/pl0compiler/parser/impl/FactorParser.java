package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.parser.ParserException;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactorParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(FactorParser.class);

    public FactorParser(Scanner scanner) {
        super(scanner);
    }

    public void parse() {
        LOG.debug("Parsing FACTOR");
        TokenType type = scanner.getNextTokenType();
        if (type == TokenType.IDENT) {
            getNextTokenAndAssertTokenType(TokenType.IDENT);
        } else if (type == TokenType.NUMBER) {
            getNextTokenAndAssertTokenType(TokenType.NUMBER);
        } else if (type == TokenType.OPEN_PARENTHESIS) {
            getNextTokenAndAssertTokenType(TokenType.OPEN_PARENTHESIS);
            PL0Parser.parseExpression();
            getNextTokenAndAssertTokenType(TokenType.CLOSE_PARENTHESIS);
        } else {
            LOG.error("Invalid option for factor: {}", scanner.getToken());
            throw new ParserException("Invalid option for factor: ");
        }
        LOG.debug("Parsing FACTOR END");
    }

}
