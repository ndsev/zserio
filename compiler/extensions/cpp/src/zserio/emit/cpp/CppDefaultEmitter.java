package zserio.emit.cpp;

import zserio.ast.ZserioType;
import zserio.emit.common.CodeDefaultEmitter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

// TODO Don't use CodeDefaultEmitter and use DefaultEmitter only
abstract class CppDefaultEmitter extends CodeDefaultEmitter
{
    public CppDefaultEmitter(String outPathName, Parameters extensionParameters)
    {
        super(outPathName, extensionParameters, CPP_TEMPLATE_LOCATION);

        this.extensionParameters = extensionParameters;
    }

    protected void processSourceTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, CPP_SOURCE_EXTENSION, true);
    }

    protected void processHeaderTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, CPP_HEADER_EXTENSION, false);
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return new TemplateDataContext(extensionParameters, getPackageMapper());
    }

    private static final String CPP_SOURCE_EXTENSION = ".cpp";
    private static final String CPP_HEADER_EXTENSION = ".h";
    private static final String CPP_TEMPLATE_LOCATION = "cpp/";

    private final Parameters extensionParameters;
}
