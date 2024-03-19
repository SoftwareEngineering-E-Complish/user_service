package com.ecomplish.user_service.model._enum;

public enum AuthType {
    LOGIN,
    SIGNUP,
    LOGOUT,
    AUTH_CODE;

    public String toPath() {
        switch (this) {
            case LOGIN:
                return "login";
            case SIGNUP:
                return "signup";
            case LOGOUT:
                return "logout";
            case AUTH_CODE:
                return "oauth2/token";
        }
        return "";
    }
}
