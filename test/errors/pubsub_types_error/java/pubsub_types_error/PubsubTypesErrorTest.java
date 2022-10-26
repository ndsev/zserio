package pubsub_types_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class PubsubTypesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void builtinType()
    {
        final String error = "builtin_type_error.zs:5:36: " +
                "Only non-parameterized compound types or bytes can be used in pubsub messages, " +
                "'int32' is not a compound type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void choiceType()
    {
        final String error = "choice_type_error.zs:14:36: " +
                "Only non-parameterized compound types or bytes can be used in pubsub messages, " +
                "'Data' is a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedMethod()
    {
        final String errors[] =
        {
            "duplicated_method_error.zs:15:44:     First defined here",
            "duplicated_method_error.zs:16:44: 'powerOfTwo' is already defined in this scope!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void invalidIntegerTopic()
    {
        final String error =
                "invalid_integer_topic_error.zs:10:19: Topic definition must be a constant string!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void nonCompoundSubtype()
    {
        final String error = "non_compound_subtype_error.zs:7:38: " +
                "Only non-parameterized compound types or bytes can be used in pubsub messages, " +
                "'Data' is not a compound type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterizedStruct()
    {
        final String error = "parameterized_struct_error.zs:11:38: " +
                "Only non-parameterized compound types or bytes can be used in pubsub messages, " +
                "'Data' is a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void sqlDatabaseType()
    {
        final String error = "sql_database_type_error.zs:15:36: " +
                "Invalid usage of SQL database 'Data' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void sqlTableType()
    {
        final String error = "sql_table_type_error.zs:10:28: " +
                "SQL table 'Data' cannot be used in pubsub messages!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
