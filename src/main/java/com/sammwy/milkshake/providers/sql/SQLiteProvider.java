package com.sammwy.milkshake.providers.sql;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.sammwy.milkshake.ProviderInfo;
import com.sammwy.milkshake.Schema;
import com.sammwy.milkshake.annotations.SchemaType;
import com.sammwy.milkshake.query.Filter.Update;
import com.sammwy.milkshake.utils.ReflectionUtils;

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
     * Initializes the database table for a Schema class.
     * This method analyzes the fields in the Schema class and creates
     * the corresponding SQLite table if it doesn't exist.
     *
     * @param <T>         The type of Schema
     * @param schemaClass The Schema class to initialize a table for
     * @return true if initialization was successful
     */
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
            createTableSQL.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
            createTableSQL.append("_id TEXT PRIMARY KEY");

            // Get all fields from the schema class including parent classes
            List<Field> fields = ReflectionUtils.getPropFields(schemaClass);
            for (Field field : fields) {
                // Skip the id field as we've already added it
                if (field.getName().equals("id")) {
                    continue;
                }

                // Skip transient fields
                if (java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                // Skip static fields
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                // Skip the cachedFields map
                if (field.getName().equals("cachedFields")) {
                    continue;
                }

                String sqlType = SQLUtils.getSQLType(field.getType(), true);
                if (sqlType != null) {
                    createTableSQL.append(", ").append(field.getName()).append(" ").append(sqlType);
                }
            }

            createTableSQL.append(")");

            // Execute CREATE TABLE statement
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL.toString());
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize table for " + schemaClass.getName(), e);
        }
    }

    /**
     * Check if a table exists in the SQLite database
     * 
     * @param tableName The name of the table to check
     * @return true if the table exists
     * @throws SQLException if a database error occurs
     */
    public boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}