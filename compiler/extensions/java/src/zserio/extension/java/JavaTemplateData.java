package zserio.extension.java;

public class JavaTemplateData
{
    public JavaTemplateData(TemplateDataContext context)
    {
        generatorDescription = context.getGeneratorDescription();
        withWriterCode = context.getWithWriterCode();
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    private final String generatorDescription;
    private final boolean withWriterCode;
}
