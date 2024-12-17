package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer<T> {
    void accept(T Object) throws SQLException;
}
