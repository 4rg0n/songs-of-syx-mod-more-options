package com.github.argon.sos.mod.sdk.data.database;

import lombok.RequiredArgsConstructor;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class KeyValueDatabase {

    public static final String SCHEMA_NAME = "key_value_store";
    private static final String KEY_NAME = "key";
    private static final String VALUE_NAME = "value";

    private static final String DATABASE_SCHEMA = "CREATE TABLE IF NOT EXISTS `"
        + SCHEMA_NAME + "` (`" + KEY_NAME + "` VARCHAR(4096), `" + VALUE_NAME + "` CLOB)";

    private final DatabaseConnection connection;

    public void init() {
        try (PreparedStatement statement = connection.connect().prepareStatement(DATABASE_SCHEMA)) {
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public int insertOrUpdate(String key, String value) {
        if (exists(key)) {
            return update(key, value);
        } else {
            return insert(key, value);
        }
    }

    public int[] insert(Map<String, String> values) {
        try (PreparedStatement statement = connection.connect().prepareStatement(
            "INSERT INTO `" + SCHEMA_NAME + "` VALUES(?, ?)"
        )) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                statement.setString(0, key);
                statement.setClob(1, newReader(value));
                statement.addBatch();
            }

            return statement.executeBatch();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public int insert(String key, String value) {
        try (PreparedStatement statement = connection.connect().prepareStatement(
            "INSERT INTO `" + SCHEMA_NAME + "` VALUES(?, ?)"
        )) {
            statement.setString(0, key);
            statement.setClob(1, newReader(value));
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public int update(String key, String value) {
        try (PreparedStatement statement = connection.connect().prepareStatement(
            "UPDATE `" + SCHEMA_NAME + "` SET `" + VALUE_NAME + "` = ? WHERE `" + KEY_NAME + "` = ?"
        )) {
            statement.setString(1, key);
            statement.setClob(0, newReader(value));

            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public Optional<String> find(String key) {
        try (PreparedStatement statement = connection.connect().prepareStatement(
            "SELECT content FROM `" + SCHEMA_NAME + "` WHERE `" + KEY_NAME + "` = ?"
        )) {
            statement.setString(0, key);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            String value = resultSet.getString(VALUE_NAME);

            return Optional.ofNullable(value);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public boolean exists(String key) {
        try (PreparedStatement statement = connection.connect().prepareStatement(
            "SELECT COUNT(*) FROM `" + SCHEMA_NAME + "` WHERE `" + KEY_NAME + "` = ?"
        )) {
            statement.setString(0, key);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public int delete(String key) {
        try (PreparedStatement statement = connection.connect().prepareStatement(
            "DELETE FROM `" + SCHEMA_NAME + "` WHERE `" + KEY_NAME + "` = ?"
        )) {
            statement.setString(0, key);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public int deleteAll() {
        try (PreparedStatement statement = connection.connect().prepareStatement(
            "TRUNCATE TABLE `" + SCHEMA_NAME + "`"
        )) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public Map<String, String> findAll() {
        try (PreparedStatement statement = connection.connect().prepareStatement(
            "SELECT * FROM `" + SCHEMA_NAME + "`"
        )) {
            ResultSet resultSet = statement.executeQuery();

            Map<String, String> results = new HashMap<>();
            while (resultSet.next()) {
                results.put(resultSet.getString(KEY_NAME), resultSet.getString(VALUE_NAME));
            }

            return results;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private StringReader newReader(String string) {
        return new StringReader(string);
    }
}
