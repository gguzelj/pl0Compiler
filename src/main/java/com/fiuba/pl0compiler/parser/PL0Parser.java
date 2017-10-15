package com.fiuba.pl0compiler.parser;

import com.fiuba.pl0compiler.parser.impl.*;
import com.fiuba.pl0compiler.scanner.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PL0Parser {

    private static final Logger LOG = LoggerFactory.getLogger(PL0Parser.class);

    private static final Scanner scanner;
    private static final AbstractParser programParser;
    private static final AbstractParser blockParser;
    private static final AbstractParser statementParser;
    private static final AbstractParser conditionParser;
    private static final AbstractParser expressionParser;
    private static final AbstractParser termParser;
    private static final AbstractParser factorParser;


    static {
        scanner = new Scanner();
        programParser = new ProgramParser(scanner);
        blockParser = new BlockParser(scanner);
        statementParser = new StatementParser(scanner);
        conditionParser = new ConditionParser(scanner);
        expressionParser = new ExpressionParser(scanner);
        termParser = new TermParser(scanner);
        factorParser = new FactorParser(scanner);
    }

    public void parse(String file) {
        LOG.info("Parsing {}", file);
        scanner.setFileToScan(file);

        this.parseProgram();
    }

    private void parseProgram() {
        programParser.parse();
    }

    public static void parseBlock() {
        blockParser.parse();
    }

    public static void parseStatement() {
        statementParser.parse();
    }

    public static void parseCondition() {
        conditionParser.parse();
    }

    public static void parseExpression() {
        expressionParser.parse();
    }

    public static void parseTerm() {
        termParser.parse();
    }

    public static void parseFactor() {
        factorParser.parse();
    }

}
