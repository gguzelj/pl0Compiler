package com.fiuba.pl0compiler.writer;

import com.fiuba.pl0compiler.parser.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//objdump -D -M=intel programa
//hexdump programa

public class Pl0Writer {
    private static final Logger LOG = LoggerFactory.getLogger(Pl0Writer.class);
    private static final String DEFAULT_OUTPUT_FILE = "./output";

    private final FileOutputStream outputFile;
    private final List<Integer> code = new ArrayList<>();

    public Pl0Writer() {
        this(DEFAULT_OUTPUT_FILE);
    }

    public Pl0Writer(String outputFile) {
        this.outputFile = this.createFileOutputFile(outputFile);
        this.code.addAll(this.readHeader());
        this.code.addAll(Arrays.asList(0xbf, 0x00, 0x00, 0x00, 0x00));
        //Guardamos en el registro EDI la dir. de memoria donde se alojan las variables
    }

    public void flush() {
        try {
            for (Integer byteToFlush : this.code) {
                this.outputFile.write(byteToFlush);
            }
            this.code.clear();
            this.outputFile.flush();
        } catch (IOException e) {
            LOG.error("Unexpected error while flushing program", e);
            throw new ParserException("Unexpected error while flushing program");
        }

    }

    private FileOutputStream createFileOutputFile(String outputFile) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(outputFile);
        } catch (Exception e) {
            LOG.error("Error while creating outputFile", e);
            throw new ParserException("Error while creating outputFile");
        }
        return out;
    }

    private List<Integer> readHeader() {
        List<Integer> response = new ArrayList<>();
        FileInputStream in = null;
        try {
            in = new FileInputStream("/home/german/workspace/pl0Compiler/src/main/resources/header.bin");
            int c;
            while ((c = in.read()) != -1) {
                response.add(c);
            }
        } catch (Exception e) {
            LOG.error("Unexpected error while reading header.bin", e);
            throw new ParserException("Error while reading header.bin");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error("Error while closing header.bin", e);
                    throw new ParserException("Error while closing header.bin");
                }
            }
        }
        return response;
    }

    public void movEaxConstant(Integer value) {
        this.write(0xB8);
        this.write(this.intToBytes(value));
    }

    public void movEaxEdiOffset(String value) {
        this.write(0x8b, 0x87);
        //Agregar offset a edi
    }

    public void pushEax() {
        this.write(0x50);
    }

    private Integer[] intToBytes(Integer v) {
        return new Integer[] {
                (v >>> 24) & 0xFF,
                (v >>> 16) & 0xFF,
                (v >>> 8) & 0xFF,
                (v) & 0xFF
        };
    }

    private void write(Integer ... bytes) {
        this.code.addAll(Arrays.asList(bytes));
    }

    public void popEax() {
        this.write(0x58);
    }

    public void popEbx() {
        this.write(0x5B);
    }

    public void imulEbx() {
        this.write(0xF7, 0xEB);
    }

    public void xchgEaxEbx() {
        this.write(0x93);
    }

    public void cdq() {
        this.write(0x99);
    }

    public void idivEbx() {
        this.write(0xF7, 0xFB);
    }
}
