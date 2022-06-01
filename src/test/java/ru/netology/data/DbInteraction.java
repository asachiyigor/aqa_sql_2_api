package ru.netology.data;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbInteraction {

    private final static Connection conn = getConnection();
    private final static QueryRunner runner = new QueryRunner();

    @SneakyThrows
    private static Connection getConnection() {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "admin", "pass");
    }

    @SneakyThrows
    public static String getVerificationCode(Data.AuthInfo authInfo) {
        val userId = runner.query(conn, "SELECT id FROM users WHERE login = '" + authInfo.getLogin() + "'", new ScalarHandler<>());
        return runner.query(conn, "SELECT code FROM auth_codes WHERE user_id = '" + userId + "' ORDER BY created DESC LIMIT 1", new ScalarHandler<>());
    }

    @SneakyThrows
    public void deleteDataFromDb() {
        var runner = new QueryRunner();
        try (var conn = getConnection()) {
            runner.update(conn, "DELETE FROM card_transactions");
            runner.update(conn, "DELETE FROM auth_codes");
            runner.update(conn, "DELETE FROM cards");
            runner.update(conn, "DELETE FROM users");
        }
    }
}