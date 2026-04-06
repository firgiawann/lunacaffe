package com.lunacaffe.model;

public class Pegawai extends Aktor {
    protected String username;
    private String password;
    protected String role;

    public Pegawai(String id, String nama, String username, String password, String role) {
        super(id, nama);
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public boolean verifikasiLogin(String pass) {
        return this.password != null && this.password.equals(pass);
    }

    @Override
    public void masukSistem() {
        System.out.println("Pegawai " + nama + " (" + role + ") memasuki sistem.");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
