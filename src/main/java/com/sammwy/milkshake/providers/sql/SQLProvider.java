package com.sammwy.milkshake.providers.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sammwy.milkshake.Provider;
import com.sammwy.milkshake.ProviderInfo;
import com.sammwy.milkshake.Repository;
import com.sammwy.milkshake.RepositoryCache;
import com.sammwy.milkshake.Schema;
import com.sammwy.milkshake.query.Filter.Find;
import com.sammwy.milkshake.query.Filter.Update;

public abstract class SQLProvider implements Provider {
    protected Connection connection;

    protected abstract String getDriverClass();

    protected abstract String getConnectionString(ProviderInfo info);

    protected abstract void setupConnection(Connection connection) throws SQLException;

    protected abstract String escapeIdentifier(String identifier);

    @Override
    public void connect(ProviderInfo info) {
        try {
            Class.forName(getDriverClass());
            this.connection = DriverManager.getConnection(getConnectionString(info),
                    info.getUsername(), info.getPassword());
            setupConnection(this.connection);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    @Override
    public boolean insert(String collection, Map<String, Object> data) {
        try {
            StringBuilder sql = new StringBuilder("INSERT INTO " + collection + " (");
            StringBuilder values = new StringBuilder(") VALUES (");
            List<Object> parameters = new ArrayList<>();

            boolean first = true;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!first) {
                    sql.append(", ");
                    values.append(", ");
                }

                sql.append(escapeIdentifier(entry.getKey()));
                values.append("?");
                parameters.add(entry.getValue());
                first = false;
            }

            sql.append(values).append(")");

            try (PreparedStatement stmt = connection.prepareStatement(sql.toString(),
                    Statement.RETURN_GENERATED_KEYS)) {
                setParameters(stmt, parameters);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert data", e);
        }
    }

    @Override
    public int insertMany(String collection, List<Map<String, Object>> dataList) {
        if (dataList.isEmpty())
            return 0;

        try {
            connection.setAutoCommit(false);
            int count = 0;

            if (canUseBatchInsert(dataList)) {
                count = executeBatchInsert(collection, dataList);
            } else {
                for (Map<String, Object> data : dataList) {
                    if (insert(collection, data))
                        count++;
                }
            }

            connection.commit();
            return count;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to insert multiple records", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    @Override
    public List<Map<String, Object>> find(String collection, Find criteria) {
        try {
            SQLCriteriaResult criteriaResult = SQLUtils.buildWhereCriteria(criteria);
            String sql = "SELECT * FROM " + collection;

            if (!criteriaResult.getWhereClause().isEmpty()) {
                sql += " WHERE " + criteriaResult.getWhereClause();
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                setParameters(stmt, criteriaResult.getParameters());
                return resultSetToList(stmt.executeQuery());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find data", e);
        }
    }

    @Override
    public Map<String, Object> findOne(String collection, Find criteria) {
        try {
            SQLCriteriaResult criteriaResult = SQLUtils.buildWhereCriteria(criteria);
            String sql = "SELECT * FROM " + collection;

            if (!criteriaResult.getWhereClause().isEmpty()) {
                sql += " WHERE " + criteriaResult.getWhereClause();
            }
            sql += " LIMIT 1";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                setParameters(stmt, criteriaResult.getParameters());
                List<Map<String, Object>> results = resultSetToList(stmt.executeQuery());
                return results.isEmpty() ? null : results.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find one record", e);
        }
    }

    @Override
    public Map<String, Object> findById(String collection, String id) {
        return findOne(collection, new Find().eq("_id", id));
    }

    @Override
    public int update(String collection, Find criteria, Update update) {
        try {
            SQLUpdateResult updateResult = SQLUtils.buildUpdateStatement(update);
            if (updateResult.getSetClause().isEmpty()) {
                return 0;
            }

            SQLCriteriaResult criteriaResult = SQLUtils.buildWhereCriteria(criteria);
            String sql = "UPDATE " + collection + " SET " + updateResult.getSetClause();

            if (!criteriaResult.getWhereClause().isEmpty()) {
                sql += " WHERE " + criteriaResult.getWhereClause();
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int paramIndex = 1;

                // Set update parameters
                for (Object param : updateResult.getParameters()) {
                    stmt.setObject(paramIndex++, param);
                }

                // Set where parameters
                for (Object param : criteriaResult.getParameters()) {
                    stmt.setObject(paramIndex++, param);
                }

                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update data", e);
        }
    }

    @Override
    public boolean updateByID(String collection, String id, Update update) {
        return update(collection, new Find().eq("_id", id), update) > 0;
    }

    @Override
    public boolean updateOne(String collection, Find criteria, Update update) {
        try {
            SQLUpdateResult updateResult = SQLUtils.buildUpdateStatement(update);
            if (updateResult.getSetClause().isEmpty()) {
                return false;
            }

            SQLCriteriaResult criteriaResult = SQLUtils.buildWhereCriteria(criteria);
            String sql = "UPDATE " + collection + " SET " + updateResult.getSetClause();

            if (!criteriaResult.getWhereClause().isEmpty()) {
                sql += " WHERE " + criteriaResult.getWhereClause();
            }
            sql += " LIMIT 1";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int paramIndex = 1;

                // Set update parameters
                for (Object param : updateResult.getParameters()) {
                    stmt.setObject(paramIndex++, param);
                }

                // Set where parameters
                for (Object param : criteriaResult.getParameters()) {
                    stmt.setObject(paramIndex++, param);
                }

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update one record", e);
        }
    }

    @Override
    public int delete(String collection, Find criteria) {
        try {
            SQLCriteriaResult criteriaResult = SQLUtils.buildWhereCriteria(criteria);
            String sql = "DELETE FROM " + collection;

            if (!criteriaResult.getWhereClause().isEmpty()) {
                sql += " WHERE " + criteriaResult.getWhereClause();
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                setParameters(stmt, criteriaResult.getParameters());
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete data", e);
        }
    }

    @Override
    public boolean deleteByID(String collection, String id) {
        return delete(collection, new Find().eq("_id", id)) > 0;
    }

    @Override
    public boolean deleteOne(String collection, Find criteria) {
        try {
            SQLCriteriaResult criteriaResult = SQLUtils.buildWhereCriteria(criteria);
            String sql = "DELETE FROM " + collection;

            if (!criteriaResult.getWhereClause().isEmpty()) {
                sql += " WHERE " + criteriaResult.getWhereClause();
            }
            sql += " LIMIT 1";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                setParameters(stmt, criteriaResult.getParameters());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete one record", e);
        }
    }

    @Override
    public <T extends Schema> Repository<T> addRepository(Class<T> schemaClass) {
        Repository<T> repo = new Repository<>(this, schemaClass);
        RepositoryCache.cache(schemaClass, repo);
        initialize(schemaClass);
        return repo;
    }

    protected void setParameters(PreparedStatement stmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
    }

    protected List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metadata.getColumnName(i), rs.getObject(i));
            }
            results.add(row);
        }
        return results;
    }

    private boolean canUseBatchInsert(List<Map<String, Object>> dataList) {
        if (dataList.size() <= 1)
            return true;

        Map<String, Object> first = dataList.get(0);
        return dataList.stream().allMatch(map -> map.keySet().equals(first.keySet()));
    }

    private int executeBatchInsert(String table, List<Map<String, Object>> dataList) throws SQLException {
        Map<String, Object> first = dataList.get(0);
        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");

        sql.append(first.keySet().stream()
                .map(this::escapeIdentifier)
                .collect(Collectors.joining(", ")));

        sql.append(") VALUES (")
                .append(String.join(", ", Collections.nCopies(first.size(), "?")))
                .append(")");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (Map<String, Object> data : dataList) {
                int i = 1;
                for (Object value : data.values()) {
                    stmt.setObject(i++, value);
                }
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            return Arrays.stream(results).sum();
        }
    }
}