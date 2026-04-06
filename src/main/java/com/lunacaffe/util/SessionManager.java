package com.lunacaffe.util;

import com.lunacaffe.model.Pegawai;

public class SessionManager {
    private static Pegawai currentUser;

    public static void setCurrentUser(Pegawai user) {
        currentUser = user;
    }

    public static Pegawai getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
