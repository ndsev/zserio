package sql_constraints_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class SqlConstraintsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void invalidIntegerConstraint()
    {
        final String error = "invalid_integer_constraint_error.zs:6:37: SQL constraint expression must be "
                + "a constant string!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void invalidStringConstraint()
    {
        final String error = "invalid_string_constraint_error.zs:7:33: SQL constraint expression must be "
                + "a constant string!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
