package zserio.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class RuleIdValidatorTest
{
    @Test
    public void validIds()
    {
        validate("validId");
        validate("valid-with-dash");
        validate("valid_with_underscores");
        validate("valid-with-1234-numbers");
        validate("valid_123-1_2_3");
        validate("VALID-123");
    }

    @Test
    public void invalidStartingWithNumber()
    {
        assertThrows(ParserException.class, () -> validate("123-invalid"));
    }

    @Test
    public void invalidContainsSlash()
    {
        assertThrows(ParserException.class, () -> validate("invalid-with/slash"));
    }

    @Test
    public void invalidContainsApostrophe()
    {
        assertThrows(ParserException.class, () -> validate("invalid-with'apostrophe"));
    }

    @Test
    public void invalidContainsAsterisk()
    {
        assertThrows(ParserException.class, () -> validate("invalid-with*asterisk"));
    }

    @Test
    public void invalidContainsBackslash()
    {
        assertThrows(ParserException.class, () -> validate("invalid-with\\backslash"));
    }

    private static void validate(String ruleId)
    {
        RuleIdValidator.validate(ruleId, DUMMY_LOCATION);
    }

    private static final AstLocation DUMMY_LOCATION = new AstLocation("", 0, 0);
}
