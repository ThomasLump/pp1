package jm.task.core.jdbc.util;

import com.mysql.cj.MysqlConnection;
import org.apache.commons.dbcp2.BasicDataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.hibernate.SessionFactory;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.*;
import java.lang.module.FindException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Util {
    // реализуйте настройку соеденения с БД

    //private static final String url = "jdbc:mysql://localhost:3306/mysql";
    //private static final String url = "jdbc:h2:mem:test; INIT=RUNSCRIPT FROM 'classpath:init.sql'";
    //private static final String user = "root";
    //private static final String password = "1";

    public void connection(SqlConsumer<? super Connection> connectionConsumer) throws SQLException {

        FileInputStream fis = null;
        Properties properties = new Properties();
        try {
            fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String url = properties.getProperty("db.host");
        final String user = properties.getProperty("user");
        final String password = properties.getProperty("password");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        //где мне закрыть пул??
        try (Connection conn = dataSource.getConnection()) {
            connectionConsumer.accept(conn);
        }
    }

    public void statement(SqlConsumer<? super Statement> statementConsumer) throws SQLException {
        connection(conn -> {
            try (Statement stmt = conn.createStatement()) {
                statementConsumer.accept(stmt);
            }
        });
    }

    public void createTable() throws SQLException {
        final String sql = getInitSql();
        statement(stmt -> {
            connection(conn -> {
                stmt.execute(sql);
            });
        });
    }

    private String getInitSql() {
        final String filePath = "src/main/resources/initDb.sql";
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(filePath)){
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            br.lines().forEach(s -> stringBuilder.append(s).append("\n"));
        } catch (IOException e) {}
        return stringBuilder.toString();
        /*
        return "CREATE TABLE IF NOT EXISTS USERS (\n" +
                "    id SERIAL PRIMARY KEY,\n" +
                "    name VARCHAR(255),\n" +
                "    lastName VARCHAR(255),\n" +
                "    age TINYINT\n" +
                "    );";

         */
    }

    public static Connection getConnection() throws SQLException {

        final String url = getProperties().getProperty("db.host");
        final String user = getProperties().getProperty("user");
        final String password = getProperties().getProperty("password");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

//        JdbcConnectionPool cp = JdbcConnectionPool.create(url, user, password);
//        Connection connectionH2 = cp.getConnection();
        return dataSource.getConnection();
    }

    private static Properties getProperties() {
        FileInputStream fis = null;
        Properties properties = new Properties();
        try {
            fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    public static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url",getProperties().getProperty("db.host") );
        configuration.setProperty("hibernate.connection.username", getProperties().getProperty("user"));
        configuration.setProperty("hibernate.connection.password", getProperties().getProperty("password"));
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return configuration;
    }




}
