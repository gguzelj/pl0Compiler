package com.fiuba.pl0compiler.parser;

import com.fiuba.pl0compiler.scanner.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {

    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

    private final Scanner scanner;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public void parse() {
        this.parseProgram();
    }


    private void parseProgram() {
        LOG.debug("Parsing program");
        Token scan = this.scanner.scan();
        //System.out.println(scan.+ ": " + analizadorLexico.getCad());


        this.scanner.scan();
    }

    public void parseBlock() {

    }


}
