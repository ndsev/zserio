package zserio.extension.cpp;

import java.util.StringJoiner;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

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
        withSourcesAmalgamation = parameters.getWithSourcesAmalgamation();
        allocatorDefinition = parameters.argumentExists(OptionUsePolymorphicAllocator)
                ? TypesContext.PROPAGATING_POLYMORPHIC_ALLOCATOR
                : TypesContext.STD_ALLOCATOR;

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
        if (withSourcesAmalgamation)
            description.add("sourcesAmalgamation");
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

    public boolean getWithSourcesAmalgamation()
    {
        return withSourcesAmalgamation;
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

        final OptionGroup allocatorGroup = new OptionGroup();
        option = new Option(OptionUseStdAllocator, false,
                "use std::allocator<uint8_t> in generated C++ code (default)");
        allocatorGroup.addOption(option);
        option = new Option(OptionUsePolymorphicAllocator, false,
                "use zserio::pmr::PropagatingPolymorphicAllocatro<> in generated C++ code");
        allocatorGroup.addOption(option);
        allocatorGroup.setRequired(false);
        options.addOptionGroup(allocatorGroup);
    }

    static boolean hasOptionCpp(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionCpp);
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
    private final static String OptionUseStdAllocator = "useStdAllocator";
    private final static String OptionUsePolymorphicAllocator = "usePolymorphicAllocator";

    private final String outputDir;
    private final boolean withWriterCode;
    private final boolean withPubsubCode;
    private final boolean withServiceCode;
    private final boolean withSqlCode;
    private final boolean withValidationCode;
    private final boolean withRangeCheckCode;
    private final boolean withSourcesAmalgamation;
    private final TypesContext.AllocatorDefinition allocatorDefinition;
    private final String parametersDescription;
}
