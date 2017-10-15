package com.fiuba.pl0compiler.parser;

import com.fiuba.pl0compiler.scanner.Scanner;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Parser {

    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

    private final Scanner scanner;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public void parse() {
        LOG.info("Parsing {}", scanner.getFilename());
        this.parseProgram();
    }

    private void parseProgram() {
        this.parseBlock();
        getNextTokenAndAssertTokenType(TokenType.END_OF_PROGRAM);
    }

    private void parseBlock() {
        LOG.info("Parsing BLOCK");

        if (scanner.getNextTokenType() == TokenType.CONST) {
            this.parseConst();
        }
        if (scanner.getNextTokenType() == TokenType.VAR){
            this.parseVar();
        }

        while (scanner.getNextTokenType() == TokenType.PROCEDURE) {
            this.parseProcedure();
        }

        this.parseStatement();
        LOG.info("Parsing BLOCK END");
    }

    private void parseConst() {
        getNextTokenAndAssertTokenType(TokenType.CONST);
        do {
            getNextTokenAndAssertTokenType(TokenType.IDENT);
            getNextTokenAndAssertTokenType(TokenType.EQUAL);
            getNextTokenAndAssertTokenType(TokenType.NUMBER);
        } while (scanner.readToken().getType() == TokenType.COMMA);

        assertTokenType(TokenType.SEMICOLON);
    }

    private void parseVar() {
        getNextTokenAndAssertTokenType(TokenType.VAR);
        do {
            getNextTokenAndAssertTokenType(TokenType.IDENT);
        } while (scanner.readToken().getType() == TokenType.COMMA);
        assertTokenType(TokenType.SEMICOLON);
    }

    private void parseProcedure() {
        LOG.info("Parsing PROCEDURE");
        getNextTokenAndAssertTokenType(TokenType.PROCEDURE);

        getNextTokenAndAssertTokenType(TokenType.IDENT);
        getNextTokenAndAssertTokenType(TokenType.SEMICOLON);

        this.parseBlock();

        getNextTokenAndAssertTokenType(TokenType.SEMICOLON);

        LOG.info("Parsing PROCEDURE END");
    }

    private void parseStatement() {
        LOG.info("Parsing STATEMENT");
        TokenType type = scanner.getNextTokenType();

        if (type == TokenType.IDENT) {
            getNextTokenAndAssertTokenType(TokenType.IDENT);
            getNextTokenAndAssertTokenType(TokenType.ASSIGN);
            this.parseExpression();
        }
        else if (type == TokenType.CALL){
            getNextTokenAndAssertTokenType(TokenType.CALL);
            getNextTokenAndAssertTokenType(TokenType.IDENT);
        }
        else if (type == TokenType.BEGIN) {
            getNextTokenAndAssertTokenType(TokenType.BEGIN);
            do {
                this.parseStatement();
                scanner.readToken();
            } while (scanner.getTokenType() == TokenType.SEMICOLON);
            assertTokenType(TokenType.END);
        }
        else if (type == TokenType.IF) {
            getNextTokenAndAssertTokenType(TokenType.IF);
            this.parseCondition();
            getNextTokenAndAssertTokenType(TokenType.THEN);
            this.parseStatement();
        }
        else if (type == TokenType.WHILE) {
            getNextTokenAndAssertTokenType(TokenType.WHILE);
            this.parseCondition();
            getNextTokenAndAssertTokenType(TokenType.DO);
            this.parseStatement();
        }
        else if (type == TokenType.READLN) {
            getNextTokenAndAssertTokenType(TokenType.READLN);
            getNextTokenAndAssertTokenType(TokenType.OPEN_PARENTHESIS);
            getNextTokenAndAssertTokenType(TokenType.IDENT);
            while (scanner.getNextTokenType() == TokenType.COMMA) {
                scanner.readToken();
                getNextTokenAndAssertTokenType(TokenType.IDENT);
            }

            getNextTokenAndAssertTokenType(TokenType.CLOSE_PARENTHESIS);
        }
        else if (type == TokenType.WRITE) {
            getNextTokenAndAssertTokenType(TokenType.WRITE);
            doWrite();
        }
        else if (type == TokenType.WRITELN) {
            getNextTokenAndAssertTokenType(TokenType.WRITELN);
            if (scanner.getNextTokenType() == TokenType.OPEN_PARENTHESIS) {
                this.doWrite();
            }
        }

        LOG.info("Parsing STATEMENT END");
    }

    private void doWrite() {
        getNextTokenAndAssertTokenType(TokenType.OPEN_PARENTHESIS);
        if (scanner.getNextTokenType() == TokenType.STRING) {
            getNextTokenAndAssertTokenType(TokenType.STRING);
        } else {
            this.parseExpression();
        }
        while (scanner.getNextTokenType() == TokenType.COMMA) {
            scanner.readToken();
            if (scanner.getNextTokenType() == TokenType.STRING) {
                getNextTokenAndAssertTokenType(TokenType.STRING);
            } else {
                this.parseExpression();
            }
        }
        getNextTokenAndAssertTokenType(TokenType.CLOSE_PARENTHESIS);
    }

    private void parseCondition() {
        LOG.info("Parsing CONDITION");
        if (scanner.getNextTokenType() == TokenType.ODD) {
            scanner.readToken();
            this.parseExpression();
            return;
        }

        this.parseExpression();
        if (scanner.getNextTokenType() == TokenType.EQUAL) {
            getNextTokenAndAssertTokenType(TokenType.EQUAL);
        } else if (scanner.getNextTokenType() == TokenType.DISTINCT) {
            getNextTokenAndAssertTokenType(TokenType.DISTINCT);
        } else if (scanner.getNextTokenType() == TokenType.LESS_THAN) {
            getNextTokenAndAssertTokenType(TokenType.LESS_THAN);
        } else if (scanner.getNextTokenType() == TokenType.LESS_OR_EQUAL) {
            getNextTokenAndAssertTokenType(TokenType.LESS_OR_EQUAL);
        } else if (scanner.getNextTokenType() == TokenType.GREATER_THAN) {
            getNextTokenAndAssertTokenType(TokenType.GREATER_THAN);
        } else if (scanner.getNextTokenType() == TokenType.GREATER_OR_EQUAL) {
            getNextTokenAndAssertTokenType(TokenType.GREATER_OR_EQUAL);
        } else {
            throw new ParserException("Unknown token " + scanner.readToken());
        }

        this.parseExpression();
        LOG.info("Parsing CONDITION END");
    }

    private void parseExpression() {
        LOG.info("Parsing EXPRESSION");
        if (scanner.getNextTokenType() == TokenType.ADD || scanner.getNextTokenType() == TokenType.SUBTRACT)
            scanner.readToken();

        this.parseTerm();
        while (scanner.getNextTokenType() == TokenType.ADD || scanner.getNextTokenType() == TokenType.SUBTRACT) {
            scanner.readToken();
            this.parseTerm();
        }
        LOG.info("Parsing EXPRESSION END");
    }

    private void parseTerm() {
        LOG.info("Parsing TERM");
        this.parseFactor();
        while(scanner.getNextTokenType() == TokenType.MULTIPLY || scanner.getNextTokenType() == TokenType.DIVIDE) {
            scanner.readToken();
            this.parseFactor();
        }
        LOG.info("Parsing TERM END");
    }

    private void parseFactor() {
        //TODO Refactor all of this...
        LOG.info("Parsing FACTOR");
        TokenType type = scanner.getNextTokenType();
        if (type == TokenType.IDENT) {
            getNextTokenAndAssertTokenType(TokenType.IDENT);
        } else if (type == TokenType.NUMBER) {
            getNextTokenAndAssertTokenType(TokenType.NUMBER);
        } else if (type == TokenType.OPEN_PARENTHESIS) {
            getNextTokenAndAssertTokenType(TokenType.OPEN_PARENTHESIS);
            this.parseExpression();
            getNextTokenAndAssertTokenType(TokenType.CLOSE_PARENTHESIS);
        } else {
            LOG.error("Invalid option for factor: {}", scanner.getToken());
            throw new ParserException("Invalid option for factor: ");
        }
        LOG.info("Parsing FACTOR END");
    }

    private void getNextTokenAndAssertTokenType(TokenType tokenType) {
        this.scanner.readToken();
        this.assertTokenType(tokenType);
    }

    private void assertTokenType(TokenType tokenType) {
        if (scanner.getTokenType() != tokenType) {
            throw new ParserException("Expecting " + tokenType + ": " + this.scanner.getToken());
        }
    }

}
