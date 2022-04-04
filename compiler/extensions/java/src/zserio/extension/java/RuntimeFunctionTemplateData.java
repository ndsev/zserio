package zserio.extension.java;

/**
 * Template data for generating of calls to Zserio runtime (e.g. for reading and writing of built-in types).
 */
public class RuntimeFunctionTemplateData
{
    public RuntimeFunctionTemplateData(String suffix)
    {
        this(suffix, null, null);
    }

    public RuntimeFunctionTemplateData(String suffix, String arg)
    {
        this(suffix, arg, null);
    }

    public RuntimeFunctionTemplateData(String suffix, String arg, String javaReadTypeName)
    {
        this.suffix = suffix;
        this.arg = arg;
        this.javaReadTypeName = javaReadTypeName;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public String getArg()
    {
        return arg;
    }

    public String getJavaReadTypeName()
    {
        return javaReadTypeName;
    }

    private final String suffix;
    private final String arg;
    private final String javaReadTypeName;
}
