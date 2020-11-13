package zserio.extension.python;

public class RuntimeFunctionTemplateData
{
    RuntimeFunctionTemplateData(String suffix)
    {
        this(suffix, null);
    }

    RuntimeFunctionTemplateData(String suffix, String arg)
    {
        this.suffix = suffix;
        this.arg = arg;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public String getArg()
    {
        return arg;
    }

    private final String suffix;
    private final String arg;
}
