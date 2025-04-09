package com.sammwy.milkshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents authentication and connection information for a database provider.
 * Encapsulates protocol, host, port, credentials, and database name
 * information.
 * Supports construction from individual components or a connection URI string.
 */
public class ProviderInfo {
    private String protocol;
    private String host;
    private String port;
    private String username;
    private String password;
    private String database;
    private Map<String, String> options;

    /**
     * Constructs a complete ProviderInfo with all connection parameters.
     *
     * @param protocol The connection protocol (e.g., "mongodb", "jdbc:mysql")
     * @param host     The database server hostname or IP address
     * @param port     The database server port number
     * @param username The authentication username
     * @param password The authentication password
     * @param database The name of the database to connect to
     */
    public ProviderInfo(String protocol, String host, String port, String username, String password, String database) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.options = new HashMap<>();
    }

    /**
     * Constructs a minimal ProviderInfo without authentication credentials.
     *
     * @param host     The database server hostname or IP address
     * @param port     The database server port number
     * @param database The name of the database to connect to
     */
    public ProviderInfo(String host, String port, String database) {
        this(null, host, port, null, null, database);
    }

    /**
     * Constructs a ProviderInfo by parsing a connection URI string.
     * The URI should be in format:
     * protocol://[username:password@]host[:port][/database]
     *
     * @param uri The connection URI string
     * @throws IllegalArgumentException if the URI format is invalid
     */
    public ProviderInfo(String uri) {
        try {
            URI parsed = new URI(uri);

            this.protocol = parsed.getScheme();
            this.host = parsed.getHost();
            this.port = parsed.getPort() == -1 ? null : String.valueOf(parsed.getPort());

            String userInfo = parsed.getUserInfo();
            if (userInfo != null) {
                String[] parts = userInfo.split(":");
                this.username = parts.length > 0 ? parts[0] : null;
                this.password = parts.length > 1 ? parts[1] : null;
            }

            String path = parsed.getPath();
            if (path != null && !path.isEmpty()) {
                this.database = path.startsWith("/") ? path.substring(1) : path;
            }

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI format: " + uri, e);
        } finally {
            this.options = new HashMap<>();
        }
    }

    /**
     * Gets the connection protocol.
     * 
     * @return The protocol, or null if not specified
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Gets the database server host.
     * 
     * @return The hostname or IP address
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the database server port.
     * 
     * @return The port number as string, or null if not specified
     */
    public String getPort() {
        return port;
    }

    /**
     * Gets the database server port as an integer.
     * 
     * @return The port number, or -1 if not specified
     */
    public int getPortInt() {
        return port != null ? Integer.parseInt(port) : -1;
    }

    /**
     * Gets the authentication username.
     * 
     * @return The username, or null if not specified
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the authentication password.
     * 
     * @return The password, or null if not specified
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the database name.
     * 
     * @return The database name, or null if not specified
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Sets the connection protocol.
     * 
     * @param protocol The protocol to use (e.g., "mongodb", "jdbc:mysql")
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Sets the database server host.
     * 
     * @param host The hostname or IP address
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the database server port.
     * 
     * @param port The port number as string
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Sets the authentication username.
     * 
     * @param username The username for authentication
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the authentication password.
     * 
     * @param password The password for authentication
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the database name.
     * 
     * @param database The name of the database to connect to
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * Get query parameters as string.
     * 
     * @return The query parameters as string
     */
    public String getOptionsAsString() {
        StringBuilder sb = new StringBuilder();
        if (!options.isEmpty()) {
            sb.append("?");
            for (Map.Entry<String, String> entry : options.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Generates a URI string representation of the connection information.
     * Uses the specified default protocol if none is set.
     *
     * @param defaultProtocol The protocol to use if none is specified
     * @return A properly formatted connection URI string
     */
    public String toURI(String defaultProtocol) {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol != null ? protocol : defaultProtocol).append("://");

        if (username != null && password != null) {
            sb.append(username).append(":").append(password).append("@");
        }

        sb.append(host != null ? host : "localhost");

        if (port != null) {
            sb.append(":").append(port);
        }

        if (database != null) {
            sb.append("/").append(database);
        }

        sb.append(this.getOptionsAsString());
        return sb.toString();
    }

    /**
     * Adds a query parameter to the connection URI.
     * 
     * @param key   The parameter name
     * @param value The parameter value
     */
    public void option(String key, String value) {
        String existing = options.get(key);
        if (existing != null) {
            options.put(key, existing + "," + value);
        } else {
            options.put(key, value);
        }
    }

    /**
     * Adds a query parameter to the connection URI.
     * 
     * @param option The parameter name
     * @param value  The parameter value
     */
    public void option(Options option, String value) {
        option(option.getKey(), value);
    }

    /**
     * Adds a query parameter to the connection URI.
     * 
     * @param option The parameter name
     * @param value  The parameter value
     */
    public void option(Options option, int value) {
        option(option.getKey(), String.valueOf(value));
    }

    /**
     * Adds a query parameter to the connection URI.
     * 
     * @param option The parameter name
     * @param value  The parameter value
     */
    public void option(Options option, long value) {
        option(option.getKey(), String.valueOf(value));
    }

    /**
     * Adds a query parameter to the connection URI.
     * 
     * @param option The parameter name
     * @param value  The parameter value
     */
    public void option(Options option, boolean value) {
        option(option.getKey(), String.valueOf(value));
    }

    /**
     * Adds a query parameter to the connection URI if it is not already present.
     * 
     * @param option The parameter name
     * @param value  The parameter value
     */
    public void optionIfNotPresent(Options option, String value) {
        if (options.get(option.getKey()) == null) {
            option(option, value);
        }
    }

    /**
     * Common options enum
     */
    public enum Options {
        ALLOW_PUBLIC_KEY_RETRIEVAL("allowPublicKeyRetrieval"),
        USE_SSL("useSSL"),
        REQUIRE_SSL("requireSSL"),
        VERIFY_SERVER_CERTIFICATE("verifyServerCertificate"),
        CLIENT_CERT_KEY_STORE_URL("clientCertificateKeyStoreUrl"),
        CLIENT_CERT_KEY_STORE_PASSWORD("clientCertificateKeyStorePassword"),
        AUTO_RECONNECT("autoReconnect"),
        SERVER_TIMEZONE("serverTimezone"),
        CHARACTER_ENCODING("characterEncoding"),
        USE_UNICODE("useUnicode"),
        CACHE_PREP_STMTS("cachePrepStmts"),
        PREP_STMT_CACHE_SIZE("prepStmtCacheSize"),
        PREP_STMT_CACHE_SQL_LIMIT("prepStmtCacheSqlLimit"),
        USE_SERVER_PREP_STMTS("useServerPrepStmts"),
        LOG_WARNINGS("logger"),
        PROFILE_SQL("profileSQL"),
        TRACE_PROTOCOL("traceProtocol"),
        CONNECTION_TIMEOUT("connectTimeout"),
        SOCKET_TIMEOUT("socketTimeout");

        private final String key;

        Options(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}