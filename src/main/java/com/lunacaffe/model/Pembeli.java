package com.lunacaffe.model;

public class Pembeli extends Aktor {
    private String idTiket;

    public Pembeli(String nama) {
        super(nama);
    }

    @Override
    public void masukSistem() {
        System.out.println("Pembeli " + nama + " memasuki sistem self-service.");
    }

    public String getIdTiket() {
        return idTiket;
    }

    public void setIdTiket(String idTiket) {
        this.idTiket = idTiket;
    }
}
