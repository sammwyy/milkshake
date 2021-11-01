package com.dotphin.milkshakeorm.utils;

public class URI {

    private String protocol;
    private String username;
    private String password;
    private String host;
    private int port = -1;
    private String path;

    public URI() {
    }

    public URI(String raw) {
        // Parse protocol
        this.protocol = raw.split("://")[0];
        raw = raw.replaceFirst(this.protocol + "://", "");

        // Parse path
        if (raw.contains("/")) {
            this.path = raw.split("/")[1];
            raw = raw.replaceFirst("/" + this.path, "");
        }

        // Parse username and password
        if (raw.contains("@")) {
            final String userPasswordPairRaw = raw.split("@")[0];
            raw = raw.replaceFirst(userPasswordPairRaw + "@", "");

            final String[] userPasswordPair = userPasswordPairRaw.split(":");
            this.username = userPasswordPair[0];
            if (userPasswordPair.length > 1) {
                this.password = userPasswordPair[1];
            }
        }

        // Parse port
        if (raw.contains(":")) {
            final String rawPort = raw.split(":")[1];
            raw = raw.replaceFirst(":" + rawPort, "");
            this.port = Integer.parseInt(rawPort);
        }

        // Parse host
        this.host = raw;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public URI setProtocol(final String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getUsername() {
        return this.username;
    }

    public URI setUsername(final String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public URI setPassword(final String password) {
        this.password = password;
        return this;
    }

    public String getHost() {
        return this.host;
    }

    public URI setHost(final String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public URI setPort(final int port) {
        this.port = port;
        return this;
    }

    public String getPath() {
        return this.path;
    }

    public URI setPath(final String path) {
        this.path = path;
        return this;
    }

    public String toString() {
        String output = protocol + "://";

        if (username != null && !username.isEmpty()) {
            output += username;
            if (password != null && !password.isEmpty()) {
                output += ":" + password;
            }
            output += "@";
        }

        if (host != null && !host.isEmpty()) {
            output += host;
        }

        if (port >= 0) {
            output += ":" + port;
        }

        if (path != null && !path.isEmpty()) {
            output += "/" + path;
        }

        return output;
    }
}
