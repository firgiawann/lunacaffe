package com.lunacaffe.model;

public class Menu {
    private String idMenu;
    private String namaMenu;
    private String kategori;
    private double harga;
    private int stok;
    private String imagePath;
    private boolean isNew;
    private boolean isBestseller;

    public Menu(String idMenu, String namaMenu, String kategori, double harga, int stok, String imagePath, boolean isNew, boolean isBestseller) {
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.kategori = kategori;
        this.harga = harga;
        this.stok = stok;
        this.imagePath = imagePath;
        this.isNew = isNew;
        this.isBestseller = isBestseller;
    }

    public boolean kurangiStok(int qty) {
        if (this.stok >= qty) {
            this.stok -= qty;
            return true;
        }
        return false;
    }

    public void tambahStok(int qty) {
        this.stok += qty;
    }

    public String getIdMenu() { return idMenu; }
    public String getNamaMenu() { return namaMenu; }
    public String getKategori() { return kategori; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }
    public String getImagePath() { return imagePath; }
    public boolean isNew() { return isNew; }
    public boolean isBestseller() { return isBestseller; }

    public void setStok(int stok) { this.stok = stok; }
    public void setHarga(double harga) { this.harga = harga; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setNew(boolean isNew) { this.isNew = isNew; }
    public void setBestseller(boolean isBestseller) { this.isBestseller = isBestseller; }
}
