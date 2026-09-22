package com.nomevini.opengrana.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

public class CookieBearerTokenResolver implements BearerTokenResolver {

    private static final String COOKIE_NAME = "access_token";

    @Override
    public @Nullable String resolve(HttpServletRequest request) {
        if (request.getCookies() == null){
            return null;
        }

        for (Cookie cookie : request.getCookies()){
            if (COOKIE_NAME.equals(cookie.getName())){
                return  cookie.getValue();
            }
        }

        return null;
    }
}
