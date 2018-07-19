package zserio.emit.java;

public class RuntimeFunctionTemplateData
{
    RuntimeFunctionTemplateData(String suffix)
    {
        this(suffix, null, null);
    }

    RuntimeFunctionTemplateData(String suffix, String arg, String javaReadTypeName)
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

    private final String    suffix;
    private final String    arg;
    private final String    javaReadTypeName;
}
