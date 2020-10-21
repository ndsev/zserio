package zserio.emit.python;

import java.io.File;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.emit.common.DefaultTreeWalker;
import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

abstract class PythonDefaultEmitter extends DefaultTreeWalker
{
    public PythonDefaultEmitter(String outputPathName, Parameters extensionParameters)
    {
        this.outputPathName = outputPathName;
        this.extensionParameters = extensionParameters;
        this.context = new TemplateDataContext(extensionParameters);
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    protected boolean getWithPubsubCode()
    {
        return extensionParameters.getWithPubsubCode();
    }

    protected boolean getWithServiceCode()
    {
        return extensionParameters.getWithServiceCode();
    }

    protected boolean getWithSqlCode()
    {
        return extensionParameters.getWithSqlCode();
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return context;
    }

    protected void processSourceTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        processSourceTemplate(templateName, templateData, zserioType.getPackage(), zserioType.getName());
    }

    protected void processSourceTemplate(String templateName, Object templateData,
            Package zserioPackage, String outFileName) throws ZserioEmitException
    {
        processTemplate(templateName, templateData, zserioPackage.getPackageName(), outFileName);
    }

    protected void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot) throws ZserioEmitException
    {
        final File outDir = new File(outputPathName, packageName.toFilesystemPath());
        final File outputFile = new File(outDir, outFileNameRoot + PYTHON_SOURCE_EXTENSION);
        FreeMarkerUtil.processTemplate(PYTHON_TEMPLATE_LOCATION + templateName, templateData, outputFile,
                false);
    }

    private static final String PYTHON_SOURCE_EXTENSION = ".py";
    private static final String PYTHON_TEMPLATE_LOCATION = "python/";

    private final String outputPathName;
    private final Parameters extensionParameters;
    private final TemplateDataContext context;
}
