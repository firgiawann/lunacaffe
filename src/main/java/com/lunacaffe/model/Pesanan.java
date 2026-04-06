package com.lunacaffe.model;

import java.util.ArrayList;
import java.util.List;

public class Pesanan {
    private String idPesanan;
    private String namaPelanggan;
    private List<DetailPesanan> items;
    private double totalHarga;
    private String status;
    private String timestamp;

    public Pesanan(String idPesanan, String namaPelanggan, String timestamp) {
        this.idPesanan = idPesanan;
        this.namaPelanggan = namaPelanggan;
        this.items = new ArrayList<>();
        this.totalHarga = 0.0;
        this.status = "Menunggu";
        this.timestamp = timestamp;
    }

    public void addItem(DetailPesanan detail) {
        this.items.add(detail);
        this.totalHarga = hitungTotal();
    }

    public double hitungTotal() {
        double total = 0;
        for (DetailPesanan item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void setStatus(String status) { this.status = status; }
    public void setIdPesanan(String id) { this.idPesanan = id; }
    public void setNamaPelanggan(String nama) { this.namaPelanggan = nama; }
    public void setTotalHarga(double total) { this.totalHarga = total; }

    public String getIdPesanan() { return idPesanan; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public List<DetailPesanan> getItems() { return items; }
    public double getTotalHarga() { return totalHarga; }
    public String getStatus() { return status; }
    public String getTimestamp() { return timestamp; }
}
