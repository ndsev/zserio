package zserio.emit.java;

public class JavaExtensionParameters
{
    public JavaExtensionParameters(String javaOutputDir, String javaMajorVersion)
    {
        this.javaOutputDir = javaOutputDir;
        this.javaMajorVersion = javaMajorVersion;
    }

    public String getJavaOutputDir()
    {
        return javaOutputDir;
    }

    public String getJavaMajorVersion()
    {
        return javaMajorVersion;
    }

    private final String    javaOutputDir;
    private final String    javaMajorVersion;
}
