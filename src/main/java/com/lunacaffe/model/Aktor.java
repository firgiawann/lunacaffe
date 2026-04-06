package com.lunacaffe.model;

public abstract class Aktor {
    protected String id;
    protected String nama;

    public Aktor(String nama) {
        this.nama = nama;
    }

    public Aktor(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public abstract void masukSistem();

    public String getNama() {
        return nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
