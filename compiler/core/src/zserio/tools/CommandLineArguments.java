package zserio.tools;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;

/**
 * The class to handle all command line arguments of Zserio Tool.
 */
class CommandLineArguments implements Serializable
{
    /**
     * Empty constructor.
     */
    public CommandLineArguments()
    {
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
     * Gets whether the GRPC code option is enabled.
     *
     * @returns True if command line arguments enable SQL code option.
     */
    public boolean getWithGrpcCode()
    {
        return withGrpcCodeOption;
    }

    /**
     * Gets whether the inspector code option is enabled.
     *
     * @returns True if command line arguments enable inspector code option.
     */
    public boolean getWithInspectorCode()
    {
        return withInspectorCodeOption;
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
     * Gets the top level package name.
     *
     * @returns Top level package name or null if not specified.
     */
    public String getTopLevelPackageName()
    {
        return topLevelPackageName;
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
        final HelpFormatter hf = new HelpFormatter();

        hf.setSyntaxPrefix("Usage: ");
        hf.setLeftPadding(2);
        hf.setOptionComparator(null);
        hf.printHelp("java -jar zserio.jar <options> zserioInputFile\n", "Options:", options, null, false);
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

        final OptionGroup grpcCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithGrpcCode, false, "enable code for GRPC services (default)");
        grpcCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutGrpcCode, false, "disable code for GRPC services");
        grpcCodeGroup.addOption(option);
        grpcCodeGroup.setRequired(false);
        options.addOptionGroup(grpcCodeGroup);

        // Blob Inspector interface has been DISABLED
        // final OptionGroup inspectorCodeGroup = new OptionGroup();
        // option = new Option(OptionNameWithInspectorCode, false, "enable code for Blob Inspector");
        // inspectorCodeGroup.addOption(option);
        // option = new Option(OptionNameWithoutInspectorCode, false, "disable code for Blob Inspector (default)");
        // inspectorCodeGroup.addOption(option);
        // inspectorCodeGroup.setRequired(false);
        // options.addOptionGroup(inspectorCodeGroup);

        final OptionGroup rangeCheckCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithRangeCheckCode, false,
                "enable code for integer range checking for field and parameter setters");
        rangeCheckCodeGroup.addOption(option);
        option = new Option(OptionNameWithoutRangeCheckCode, false,
                "disable code for integer range checking for field and parameter setters (default)");
        rangeCheckCodeGroup.addOption(option);
        rangeCheckCodeGroup.setRequired(false);
        options.addOptionGroup(rangeCheckCodeGroup);

        final OptionGroup soucresAmalgamationGroup = new OptionGroup();
        option = new Option(OptionNameWithSourcesAmalgamation, false,
                            "enable amalgamation of generated C++ sources (default)");
        soucresAmalgamationGroup.addOption(option);
        option = new Option(OptionNameWithoutSourcesAmalgamation, false,
                            "disable amalgamation of generated C++ sources");
        soucresAmalgamationGroup.addOption(option);
        soucresAmalgamationGroup.setRequired(false);
        options.addOptionGroup(soucresAmalgamationGroup);

        final OptionGroup sqlCodeGroup = new OptionGroup();
        option = new Option(OptionNameWithSqlCode, false, "enable code for relational (SQLite) parts (default)");
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

        option = new Option(OptionNameSetTopLevelPackage, true,
                            "set top level package/namespace to use for generated Java/C++ code");
        option.setArgName("packageName");
        option.setRequired(false);
        options.addOption(option);
    }

    private void readOptions() throws ParseException
    {
        validateOptions();

        srcPathName = getOptionValue(OptionNameSource);
        helpOption = hasOption(OptionNameHelpShort);
        versionOption = hasOption(OptionNameVersionShort);
        topLevelPackageName = getOptionValue(OptionNameSetTopLevelPackage);

        withGrpcCodeOption = !hasOption(OptionNameWithoutGrpcCode) && !hasOption(OptionNameWithoutWriterCode);
        withInspectorCodeOption = hasOption(OptionNameWithInspectorCode);
        withRangeCheckCodeOption = hasOption(OptionNameWithRangeCheckCode);
        withSourcesAmalgamationOption = !hasOption(OptionNameWithoutSourcesAmalgamation);
        withSqlCodeOption = !hasOption(OptionNameWithoutSqlCode);
        withValidationCodeOption = hasOption(OptionNameWithValidationCode);
        withWriterCodeOption = !hasOption(OptionNameWithoutWriterCode);

        withUnusedWarningsOption = hasOption(OptionNameWithUnusedWarnings);
    }

    private void validateOptions() throws ParseException
    {
        // check explicitly specified conflicting options
        if (hasOption(OptionNameWithoutWriterCode))
        {
            if (hasOption(OptionNameWithInspectorCode))
            {
                throw new ParseException(
                        "The specified option 'withInspectorCode' conflicts with another option: " +
                        "'withoutWriterCode'");
            }
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
            if (hasOption(OptionNameWithGrpcCode))
            {
                throw new ParseException(
                        "The specified option 'withGrpcCode' conflicts with another option: " +
                        "'withoutWriterCode'");
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

    private static final long serialVersionUID = -1L;

    private static final String OptionNameHelpShort = "h";
    private static final String OptionNameSource = "src";
    private static final String OptionNameVersionShort = "v";
    private static final String OptionNameSetTopLevelPackage = "setTopLevelPackage";
    private static final String OptionNameWithGrpcCode = "withGrpcCode";
    private static final String OptionNameWithoutGrpcCode = "withoutGrpcCode";
    private static final String OptionNameWithInspectorCode = "withInspectorCode";
    private static final String OptionNameWithoutInspectorCode = "withoutInspectorCode";
    private static final String OptionNameWithRangeCheckCode = "withRangeCheckCode";
    private static final String OptionNameWithoutRangeCheckCode = "withoutRangeCheckCode";
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

    private final Options options;
    private CommandLine parsedCommandLine;

    private String  inputFileName;
    private boolean helpOption;
    private String  srcPathName;
    private String  topLevelPackageName;
    private boolean versionOption;
    private boolean withGrpcCodeOption;
    private boolean withInspectorCodeOption;
    private boolean withRangeCheckCodeOption;
    private boolean withSourcesAmalgamationOption;
    private boolean withSqlCodeOption;
    private boolean withValidationCodeOption;
    private boolean withWriterCodeOption;
    private boolean withUnusedWarningsOption;
}
