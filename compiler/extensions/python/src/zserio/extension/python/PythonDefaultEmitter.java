package zserio.extension.python;

import java.io.File;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.FreeMarkerUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

abstract class PythonDefaultEmitter extends DefaultTreeWalker
{
    public PythonDefaultEmitter(String outputPathName, ExtensionParameters extensionParameters)
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
            throws ZserioExtensionException
    {
        processSourceTemplate(templateName, templateData, zserioType.getPackage(), zserioType.getName());
    }

    protected void processSourceTemplate(String templateName, Object templateData,
            Package zserioPackage, String outFileName) throws ZserioExtensionException
    {
        processTemplate(templateName, templateData, zserioPackage.getPackageName(), outFileName);
    }

    protected void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot) throws ZserioExtensionException
    {
        final File outDir = new File(outputPathName, packageName.toFilesystemPath());
        final File outputFile = new File(outDir, outFileNameRoot + PYTHON_SOURCE_EXTENSION);
        FreeMarkerUtil.processTemplate(PYTHON_TEMPLATE_LOCATION + templateName, templateData, outputFile,
                false);
    }

    private static final String PYTHON_SOURCE_EXTENSION = ".py";
    private static final String PYTHON_TEMPLATE_LOCATION = "python/";

    private final String outputPathName;
    private final ExtensionParameters extensionParameters;
    private final TemplateDataContext context;
}
