package com.sastreria.gestiondeprecios.util;

public class StringNormalizer {

    public static String normalize(String text) {
        return text.replaceAll("\\s+", "").toLowerCase();
    }
}
