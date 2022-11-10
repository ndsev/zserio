package with_validation_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import with_validation_code.virtual_table_validation.VirtualTableValidationDb;

import zserio.runtime.ZserioError;
import zserio.runtime.validation.ValidationReport;

public class VirtualTableValidationTest
{
    @BeforeAll
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @BeforeEach
    public void setUp() throws IOException, SQLException
    {
        FileUtil.deleteFileIfExists(file);
        database = new VirtualTableValidationDb(file.toString());
        database.createSchema();
    }

    @AfterEach
    public void tearDown() throws SQLException
    {
        if (database != null)
        {
            database.close();
            database = null;
        }
    }

    @Test
    public void validation() throws SQLException, ZserioError
    {
        populateDb(database.connection());
        final ValidationReport report = database.validate();
        assertEquals(2, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    private void populateDb(Connection connection) throws SQLException
    {
        for (short id = 0; id < ENTRY_COUNT; id++)
            insertTestTableRow(connection, id);
    }

    private void insertTestTableRow(Connection connection, short id) throws SQLException
    {
        final String sql = "INSERT INTO TestTable (docId, text, anotherId) VALUES (?, ?, ?)";

        try (final PreparedStatement statement = connection.prepareStatement(sql))
        {
            int argIdx = 1;
            statement.setLong(argIdx++, id);
            statement.setString(argIdx++, "Text" + id);
            if (id % 2 == 0)
                statement.setLong(argIdx++, id / 2);
            else
                statement.setNull(argIdx++, java.sql.Types.NULL);
            statement.execute();
        }
    }

    private static final String FILE_NAME = "virtual_table_validation_test.sqlite";

    private static final int ENTRY_COUNT = 5;

    private File file = new File(FILE_NAME);
    private VirtualTableValidationDb database = null;
};
