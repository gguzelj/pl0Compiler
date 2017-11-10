package com.fiuba.pl0compiler.parser.impl;

import com.fiuba.pl0compiler.internal.SymbolTable;
import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.parser.ParserException;
import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.Token;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fiuba.pl0compiler.parser.PL0Parser.*;

public class FactorParser extends AbstractParser {

    private static final Logger LOG = LoggerFactory.getLogger(FactorParser.class);

    public FactorParser(Scanner scanner) {
        super(scanner);
    }

    public void parse() {
        this.parse(0,0);
    }

    public void parse(Integer base, Integer offset) {
        LOG.debug("Parsing FACTOR");
        TokenType type = scanner.getNextTokenType();
        if (type == TokenType.IDENT) {
            Token ident = getAndAssertToken(TokenType.IDENT);
            SymbolTable.checkVarOrConstExistence(ident.getValue(), base, offset);

            //Identifier is var or const
            if (SymbolTable.isVar(ident.getValue(), base, offset)) {
                Integer varNumber = SymbolTable.getVar(ident.getValue(), base, offset);
                writer.movEaxEdiOffset(varNumber);
                writer.pushEax();
            } else if (SymbolTable.isConst(ident.getValue(), base, offset)){
                Integer aConst = SymbolTable.getConst(ident.getValue(), base, offset);
                writer.movEaxConstant(aConst);
                writer.pushEax();
            }
        } else if (type == TokenType.NUMBER) {
            Token number = getAndAssertToken(TokenType.NUMBER);
            writer.movEaxConstant(this.parseInteger(number.getValue()));
            writer.pushEax();
        } else if (type == TokenType.OPEN_PARENTHESIS) {
            getAndAssertToken(TokenType.OPEN_PARENTHESIS);
            parseExpression(base, offset);
            getAndAssertToken(TokenType.CLOSE_PARENTHESIS);
        } else {
            LOG.error("Invalid option for factor: {}", scanner.getToken());
            throw new ParserException("Invalid option for factor: ");
        }
        LOG.debug("Parsing FACTOR END");
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ParserException("Hubo un error al leer el numero " +  value);
        }
    }

}
