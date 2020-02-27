package service_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class PubsubTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void builtinType()
    {
        final String error = "builtin_type_error.zs:5:30: " +
                "Only non-parameterized compound types can be used in pubsub messages, " +
                "'int32' is not a compound type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void choiceType()
    {
        final String error = "choice_type_error.zs:14:30: " +
                "Only non-parameterized compound types can be used in pubsub messages, " +
                "'Data' is a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedMethod()
    {
        final String errors[] =
        {
            "duplicated_method_error.zs:15:38:     First defined here",
            "duplicated_method_error.zs:16:38: 'powerOfTwo' is already defined in this scope!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void nonCompoundSubtype()
    {
        final String error = "non_compound_subtype_error.zs:7:32: " +
                "Only non-parameterized compound types can be used in pubsub messages, " +
                "'Data' is not a compound type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterizedStruct()
    {
        final String error = "parameterized_struct_error.zs:11:32: " +
                "Only non-parameterized compound types can be used in pubsub messages, " +
                "'Data' is a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void sqlDatabaseType()
    {
        final String error = "sql_database_type_error.zs:15:30: " +
                "Invalid usage of SQL database 'Data' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void sqlTableType()
    {
        final String error = "sql_table_type_error.zs:10:29: " +
                "SQL table 'Data' cannot be used in pubsub messages!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
