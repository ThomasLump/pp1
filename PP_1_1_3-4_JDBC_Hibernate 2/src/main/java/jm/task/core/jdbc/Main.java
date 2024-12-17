package jm.task.core.jdbc;

import jm.task.core.jdbc.dao.UserDao;
import jm.task.core.jdbc.dao.UserDaoJDBCImpl;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.ConnectionSingletone;

public class Main {
    public static void main(String[] args) {
        try {
            UserService userService = new UserServiceImpl();
            //userService.dropUsersTable();
            userService.createUsersTable();
            ConnectionSingletone cs = new ConnectionSingletone();

        } finally {
            ConnectionSingletone.conectionClose();
        }

    }
}
