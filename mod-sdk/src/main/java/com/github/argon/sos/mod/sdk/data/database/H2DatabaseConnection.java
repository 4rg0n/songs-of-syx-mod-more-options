package com.github.argon.sos.mod.sdk.data.database;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DatabaseConnection implements DatabaseConnection {

    public final static String MEMORY_CONNECTION_STRING = "jdbc:h2:memory:";
    public final static String FILE_CONNECTION_STRING = "jdbc:h2:file:";

    @Nullable
    public Connection connection;

    private final String connectionUrl;

    public H2DatabaseConnection(String databaseName) {
        this.connectionUrl = MEMORY_CONNECTION_STRING + databaseName;
    }

    public H2DatabaseConnection(Path databaseFile) {
        this.connectionUrl = FILE_CONNECTION_STRING + databaseFile;
    }

    @Override
    public Connection connect() throws SQLException {
        if (connection != null) {
            return connection;
        }

        Connection connection = DriverManager.getConnection(connectionUrl, "sa", "");
        this.connection = connection;
        return connection;
    }

    @Override
    public void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
            this.connection = null;
        } catch (Exception e) {
            // do not care
        }
    }
}
