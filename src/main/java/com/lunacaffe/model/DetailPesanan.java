package com.lunacaffe.model;

public class DetailPesanan {
    private Menu menu;
    private int qty;
    private double subtotal;

    public DetailPesanan(Menu menu, int qty) {
        this.menu = menu;
        this.qty = qty;
        this.subtotal = menu.getHarga() * qty;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
        this.subtotal = this.menu.getHarga() * qty;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
