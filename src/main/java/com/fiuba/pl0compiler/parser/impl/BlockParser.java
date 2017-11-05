package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.internal.SymbolTable;
import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.Token;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(BlockParser.class);

    public BlockParser(Scanner scanner) {
        super(scanner);
    }

    public void parse() {
        this.parse(0);
    }

    public void parse(Integer base) {
        LOG.debug("Parsing BLOCK");
        Integer offset = 0;

        if (this.scanner.getNextTokenType() == TokenType.CONST) {
            offset = this.parseConst(base, offset);
        }
        if (scanner.getNextTokenType() == TokenType.VAR){
            offset = this.parseVar(base, offset);
        }
        while (scanner.getNextTokenType() == TokenType.PROCEDURE) {
            offset = this.parseProcedure(base, offset);
        }

        PL0Parser.parseStatement(base, offset);
        LOG.debug("Parsing BLOCK END");
    }

    private Integer parseConst(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.CONST);
        do {
            Token ident = getNextTokenAndAssertTokenType(TokenType.IDENT);
            getNextTokenAndAssertTokenType(TokenType.EQUAL);
            Token value = getNextTokenAndAssertTokenType(TokenType.NUMBER);
            SymbolTable.addConst(ident.getValue(), value.getValue(), base, offset++);
        } while (scanner.readToken().getType() == TokenType.COMMA);

        assertTokenType(TokenType.SEMICOLON);
        return offset;
    }

    private Integer parseVar(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.VAR);
        do {
            Token ident = getNextTokenAndAssertTokenType(TokenType.IDENT);
            SymbolTable.addVar(ident.getValue(), base, offset++);
        } while (scanner.readToken().getType() == TokenType.COMMA);
        assertTokenType(TokenType.SEMICOLON);
        return offset;
    }

    private Integer parseProcedure(Integer base, Integer offset) {
        getNextTokenAndAssertTokenType(TokenType.PROCEDURE);

        Token ident = getNextTokenAndAssertTokenType(TokenType.IDENT);
        SymbolTable.addProcedure(ident.getValue(), base, offset++);
        getNextTokenAndAssertTokenType(TokenType.SEMICOLON);


        PL0Parser.parseBlock(base + offset);

        getNextTokenAndAssertTokenType(TokenType.SEMICOLON);
        return offset;
    }

}
