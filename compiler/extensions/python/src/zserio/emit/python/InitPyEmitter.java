package zserio.emit.python;

import zserio.ast.EnumType;
import zserio.ast.ZserioType;
import zserio.ast.Package;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class InitPyEmitter extends PythonDefaultEmitter
{
    public InitPyEmitter (String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginPackage(Package packageToken) throws ZserioEmitException
    {
        processAmalgamatedTemplate(INIT_PY_TEMPLATE, new InitPyEmitterTemplateData(), packageToken,
                INIT_PY_FILENAME_ROOT);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        processInitPyTemplate(enumType);
    }

    protected void processInitPyTemplate(ZserioType zserioType) throws ZserioEmitException
    {
        super.processAmalgamatedTemplate(INIT_PY_TEMPLATE, new InitPyEmitterTemplateData(zserioType),
                zserioType.getPackage(), INIT_PY_FILENAME_ROOT);
    }

    private static final String INIT_PY_TEMPLATE = "__init__.py.ftl";
    private static final String INIT_PY_FILENAME_ROOT = "__init__";
}
