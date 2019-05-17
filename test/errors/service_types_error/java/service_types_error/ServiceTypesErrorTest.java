package service_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class ServiceTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void builtinType()
    {
        final String error = "builtin_type_error.zs:10:29: " +
                "mismatched input 'int32' expecting {'stream', ID} ('int32' is a reserved keyword)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void choiceType()
    {
        final String error = "choice_type_error.zs:19:29: " +
                "Only non-parameterized compound types can be used in RPC calls, " +
                "'Request' is a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedRpc()
    {
        final String error = "duplicated_rpc_error.zs:21:18: 'powerOfTwo' is already defined in this scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void nonCompoundSubtype()
    {
        final String error = "non_compound_subtype_error.zs:12:29: " +
                "Only non-parameterized compound types can be used in RPC calls, " +
                "'Request' is not a compound type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterizedStruct()
    {
        final String error = "parameterized_struct_error.zs:16:29: " +
                "Only non-parameterized compound types can be used in RPC calls, " +
                "'Request' is a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void sqlDatabaseType()
    {
        final String error = "sql_database_type_error.zs:20:29: " +
                "Invalid usage of SQL database 'Request' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void sqlTableType()
    {
        final String error = "sql_table_type_error.zs:15:29: SQL table 'Request' cannot be used in RPC call";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
