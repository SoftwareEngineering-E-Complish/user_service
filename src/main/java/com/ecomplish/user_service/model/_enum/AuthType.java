package com.ecomplish.user_service.model._enum;

public enum AuthType {
    LOGIN,
    SIGNUP,
    LOGOUT;

    public String toPath() {
        switch (this) {
            case LOGIN:
                return "login";
            case SIGNUP:
                return "signup";
            case LOGOUT:
                return "logout";
        }
        return "";
    }
}
