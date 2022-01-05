package sql_databases_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class SqlDatabasesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void databaseField()
    {
        final String error = "database_field_error.zs:20:5: " +
                "Invalid usage of SQL database 'TestDatabase' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void emptyDatabase()
    {
        final String error = "empty_database_error.zs:6:1: mismatched input '}' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void structureField()
    {
        final String error = "structure_field_error.zs:19:22: Field 'testStructure' is not a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void subtypedStructureField()
    {
        final String error = "subtyped_structure_field_error.zs:15:23: " +
                "Field 'subtypedTestStructure' is not a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
