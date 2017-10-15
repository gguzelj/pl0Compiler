package com.fiuba.pl0compiler;

import com.fiuba.pl0compiler.parser.Parser;
import com.fiuba.pl0compiler.scanner.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class main {

    private static final Logger LOG = LoggerFactory.getLogger(main.class);

    public static void main(String argv[]) throws IOException {

        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-00.PL0");
        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-01.PL0");
        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-02.PL0");
        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-03.PL0");
        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-04.PL0");
        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-05.PL0");
        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-06.PL0");
        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-07.PL0");
        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-08.PL0");
        parse("/home/german/workspace/pl0Compiler/src/main/resources/BIEN-09.PL0");

    }

    private static void parse(String file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        Parser parser = new Parser(scanner);
        parser.parse();
    }
}