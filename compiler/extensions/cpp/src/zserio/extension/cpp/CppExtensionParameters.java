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

        final String compoundParameterTreshold = parameters.getCommandLineArg(OptionSetCompoundParameterTreshold);
        if (compoundParameterTreshold != null)
        {
            this.compoundParameterTreshold = Integer.parseInt(compoundParameterTreshold);
        }
        else
        {
            this.compoundParameterTreshold = DefaultCompoundParameterTreshold;
        }

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

    public int getCompoundParameterTreshold()
    {
        return compoundParameterTreshold;
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

        option = new Option(OptionSetCompoundParameterTreshold, true,
                "set the size treshold in bytes to store compound parameters as shared pointers (default is 8)");
        option.setArgName("size");
        option.setRequired(false);
        options.addOption(option);
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

        final String compoundParameterTreshold = parameters.getCommandLineArg(OptionSetCompoundParameterTreshold);
        if (compoundParameterTreshold != null)
        {
            try
            {
                Integer.parseInt(compoundParameterTreshold);
            }
            catch (NumberFormatException e)
            {
                throw new ZserioExtensionException("The specified option '" + OptionSetCompoundParameterTreshold
                        + "' has invalid value (" + e.getMessage() + ")!");
            }
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
    private static final String OptionSetCompoundParameterTreshold = "setCompoundParameterTreshold";
    private final static String OptionWithoutReflectionCode = "withoutReflectionCode";
    private static final String OptionWithReflectionCode = "withReflectionCode";
    private static final String OptionWithSourcesAmalgamation = "withSourcesAmalgamation";
    private static final String OptionWithoutSourcesAmalgamation = "withoutSourcesAmalgamation";

    private final static String StdAllocator = "std";
    private final static String PolymorphicAllocator = "polymorphic";

    private final static int DefaultCompoundParameterTreshold = 8;

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
    private final int compoundParameterTreshold;
    private final String parametersDescription;
    private final String zserioVersion;
}
