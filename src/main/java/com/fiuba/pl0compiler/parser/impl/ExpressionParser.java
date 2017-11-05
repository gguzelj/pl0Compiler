package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ExpressionParser extends AbstractParser{

    private static final Logger LOG = LoggerFactory.getLogger(ExpressionParser.class);
    private static final Set<TokenType> EXPRESSION = new HashSet<TokenType>() {{
        add(TokenType.ADD);
        add(TokenType.SUBTRACT);
    }};

    public ExpressionParser(Scanner scanner) {
        super(scanner);
    }

    public void parse() {
        this.parse(0,0);
    }

    public void parse(Integer base, Integer offset) {
        LOG.debug("Parsing EXPRESSION");
        if (EXPRESSION.contains(scanner.getNextTokenType()))
            scanner.readToken();

        PL0Parser.parseTerm(base, offset);
        while (EXPRESSION.contains(scanner.getNextTokenType())) {
            scanner.readToken();
            PL0Parser.parseTerm(base, offset);
        }
        LOG.debug("Parsing EXPRESSION END");
    }

}
