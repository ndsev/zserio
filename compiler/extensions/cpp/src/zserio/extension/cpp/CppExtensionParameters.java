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
public class CppExtensionParameters
{
    public CppExtensionParameters(ExtensionParameters parameters)
    {
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

        final String cppAllocator = parameters.getCommandLineArg(OptionSetCppAllocator);
        if (cppAllocator == null || cppAllocator.equals(StdAllocator))
            allocatorDefinition = TypesContext.STD_ALLOCATOR;
        else
            allocatorDefinition = TypesContext.PROPAGATING_POLYMORPHIC_ALLOCATOR;

        final StringJoiner description = new StringJoiner(", ");
        if (withWriterCode)
            description.add("writerCode");
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
        addAllocatorDescription(description);
        parametersDescription = description.toString();
    }

    public String getOutputDir()
    {
        return outputDir;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
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

    public TypesContext.AllocatorDefinition getAllocatorDefinition()
    {
        return allocatorDefinition;
    }

    public String getParametersDescription()
    {
        return parametersDescription;
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
        option = new Option(OptionWithSourcesAmalgamation, false,
                            "enable amalgamation of generated C++ sources (default)");
        sourcesAmalgamationGroup.addOption(option);
        option = new Option(OptionWithoutSourcesAmalgamation, false,
                            "disable amalgamation of generated C++ sources");
        sourcesAmalgamationGroup.addOption(option);
        sourcesAmalgamationGroup.setRequired(false);
        options.addOptionGroup(sourcesAmalgamationGroup);
    }

    static boolean hasOptionCpp(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionCpp);
    }

    static void check(ExtensionParameters parameters) throws ZserioExtensionException
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
            description.add("stdAllocator");
        }
        else if (allocatorDefinition == TypesContext.PROPAGATING_POLYMORPHIC_ALLOCATOR)
        {
            description.add("polymorhpicAllocator");
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

    private final static String StdAllocator = "std";
    private final static String PolymorphicAllocator = "polymorphic";

    private final String outputDir;
    private final boolean withWriterCode;
    private final boolean withPubsubCode;
    private final boolean withServiceCode;
    private final boolean withSqlCode;
    private final boolean withTypeInfoCode;
    private final boolean withReflectionCode;
    private final boolean withValidationCode;
    private final boolean withRangeCheckCode;
    private final boolean withSourcesAmalgamation;
    private final boolean withCodeComments;
    private final TypesContext.AllocatorDefinition allocatorDefinition;
    private final String parametersDescription;
}
