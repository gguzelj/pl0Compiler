package com.fiuba.pl0compiler.parser;

import com.fiuba.pl0compiler.parser.impl.*;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.writer.Pl0Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PL0Parser {

    private static final Logger LOG = LoggerFactory.getLogger(PL0Parser.class);

    public static final Pl0Writer writer;
    private static final Scanner scanner;
    private static final AbstractParser programParser;
    private static final BlockParser blockParser;
    private static final StatementParser statementParser;
    private static final ConditionParser conditionParser;
    private static final ExpressionParser expressionParser;
    private static final TermParser termParser;
    private static final FactorParser factorParser;


    static {
        writer = new Pl0Writer();
        scanner = new Scanner();
        programParser = new ProgramParser(scanner);
        blockParser = new BlockParser(scanner);
        statementParser = new StatementParser(scanner);
        conditionParser = new ConditionParser(scanner);
        expressionParser = new ExpressionParser(scanner);
        termParser = new TermParser(scanner);
        factorParser = new FactorParser(scanner);
    }

    public void parse(String file, String outputFile) {
        LOG.info("Parsing {}", file);
        scanner.setFileToScan(file);
        writer.outputFile(outputFile);

        this.parseProgram();
    }

    private void parseProgram() {
        programParser.parse();
    }

    public static void parseBlock(Integer base) {
        blockParser.parse(base);
    }

    public static void parseStatement(Integer base, Integer offset) {
        statementParser.parse(base, offset);
    }

    public static void parseCondition(Integer base, Integer offset) {
        conditionParser.parse(base, offset);
    }

    public static void parseExpression(Integer base, Integer offset) {
        expressionParser.parse(base, offset);
    }

    public static void parseTerm(Integer base, Integer offset) {
        termParser.parse(base, offset);
    }

    public static void parseFactor(Integer base, Integer offset) {
        factorParser.parse(base, offset);
    }

}
