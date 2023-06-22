package functions_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class ExtendedMembersErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void extendedDeepNested()
    {
        final String errors[] =
        {
            "extended_deep_nested_error.zs:6:19:     extended field used here",
            "extended_deep_nested_error.zs:18:14:     extended field used here",
            "extended_deep_nested_error.zs:24:15:     extended field used here",
            "extended_deep_nested_error.zs:12:19: Field 'choiceArray' contains an extended field!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void extendedInChoice()
    {
        final String error = "extended_in_choice_error.zs:8:9: extraneous input 'extend'";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void extendedInRecursion()
    {
        final String errors[] =
        {
            "extended_in_recursion_error.zs:7:19:     extended field used here",
            "extended_in_recursion_error.zs:6:23: Field 'recursive' contains an extended field!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void extendedInUnion()
    {
        final String error = "extended_in_union_error.zs:6:5: extraneous input 'extend'";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void extendedMultipleNotLast()
    {
        final String errors[] =
        {
            "extended_multiple_not_last_error.zs:6:19:     extended field used here",
            "extended_multiple_not_last_error.zs:9:13: " +
                    "Field 'field5' follows an extended field and is not marked as extended!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void extendedNestedInArray()
    {
        final String errors[] =
        {
            "extended_nested_in_array_error.zs:6:19:     extended field used here",
            "extended_nested_in_array_error.zs:11:14: Field 'array' contains an extended field!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    };

    @Test
    public void extendedNestedInChoice()
    {
        final String errors[] =
        {
            "extended_nested_in_choice_error.zs:6:19:     extended field used here",
            "extended_nested_in_choice_error.zs:14:18: Field 'extended' contains an extended field!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void extendedNestedInUnion()
    {
        final String errors[] =
        {
            "extended_nested_in_union_error.zs:6:19:     extended field used here",
            "extended_nested_in_union_error.zs:12:14: Field 'extended' contains an extended field!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    };

    @Test
    public void extendedNotLast()
    {
        final String errors[] =
        {
            "extended_not_last_error.zs:6:19:     extended field used here",
            "extended_not_last_error.zs:7:13: " +
                    "Field 'field3' follows an extended field and is not marked as extended!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void extendedNotLastInTemplate()
    {
        final String errors[] =
        {
            "extended_not_last_in_template_error.zs:10:13: " +
                    "    In instantiation of 'Extended' required from here",
            "extended_not_last_in_template_error.zs:6:14:     extended field used here",
            "extended_not_last_in_template_error.zs:7:13: " +
                    "Field 'field3' follows an extended field and is not marked as extended!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void extendedNotTopLevel()
    {
        final String errors[] =
        {
            "extended_not_top_level_error.zs:6:19:     extended field used here",
            "extended_not_top_level_error.zs:11:14: Field 'extended' contains an extended field!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void extendedNotTopLevelInTemplate()
    {
        final String errors[] =
        {
            "extended_not_top_level_in_template_error.zs:6:14:     extended field used here",
            "extended_not_top_level_in_template_error.zs:11:22: Field 'extended' contains an extended field!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    private static ZserioErrorOutput zserioErrors;
}
