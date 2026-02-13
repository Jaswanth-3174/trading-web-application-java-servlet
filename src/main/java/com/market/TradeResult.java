package com.market;

public class TradeResult {

    private String buyer;
    private String seller;
    private String stock;
    private int quantity;
    private double price;
    private double total;

    public TradeResult(String buyer, String seller, String stock,
                       int quantity, double price, double total) {
        this.buyer = buyer;
        this.seller = seller;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public String getBuyer() { return buyer; }
    public String getSeller() { return seller; }
    public String getStock() { return stock; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }
}
