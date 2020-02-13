package zserio.emit.java;

public class JavaTemplateData
{
    public JavaTemplateData(TemplateDataContext context)
    {
        generatorDescription = "Zserio Java extension version " + JavaExtensionVersion.VERSION_STRING;
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    private final String generatorDescription;
}
