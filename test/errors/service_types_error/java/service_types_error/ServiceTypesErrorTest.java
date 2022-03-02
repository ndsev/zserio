package service_types_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class ServiceTypesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void builtinType()
    {
        final String error = "builtin_type_error.zs:10:25: " +
                "Only non-parameterized compound types can be used in service methods, " +
                "'int32' is not a compound type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void choiceType()
    {
        final String error = "choice_type_error.zs:19:25: " +
                "Only non-parameterized compound types can be used in service methods, " +
                "'Request' is a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedMethod()
    {
        final String errors[] =
        {
            "duplicated_method_error.zs:20:14:     First defined here",
            "duplicated_method_error.zs:21:14: 'powerOfTwo' is already defined in this scope!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void nonCompoundSubtype()
    {
        final String error = "non_compound_subtype_error.zs:12:25: " +
                "Only non-parameterized compound types can be used in service methods, " +
                "'Request' is not a compound type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterizedStruct()
    {
        final String error = "parameterized_struct_error.zs:16:25: " +
                "Only non-parameterized compound types can be used in service methods, " +
                "'Request' is a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void sqlDatabaseType()
    {
        final String error = "sql_database_type_error.zs:20:25: " +
                "Invalid usage of SQL database 'Request' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void sqlTableType()
    {
        final String error = "sql_table_type_error.zs:15:25: " +
                "SQL table 'Request' cannot be used in service methods!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
