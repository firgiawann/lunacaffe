package com.lunacaffe.model;

public class Kasir extends Pegawai {

    public Kasir(String nama, String username, String pass) {
        super(null, nama, username, pass, "kasir");
    }

    public void updateStatusPesanan(String idPesanan) {
        System.out.println("Kasir " + nama + " memperbarui status pesanan: " + idPesanan);
    }
}
