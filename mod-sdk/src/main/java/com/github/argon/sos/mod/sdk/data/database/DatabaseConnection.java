package com.github.argon.sos.mod.sdk.data.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection extends AutoCloseable {

    Connection connect() throws SQLException;

    void close();
}
