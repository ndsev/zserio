package zserio.tools;

import org.apache.commons.cli.ParseException;

import static org.junit.Assert.*;
import org.junit.Test;

public class CommandLineArgumentsTest
{
    @Test
    public void helpShort() throws ParseException
    {
        String[] args = { "-h" };
        assertTrue(parse(args).hasHelpOption());
    }

    @Test
    public void helpLong() throws ParseException
    {
        String[] args = { "-help" };
        assertTrue(parse(args).hasHelpOption());
    }

    @Test
    public void withRangeCheckCode() throws ParseException
    {
        String[] args = { "-withRangeCheckCode" };
        CommandLineArguments parsedArgs = parse(args);
        assertTrue(parsedArgs.getWithRangeCheckCode());
    }

    @Test
    public void withoutRangeCheckCode() throws ParseException
    {
        String[] args = { "-withoutRangeCheckCode" };
        assertFalse(parse(args).getWithRangeCheckCode());
    }

    @Test
    public void withRangeCheckCodeDefault() throws ParseException
    {
        String[] args = {};
        assertFalse(parse(args).getWithRangeCheckCode());
    }

    @Test(expected=ParseException.class)
    public void rangeCheckCodeConflict() throws ParseException
    {
        String[] args = { "-withRangeCheckCode", "-withoutRangeCheckCode" };
        parse(args);
    }

    @Test
    public void withServiceCode() throws ParseException
    {
        String[] args = { "-withServiceCode" };
        assertTrue(parse(args).getWithServiceCode());
    }

    @Test
    public void withoutServiceCode() throws ParseException
    {
        String[] args = { "-withoutServiceCode" };
        assertFalse(parse(args).getWithServiceCode());
    }

    @Test
    public void withServiceCodeDefault() throws ParseException
    {
        String[] args = {};
        assertTrue(parse(args).getWithServiceCode());
    }

    @Test(expected=ParseException.class)
    public void serviceCodeConflict() throws ParseException
    {
        String[] args = { "-withServiceCode", "-withoutServiceCode" };
        parse(args);
    }

    @Test
    public void withSourcesAmalgamation() throws ParseException
    {
        String[] args = { "-withSourcesAmalgamation" };
        assertTrue(parse(args).getWithSourcesAmalgamation());
    }

    @Test
    public void withoutSourcesAmalgamation() throws ParseException
    {
        String[] args = { "-withoutSourcesAmalgamation" };
        assertFalse(parse(args).getWithSourcesAmalgamation());
    }

    @Test
    public void withSourcesAmalgamationDefault() throws ParseException
    {
        String[] args = {};
        assertTrue(parse(args).getWithSourcesAmalgamation());
    }

    @Test(expected=ParseException.class)
    public void sourcesAmalgamationConflict() throws ParseException
    {
        String[] args = { "-withSourcesAmalgamation", "-withoutSourcesAmalgamation" };
        parse(args);
    }

    @Test
    public void withSqlCode() throws ParseException
    {
        String[] args = { "-withSqlCode" };
        assertTrue(parse(args).getWithSqlCode());
    }

    @Test
    public void withoutSqlCode() throws ParseException
    {
        String[] args = { "-withoutSqlCode" };
        assertFalse(parse(args).getWithSqlCode());
    }

    @Test
    public void withSqlCodeDefault() throws ParseException
    {
        String[] args = {};
        assertTrue(parse(args).getWithSqlCode());
    }

    @Test(expected=ParseException.class)
    public void sqlCodeConflict() throws ParseException
    {
        String[] args = { "-withSqlCode", "-withoutSqlCode" };
        parse(args);
    }

    @Test
    public void withValidationCode() throws ParseException
    {
        String[] args = { "-withValidationCode" };
        CommandLineArguments parsedArgs = parse(args);
        assertTrue(parsedArgs.getWithValidationCode());
    }

    @Test
    public void withoutValidationCode() throws ParseException
    {
        String[] args = { "-withoutValidationCode" };
        assertFalse(parse(args).getWithValidationCode());
    }

    @Test
    public void withValidationCodeDefault() throws ParseException
    {
        String[] args = {};
        assertFalse(parse(args).getWithValidationCode());
    }

    @Test(expected=ParseException.class)
    public void validationCodeConflict() throws ParseException
    {
        String[] args = { "-withValidationCode", "-withoutValidationCode" };
        parse(args);
    }

    @Test
    public void withWriterCode() throws ParseException
    {
        String[] args = { "-withWriterCode" };
        assertTrue(parse(args).getWithWriterCode());
    }

    @Test
    public void withoutWriterCode() throws ParseException
    {
        String[] args = { "-withoutWriterCode" };
        CommandLineArguments parsedArgs = parse(args);
        assertFalse(parsedArgs.getWithWriterCode());
        assertFalse(parsedArgs.getWithServiceCode()); // auto-disabled
    }

    @Test
    public void withWriterCodeDefault() throws ParseException
    {
        String[] args = {};
        assertTrue(parse(args).getWithWriterCode());
    }

    @Test(expected=ParseException.class)
    public void writerCodeConflict() throws ParseException
    {
        String[] args = { "-withWriterCode", "-withoutWriterCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithRangeCheckCodeConflict() throws ParseException
    {
        String[] args = { "-withoutWriterCode", "-withRangeCheckCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithServiceCodeConflict() throws ParseException
    {
        String[] args = { "-withoutWriterCode", "-withServiceCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithValidationCodeConflict() throws ParseException
    {
        String[] args = { "-withoutWriterCode", "-withValidationCode" };
        parse(args);
    }

    @Test
    public void withUnusedWarnings() throws ParseException
    {
        String[] args = { "-withUnusedWarnings" };
        assertTrue(parse(args).getWithUnusedWarnings());
    }

    @Test
    public void withoutUnusedWarnings() throws ParseException
    {
        String[] args = { "-withoutUnusedWarnings" };
        CommandLineArguments parsedArgs = parse(args);
        assertFalse(parsedArgs.getWithUnusedWarnings());
    }

    @Test
    public void withUnusedWarningsDefault() throws ParseException
    {
        String[] args = {};
        assertFalse(parse(args).getWithUnusedWarnings());
    }

    @Test(expected=ParseException.class)
    public void unusedWarningsConflict() throws ParseException
    {
        String[] args = { "-withUnusedWarnings", "-withoutUnusedWarnings" };
        parse(args);
    }

    private static CommandLineArguments parse(String[] args) throws ParseException
    {
        CommandLineArguments commandLineArgs = new CommandLineArguments();
        commandLineArgs.parse(args);
        return commandLineArgs;
    }
}
