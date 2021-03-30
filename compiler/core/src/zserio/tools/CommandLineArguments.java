package zserio.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import zserio.ast.IdentifierValidator;

import org.apache.commons.cli.DefaultParser;

/**
 * The class to handle all command line arguments of Zserio Tool.
 */
class CommandLineArguments
{
    /**
     * Constructor.
     *
     * @param executor Specifies in which way the Zserio tool has been executed.
     */
    public CommandLineArguments(ZserioTool.Executor executor)
    {
        this.executor = executor;

        options = new Options();
        addOptions();
    }

    /**
     * Gets the option abstraction to have possibility to add new options by extensions.
     *
     * @returns The option abstraction.
     */
    public Options getOptions()
    {
        return options;
    }

    /**
     * Parses the command line arguments.
     *
     * @param args Command line arguments to parse.
     *
     * @throws ParseException Throws if any parse error occurred.
     */
    public void parse(String[] args) throws ParseException
    {
        CommandLineParser cliParser = new DefaultParser();
        parsedCommandLine = cliParser.parse(options, args, false);
        readArguments();
        readOptions();
    }

    /**
     * Gets the Zserio input file name.
     *
     * @return Zserio input file name.
     */
    public String getInputFileName()
    {
        return inputFileName;
    }

    /**
     * Gets the path name to the Zserio sources.
     *
     * @returns Path name to the Zserio sources.
     */
    public String getSrcPathName()
    {
        return srcPathName;
    }

    /**
     * Gets the help option.
     *
     * @returns True if command line arguments contain help option.
     */
    public boolean hasHelpOption()
    {
        return helpOption;
    }

    /**
     * Gets the version option.
     *
     * @returns True if command line arguments contain version option.
     */
    public boolean hasVersionOption()
    {
        return versionOption;
    }

