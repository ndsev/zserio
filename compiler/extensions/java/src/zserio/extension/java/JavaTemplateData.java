package zserio.extension.java;

/**
 * Base class for all Java template data for FreeMarker.
 */
public class JavaTemplateData
{
    public JavaTemplateData(TemplateDataContext context)
    {
        generatorDescription = context.getGeneratorDescription();
        withWriterCode = context.getWithWriterCode();
        withTypeInfoCode = context.getWithTypeInfoCode();
        withCodeComments = context.getWithCodeComments();
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithTypeInfoCode()
    {
        return withTypeInfoCode;
    }

    public boolean getWithCodeComments()
    {
        return withCodeComments;
    }

    private final String generatorDescription;
    private final boolean withWriterCode;
    private final boolean withTypeInfoCode;
    private final boolean withCodeComments;
}
