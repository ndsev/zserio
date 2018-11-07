package zserio.emit.python;

import zserio.ast.ZserioType;

public class InitPyEmitterTemplateData extends PythonTemplateData
{
    public InitPyEmitterTemplateData()
    {
        this.printHeader = true;
        this.name = null;
    }

    public InitPyEmitterTemplateData(ZserioType type)
    {
        this.printHeader = false;
        this.name = type.getName();
    }

    public boolean getPrintHeader()
    {
        return printHeader;
    }

    public String getName()
    {
        return name;
    }

    private final boolean printHeader;
    private final String name;
}