package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.ConnectionSingletone;
import jm.task.core.jdbc.util.Util;
import org.hibernate.tool.schema.ast.SqlScriptParserException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    Util utilObj = new Util();
    public UserDaoJDBCImpl() {
//конструктор
    }

    /***
     * Создает таблицу в бд
     */
    public void createUsersTable(){
        //собрать в ддл запрос
        try {
            utilObj.createTable();
        }
        catch (SQLException e) {}
    }

    public void dropUsersTable() {
//удаляем таблицу из бд
        String sql = "DROP TABLE IF EXISTS USERS";
        try (Statement stmt = ConnectionSingletone.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(String name, String lastName, byte age) {
//заносим юзера с параметрами
        String sql = "INSERT INTO USERS (name, lastName, age) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = Util.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, lastName);
            pstmt.setByte(3, age);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeUserById(long id) {
//удаляем из бд по ид
        String sql = "DELETE FROM USERS WHERE id = ?";
        try (PreparedStatement pstmt = Util.getConnection().prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        //в лист выгружаем всю бд
        String sql = "SELECT * FROM USERS";
        List<User> users = new ArrayList<>();

        try (Statement stmt = Util.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String lastName = rs.getString("lastName");
                byte age = rs.getByte("age");
                users.add(new User(id, name, lastName,age));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return users;
    }

    public void cleanUsersTable() {
//удаляем всех пользователей из бд
        String sql = "TRUNCATE TABLE USERS";
        try (Statement stmn = Util.getConnection().createStatement()) {
            stmn.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //autocommit off
        //rollback
    }
}
