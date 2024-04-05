package com.luizalabs.simple.common.util;

import java.net.InetSocketAddress;

public class Converter {
    private Converter() {
    }

    public static InetSocketAddress inetSocketAddress(String hostname) {
        String uri[] = hostname.split(":");
        return InetSocketAddress.createUnresolved(uri[0], Integer.parseInt(uri[1]));
    }
}
