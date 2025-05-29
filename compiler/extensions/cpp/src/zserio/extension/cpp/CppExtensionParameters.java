package zserio.extension.cpp;

import java.util.StringJoiner;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

/**
 * Command line parameters for C++ extension.
 *
 * The class holds all command line parameters passed by core to the C++ extension, which are really
 * used by C++ emitters.
 */
public final class CppExtensionParameters
{
    public CppExtensionParameters(ExtensionParameters parameters) throws ZserioExtensionException
    {
        check(parameters);

        outputDir = parameters.getCommandLineArg(OptionCpp);
        withWriterCode = parameters.getWithWriterCode();
        withPubsubCode = parameters.getWithPubsubCode();
        withServiceCode = parameters.getWithServiceCode();
        withSqlCode = parameters.getWithSqlCode();
        withValidationCode = parameters.getWithValidationCode();
        withRangeCheckCode = parameters.getWithRangeCheckCode();
        withTypeInfoCode = parameters.getWithTypeInfoCode();
        withReflectionCode = parameters.argumentExists(OptionWithReflectionCode);
        withSourcesAmalgamation = !parameters.argumentExists(OptionWithoutSourcesAmalgamation);
        withCodeComments = parameters.getWithCodeComments();
        withParsingInfoCode = parameters.argumentExists(OptionWithParsingInfoCode);

        final String cppAllocator = parameters.getCommandLineArg(OptionSetCppAllocator);
        if (cppAllocator == null || cppAllocator.equals(StdAllocator))
            allocatorDefinition = TypesContext.STD_ALLOCATOR;
        else
            allocatorDefinition = TypesContext.PROPAGATING_POLYMORPHIC_ALLOCATOR;

        if (withWriterCode)
            withSettersCode = true;
        else
            withSettersCode = parameters.argumentExists(OptionWithSettersCode);

        final StringJoiner description = new StringJoiner(", ");
        if (withWriterCode)
            description.add("writerCode");
        if (withSettersCode)
            description.add("settersCode");
        if (withPubsubCode)
            description.add("pubsubCode");
        if (withServiceCode)
            description.add("serviceCode");
        if (withSqlCode)
            description.add("sqlCode");
        if (withValidationCode)
            description.add("validationCode");
        if (withRangeCheckCode)
            description.add("rangeCheckCode");
        if (withTypeInfoCode)
            description.add("typeInfoCode");
        if (withReflectionCode)
            description.add("reflectionCode");
        if (withSourcesAmalgamation)
            description.add("sourcesAmalgamation");
        if (withCodeComments)
            description.add("codeComments");
        if (withParsingInfoCode)
            description.add("parsingInfoCode");
        addAllocatorDescription(description);
        parametersDescription = description.toString();

        zserioVersion = parameters.getZserioVersion();
    }

    public String getOutputDir()
    {
        return outputDir;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithSettersCode()
    {
        return withSettersCode;
    }

    public boolean getWithPubsubCode()
    {
        return withPubsubCode;
    }

    public boolean getWithServiceCode()
    {
        return withServiceCode;
    }

    public boolean getWithSqlCode()
    {
        return withSqlCode;
    }

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    public boolean getWithTypeInfoCode()
    {
        return withTypeInfoCode;
    }

    public boolean getWithReflectionCode()
    {
        return withReflectionCode;
    }

    public boolean getWithSourcesAmalgamation()
    {
        return withSourcesAmalgamation;
    }

    public boolean getWithCodeComments()
    {
        return withCodeComments;
    }

    public boolean getWithParsingInfoCode()
    {
        return withParsingInfoCode;
    }

    public TypesContext.AllocatorDefinition getAllocatorDefinition()
    {
        return allocatorDefinition;
    }

    public String getParametersDescription()
    {
        return parametersDescription;
    }

    public String getZserioVersion()
    {
        return zserioVersion;
    }

    static void registerOptions(Options options)
    {
        Option option = new Option(OptionCpp, true, "generate C++ sources");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(OptionSetCppAllocator, true,
                "set the C++ allocator to be used in generated code: std (default), polymorphic");
        option.setArgName("allocator");
        option.setRequired(false);
        options.addOption(option);

        final OptionGroup reflectionGroup = new OptionGroup();
        option = new Option(OptionWithReflectionCode, false, "enable reflection code");
        reflectionGroup.addOption(option);
        option = new Option(OptionWithoutReflectionCode, false, "disable reflection code (default)");
        reflectionGroup.addOption(option);
        reflectionGroup.setRequired(false);
        options.addOptionGroup(reflectionGroup);

        final OptionGroup sourcesAmalgamationGroup = new OptionGroup();
        option = new Option(
                OptionWithSourcesAmalgamation, false, "enable amalgamation of generated C++ sources (default)");
        sourcesAmalgamationGroup.addOption(option);
        option = new Option(
                OptionWithoutSourcesAmalgamation, false, "disable amalgamation of generated C++ sources");
        sourcesAmalgamationGroup.addOption(option);
        sourcesAmalgamationGroup.setRequired(false);
        options.addOptionGroup(sourcesAmalgamationGroup);

        final OptionGroup bitPositionGroup = new OptionGroup();
        bitPositionGroup.addOption(new Option(
                OptionWithParsingInfoCode, false, "enable parsing info code (experimental, not part of API)"));
        bitPositionGroup.addOption(
                new Option(OptionWithoutParsingInfoCode, false, "disable parsing info code (default)"));
        bitPositionGroup.setRequired(false);
        options.addOptionGroup(bitPositionGroup);

        final OptionGroup settersGroup = new OptionGroup();
        settersGroup.addOption(
                new Option(OptionWithSettersCode, false, "enable writing setters code (default)"));
        settersGroup.addOption(new Option(OptionWithoutSettersCode, false, "disable writing setters code"));
        settersGroup.setRequired(false);
        options.addOptionGroup(settersGroup);
    }

    static boolean hasOptionCpp(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionCpp);
    }

