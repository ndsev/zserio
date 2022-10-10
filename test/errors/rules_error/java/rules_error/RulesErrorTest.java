package rules_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class RulesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void duplicatedIdBetweenPackages()
    {
        final String errors[] =
        {
            "duplicated_id_between_packages_error.zs:11:10:     Conflicting rule defined here",
            "package_rules.zs:13:10: Rule ID 'rule-two' is not unique (case insensitive) between all packages!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void duplicatedIdWithinGroup()
    {
        final String errors[] =
        {
            "duplicated_id_within_group_error.zs:6:10:     Conflicting rule defined here",
            "duplicated_id_within_group_error.zs:12:10: Rule ID 'rule-one' is not unique (case insensitive) " +
                    "within a package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void duplicatedIdWithinPackage()
    {
        final String errors[] =
        {
            "duplicated_id_within_package_error.zs:6:10:     Conflicting rule defined here",
            "duplicated_id_within_package_error.zs:15:10: Rule ID 'rule-ONE' is not unique (case " +
                    "insensitive) within a package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void idInvalidExpressionType()
    {
        final String error = "id_invalid_expression_type_error.zs:5:10: " +
                "Rule identifier must be a constant string expression!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void invalidIdFormatError()
    {
        final String error = "invalid_id_format_error.zs:5:10: Invalid rule identifier! " +
                "Rule identifier must match pattern '[a-zA-Z][a-zA-Z0-9_:.-]*'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
