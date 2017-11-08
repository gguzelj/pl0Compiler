package com.fiuba.pl0compiler.internal;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.parser.ParserException;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Optional;

import static com.fiuba.pl0compiler.internal.SymbolType.CONST;
import static com.fiuba.pl0compiler.internal.SymbolType.PROCEDURE;
import static com.fiuba.pl0compiler.internal.SymbolType.VAR;
import static java.util.Objects.requireNonNull;

public class SymbolTable {

    private static final Logger LOG = LoggerFactory.getLogger(SymbolTable.class);
    private static final ArrayList<Symbol> symbols = new ArrayList<>();
    private static Integer varCounter = 0;

    public static Boolean isVar(String name, Integer base, Integer offset) {
        return isFromType(VAR, name, base, offset);
    }

    public static Boolean isConst(String name, Integer base, Integer offset) {
        return isFromType(CONST, name, base, offset);
    }

    public static Boolean isProcedure(String name, Integer base, Integer offset) {
        return isFromType(PROCEDURE, name, base, offset);
    }

    public static Integer getProcedure(String name, Integer base, Integer offset) {
        Symbol symbol = getSymbol(name, base, offset)
                .orElseThrow(() -> new IllegalStateException("No procedure with name " + name));
        return symbol.getValue();
    }

    public static Integer getVar(String name, Integer base, Integer offset) {
        Symbol symbol = getSymbol(name, base, offset)
                .orElseThrow(() -> new IllegalStateException("No var with name " + name));
        return symbol.getValue();
    }

    public static Integer getConst(String name, Integer base, Integer offset) {
        Symbol symbol = getSymbol(name, base, offset)
                .orElseThrow(() -> new IllegalStateException("No const with name " + name));
        return symbol.getValue();
    }

    private static Boolean isFromType(SymbolType type, String name, Integer base, Integer offset) {
        Optional<SymbolType> symbolType = getSymbol(name, base, offset).map(Symbol::getSymbolType);
        return symbolType.isPresent() && symbolType.get().equals(type);
    }

    private static Optional<Symbol> getSymbol(String name, Integer base, Integer offset) {
        for (int i = base+offset-1; i >= 0; i--) {
            Symbol symbol = SymbolTable.symbols.get(i);
            if (symbol.getName().equalsIgnoreCase(name)) {
                return Optional.of(symbol);
            }
        }
        return Optional.empty();
    }

    public static void addConst(String name, String value, Integer base, Integer offset) {
        SymbolTable.checkDefinition(name, base, offset);
        SymbolTable.symbols.add(base+offset, new Symbol(SymbolType.CONST, name, Integer.valueOf(value)));
    }

    public static void addVar(String name, Integer base, Integer offset) {
        SymbolTable.checkDefinition(name, base, offset);
        SymbolTable.symbols.add(base+offset, new Symbol(VAR, name, varCounter++));
    }

    public static void addProcedure(String name, Integer base, Integer offset) {
        SymbolTable.checkDefinition(name, base, offset);
        SymbolTable.symbols.add(base+offset, new Symbol(SymbolType.PROCEDURE, name, PL0Parser.writer.getPosition()));
    }

    public static void checkVarExistence(String name, Integer base, Integer offset) {
        doCheck(name, VAR, base, offset);
    }

    public static void checkProcedureExistence(String name, Integer base, Integer offset) {
        doCheck(name, SymbolType.PROCEDURE, base, offset);
    }

    /**
     * Check if the identifier was already defined
     */
    private static void checkDefinition(String name, Integer base, Integer offset) {
        for (int i = base; i <base+offset; i++) {
            Symbol symbol = SymbolTable.symbols.get(i);
            if (symbol.getName().equalsIgnoreCase(name)) {
                LOG.error("Identifier already defined: {}", name);
                throw new ParserException("Identifier already defined: " + name);
            }
        }
    }

    public static void checkVarOrConstExistence(String name, Integer base, Integer offset) {
        for (int i = base+offset-1; i >= 0; i--) {
            Symbol symbol = SymbolTable.symbols.get(i);
            if (symbol.getName().equalsIgnoreCase(name)) {
                if (symbol.getSymbolType() == SymbolType.PROCEDURE) {
                    StringBuilder msg = new StringBuilder("Identifier ")
                            .append(name)
                            .append(" is not a const or var");
                    LOG.error(msg.toString());
                    throw new ParserException(msg.toString());
                }
                return;
            }
        }
        LOG.error("Unknown identifier: {}", name);
        throw new ParserException("Unknown identifier: " + name);
    }

    /**
     * Check the existence of the specified identifier (value + type) in the symbol table.
     */
    private static void doCheck(String name, SymbolType type, Integer base, Integer offset) {
        for (int i = base+offset-1; i >= 0; i--) {
            Symbol symbol = SymbolTable.symbols.get(i);
            if (symbol.getName().equalsIgnoreCase(name)) {
                if (symbol.getSymbolType() != type) {
                    StringBuilder msg = new StringBuilder("Identifier ")
                            .append(name)
                            .append(" is not from type ")
                            .append(type)
                            .append("(type ")
                            .append(symbol.getSymbolType())
                            .append(")");
                    LOG.error(msg.toString());
                    throw new ParserException(msg.toString());
                }
                return;
            }
        }
        LOG.error("Unknown identifier: {}", name);
        throw new ParserException("Unknown identifier: " + name);
    }

    public static int getVarCount() {
        return varCounter;
    }

    public static class Symbol {
        private final String name;
        private final SymbolType symbolType;
        private final Integer value;

        public Symbol(SymbolType symbolType, String name, Integer value) {
            this.symbolType = symbolType;
            this.name = requireNonNull(name, "Name of symbol cant be null");
            this.value = value;
        }

        public SymbolType getSymbolType() {
            return symbolType;
        }

        public String getName() {
            return name;
        }

        public Integer getValue() {
            return value;
        }


    }
}
