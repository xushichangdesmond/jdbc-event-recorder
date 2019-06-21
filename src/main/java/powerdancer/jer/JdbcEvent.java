package powerdancer.jer;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Category("ucex")
@Label("JdbcEvent")
public class JdbcEvent extends Event {
    @Label("traceId")
    final String traceId;

    @Label("type")
    final String type;

    @Label("args")
    final String args;

    public static Supplier<String> defaultTraceIdSupplier = ()->"defaultTraceId";

    public JdbcEvent(String traceId, String type, String... args) {
        this.traceId = traceId;
        this.type = type;
        this.args = Arrays.stream(args).collect(Collectors.joining(","));
    }

    public static <T> T record(SqlCallable<T> callable, String type, String... args) throws SQLException {
        return record(callable, defaultTraceIdSupplier.get(), type, args);
    }

    public static <T> T record(SqlCallable<T> callable, String traceId, String type, String... args) throws SQLException {
        JdbcEvent e = new JdbcEvent(traceId, type, args);
        if (e.isEnabled()) {
            e.begin();
        }
        try {
            return callable.call();
        } finally {
            if (e.shouldCommit()) {
                e.commit();
            }
        }
    }

    public static void record(SqlRunnable runnable, String type, String... args) throws SQLException {
        record(runnable, defaultTraceIdSupplier.get(), type, args);
    }

    public static void record(SqlRunnable runnable, String traceId, String type, String... args) throws SQLException {
        JdbcEvent e = new JdbcEvent(traceId, type, args);
        if (e.isEnabled()) {
            e.begin();
        }
        try {
            runnable.run();
        } finally {
            if (e.shouldCommit()) {
                e.commit();
            }
        }
    }



    public interface SqlRunnable {
        void run() throws SQLException;
    }

    public interface SqlCallable<T> {
        T call() throws SQLException;
    }
}
