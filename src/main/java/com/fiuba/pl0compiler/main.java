package com.fiuba.pl0compiler;

import com.fiuba.pl0compiler.parser.PL0Parser;
import com.fiuba.pl0compiler.scanner.TokenType;
import com.fiuba.pl0compiler.writer.Pl0Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class main {

    private static final Logger LOG = LoggerFactory.getLogger(main.class);

    public static void main(String argv[]) throws IOException {
        if (argv.length != 1 && argv.length != 2) {
            LOG.error("Indique el archivo PL0 a parsear: \n java -jar ./pl0parser \"inputFile.pl0\" \"outputfile\"");
            return;
        }

        try {
            PL0Parser pl0Parser = new PL0Parser();
            pl0Parser.parse(argv[0], argv.length == 2 ? argv[1] : null);
        } catch (Exception e) {
            LOG.error("Corrija los problemas indicados antes de volver a compilar");
        }
    }

}