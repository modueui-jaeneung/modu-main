package com.modu.ClientViewServer.utils;

import org.springframework.web.util.UriComponentsBuilder;

public class UrlUtils {

    public static String GetUrlString(String scheme, String host, int port, String path ){
        String uriString = UriComponentsBuilder
                .newInstance()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(path)
                .build().toUriString();

        return uriString;

    }

}
