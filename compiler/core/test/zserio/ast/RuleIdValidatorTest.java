package zserio.ast;

import org.junit.Test;

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

    @Test(expected=ParserException.class)
    public void invalidStartingWithNumber()
    {
        validate("123-invalid");
    }

    @Test(expected=ParserException.class)
    public void invalidContainsSlash()
    {
        validate("invalid-with/slash");
    }

    @Test(expected=ParserException.class)
    public void invalidContainsApostrophe()
    {
        validate("invalid-with'apostrophe");
    }

    @Test(expected=ParserException.class)
    public void invalidContainsAsterisk()
    {
        validate("invalid-with*asterisk");
    }

    @Test(expected=ParserException.class)
    public void invalidContainsBaclslash()
    {
        validate("invalid-with\\backslash");
    }

    private static void validate(String ruleId)
    {
        RuleIdValidator.validate(ruleId, DUMMY_LOCATION);
    }

    private static final AstLocation DUMMY_LOCATION = new AstLocation("", 0, 0);
}