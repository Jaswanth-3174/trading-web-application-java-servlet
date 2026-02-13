package com.dao;

import com.dbConnection.DatabaseConfig;
import com.dbOperations.*;
import com.market.TradeResult;
import com.trading.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TransactionDAO {

    private final String table = "transactions";
    private final String tableAlias = "transactions t";

    private final String joinCondition = "JOIN stocks s ON t.stock_id = s.stock_id " +
            "JOIN users buy ON t.buyer_id = buy.user_id " +
            "JOIN users sel ON t.seller_id = sel.user_id";

    private final String[] columns = {
            "t.transactions_id", "t.buyer_id", "t.seller_id", "t.stock_id", "t.quantity", "t.price",
            "s.stock_name", "buy.username AS buyer_name", "sel.username AS seller_name"
    };

    public Transaction createTransaction(int buyerId, int sellerId,
                                         int stockId, int quantity, double price) throws SQLException {
        Condition data = new Condition();
        data.add("buyer_id", buyerId);
        data.add("seller_id", sellerId);
        data.add("stock_id", stockId);
        data.add("quantity", quantity);
        data.add("price", price);

        int transId = InsertOperation.insert(table, data);
        return transId > 0 ? findById(transId) : null;
    }

    public Transaction findById(int transactionId) throws SQLException {
        Condition where = new Condition();
        where.add("t.transactions_id", transactionId);

        ArrayList<HashMap<String, Object>> rows =
                SelectOperation.selectWithJoin(tableAlias, columns, joinCondition, where, null);
        return rows.isEmpty() ? null : mapToTransaction(rows.get(0));
    }

    public List<Transaction> findByUserId(int userId) {
        SpecialCondition extra = new SpecialCondition("t.buyer_id = ? OR t.seller_id = ?",
                userId, userId);
        ArrayList<HashMap<String, Object>> rows =
                SelectOperation.selectWithAdvancedCondition(tableAlias,
                        columns, joinCondition, null,extra, null, 0);
        return mapToRowList(rows);
    }

    public List<Transaction> findAll() {
        ArrayList<HashMap<String, Object>> rows =
                SelectOperation.selectWithJoin(tableAlias, columns, joinCondition,
                        null, null);
        return mapToRowList(rows);
    }

    private Transaction mapToTransaction(HashMap<String, Object> row){
        Transaction t = new Transaction();
        t.setTransactionId(((Number) row.get("transactions_id")).intValue());
        t.setBuyerId(((Number) row.get("buyer_id")).intValue());
        t.setSellerId(((Number) row.get("seller_id")).intValue());
        t.setStockId(((Number) row.get("stock_id")).intValue());
        t.setQuantity(((Number) row.get("quantity")).intValue());
        t.setPrice(((Number) row.get("price")).doubleValue());
        return t;
    }

    private List<Transaction> mapToRowList(ArrayList<HashMap<String, Object>> rows){
        List<Transaction> transactions = new ArrayList<>();
        for (HashMap<String, Object> row : rows) {
            transactions.add(mapToTransaction(row));
        }
        return transactions;
    }

    public TradeResult getLastTrade() {

        String sql = "SELECT t.quantity, t.price, " +
                "(t.quantity * t.price) AS total, " +
                "s.stock_name, " +
                "buy.username AS buyer_name, " +
                "sel.username AS seller_name " +
                "FROM transactions t " +
                "JOIN stocks s ON t.stock_id = s.stock_id " +
                "JOIN users buy ON t.buyer_id = buy.user_id " +
                "JOIN users sel ON t.seller_id = sel.user_id " +
                "ORDER BY t.transactions_id DESC LIMIT 1";

        Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (rs.next()) {
                return new TradeResult(
                        rs.getString("buyer_name"),
                        rs.getString("seller_name"),
                        rs.getString("stock_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}