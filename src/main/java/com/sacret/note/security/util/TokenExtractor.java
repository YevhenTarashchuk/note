package com.sacret.note.security.util;

public interface TokenExtractor {

    String extract(String payload, JwtAuthType type);

}
