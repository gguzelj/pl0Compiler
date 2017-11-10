package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.Token;
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
        TokenType type = scanner.getNextTokenType();

        if (EXPRESSION.contains(type)) {
            scanner.readToken();
        }

        PL0Parser.parseTerm(base, offset);

        if (TokenType.SUBTRACT.equals(type)) {
            PL0Parser.writer.popEax();
            PL0Parser.writer.negEax();
            PL0Parser.writer.pushEax();
        }

        while (EXPRESSION.contains(scanner.getNextTokenType())) {
            Token token = scanner.readToken();
            PL0Parser.parseTerm(base, offset);

            if (TokenType.ADD.equals(token.getType())) {
                PL0Parser.writer.popEax();
                PL0Parser.writer.popEbx();
                PL0Parser.writer.addEaxEbx();
                PL0Parser.writer.pushEax();
            } else if (TokenType.SUBTRACT.equals(token.getType())) {
                PL0Parser.writer.popEax();
                PL0Parser.writer.popEbx();
                PL0Parser.writer.xchgEaxEbx();
                PL0Parser.writer.subEaxEbx();
                PL0Parser.writer.pushEax();
            }
        }
        LOG.debug("Parsing EXPRESSION END");
    }

}
