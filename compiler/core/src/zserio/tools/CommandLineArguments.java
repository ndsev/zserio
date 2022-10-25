package zserio.tools;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

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
     * @return The option abstraction.
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
        try
        {
            CommandLineParser cliParser = new DefaultParser();
            parsedCommandLine = cliParser.parse(options, args, false);
            readArguments();
            readOptions();
        }
        catch (UnrecognizedOptionException e)
        {
            // TODO[Mi-L@]: Should be removed after release 2.8!
            if (e.getOption().equals("-withUnusedWarnings"))
            {
                throw new ParseException("Option '-withUnusedWarnings' was removed, use " +
                        "'-withWarnings unused' instead! See '--help warnings' for more info.");
            }
            else if (e.getOption().equals("-withoutUnusedWarnings"))
            {
                throw new ParseException("Option '-withoutUnusedWarnings' was removed, use " +
                        "'-withoutWarnings unused' instead! See '--help warnings' for more info.");
            }

            throw e; // rethrow
        }
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
     * @return Path name to the Zserio sources.
     */
    public String getSrcPathName()
    {
        return srcPathName;
    }

    /**
     * Gets the help option.
     *
     * @return True if command line arguments contain help option.
     */
    public boolean hasHelpOption()
    {
        return helpOption;
    }

    /**
     * Gets the version option.
     *
     * @return True if command line arguments contain version option.
     */
    public boolean hasVersionOption()
    {
        return versionOption;
    }

    /**
     * Gets whether the Pub/Sub code option is enabled.
     *
     * @return True if command line arguments enable Pub/Sub code option.
     */
    public boolean getWithPubsubCode()
    {
        return withPubsubCodeOption;
    }

    /**
     * Gets whether the range check code option is enabled.
     *
     * @return True if command line arguments enable range check code option.
     */
    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCodeOption;
    }

    /**
     * Gets whether the Service code option is enabled.
     *
     * @return True if command line arguments enable Service code option.
     */
    public boolean getWithServiceCode()
    {
        return withServiceCodeOption;
    }

    /**
     * Gets whether the SQL code option is enabled.
     *
     * @return True if command line arguments enable SQL code option.
     */
    public boolean getWithSqlCode()
    {
        return withSqlCodeOption;
    }

    /**
     * Gets whether the type info code option is enabled.
     *
     * @return True if command line arguments enable type info code option.
     */
    public boolean getWithTypeInfoCode()
    {
        return withTypeInfoCodeOption;
    }

    /**
     * Gets whether the validation code option is enabled.
     *
     * @return True if command line arguments enable validation code option.
     */
    public boolean getWithValidationCode()
    {
        return withValidationCodeOption;
    }

    /**
     * Gets whether the writer code option.
     *
     * @return True if command line arguments enable writer code option.
     */
    public boolean getWithWriterCode()
    {
        return withWriterCodeOption;
    }

    /**
     * Gets configuration of warnings.
     *
     * @return Warnings config.
     */
    public WarningsConfig getWarningsConfig()
    {
        return warningsConfig;
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
     * Gets whether to enable checking of rule id uniqueness between all packages.
     *
     * @return True if command line arguments enable checking of rule id uniqueness between all packages.
     */
    public boolean getWithGlobalRuleIdCheck()
    {
        return withGlobalRuleIdCheckOption;
    }

    /**
     * Gets whether to generate comments in code.
     *
     * @return True if command line arguments enable generating of comments in code.
     */
    public boolean getWithCodeComments()
    {
        return withCodeCommentsOption;
    }

    /**
     * Gets the top level package name identifier list.
     *
     * @return List of top level package name identifier or empty list if not specified.
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
     * Gets whether to allow implicit arrays in zserio language.
     *
     * @return True if implicit arrays should be allowed.
     */
    public boolean getAllowImplicitArrays()
    {
        return allowImplicitArraysOption;
    }

    /**
     * Returns true if command line arguments contain given option name.
     *
     * @param optionName Option name to check.
     *
     * @return True if the option is present.
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
     * Gets list of arguments of the given option.
     *
     * @param optionName Option name of which arguments to get.
     *
     * @return List of option arguments.
     */
    public List<String> getOptionValues(String optionName)
    {
        final String[] values = parsedCommandLine.getOptionValues(optionName);
        return values != null ? Arrays.asList(values) : new ArrayList<String>();
    }

    /**
     * Prints help.
     */
    public void printHelp(List<Extension> extensions)
    {
        if (HelpTopicWarnings.equals(helpTopic))
        {
            printHelpWarnings();
            return;
        }

        final String command = (executor == ZserioTool.Executor.PYTHON_MAIN) ? "zserio" :
            "java -jar zserio.jar";

        final HelpFormatter hf = new HelpFormatter();
        hf.setSyntaxPrefix("Usage: ");
        hf.setLeftPadding(2);
        hf.setOptionComparator(null);
        hf.printHelp(command + " <options> zserioInputFile\n", "Options:", options, null, false);
        ZserioToolPrinter.printMessage("");

        printExtensions(extensions);
    }

    private void addOptions()
    {
        Option option = new Option(OptionNameHelpShort, "help", true,
                "print this help text and exit, " +
                "specify one of the following topics for detailed description: warnings");
        option.setRequired(false);
        option.setOptionalArg(true);
        option.setArgName("[topic]");
        options.addOption(option);

        option = new Option(OptionNameVersionShort, "version", false, "print Zserio version info");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(OptionNameSource, true, "path to Zserio source files");
        option.setArgName("srcDir");
        option.setRequired(false);
        options.addOption(option);

        final OptionGroup pubsubCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithPubsubCode, false, "enable code for Zserio Pub/Sub (default)");
        pubsubCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutPubsubCode, false, "disable code for Zserio Pub/Sub");
        pubsubCodeGroup.addOption(option);
        pubsubCodeGroup.setRequired(false);
        options.addOptionGroup(pubsubCodeGroup);

        final OptionGroup rangeCheckCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithRangeCheckCode, false,
                "enable code for integer range checking for field and parameter setters");
        rangeCheckCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutRangeCheckCode, false,
                "disable code for integer range checking for field and parameter setters (default)");
        rangeCheckCodeGroup.addOption(option);
        rangeCheckCodeGroup.setRequired(false);
        options.addOptionGroup(rangeCheckCodeGroup);

        final OptionGroup serviceCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithServiceCode, false, "enable code for Zserio services (default)");
        serviceCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutServiceCode, false, "disable code for Zserio services");
        serviceCodeGroup.addOption(option);
        serviceCodeGroup.setRequired(false);
        options.addOptionGroup(serviceCodeGroup);

        final OptionGroup sqlCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithSqlCode, false,
                "enable code for relational (SQLite) parts (default)");
        sqlCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutSqlCode, false, "disable code for relational (SQLite) parts");
        sqlCodeGroup.addOption(option);
        sqlCodeGroup.setRequired(false);
        options.addOptionGroup(sqlCodeGroup);

        final OptionGroup typeInfoGroup = new OptionGroup();
        option = new Option(OptionNameWithTypeInfoCode, false, "enable type info code");
        typeInfoGroup.addOption(option);
        option = new Option(OptionNameWithoutTypeInfoCode, false, "disable type info code (default)");
        typeInfoGroup.addOption(option);
        typeInfoGroup.setRequired(false);
        options.addOptionGroup(typeInfoGroup);

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

        option = new Option(OptionNameWithWarnings, true,
                "enable specified warnings, use '--help warnings' for detailed description");
        option.setArgName("warning[,warning]*");
        option.setArgs(Option.UNLIMITED_VALUES);
        option.setValueSeparator(WarningsConfig.WARNINGS_OPTIONS_SEPARATOR);
        option.setRequired(false);
        options.addOption(option);
        option = new Option(OptionNameWithoutWarnings, true,
                "disable specified warnings, use '--help warnings' for detailed description");
        option.setArgName("warning[,warning]*");
        option.setArgs(Option.UNLIMITED_VALUES);
        option.setValueSeparator(WarningsConfig.WARNINGS_OPTIONS_SEPARATOR);
        option.setRequired(false);
        options.addOption(option);

        final OptionGroup crossExtensionCheckGroup = new OptionGroup();
        option = new Option(OptionNameWithCrossExtensionCheck, false, "enable cross extension check (default)");
        crossExtensionCheckGroup.addOption(option);
        option = new Option(OptionNameWithoutCrossExtensionCheck, false, "disable cross extension check");
        crossExtensionCheckGroup.addOption(option);
        crossExtensionCheckGroup.setRequired(false);
        options.addOptionGroup(crossExtensionCheckGroup);

        final OptionGroup globalRuleIdCheckGroup = new OptionGroup();
        option = new Option(OptionNameWithGlobalRuleIdCheck, false,
                "enable rule id check for uniqueness between all packages");
        globalRuleIdCheckGroup.addOption(option);
        option = new Option(OptionNameWithoutGlobalRuleIdCheck, false,
                "disable rule id check for uniqueness between all packages (default)");
        globalRuleIdCheckGroup.addOption(option);
        globalRuleIdCheckGroup.setRequired(false);
        options.addOptionGroup(globalRuleIdCheckGroup);

        final OptionGroup codeCommentsGroup = new OptionGroup();
        option = new Option(OptionNameWithCodeComments, false, "enable comments in generated code");
        codeCommentsGroup.addOption(option);
        option = new Option(OptionNameWithoutCodeComments, false,
                "disable comments in generated code (default)");
        codeCommentsGroup.addOption(option);
        codeCommentsGroup.setRequired(false);
        options.addOptionGroup(codeCommentsGroup);

        option = new Option(OptionNameSetTopLevelPackage, true,
                "force top level package prefix to all zserio packages");
        option.setArgName("packageName");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(OptionNameIgnoreTimestamps, false,
                "ignore timestamps and always regenerate output");
        options.addOption(option);

        option = new Option(OptionNameAllowImplicitArrays, false,
                "allow implicit arrays in zserio language");
        options.addOption(option);
    }

    private void readOptions() throws ParseException
    {
        srcPathName = getOptionValue(OptionNameSource);
        helpOption = hasOption(OptionNameHelpShort);
        helpTopic = getOptionValue(OptionNameHelpShort);
        versionOption = hasOption(OptionNameVersionShort);
        withPubsubCodeOption = !hasOption(OptionNameWithoutPubsubCode);
        withRangeCheckCodeOption = hasOption(OptionNameWithRangeCheckCode);
        withServiceCodeOption = !hasOption(OptionNameWithoutServiceCode);
        withSqlCodeOption = !hasOption(OptionNameWithoutSqlCode);
        withTypeInfoCodeOption = hasOption(OptionNameWithTypeInfoCode);
        withValidationCodeOption = hasOption(OptionNameWithValidationCode);
        withWriterCodeOption = !hasOption(OptionNameWithoutWriterCode);
        warningsConfig = new WarningsConfig(
                getOptionValues(OptionNameWithWarnings),
                getOptionValues(OptionNameWithoutWarnings));
        withCrossExtensionCheckOption = !hasOption(OptionNameWithoutCrossExtensionCheck);
        withGlobalRuleIdCheckOption = hasOption(OptionNameWithGlobalRuleIdCheck);
        withCodeCommentsOption = hasOption(OptionNameWithCodeComments);
        final String topLevelPackageName = getOptionValue(OptionNameSetTopLevelPackage);
        topLevelPackageNameIds = topLevelPackageName == null
                ? new ArrayList<String>()
                : Arrays.asList(topLevelPackageName.split("\\" + TOP_LEVEL_PACKAGE_NAME_SEPARATOR));
        ignoreTimestampsOption = hasOption(OptionNameIgnoreTimestamps);
        allowImplicitArraysOption = hasOption(OptionNameAllowImplicitArrays);

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

        if (helpTopic != null && !HelpTopicWarnings.equals(helpTopic))
        {
            throw new ParseException(
                    "The specified help topic '" + helpTopic + "' does not exist!");
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

    private void printHelpWarnings()
    {
        final HelpFormatter hf = new HelpFormatter();

        final PrintWriter printWriter = new PrintWriter(
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        hf.printWrapped(printWriter, HelpFormatter.DEFAULT_WIDTH, 2,
                "Zserio Warnings Subsystem:\n" +
                "  Zserio provides possibility to configure warnings on command line. Each warning has " +
                "it's own specifier (i.e. tag) which can be used to either enable or disable the particular " +
                "warning.\n" +
                "\n" +
                "  Options -" + OptionNameWithWarnings + " and -" + OptionNameWithoutWarnings +
                " can be combined. When warnings options groups are used, more generic groups are applied " +
                "first so that it is possible to enable all warnings in a group and then disable some " +
                "smaller set of warnings or just a single warning.\n" +
                "\n");

        final WarningsConfig defaultWarningsConfig = new WarningsConfig();
        final StringBuilder warningsList = new StringBuilder("List of Warnings:\n");
        for (Map.Entry<String, WarningsConfig.WarningDefinition> entry :
                defaultWarningsConfig.getWarningsDefinition().entrySet())
        {
            warningsList.append("  ");
            warningsList.append(entry.getKey());
            warningsList.append("\n");
            warningsList.append("    " + entry.getValue().getDescription());
            if (entry.getValue().getPriority() == Integer.MAX_VALUE)
            {
                // do not print it for warnings groups
                warningsList.append(defaultWarningsConfig.isEnabled(entry.getKey()) ? " Enabled" : " Disabled");
                warningsList.append(" by default.\n");
            }
            else
            {
                warningsList.append("\n");
            }
        }

        hf.printWrapped(printWriter, HelpFormatter.DEFAULT_WIDTH, 4, warningsList.toString());
        printWriter.flush();
    }

    private void printExtensions(List<Extension> extensions)
    {
        if (extensions.isEmpty())
        {
            ZserioToolPrinter.printMessage("No extensions found!");
        }
        else
        {
            ZserioToolPrinter.printMessage("Available extensions:");
            for (Extension extension : extensions)
            {
                ZserioToolPrinter.printMessage("  " + extension.getName());
            }
        }
    }

    private static final String OptionNameHelpShort = "h";
    private static final String OptionNameSource = "src";
    private static final String OptionNameVersionShort = "v";
    private static final String OptionNameWithPubsubCode = "withPubsubCode";
    private static final String OptionNameWithoutPubsubCode = "withoutPubsubCode";
    private static final String OptionNameWithRangeCheckCode = "withRangeCheckCode";
    private static final String OptionNameWithoutRangeCheckCode = "withoutRangeCheckCode";
    private static final String OptionNameWithServiceCode = "withServiceCode";
    private static final String OptionNameWithoutServiceCode = "withoutServiceCode";
    private static final String OptionNameWithSqlCode = "withSqlCode";
    private static final String OptionNameWithoutSqlCode = "withoutSqlCode";
    private final static String OptionNameWithTypeInfoCode = "withTypeInfoCode";
    private final static String OptionNameWithoutTypeInfoCode = "withoutTypeInfoCode";
    private static final String OptionNameWithValidationCode = "withValidationCode";
    private static final String OptionNameWithoutValidationCode = "withoutValidationCode";
    private static final String OptionNameWithWriterCode = "withWriterCode";
    private static final String OptionNameWithoutWriterCode = "withoutWriterCode";
    private static final String OptionNameWithWarnings = "withWarnings";
    private static final String OptionNameWithoutWarnings = "withoutWarnings";
    private static final String OptionNameWithCrossExtensionCheck = "withCrossExtensionCheck";
    private static final String OptionNameWithoutCrossExtensionCheck = "withoutCrossExtensionCheck";
    private static final String OptionNameWithGlobalRuleIdCheck = "withGlobalRuleIdCheck";
    private static final String OptionNameWithoutGlobalRuleIdCheck = "withoutGlobalRuleIdCheck";
    private static final String OptionNameWithCodeComments = "withCodeComments";
    private static final String OptionNameWithoutCodeComments = "withoutCodeComments";
    private static final String OptionNameSetTopLevelPackage = "setTopLevelPackage";
    private static final String OptionNameIgnoreTimestamps = "ignoreTimestamps";
    private static final String OptionNameAllowImplicitArrays = "allowImplicitArrays";

    private static final String HelpTopicWarnings = "warnings";

    private static final String TOP_LEVEL_PACKAGE_NAME_SEPARATOR = ".";

    private final ZserioTool.Executor executor;
    private final Options options;

    private CommandLine parsedCommandLine;

    private String inputFileName;
    private boolean helpOption;
    private String helpTopic;
    private String srcPathName;
    private boolean versionOption;
    private boolean withPubsubCodeOption;
    private boolean withRangeCheckCodeOption;
    private boolean withServiceCodeOption;
    private boolean withSqlCodeOption;
    private boolean withTypeInfoCodeOption;
    private boolean withValidationCodeOption;
    private boolean withWriterCodeOption;
    private WarningsConfig warningsConfig;
    private boolean withCrossExtensionCheckOption;
    private boolean withGlobalRuleIdCheckOption;
    private boolean withCodeCommentsOption;
    private List<String> topLevelPackageNameIds;
    private boolean ignoreTimestampsOption;
    private boolean allowImplicitArraysOption;
}
