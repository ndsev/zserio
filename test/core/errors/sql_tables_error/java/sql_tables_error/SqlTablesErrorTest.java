package sql_tables_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class SqlTablesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void databaseArrayField()
    {
        final String error = "database_array_field_error.zs:21:38: mismatched input '[' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void databaseField()
    {
        final String error = "database_field_error.zs:20:5: "
                + "Invalid usage of SQL database 'TestDatabase' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedFieldName()
    {
        final String errors[] = {"duplicated_field_name_error.zs:5:13:     First defined here",
                "duplicated_field_name_error.zs:7:13: 'columnA' is already defined in this scope!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void noFields()
    {
        final String error = "no_fields_error.zs:3:11: Ordinary table must have at least one field!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterizedTable()
    {
        final String error = "parameterized_table_error.zs:3:20: mismatched input '(' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tableFieldConstraint()
    {
        final String error = "table_field_constraint_error.zs:7:38: mismatched input ':' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void subtypedTableField()
    {
        final String error = "subtyped_table_field_error.zs:17:19: "
                + "Field 'subtypedTestTable' cannot be a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tableArrayField()
    {
        final String error = "table_array_field_error.zs:16:31: mismatched input '[' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tableField()
    {
        final String error = "table_field_error.zs:15:17: Field 'testTable' cannot be a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unknownPrimaryKeyColumn()
    {
        final String error = "unknown_primary_key_column_error.zs:9:9: "
                + "Primary key column 'unknownColumn' not found in sql table 'TestTable'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unknownUniqueColumn()
    {
        final String error = "unknown_unique_column_error.zs:9:9: "
                + "Unique column 'unknownColumn' not found in sql table 'TestTable'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void virtualColumn()
    {
        final String error = "virtual_column_error.zs:6:25: "
                + "Ordinary table 'TestTable' cannot contain virtual column 'columnB'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