    /**
     * Gets whether the range check code option is enabled.
     *
     * @returns True if command line arguments enable range check code option.
     */
    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCodeOption;
    }

    /**
     * Gets whether the Pub/Sub code option is enabled.
     *
     * @returns True if command line arguments enable Pub/Sub code option.
     */
    public boolean getWithPubsubCode()
    {
        return withPubsubCodeOption;
    }

    /**
     * Gets whether the Service code option is enabled.
     *
     * @returns True if command line arguments enable Service code option.
     */
    public boolean getWithServiceCode()
    {
        return withServiceCodeOption;
    }

    /**
     * Gets whether the sources amalgamation option is enabled.
     *
     * @returns True if sources amalgamation is enabled.
     */
    public boolean getWithSourcesAmalgamation()
    {
        return withSourcesAmalgamationOption;
    }

    /**
     * Gets whether the SQL code option is enabled.
     *
     * @returns True if command line arguments enable SQL code option.
     */
    public boolean getWithSqlCode()
    {
        return withSqlCodeOption;
    }

    /**
     * Gets whether the validation code option is enabled.
     *
     * @returns True if command line arguments enable validation code option.
     */
    public boolean getWithValidationCode()
    {
        return withValidationCodeOption;
    }

    /**
     * Gets whether the writer code option.
     *
     * @returns True if command line arguments enable writer code option.
     */
    public boolean getWithWriterCode()
    {
        return withWriterCodeOption;
    }

    /**
     * Gets whether warnings for unused types are enabled.
     *
     * @returns True if command line arguments enable unused warnings option.
     */
    public boolean getWithUnusedWarnings()
    {
        return withUnusedWarningsOption;
    }

    /**
     * Gets whether to run check on all available extensions to ensure that the schema will be portable.
     *
     * @return True if command line arguments enable cross extension check.
     */
    public boolean getWithCrossExtensionCheck()
    {
        return withCrossExtensionCheckOption;
    }

    /**
     * Gets the top level package name identifier list.
     *
     * @returns List of top level package name identifier or empty list if not specified.
     */
    public List<String> getTopLevelPackageNameIds()
    {
        return Collections.unmodifiableList(topLevelPackageNameIds);
    }

    /**
     * Gets whether to ignore timestamps and thus always regenerate output.
     *
     * @return True if timestamps should be ignored.
     */
    public boolean getIgnoreTimestamps()
    {
        return ignoreTimestampsOption;
    }

    /**
     * Returns true if command line arguments contain given option name.
     *
     * @param optionName Option name to check.
     */
    public boolean hasOption(String optionName)
    {
        return parsedCommandLine.hasOption(optionName);
    }

    /**
     * Gets the argument of the given option.
     *
     * @param optionName Option name of which argument to get.
     *
     * @return Option argument or null if option name is invalid or option does not have an argument.
     */
    public String getOptionValue(String optionName)
    {
        return parsedCommandLine.getOptionValue(optionName);
    }

    /**
     * Prints help.
     */
    public void printHelp()
    {
        final String command = (executor == ZserioTool.Executor.PYTHON_MAIN) ? "zserio" :
            "java -jar zserio.jar";

        final HelpFormatter hf = new HelpFormatter();
        hf.setSyntaxPrefix("Usage: ");
        hf.setLeftPadding(2);
        hf.setOptionComparator(null);
        hf.printHelp(command + " <options> zserioInputFile\n", "Options:", options, null, false);
        ZserioToolPrinter.printMessage("");
    }

    private void addOptions()
    {
        Option option = new Option(OptionNameHelpShort, "help", false, "print this help text and exit");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(OptionNameVersionShort, "version", false, "print Zserio version info");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(OptionNameSource, true, "path to Zserio source files");
        option.setArgName("srcDir");
        option.setRequired(false);
        options.addOption(option);

        final OptionGroup rangeCheckCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithRangeCheckCode, false,
                "enable code for integer range checking for field and parameter setters");
        rangeCheckCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutRangeCheckCode, false,
                "disable code for integer range checking for field and parameter setters (default)");
        rangeCheckCodeGroup.addOption(option);
        rangeCheckCodeGroup.setRequired(false);
        options.addOptionGroup(rangeCheckCodeGroup);

        final OptionGroup pubsubCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithPubsubCode, false, "enable code for Zserio Pub/Sub (default)");
        pubsubCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutPubsubCode, false, "disable code for Zseiro Pub/Sub");
        pubsubCodeGroup.addOption(option);
        pubsubCodeGroup.setRequired(false);
        options.addOptionGroup(pubsubCodeGroup);

        final OptionGroup serviceCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithServiceCode, false, "enable code for Zserio services (default)");
        serviceCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutServiceCode, false, "disable code for Zseiro services");
        serviceCodeGroup.addOption(option);
        serviceCodeGroup.setRequired(false);
        options.addOptionGroup(serviceCodeGroup);

        final OptionGroup sourcesAmalgamationGroup = new OptionGroup();
        option = new Option(OptionNameWithSourcesAmalgamation, false,
                            "enable amalgamation of generated C++ sources (default)");
        sourcesAmalgamationGroup.addOption(option);
        option = new Option(OptionNameWithoutSourcesAmalgamation, false,
                            "disable amalgamation of generated C++ sources");
        sourcesAmalgamationGroup.addOption(option);
        sourcesAmalgamationGroup.setRequired(false);
        options.addOptionGroup(sourcesAmalgamationGroup);

        final OptionGroup sqlCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithSqlCode, false,
                "enable code for relational (SQLite) parts (default)");
        sqlCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutSqlCode, false, "disable code for relational (SQLite) parts");
        sqlCodeGroup.addOption(option);
        sqlCodeGroup.setRequired(false);
        options.addOptionGroup(sqlCodeGroup);

        final OptionGroup validationCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithValidationCode, false, "enable validation code");
        validationCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutValidationCode, false, "disable validation code (default)");
        validationCodeGroup.addOption(option);
        validationCodeGroup.setRequired(false);
        options.addOptionGroup(validationCodeGroup);

        final OptionGroup writerCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithWriterCode, false,
                "enable writing interface code (default)");
        writerCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutWriterCode, false,
                "disable writing interface code");
        writerCodeGroup.addOption(option);
        writerCodeGroup.setRequired(false);
        options.addOptionGroup(writerCodeGroup);

        final OptionGroup unusedWarningsGroup = new OptionGroup();
        option = new Option(OptionNameWithUnusedWarnings, false, "enable unused warnings");
        unusedWarningsGroup.addOption(option);
        option = new Option(OptionNameWithoutUnusedWarnings, false, "disable unused warnings (default)");
        unusedWarningsGroup.addOption(option);
        unusedWarningsGroup.setRequired(false);
        options.addOptionGroup(unusedWarningsGroup);

        final OptionGroup crossExtensionCheckGroup = new OptionGroup();
        option = new Option(OptionNameWithCrossExtensionCheck, false, "enable cross extension check (default)");
        crossExtensionCheckGroup.addOption(option);
        option = new Option(OptionNameWithoutCrossExtensionCheck, false, "disable cross extension check");
        crossExtensionCheckGroup.addOption(option);
        crossExtensionCheckGroup.setRequired(false);
        options.addOptionGroup(crossExtensionCheckGroup);

        option = new Option(OptionNameSetTopLevelPackage, true,
                "force top level package prefix to all zserio packages");
        option.setArgName("packageName");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(OptionNameIgnoreTimestamps, false,
                "ignore timestamps and always regenerate output");
        options.addOption(option);
    }

    private void readOptions() throws ParseException
    {
        srcPathName = getOptionValue(OptionNameSource);
        helpOption = hasOption(OptionNameHelpShort);
        versionOption = hasOption(OptionNameVersionShort);
        withRangeCheckCodeOption = hasOption(OptionNameWithRangeCheckCode);
        withPubsubCodeOption = !hasOption(OptionNameWithoutPubsubCode);
        withServiceCodeOption = !hasOption(OptionNameWithoutServiceCode);
        withSourcesAmalgamationOption = !hasOption(OptionNameWithoutSourcesAmalgamation);
        withSqlCodeOption = !hasOption(OptionNameWithoutSqlCode);
        withValidationCodeOption = hasOption(OptionNameWithValidationCode);
        withWriterCodeOption = !hasOption(OptionNameWithoutWriterCode);
        withUnusedWarningsOption = hasOption(OptionNameWithUnusedWarnings);
        withCrossExtensionCheckOption = !hasOption(OptionNameWithoutCrossExtensionCheck);
        final String topLevelPackageName = getOptionValue(OptionNameSetTopLevelPackage);
        topLevelPackageNameIds = (topLevelPackageName == null) ? new ArrayList<String>() :
            java.util.Arrays.asList(topLevelPackageName.split("\\" + TOP_LEVEL_PACKAGE_NAME_SEPARATOR));
        ignoreTimestampsOption = hasOption(OptionNameIgnoreTimestamps);

        validateOptions();

        if (!withWriterCodeOption)
        {
            // automatically disable options which are not compatible with withoutWriterCodeOption
            if (withPubsubCodeOption)
            {
                withPubsubCodeOption = false;
                ZserioToolPrinter.printInfo("Applying '" + OptionNameWithoutPubsubCode + "' because of '" +
                        OptionNameWithoutWriterCode + "'");
            }
            if (withServiceCodeOption)
            {
                withServiceCodeOption = false;
                ZserioToolPrinter.printInfo("Applying '" + OptionNameWithoutServiceCode + "' because of '" +
                        OptionNameWithoutWriterCode + "'");
            }
        }
    }

    private void validateOptions() throws ParseException
    {
        validateTopLevelPackageNameIds();

        // check explicitly specified conflicting options
        if (hasOption(OptionNameWithoutWriterCode))
        {
            if (hasOption(OptionNameWithRangeCheckCode))
            {
                throw new ParseException(
                        "The specified option 'withRangeCheckCode' conflicts with another option: " +
                        "'withoutWriterCode'");
            }
            if (hasOption(OptionNameWithValidationCode))
            {
                throw new ParseException(
                        "The specified option 'withValidationCode' conflicts with another option: " +
                        "'withoutWriterCode'");
            }
            if (hasOption(OptionNameWithPubsubCode))
            {
                throw new ParseException(
                        "The specified option 'withPubsubCode' conflicts with another option: " +
                        "'withoutWriterCode'");
            }
            if (hasOption(OptionNameWithServiceCode))
            {
                throw new ParseException(
                        "The specified option 'withServiceCode' conflicts with another option: " +
                        "'withoutWriterCode'");
            }
        }
    }

    private void validateTopLevelPackageNameIds() throws ParseException
    {
        // check set top level package identifiers
        for (String topLevelId : topLevelPackageNameIds)
        {
            try
            {
                IdentifierValidator.validateTopLevelPackageId(topLevelId);
            }
            catch (RuntimeException exception)
            {
                throw new ParseException("The specified option 'setTopLevelPackage' has bad format: " +
                        exception.getMessage());
            }
        }
    }

    private void readArguments() throws ParseException
    {
        String[] arguments = parsedCommandLine.getArgs();
        if (arguments.length > 1)
        {
            // more than one unparsed argument (input file name) is always an error
            throw new ParseException("Unknown argument " + arguments[1]);
        }
        else if (arguments.length == 1)
        {
            // normalize slashes and backslashes
            inputFileName = new File(arguments[0]).getPath();
        }
        else
        {
            // don't fail yet - file name is not required for -h or -v
            inputFileName = null;
        }
    }

    private static final String OptionNameHelpShort = "h";
    private static final String OptionNameSource = "src";
    private static final String OptionNameVersionShort = "v";
    private static final String OptionNameWithRangeCheckCode = "withRangeCheckCode";
    private static final String OptionNameWithoutRangeCheckCode = "withoutRangeCheckCode";
    private static final String OptionNameWithPubsubCode = "withPubsubCode";
    private static final String OptionNameWithoutPubsubCode = "withoutPubsubCode";
    private static final String OptionNameWithServiceCode = "withServiceCode";
    private static final String OptionNameWithoutServiceCode = "withoutServiceCode";
    private static final String OptionNameWithSourcesAmalgamation = "withSourcesAmalgamation";
    private static final String OptionNameWithoutSourcesAmalgamation = "withoutSourcesAmalgamation";
    private static final String OptionNameWithSqlCode = "withSqlCode";
    private static final String OptionNameWithoutSqlCode = "withoutSqlCode";
    private static final String OptionNameWithValidationCode = "withValidationCode";
    private static final String OptionNameWithoutValidationCode = "withoutValidationCode";
    private static final String OptionNameWithWriterCode = "withWriterCode";
    private static final String OptionNameWithoutWriterCode = "withoutWriterCode";
    private static final String OptionNameWithUnusedWarnings = "withUnusedWarnings";
    private static final String OptionNameWithoutUnusedWarnings = "withoutUnusedWarnings";
    private static final String OptionNameWithCrossExtensionCheck = "withCrossExtensionCheck";
    private static final String OptionNameWithoutCrossExtensionCheck = "withoutCrossExtensionCheck";
    private static final String OptionNameSetTopLevelPackage = "setTopLevelPackage";
    private static final String OptionNameIgnoreTimestamps = "ignoreTimestamps";

    private static final String TOP_LEVEL_PACKAGE_NAME_SEPARATOR = ".";

    private final ZserioTool.Executor executor;
    private final Options options;

    private CommandLine parsedCommandLine;

    private String  inputFileName;
    private boolean helpOption;
    private String  srcPathName;
    private boolean versionOption;
    private boolean withRangeCheckCodeOption;
    private boolean withPubsubCodeOption;
    private boolean withServiceCodeOption;
    private boolean withSourcesAmalgamationOption;
    private boolean withSqlCodeOption;
    private boolean withValidationCodeOption;
    private boolean withWriterCodeOption;
    private boolean withUnusedWarningsOption;
    private boolean withCrossExtensionCheckOption;
    private List<String> topLevelPackageNameIds;
    private boolean ignoreTimestampsOption;
}
