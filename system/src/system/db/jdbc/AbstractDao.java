package system.db.jdbc;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;

import system.logging.LogManager;
import system.logging.Logger;

public class AbstractDao {

    private Connection conn;

    private Logger logger = LogManager.getLogger(AbstractDao.class);

    private static ThreadLocal<String> curSql = new ThreadLocal<String>() {

        @Override
        protected String initialValue() {
            return new String();
        }

    };

    public AbstractDao(Connection conn) {
        this.conn = conn;
    }

    public void rollback() throws SQLException {
    	conn.rollback();
    }

    protected Statement createStatement() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.closeOnCompletion();
        return stmt;
    }

    protected Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        Statement stmt = conn.createStatement(resultSetType, resultSetConcurrency);
        stmt.closeOnCompletion();
        return stmt;
    }

    protected Statement createStatement(int resultSetType, int resultSetConcurrency,
    		int resultSetHoldability) throws SQLException {
        Statement stmt = conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        stmt.closeOnCompletion();
        return stmt;
    }

    protected PreparedStatement createPreparedStatement(String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        curSql.set(sql);
        ps.closeOnCompletion();
        return ps;
    }

    protected PreparedStatement createPreparedStatement(String sql, int resultSetType,
    		int resultSetConcurrency) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
        curSql.set(sql);
        ps.closeOnCompletion();
        return ps;
    }

    protected PreparedStatement createPreparedStatement(String sql, int resultSetType,
    		int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        curSql.set(sql);
        ps.closeOnCompletion();
        return ps;
    }

    protected PreparedStatement createPreparedStatement(String sql, Map<String, Object> params) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql);
        setParamters(ps, params);
        return ps;
    }

    protected PreparedStatement createPreparedStatement(String sql, Object... params) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql);
        setParamters(ps, params);
        return ps;
    }

    protected PreparedStatement createCallableStatement(String sql) throws SQLException {
    	CallableStatement stmt = conn.prepareCall(sql);
        curSql.set(sql);
        stmt.closeOnCompletion();
        return stmt;
    }

    protected PreparedStatement createCallableStatement(String sql, int resultSetType,
    		int resultSetConcurrency) throws SQLException {
    	CallableStatement stmt = conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        curSql.set(sql);
        stmt.closeOnCompletion();
        return stmt;
    }

    protected PreparedStatement createCallableStatement(String sql, int resultSetType,
    		int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    	CallableStatement stmt = conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        curSql.set(sql);
        stmt.closeOnCompletion();
        return stmt;
    }

    protected ResultSet executeQuery(PreparedStatement ps) throws SQLException {
        logger.info("SQL:{}", curSql.get());
        return ps.executeQuery();
    }

    protected int executeUpdate(PreparedStatement ps) throws SQLException {
        logger.info("SQL:{}", curSql.get());
        return ps.executeUpdate();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql);
        return executeQuery(ps);
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql, params);
        return executeQuery(ps);
    }

    public int executeUpdate(String sql) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql);
        return executeUpdate(ps);
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql, params);
        return executeUpdate(ps);
    }

    public void setParamters(PreparedStatement ps, Object... params) throws SQLException {
        int index = 0;
        for (Object param : params) {
            if (param instanceof String) {
                ps.setString(index, (String) param);
            } else if (param instanceof Number) {
                if (param instanceof Byte) {
                    ps.setByte(index, (byte) param);
                } else if (param instanceof Short) {
                    ps.setShort(index, (short) param);
                } else if (param instanceof Integer) {
                    ps.setInt(index, (int) param);
                } else if (param instanceof Long) {
                    ps.setLong(index, (long) param);
                } else if (param instanceof Float) {
                    ps.setFloat(index, (float) param);
                } else if (param instanceof Double) {
                    ps.setDouble(index, (double) param);
                } else if (param instanceof BigDecimal) {
                    ps.setBigDecimal(index, (BigDecimal) param);
                }
            } else if (param instanceof Boolean) {
                ps.setBoolean(index, (boolean) param);
            } else if (param instanceof Date) {
                ps.setDate(index, (Date) param);
            } else if (param instanceof Time) {
                ps.setTime(index, (Time) param);
            } else if (param instanceof Timestamp) {
                ps.setTimestamp(index, (Timestamp) param);
            } else if (param instanceof Blob) {
                ps.setBlob(index, (Blob) param);
            } else if (param instanceof Clob) {
                ps.setClob(index, (Clob) param);
            } else {
                ps.setObject(index, param);
            }

            index++;
        }
    }

}
