package com.dbOperations;

import java.sql.*;

public class UpdateOperation {

    public static int update(String table, Condition setData, Condition whereCondition) {

        if (setData == null || setData.isEmpty()) {
            return 0;
        }

       String tableName = table.split(" ")[0];
//        String setSQL = setData.toSQL().replace(" AND ", ", ");
        String setSQL = setData.toSetSQL();
        String sql;
        if (whereCondition == null || whereCondition.isEmpty()) {
            sql = "UPDATE " + tableName + " SET " + setSQL;
        } else {
            sql = "UPDATE " + tableName + " SET " + setSQL + " WHERE " + whereCondition.toSQL();
        }

        Connection con = null;
        try {
            con = DbHelper.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int i = 1;
        for (Object val : setData.getValues()) {
            try {
                ps.setObject(i++, val);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (whereCondition != null) {
            for (Object val : whereCondition.getValues()) {
                try {
                    ps.setObject(i++, val);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}