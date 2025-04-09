package com.sammwy.milkshake.providers.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.sammwy.milkshake.ProviderInfo;
import com.sammwy.milkshake.query.Filter.Update;

public class SQLiteProvider extends SQLProvider {
    public SQLiteProvider open(File file) {
        ProviderInfo info = new ProviderInfo(null, null, file.getAbsolutePath());
        connect(info);
        return this;
    }

    @Override
    protected String getDriverClass() {
        return "org.sqlite.JDBC";
    }

    @Override
    protected String getConnectionString(ProviderInfo info) {
        return "jdbc:sqlite:" + info.getDatabase();
    }

    @Override
    protected void setupConnection(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    @Override
    protected String escapeIdentifier(String identifier) {
        return identifier;
    }

    @Override
    public boolean upsert(String collection, Map<String, Object> data) {
        String idField = "_id";
        Object idValue = data.get(idField);

        if (idValue == null) {
            throw new IllegalArgumentException("Missing ID field for upsert operation");
        }

        try {
            // Check if record exists
            String checkSql = "SELECT 1 FROM " + collection + " WHERE " + idField + " = ?";
            boolean exists;

            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setObject(1, idValue);
                exists = checkStmt.executeQuery().next();
            }

            if (exists) {
                // Update
                // Find criteria = new Find().eq(idField, idValue);
                Update update = new Update();

                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (!entry.getKey().equals(idField)) {
                        update.set(entry.getKey(), entry.getValue());
                    }
                }

                return updateByID(collection, idValue.toString(), update);
            } else {
                // Insert
                return insert(collection, data);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to upsert data", e);
        }
    }

    /**
     * Check if a table exists in the SQLite database
     * 
     * @param tableName The name of the table to check
     * @return true if the table exists
     * @throws SQLException if a database error occurs
     */
    @Override
    public boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean isSQLite() {
        return true;
    }

}