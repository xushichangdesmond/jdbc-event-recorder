package powerdancer.jer;

import java.sql.*;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class EventRecordingJdbcDriver implements Driver {
    final Driver delegate;

    public EventRecordingJdbcDriver(Driver delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return JdbcEvent.record(()->new EventRecordingConnection(delegate.connect(url, info)),"connect", url);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return delegate.acceptsURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return delegate.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion() {
        return delegate.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return delegate.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return delegate.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return delegate.getParentLogger();
    }
}
