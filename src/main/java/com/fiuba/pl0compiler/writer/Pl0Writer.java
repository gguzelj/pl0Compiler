package com.fiuba.pl0compiler.writer;

import com.fiuba.pl0compiler.parser.ParserException;
import com.fiuba.pl0compiler.scanner.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


//objdump -D -M=intel programa
//hexdump programa

public class Pl0Writer {
    private static final Logger LOG = LoggerFactory.getLogger(Pl0Writer.class);
    private static final String DEFAULT_OUTPUT_FILE = "./output";

    private final FileOutputStream outputFile;
    private final List<Integer> code = new ArrayList<>();
    private final Map<TokenType, Integer> jumpMap = new HashMap<>();

    public Pl0Writer() {
        this(DEFAULT_OUTPUT_FILE);
    }

    public Pl0Writer(String outputFile) {
        this.outputFile = this.createFileOutputFile(outputFile);
        this.code.addAll(this.readHeader());
        this.code.addAll(Arrays.asList(0xbf, 0x00, 0x00, 0x00, 0x00));
        //Guardamos en el registro EDI la dir. de memoria donde se alojan las variables

        this.jumpMap.put(TokenType.EQUAL, 0x74);
        this.jumpMap.put(TokenType.DISTINCT, 0x75);
        this.jumpMap.put(TokenType.LESS_THAN, 0x7C);
        this.jumpMap.put(TokenType.LESS_OR_EQUAL, 0x7E);
        this.jumpMap.put(TokenType.GREATER_THAN, 0x7F);
        this.jumpMap.put(TokenType.GREATER_OR_EQUAL, 0x7D);
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

    public void movEaxEdiOffset(Integer offset) {
        this.write(0x8b, 0x87);
        this.write(this.intToBytes(offset * 4));
    }

    public void pushEax() {
        this.write(0x50);
    }

    private Integer[] intToBytes(Integer v) {
        return new Integer[] {
                (v) & 0xFF,
                (v >>> 8) & 0xFF,
                (v >>> 16) & 0xFF,
                (v >>> 24) & 0xFF
        };
    }

    private void write(Integer ... bytes) {
        this.code.addAll(Arrays.asList(bytes));
    }

    public void popEax() {
        if (this.code.get(this.code.size()-1) == 0x50) {
            this.code.remove(this.code.size()-1);
        } else {
            this.write(0x58);
        }
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

    public void negEax() {
        this.write(0xF7, 0xD8);
    }

    public void addEaxEbx() {
        this.write(0x01, 0xD8);
    }

    public void subEaxEbx() {
        this.write(0x29, 0xD8);
    }

    public void testAl(int toTest) {
        this.write(0xa8, toTest);
    }

    public void oddJump() {
        this.write(0x7B, 0x05);
    }

    public void cmpEbxEax() {
        this.write(0x39, 0xC3);
    }

    public void conditionalJump(TokenType tokenType) {
        this.write(this.jumpMap.get(tokenType), 0x05);
    }

    public void inconditionalJump(Integer offset) {
        this.write(0xE9);
        this.write(this.intToBytes(offset));
    }

    public void inconditionalJump() {
        this.write(0xE9, 0x00, 0x00, 0x00, 0x00);
    }

    public void ediOffsetEax(Integer offset) {
        this.write(0x89, 0x87);
        this.write(this.intToBytes(offset * 4));
    }

    public void call(int offset) {
        this.write(0xE8);
        this.write(intToBytes(offset - (this.code.size() + 4)));
    }

    public Integer getPosition() {
        return this.code.size();
    }

    public void fixUp(Integer address, Integer value) {
        if (this.code.get(this.code.size() -1) == 0x00 &&
                this.code.get(this.code.size() -2) == 0x00 &&
                this.code.get(this.code.size() -3) == 0x00 &&
                this.code.get(this.code.size() -4) == 0x00 && value == 0) {
            this.code.remove(this.code.size()-1);
            this.code.remove(this.code.size()-1);
            this.code.remove(this.code.size()-1);
            this.code.remove(this.code.size()-1);
            this.code.remove(this.code.size()-1);
        } else {
            Integer[] valueAsBytes = this.intToBytes(value);
            this.code.set(address, valueAsBytes[0]);
            this.code.set(address + 1, valueAsBytes[1]);
            this.code.set(address + 2, valueAsBytes[2]);
            this.code.set(address + 3, valueAsBytes[3]);
        }
    }

    public Integer getIntFromAddress(Integer address) {
        Integer integer1 = this.code.get(address);
        Integer integer2 = this.code.get(address+1);
        Integer integer3 = this.code.get(address+2);
        Integer integer4 = this.code.get(address+3);

        return  integer1 + (integer2 * 256) + (integer3 * 256 * 256) + (integer4 * 256 * 256 * 256);
    }

    public void movEcxOffset(Integer offset) {
        this.write(0xB9);
        this.write(this.intToBytes(offset));
    }

    public void movEdx(Integer offset) {
        this.write(0xBA);
        this.write(this.intToBytes(offset));
    }

    public void string(String string) {
        for (int i = 1; i < string.length() - 1; i++) {
            this.write((int) string.charAt(i));
        }
    }

    public void ret() {
        this.write(0xC3);
    }

    public void end() {
        this.write(0xE9);
        this.write(intToBytes(0x300 - (this.code.size() + 4)));
    }

    public void addZeros(int i) {
        for (int j = 0; j < i; j++) {
            this.write(0x00);
        }
    }
}
