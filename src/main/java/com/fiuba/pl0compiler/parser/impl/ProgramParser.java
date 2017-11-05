package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramParser.class);

    public ProgramParser(Scanner scanner) {
        super(scanner);
    }

    public void parse() {
        LOG.debug("Parsing PROGRAM");
        PL0Parser.parseBlock(0);
        getNextTokenAndAssertTokenType(TokenType.END_OF_PROGRAM);
        LOG.debug("Parsing PROGRAM END");
    }

}
