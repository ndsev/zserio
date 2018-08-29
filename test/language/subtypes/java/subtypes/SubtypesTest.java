package subtypes;

import static org.junit.Assert.*;

import java.io.File;

import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import zserio.runtime.array.ObjectArray;

public class SubtypesTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws SQLException
    {
        FileUtil.deleteFileIfExists(dbFile);
    }

    @Test
    public void uint16Subtype()
    {
        final int identifier = 0xFFFF;
        final String name = "Name";
        final TestStructure testStructure = new TestStructure(identifier, name);
        final int readIdentifier = testStructure.getIdentifier();
        assertEquals(identifier, readIdentifier);
    }

    @Test
    public void testStructureSubtype()
    {
        final int identifier = 0xFFFF;
        final String name = "Name";
        final TestStructure testStructure = new TestStructure(identifier, name);
        final SubtypeStructure subtypeStructure = new SubtypeStructure(testStructure);
        final TestStructure readTestStructure = subtypeStructure.getStudent();
        assertEquals(testStructure, readTestStructure);
    }

    @Test
    public void testSubtypedTable() throws SQLException, URISyntaxException
    {
        Database db = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try
        {
            db = new Database(dbFile.toString());
            db.createSchema();
            // check if database does contain the table
            final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='students'";
            statement = db.prepareStatement(sqlQuery);
            resultSet = statement.executeQuery();
            assertTrue(resultSet.next());
            final String tableName = resultSet.getString(1);
            assertEquals("students", tableName);

            // check table getter
            final TestTable students = db.getStudents();
            assertTrue(students != null);
        }
        catch (RuntimeException e)
        {
            fail("RuntimeException thrown: " + e.getMessage());
        }
        finally
        {
            try
            {
                if (resultSet != null)
                    resultSet.close();
            }
            finally
            {
                try
                {
                    if (statement != null)
                        statement.close();
                }
                finally
                {
                    if (db != null)
                        db.close();
                }
            }
        }
    }

    @Test
    public void testParameterizedSubtype()
    {
        // check that correct type is used instead of subtype (Java has nothing like a typedef)
        ParameterizedStruct parameterizedStruct  = new ParameterizedStruct(10);

        ParameterizedSubtypeStruct parameterizedSubtypeStruct = new ParameterizedSubtypeStruct();
        parameterizedSubtypeStruct.setLength(10);
        parameterizedSubtypeStruct.setParameterizedSubtype(parameterizedStruct);
        ObjectArray<ParameterizedStruct> parameterizedStructArray = new ObjectArray<ParameterizedStruct>(1);
        parameterizedStructArray.setElementAt(new ParameterizedStruct(10), 0);
        parameterizedSubtypeStruct.setParameterizedSubtypeArray(parameterizedStructArray);

        // it's enough that the code above compiles ok, just check something
        ParameterizedStruct parameterizedSubtype = parameterizedSubtypeStruct.getParameterizedSubtype();
        assertEquals(parameterizedSubtypeStruct.getLength(), parameterizedSubtype.getLength());
        ObjectArray<ParameterizedStruct> parameterizedSubtypeArray =
                parameterizedSubtypeStruct.getParameterizedSubtypeArray();
        assertEquals(parameterizedSubtypeStruct.getLength(),
                parameterizedSubtypeArray.elementAt(0).getLength());
    }

    private static final String DB_FILE_NAME = "subtypes_test.sqlite";

    private final File dbFile = new File(DB_FILE_NAME);
}
