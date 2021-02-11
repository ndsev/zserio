package zserio.extension.java;

public class JavaTemplateData
{
    public JavaTemplateData(TemplateDataContext context)
    {
        generatorDescription = context.getGeneratorDescription();
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    private final String generatorDescription;
}
