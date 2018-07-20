package test_utils;

/**
 * The class provides help methods for manipulation with JDBC driver.
 */
public class JdbcUtil
{
    /**
     * Registers JDBC driver.
     */
    public static void registerJdbc()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException exception)
        {
            throw new RuntimeException("Can't register SQLite JDBC driver!");
        }
    }
}
