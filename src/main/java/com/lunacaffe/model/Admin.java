package com.lunacaffe.model;

public class Admin extends Pegawai {

    public Admin(String nama, String username, String pass) {
        super(null, nama, username, pass, "admin");
    }

    public void kelolaMenu() {
        System.out.println("Admin " + nama + " mengelola menu.");
    }

    public void kelolaAkun() {
        System.out.println("Admin " + nama + " mengelola akun pegawai.");
    }
}
