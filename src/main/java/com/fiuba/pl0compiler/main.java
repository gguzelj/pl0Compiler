package com.fiuba.pl0compiler;

import java.io.*;

public class main {

    public static void main(String argv[]) throws IOException {

        String nomArch = "/home/german/Escritorio/Facultad/7516 - Lenguajes de Programacion/pl0compiler/src/main/resources/BIEN-00.PL0";
        Reader file = new BufferedReader(new FileReader(nomArch));
        AnalizadorLexico analizadorLexico = new AnalizadorLexico(file);

        do {
            analizadorLexico.escanear();
            System.out.println(analizadorLexico.getS() + ": " + analizadorLexico.getCad());
        } while (analizadorLexico.getS() != Token.EOF);


        String aa = "const a = 2; \"hola \"\" mundo";

    }
}