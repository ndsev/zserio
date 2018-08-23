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
    public void withInspectorCode() throws ParseException
    {
        String[] args = { "-withInspectorCode" };
        CommandLineArguments parsedArgs = parse(args);
        assertTrue(parsedArgs.getWithInspectorCode());
    }

    @Test
    public void withoutInspectorCode() throws ParseException
    {
        String[] args = { "-withoutInspectorCode" };
        assertFalse(parse(args).getWithInspectorCode());
    }

    @Test
    public void withInspectorCodeDefault() throws ParseException
    {
        String[] args = {};
        assertFalse(parse(args).getWithInspectorCode());
    }

    @Test(expected=ParseException.class)
    public void inspectorCodeConflict() throws ParseException
    {
        String[] args = { "-withInspectorCode", "-withoutInspectorCode" };
        parse(args);
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
    public void withGrpcCode() throws ParseException
    {
        String[] args = { "-withGrpcCode" };
        assertTrue(parse(args).getWithGrpcCode());
    }

    @Test
    public void withoutGrpcCode() throws ParseException
    {
        String[] args = { "-withoutGrpcCode" };
        assertFalse(parse(args).getWithGrpcCode());
    }

    @Test
    public void withGrpcCodeDefault() throws ParseException
    {
        String[] args = {};
        assertTrue(parse(args).getWithGrpcCode());
    }

    @Test(expected=ParseException.class)
    public void grpcCodeConflict() throws ParseException
    {
        String[] args = { "-withGrpcCode", "-withoutGrpcCode" };
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
        assertFalse(parsedArgs.getWithGrpcCode()); // auto-disabled
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
    public void withoutWriterCodeWithInspectorCodeConflict() throws ParseException
    {
        String[] args = { "-withoutWriterCode", "-withInspectorCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithRangeCheckCodeConflict() throws ParseException
    {
        String[] args = { "-withoutWriterCode", "-withRangeCheckCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithValidationCodeConflict() throws ParseException
    {
        String[] args = { "-withoutWriterCode", "-withValidationCode" };
        parse(args);
    }

    @Test(expected=ParseException.class)
    public void withoutWriterCodeWithGrpcCodeConflict() throws ParseException
    {
        String[] args = { "-withoutWriterCode", "-withGrpcCode" };
        parse(args);
    }

    private static CommandLineArguments parse(String[] args) throws ParseException
    {
        CommandLineArguments commandLineArgs = new CommandLineArguments();
        commandLineArgs.parse(args);
        return commandLineArgs;
    }
}
