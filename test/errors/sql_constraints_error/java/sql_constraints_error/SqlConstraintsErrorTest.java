package sql_constraints_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class SqlConstraintsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void invalidTypeReference()
    {
        final String error = "invalid_type_reference_error.zs:8:38: " +
                "Unresolved referenced symbol 'ConstraintsConstant.wrongFieldName'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void nonIntegerConstantReference()
    {
        final String error = "non_integer_constant_reference_error.zs:8:38: Reference 'ConstraintsConstant' " +
                "refers to non-integer constant!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unresolvedReference()
    {
        final String error = "unresolved_reference_error.zs:8:38: Unresolved referenced symbol " +
                "'ConstraintsConstant' for type 'ConstraintsTable'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unresolvedSymbol()
    {
        final String error = "unresolved_symbol_error.zs:12:38: Unresolved referenced symbol 'VALUE3' for " +
                "type 'ConstraintsEnum'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongTypeReference()
    {
        final String error = "wrong_type_reference_error.zs:11:38: Reference 'ConstraintsConstant' does " +
                "refer to neither enumeration, bitmask nor constant!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
