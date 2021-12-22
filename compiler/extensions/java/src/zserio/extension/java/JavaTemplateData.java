package zserio.extension.java;

public class JavaTemplateData
{
    public JavaTemplateData(TemplateDataContext context)
    {
        generatorDescription = context.getGeneratorDescription();
        withWriterCode = context.getWithWriterCode();
        withTypeInfoCode = context.getWithTypeInfoCode();
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

    private final String generatorDescription;
    private final boolean withWriterCode;
    private final boolean withTypeInfoCode;
}
