package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TermParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(TermParser.class);

    public TermParser(Scanner scanner) {
        super(scanner);
    }

    public void parse() {
        LOG.debug("Parsing TERM");
        PL0Parser.parseFactor();
        while(scanner.getNextTokenType() == TokenType.MULTIPLY || scanner.getNextTokenType() == TokenType.DIVIDE) {
            scanner.readToken();
            PL0Parser.parseFactor();
        }
        LOG.debug("Parsing TERM END");
    }

}
