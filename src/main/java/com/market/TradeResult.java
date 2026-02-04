package com.market;

public class TradeResult {
    public static TradeResult lastTrade;

    public String buyer, seller, stock;
    public int quantity;
    public double price, total;

    public TradeResult(String b, String s, String st, int q, double p, double t){
        buyer=b; seller=s; stock=st;
        quantity=q; price=p; total=t;
    }
}