    private static void check(ExtensionParameters parameters) throws ZserioExtensionException
    {
        final String cppAllocator = parameters.getCommandLineArg(OptionSetCppAllocator);
        if (cppAllocator != null && !cppAllocator.equals(StdAllocator) &&
                !cppAllocator.equals(PolymorphicAllocator))
        {
            throw new ZserioExtensionException("The specified option '" + OptionSetCppAllocator + "' has "
                    + "unknown allocator '" + cppAllocator + "'!");
        }

        final boolean withReflectionCode = parameters.argumentExists(OptionWithReflectionCode);
        if (withReflectionCode)
        {
            if (parameters.getWithTypeInfoCode() == false)
            {
                throw new ZserioExtensionException("The specified option '" + OptionWithReflectionCode +
                        "' needs enabled type info code ('withTypeInfoCode')!");
            }
        }
    }

    private void addAllocatorDescription(StringJoiner description)
    {
        if (allocatorDefinition == TypesContext.STD_ALLOCATOR)
        {
            description.add(StdAllocator + "Allocator");
        }
        else if (allocatorDefinition == TypesContext.PROPAGATING_POLYMORPHIC_ALLOCATOR)
        {
            description.add(PolymorphicAllocator + "Allocator");
        }
        else
        {
            description.add("customAllocator(" + allocatorDefinition.getAllocatorType() + ", " +
                    allocatorDefinition.getAllocatorSystemInclude() + ")");
        }
    }

    private final static String OptionCpp = "cpp";
    private final static String OptionSetCppAllocator = "setCppAllocator";
    private final static String OptionWithoutReflectionCode = "withoutReflectionCode";
    private static final String OptionWithReflectionCode = "withReflectionCode";
    private static final String OptionWithSourcesAmalgamation = "withSourcesAmalgamation";
    private static final String OptionWithoutSourcesAmalgamation = "withoutSourcesAmalgamation";
    private static final String OptionWithParsingInfoCode = "withParsingInfoCode";
    private static final String OptionWithoutParsingInfoCode = "withoutParsingInfoCode";
    private static final String OptionWithSettersCode = "withSettersCode";
    private static final String OptionWithoutSettersCode = "withoutSettersCode";

    private final static String StdAllocator = "std";
    private final static String PolymorphicAllocator = "polymorphic";

    private final String outputDir;
    private final boolean withWriterCode;
    private final boolean withSettersCode;
    private final boolean withPubsubCode;
    private final boolean withServiceCode;
    private final boolean withSqlCode;
    private final boolean withTypeInfoCode;
    private final boolean withReflectionCode;
    private final boolean withValidationCode;
    private final boolean withRangeCheckCode;
    private final boolean withSourcesAmalgamation;
    private final boolean withCodeComments;
    private final boolean withParsingInfoCode;
    private final TypesContext.AllocatorDefinition allocatorDefinition;
    private final String parametersDescription;
    private final String zserioVersion;
}
