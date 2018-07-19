package zserio.emit.java;

public class JavaTemplateData
{
    public JavaTemplateData(TemplateDataContext context)
    {
        generatorDescription = "Zserio Java extension version " + JavaExtensionVersion.VERSION_STRING;

        javaMajorVersion = context.getJavaMajorVersion();
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    public String getJavaMajorVersion()
    {
        return javaMajorVersion;
    }

    private final String generatorDescription;
    private final String javaMajorVersion;
}
