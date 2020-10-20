package zserio.tools;

import org.apache.commons.cli.ParseException;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CommandLineArgumentsTest
{
    @Test
    public void helpShort() throws ParseException
    {
        final String[] args = { "-h" };
        assertTrue(parse(args).hasHelpOption());
    }

    @Test
    public void helpLong() throws ParseException
    {
        final String[] args = { "-help" };
        assertTrue(parse(args).hasHelpOption());
    }

    @Test
    public void withRangeCheckCode() throws ParseException
    {
        final String[] args = { "-withRangeCheckCode" };
        CommandLineArguments parsedArgs = parse(args);
        assertTrue(parsedArgs.getWithRangeCheckCode());
    }

    @Test
    public void withoutRangeCheckCode() throws ParseException
    {
        final String[] args = { "-withoutRangeCheckCode" };
        assertFalse(parse(args).getWithRangeCheckCode());
    }

    @Test
    public void withRangeCheckCodeDefault() throws ParseException
    {
        final String[] args = {};
        assertFalse(parse(args).getWithRangeCheckCode());
    }

    @Test(expected=ParseException.class)
    public void rangeCheckCodeConflict() throws ParseException
    {
        final String[] args = { "-withRangeCheckCode", "-withoutRangeCheckCode" };
        parse(args);
    }

    @Test
    public void withPubsubCode() throws ParseException
    {
        final String[] args = { "-withPubsubCode" };
        assertTrue(parse(args).getWithPubsubCode());
    }

    @Test
    public void withoutPubsubCode() throws ParseException
    {
        final String[] args = { "-withoutPubsubCode" };
        assertFalse(parse(args).getWithPubsubCode());
    }

    @Test
    public void withPubsubCodeDefault() throws ParseException
    {
        final String[] args = {};
        assertTrue(parse(args).getWithPubsubCode());
    }

    @Test(expected=ParseException.class)
    public void pubsubCodeConflict() throws ParseException
    {
        final String[] args = { "-withPubsubCode", "-withoutPubsubCode" };
        parse(args);
    }

    @Test
    public void withServiceCode() throws ParseException
    {
        final String[] args = { "-withServiceCode" };
        assertTrue(parse(args).getWithServiceCode());
    }

    @Test
    public void withoutServiceCode() throws ParseException
    {
        final String[] args = { "-withoutServiceCode" };
        assertFalse(parse(args).getWithServiceCode());
    }

    @Test
    public void withServiceCodeDefault() throws ParseException
    {
        final String[] args = {};
        assertTrue(parse(args).getWithServiceCode());
    }

    @Test(expected=ParseException.class)
    public void serviceCodeConflict() throws ParseException
    {
        final String[] args = { "-withServiceCode", "-withoutServiceCode" };
        parse(args);
    }

    @Test
    public void withSourcesAmalgamation() throws ParseException
    {
        final String[] args = { "-withSourcesAmalgamation" };
        assertTrue(parse(args).getWithSourcesAmalgamation());
    }

    @Test
    public void withoutSourcesAmalgamation() throws ParseException
    {
        final String[] args = { "-withoutSourcesAmalgamation" };
        assertFalse(parse(args).getWithSourcesAmalgamation());
    }

    @Test
    public void withSourcesAmalgamationDefault() throws ParseException
    {
        final String[] args = {};
        assertTrue(parse(args).getWithSourcesAmalgamation());
    }

    @Test(expected=ParseException.class)
    public void sourcesAmalgamationConflict() throws ParseException
    {
        final String[] args = { "-withSourcesAmalgamation", "-withoutSourcesAmalgamation" };
        parse(args);
    }

    @Test
    public void withSqlCode() throws ParseException
    {
        final String[] args = { "-withSqlCode" };
        assertTrue(parse(args).getWithSqlCode());
    }

    @Test
    public void withoutSqlCode() throws ParseException
    {
        final String[] args = { "-withoutSqlCode" };
        assertFalse(parse(args).getWithSqlCode());
    }

    @Test
    public void withSqlCodeDefault() throws ParseException
    {
        final String[] args = {};
        assertTrue(parse(args).getWithSqlCode());
    }

    @Test(expected=ParseException.class)
    public void sqlCodeConflict() throws ParseException
    {
        final String[] args = { "-withSqlCode", "-withoutSqlCode" };
        parse(args);
    }

    @Test
    public void withValidationCode() throws ParseException
    {
        final String[] args = { "-withValidationCode" };
        CommandLineArguments parsedArgs = parse(args);
        assertTrue(parsedArgs.getWithValidationCode());
    }

    @Test
    public void withoutValidationCode() throws ParseException
    {
        final String[] args = { "-withoutValidationCode" };
        assertFalse(parse(args).getWithValidationCode());
    }

    @Test
    public void withValidationCodeDefault() throws ParseException
    {
        final String[] args = {};
        assertFalse(parse(args).getWithValidationCode());
    }

    @Test(expected=ParseException.class)
    public void validationCodeConflict() throws ParseException
    {
        final String[] args = { "-withValidationCode", "-withoutValidationCode" };
        parse(args);
    }

    @Test
    public void withWriterCode() throws ParseException
    {
        final String[] args = { "-withWriterCode" };
        assertTrue(parse(args).getWithWriterCode());
    }

    @Test
    public void withoutWriterCode() throws ParseException
    {
        final String[] args = { "-withoutWriterCode" };
        CommandLineArguments parsedArgs = parse(args);
        assertFalse(parsedArgs.getWithWriterCode());
        assertFalse(parsedArgs.getWithPubsubCode()); // auto-disabled
        assertFalse(parsedArgs.getWithServiceCode()); // auto-disabled
    }

    @Test
    public void withWriterCodeDefault() throws ParseException
    {
        final String[] args = {};
        assertTrue(parse(args).getWithWriterCode());
    }

    @Test(expected=ParseException.class)
    public void writerCodeConflict() throws ParseException
    {
        final String[] args = { "-withWriterCode", "-withoutWriterCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithRangeCheckCodeConflict() throws ParseException
    {
        final String[] args = { "-withoutWriterCode", "-withRangeCheckCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithPubsubCodeConflict() throws ParseException
    {
        final String[] args = { "-withoutWriterCode", "-withPubsubCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithServiceCodeConflict() throws ParseException
    {
        final String[] args = { "-withoutWriterCode", "-withServiceCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithValidationCodeConflict() throws ParseException
    {
        final String[] args = { "-withoutWriterCode", "-withValidationCode" };
        parse(args);
    }

    @Test
    public void withUnusedWarnings() throws ParseException
    {
        final String[] args = { "-withUnusedWarnings" };
        assertTrue(parse(args).getWithUnusedWarnings());
    }

    @Test
    public void withoutUnusedWarnings() throws ParseException
    {
        final String[] args = { "-withoutUnusedWarnings" };
        assertFalse(parse(args).getWithUnusedWarnings());
    }

    @Test
    public void withUnusedWarningsDefault() throws ParseException
    {
        final String[] args = {};
        assertFalse(parse(args).getWithUnusedWarnings());
    }

    @Test(expected=ParseException.class)
    public void unusedWarningsConflict() throws ParseException
    {
        final String[] args = { "-withUnusedWarnings", "-withoutUnusedWarnings" };
        parse(args);
    }

    @Test
    public void setTopLevelPackage() throws ParseException
    {
        final String[] args = { "-setTopLevelPackage", "top.main" };
        final List<String> ids = parse(args).getTopLevelPackageNameIds();
        assertEquals(2, ids.size());
        assertEquals("top", ids.get(0));
        assertEquals("main", ids.get(1));
    }

    @Test(expected=ParseException.class)
    public void setTopLevelPackageWrongId() throws ParseException
    {
        final String[] args = { "-setTopLevelPackage", "top.5main" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void setTopLevelPackageReservedId() throws ParseException
    {
        final String[] args = { "-setTopLevelPackage", "top.static" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void setTopLevelPackageZserioId() throws ParseException
    {
        final String[] args = { "-setTopLevelPackage", "top.zserio" };
        parse(args);
    }

    private static CommandLineArguments parse(String[] args) throws ParseException
    {
        final CommandLineArguments commandLineArgs = new CommandLineArguments();
        commandLineArgs.parse(args);
        return commandLineArgs;
    }
}
