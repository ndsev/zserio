package zserio.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import org.apache.commons.cli.ParseException;

import java.util.List;

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

    @Test
    public void rangeCheckCodeConflict() throws ParseException
    {
        final String[] args = { "-withRangeCheckCode", "-withoutRangeCheckCode" };
        assertThrows(ParseException.class, () -> parse(args));
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

    @Test
    public void pubsubCodeConflict() throws ParseException
    {
        final String[] args = { "-withPubsubCode", "-withoutPubsubCode" };
        assertThrows(ParseException.class, () -> parse(args));
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

    @Test
    public void serviceCodeConflict() throws ParseException
    {
        final String[] args = { "-withServiceCode", "-withoutServiceCode" };
        assertThrows(ParseException.class, () -> parse(args));
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

    @Test
    public void sqlCodeConflict() throws ParseException
    {
        final String[] args = { "-withSqlCode", "-withoutSqlCode" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void withTypeInfoCode() throws ParseException
    {
        final String[] args = { "-withTypeInfoCode" };
        CommandLineArguments parsedArgs = parse(args);
        assertTrue(parsedArgs.getWithTypeInfoCode());
    }

    @Test
    public void withoutTypeInfoCode() throws ParseException
    {
        final String[] args = { "-withoutTypeInfoCode" };
        assertFalse(parse(args).getWithTypeInfoCode());
    }

    @Test
    public void withTypeInfoCodeDefault() throws ParseException
    {
        final String[] args = {};
        assertFalse(parse(args).getWithTypeInfoCode());
    }

    @Test
    public void typeInfoCodeConflict() throws ParseException
    {
        final String[] args = { "-withTypeInfoCode", "-withoutTypeInfoCode" };
        assertThrows(ParseException.class, () -> parse(args));
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

    @Test
    public void validationCodeConflict() throws ParseException
    {
        final String[] args = { "-withValidationCode", "-withoutValidationCode" };
        assertThrows(ParseException.class, () -> parse(args));
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

    @Test
    public void writerCodeConflict() throws ParseException
    {
        final String[] args = { "-withWriterCode", "-withoutWriterCode" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void withoutWriterCodeWithRangeCheckCodeConflict() throws ParseException
    {
        final String[] args = { "-withoutWriterCode", "-withRangeCheckCode" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void withoutWriterCodeWithPubsubCodeConflict() throws ParseException
    {
        final String[] args = { "-withoutWriterCode", "-withPubsubCode" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void withoutWriterCodeWithServiceCodeConflict() throws ParseException
    {
        final String[] args = { "-withoutWriterCode", "-withServiceCode" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void withoutWriterCodeWithValidationCodeConflict() throws ParseException
    {
        final String[] args = { "-withoutWriterCode", "-withValidationCode" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void withoutWriterCodeWithReflectionCodeConflict() throws ParseException
    {
        final String[] args = { "-withoutWriterCode", "-withReflectionCode" };
        assertThrows(ParseException.class, () -> parse(args));
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

    @Test
    public void unusedWarningsConflict() throws ParseException
    {
        final String[] args = { "-withUnusedWarnings", "-withoutUnusedWarnings" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void withCrossExtensionCheck() throws ParseException
    {
        final String[] args = { "-withCrossExtensionCheck" };
        assertTrue(parse(args).getWithCrossExtensionCheck());
    }

    @Test
    public void withoutCrossExtensionCheck() throws ParseException
    {
        final String[] args = { "-withoutCrossExtensionCheck" };
        assertFalse(parse(args).getWithCrossExtensionCheck());
    }

    @Test
    public void withCrossExtensionCheckDefault() throws ParseException
    {
        final String[] args = {};
        assertTrue(parse(args).getWithCrossExtensionCheck());
    }

    @Test
    public void crossExtensionCheck() throws ParseException
    {
        final String[] args = { "-withCrossExtensionCheck", "-withoutCrossExtensionCheck" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void withoutGlobalRuleIdCheckDefault() throws ParseException
    {
        final String[] args = {};
        assertFalse(parse(args).getWithGlobalRuleIdCheck());
    }

    @Test
    public void withGlobalRuleIdCheck() throws ParseException
    {
        final String[] args = { "-withGlobalRuleIdCheck" };
        assertTrue(parse(args).getWithGlobalRuleIdCheck());
    }

    @Test
    public void globalRuleIdCheck() throws ParseException
    {
        final String[] args = { "-withGlobalRuleCheck", "-withoutGlobalRuleCheck" };
        assertThrows(ParseException.class, () -> parse(args));
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

    @Test
    public void setTopLevelPackageWrongId() throws ParseException
    {
        final String[] args = { "-setTopLevelPackage", "top.5main" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void setTopLevelPackageZserioId() throws ParseException
    {
        final String[] args = { "-setTopLevelPackage", "top.zserio" };
        assertThrows(ParseException.class, () -> parse(args));
    }

    @Test
    public void ignoreTimestampDefault() throws ParseException
    {
        final String[] args = { };
        assertFalse(parse(args).getIgnoreTimestamps());
    }

    @Test
    public void ignoreTimestamps() throws ParseException
    {
        final String[] args = { "-ignoreTimestamps" };
        assertTrue(parse(args).getIgnoreTimestamps());
    }

    @Test
    public void allowImplicitArraysDefault() throws ParseException
    {
        final String[] args = { };
        assertFalse(parse(args).getAllowImplicitArrays());
    }

    @Test
    public void allowImplicitArrays() throws ParseException
    {
        final String[] args = { "-allowImplicitArrays" };
        assertTrue(parse(args).getAllowImplicitArrays());
    }

    private static CommandLineArguments parse(String[] args) throws ParseException
    {
        final CommandLineArguments commandLineArgs = new CommandLineArguments(ZserioTool.Executor.JAVA_MAIN);
        commandLineArgs.parse(args);
        return commandLineArgs;
    }
}
