package com.fiuba.pl0compiler.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(ParserException.class);

    public ParserException(String description) {
        LOG.error(description);
    }
}
