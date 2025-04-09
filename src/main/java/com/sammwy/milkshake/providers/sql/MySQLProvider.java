package com.sammwy.milkshake.providers.sql;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sammwy.milkshake.ProviderInfo;
import com.sammwy.milkshake.ProviderInfo.Options;
import com.sammwy.milkshake.Schema;
import com.sammwy.milkshake.annotations.SchemaType;
import com.sammwy.milkshake.utils.ReflectionUtils;

public class MySQLProvider extends SQLProvider {
    @Override
    protected String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    protected String getConnectionString(ProviderInfo info) {
        info.optionIfNotPresent(Options.USE_SSL, "false");
        info.optionIfNotPresent(Options.ALLOW_PUBLIC_KEY_RETRIEVAL, "true");
        info.optionIfNotPresent(Options.SERVER_TIMEZONE, "UTC");

        String protocol = info.getProtocol() != null ? info.getProtocol() : "mysql";
        String format = "jdbc:%s://%s:%d/%s%s";
        return String.format(
                format,
                protocol,
                info.getHost(),
                info.getPortInt(),
                info.getDatabase(),
                info.getOptionsAsString());
    }

    @Override
    protected void setupConnection(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @Override
    protected String escapeIdentifier(String identifier) {
        return "`" + identifier + "`";
    }

    @Override
    public boolean upsert(String collection, Map<String, Object> data) {
        try {
            StringBuilder sql = new StringBuilder("INSERT INTO " + collection + " (");
            StringBuilder values = new StringBuilder();
            StringBuilder updates = new StringBuilder();

            List<String> keys = new ArrayList<>(data.keySet());
            List<Object> insertParams = new ArrayList<>();
            List<Object> updateParams = new ArrayList<>();

            boolean first = true;
            for (String key : keys) {
                if (!first) {
                    sql.append(", ");
                    values.append(", ");
                }

                sql.append(escapeIdentifier(key));
                values.append("?");

                insertParams.add(data.get(key));
                first = false;
            }

            sql.append(") VALUES (").append(values).append(")");

            // Prepare ON DUPLICATE KEY UPDATE clause
            first = true;
            for (String key : keys) {
                if (key.equals("_id"))
                    continue; // Avoid updating the primary key

                if (!first) {
                    updates.append(", ");
                }

                updates.append(escapeIdentifier(key)).append(" = ?");
                updateParams.add(data.get(key));

                first = false;
            }

            if (!updateParams.isEmpty()) {
                sql.append(" ON DUPLICATE KEY UPDATE ").append(updates);
            }

            List<Object> allParams = new ArrayList<>();
            allParams.addAll(insertParams);
            allParams.addAll(updateParams);

            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                setParameters(stmt, allParams);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to upsert data", e);
        }
    }

    @Override
    public <T extends Schema> boolean initialize(Class<T> schemaClass) {
        SchemaType schemaTypeAnnotation = schemaClass.getAnnotation(SchemaType.class);
        if (schemaTypeAnnotation == null) {
            throw new RuntimeException("Schema class " + schemaClass.getName() + " is missing @SchemaType annotation");
        }

        String tableName = schemaTypeAnnotation.value();
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new RuntimeException(
                    "Schema class " + schemaClass.getName() + " has empty table name in @SchemaType");
        }

        try {
            // Check if table already exists
            if (tableExists(tableName)) {
                return true;
            }

            // Build CREATE TABLE statement
            StringBuilder createTableSQL = new StringBuilder();
            createTableSQL.append("CREATE TABLE IF NOT EXISTS ").append(escapeIdentifier(tableName)).append(" (");
            createTableSQL.append("`_id` VARCHAR(255) PRIMARY KEY");

            List<Field> fields = ReflectionUtils.getPropFields(schemaClass);
            for (Field field : fields) {
                String fieldName = field.getName();

                // Skip the id field as we've already added it
                if (fieldName.equals("id"))
                    continue;

                // Skip transient or static fields
                if (java.lang.reflect.Modifier.isTransient(field.getModifiers()))
                    continue;
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()))
                    continue;
                if (fieldName.equals("cachedFields"))
                    continue;

                String sqlType = SQLUtils.getSQLType(field.getType(), false);
                if (sqlType != null) {
                    createTableSQL.append(", ").append(escapeIdentifier(fieldName)).append(" ").append(sqlType);
                }
            }

            createTableSQL.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL.toString());
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize table for " + schemaClass.getName(), e);
        }
    }

    /**
     * Check if a table exists in the MySQL database.
     *
     * @param tableName The name of the table to check
     * @return true if the table exists
     * @throws SQLException if a database error occurs
     */
    public boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT 1 FROM information_schema.tables WHERE table_schema = ? AND table_name = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, connection.getCatalog()); // Gets the current database name
            stmt.setString(2, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}