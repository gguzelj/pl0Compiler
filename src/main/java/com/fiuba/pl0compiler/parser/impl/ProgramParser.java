package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.internal.SymbolTable;
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
        getAndAssertToken(TokenType.END_OF_PROGRAM);
        LOG.debug("Parsing PROGRAM END");
        PL0Parser.writer.end();

        Integer from = PL0Parser.writer.getIntFromAddress(193);
        Integer to = PL0Parser.writer.getIntFromAddress(197);

        int absolutBase = from - to;

        PL0Parser.writer.fixUp(1153, absolutBase + PL0Parser.writer.getPosition());
        PL0Parser.writer.addZeros(4 * SymbolTable.getVarCount());

        PL0Parser.writer.fixUp(68, PL0Parser.writer.getPosition());
        PL0Parser.writer.fixUp(72, PL0Parser.writer.getPosition());
        PL0Parser.writer.fixUp(201, PL0Parser.writer.getPosition() - to);
        PL0Parser.writer.flush();
    }

}
