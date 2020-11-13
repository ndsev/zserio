package zserio.extension.java;

public class JavaExtensionParameters
{
    public JavaExtensionParameters(String javaOutputDir)
    {
        this.javaOutputDir = javaOutputDir;
    }

    public String getJavaOutputDir()
    {
        return javaOutputDir;
    }

    private final String javaOutputDir;
}
