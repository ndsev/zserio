package zserio.emit.python;

import zserio.ast.ZserioType;
import zserio.ast.Package;
import zserio.emit.common.CodeDefaultEmitter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

abstract class PythonDefaultEmitter extends CodeDefaultEmitter
{
    public PythonDefaultEmitter(String outputPathName, Parameters extensionParameters)
    {
        super(outputPathName, extensionParameters, PYTHON_TEMPLATE_LOCATION, ".");

        this.extensionParameters = extensionParameters;
    }

    protected void processSourceTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, PYTHON_SOURCE_EXTENSION, false);
    }

    protected void processAmalgamatedTemplate(String templateName, Object templateData, Package packageToken,
            String outputFilename) throws ZserioEmitException
    {
        super.processAmalgamatedTemplate(templateName, templateData, packageToken, outputFilename,
                PYTHON_SOURCE_EXTENSION);
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return new TemplateDataContext(extensionParameters, getPackageMapper());
    }

    private static final String PYTHON_SOURCE_EXTENSION = ".py";
    private static final String PYTHON_TEMPLATE_LOCATION = "python/";

    private final Parameters extensionParameters;
}
