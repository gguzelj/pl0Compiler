package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.Token;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TermParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(TermParser.class);

    public TermParser(Scanner scanner) {
        super(scanner);
    }

    public void parse() {
        this.parse(0,0);
    }

    public void parse(Integer base, Integer offset) {
        LOG.debug("Parsing TERM");
        PL0Parser.parseFactor(base, offset);
        while(scanner.getNextTokenType() == TokenType.MULTIPLY || scanner.getNextTokenType() == TokenType.DIVIDE) {
            Token token = scanner.readToken();
            PL0Parser.parseFactor(base, offset);

            if (token.getType() == TokenType.MULTIPLY) {
                PL0Parser.writer.popEax();
                PL0Parser.writer.popEbx();
                PL0Parser.writer.imulEbx();
                PL0Parser.writer.pushEax();
            } else {
                PL0Parser.writer.popEax();
                PL0Parser.writer.popEbx();
                PL0Parser.writer.xchgEaxEbx();
                PL0Parser.writer.cdq();
                PL0Parser.writer.idivEbx();
                PL0Parser.writer.pushEax();
            }
        }
        LOG.debug("Parsing TERM END");
    }

}
