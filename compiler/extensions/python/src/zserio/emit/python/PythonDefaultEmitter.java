package zserio.emit.python;

import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.emit.common.CodeDefaultEmitter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

abstract class PythonDefaultEmitter extends CodeDefaultEmitter
{
    public PythonDefaultEmitter(String outputPathName, Parameters extensionParameters)
    {
        super(outputPathName, extensionParameters, PYTHON_TEMPLATE_LOCATION);

        this.extensionParameters = extensionParameters;
    }

    protected void processSourceTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, PYTHON_SOURCE_EXTENSION, false);
    }

    protected void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot) throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, packageName, outFileNameRoot,
                PYTHON_SOURCE_EXTENSION, false);
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return new TemplateDataContext(extensionParameters, getPackageMapper());
    }

    private static final String PYTHON_SOURCE_EXTENSION = ".py";
    private static final String PYTHON_TEMPLATE_LOCATION = "python/";

    private final Parameters extensionParameters;
}
