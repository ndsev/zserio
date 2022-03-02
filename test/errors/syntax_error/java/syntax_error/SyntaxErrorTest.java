package syntax_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class SyntaxErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void rshiftWithSpace()
    {
        final String error = "rshift_with_space_error.zs:3:31: Operator '>>' cannot contain spaces!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unexpectedEofInArrayLength()
    {
        final String error = "unexpected_eof_in_array_length_error.zs:6:1: " +
                "mismatched input '<EOF>' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unexpectedEofInConstDefinition()
    {
        final String error = "unexpected_eof_in_const_definition_error.zs:4:1: " +
                "mismatched input '<EOF>' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unexpectedEofInFieldDefinition()
    {
        final String error = "unexpected_eof_in_field_definition_error.zs:6:1: " +
                "mismatched input '<EOF>' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unexpectedEofInParameterizedFieldDefinition()
    {
        final String error = "unexpected_eof_in_parameterized_field_definition_error.zs:11:1: " +
                "mismatched input '<EOF>' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unexpectedEofInStructDefinition()
    {
        final String error = "unexpected_eof_in_struct_definition_error.zs:6:1: " +
                "extraneous input '<EOF>' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unexpectedEofMissingSemicolon()
    {
        final String error = "unexpected_eof_missing_semicolon_error.zs:7:1: missing ';' at '<EOF>'";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
