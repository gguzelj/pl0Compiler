package com.fiuba.pl0compiler;

import com.fiuba.pl0compiler.parser.PL0Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class main {

    private static final Logger LOG = LoggerFactory.getLogger(main.class);

    public static void main(String argv[]) throws IOException {

        parse("/home/german/workspace/pl0Compiler/src/main/resources/pl0_examples/MAL-01.PL0");

    }

    private static void parse(String file) throws FileNotFoundException {
        PL0Parser pl0Parser = new PL0Parser();
        pl0Parser.parse(file);
    }
}