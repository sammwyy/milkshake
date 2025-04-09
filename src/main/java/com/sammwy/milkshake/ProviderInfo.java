package com.sammwy.milkshake;

import java.net.URI;
import java.net.URISyntaxException;

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

        return sb.toString();
    }
}