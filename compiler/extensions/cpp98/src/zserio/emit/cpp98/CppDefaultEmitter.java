package zserio.emit.cpp98;

import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.emit.common.CodeDefaultEmitter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

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

    protected void processHeaderTemplate(String templateName, Object templateData,
            PackageName zserioPackageName, String outFileName) throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, getPackageMapper().getPackageName(zserioPackageName),
                outFileName, CPP_HEADER_EXTENSION, false);
    }

    protected void processSourceTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outFileName) throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, outFileName, CPP_SOURCE_EXTENSION, true);
    }

    protected void processHeaderTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outFileName) throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, outFileName, CPP_HEADER_EXTENSION, false);
    }

    protected void processSourceTemplateToRootDir(String templateName, Object templateData,
            String outFileNameRoot) throws ZserioEmitException
    {
        super.processTemplateToRootDir(templateName, templateData, outFileNameRoot, CPP_SOURCE_EXTENSION, true);
    }

    protected void processHeaderTemplateToRootDir(String templateName, Object templateData,
            String outFileNameRoot) throws ZserioEmitException
    {
        super.processTemplateToRootDir(templateName, templateData, outFileNameRoot, CPP_HEADER_EXTENSION,
                false);
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return new TemplateDataContext(extensionParameters, getPackageMapper());
    }

    private static final String CPP_SOURCE_EXTENSION = ".cpp";
    private static final String CPP_HEADER_EXTENSION = ".h";
    private static final String CPP_TEMPLATE_LOCATION = "cpp98/";

    private final Parameters extensionParameters;
}
