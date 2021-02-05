package zserio.extension.java;

import java.io.File;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.FreeMarkerUtil;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;

abstract class JavaDefaultEmitter extends DefaultTreeWalker
{
    public JavaDefaultEmitter(OutputFileManager outputFileManager, JavaExtensionParameters javaParameters)
    {
        this.outputFileManager = outputFileManager;
        this.javaParameters = javaParameters;
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioExtensionException
    {
        super.beginPackage(pkg);

        if (context == null)
        {
            context = new TemplateDataContext(javaParameters, pkg.getPackageName());
        }
    }

    protected boolean getWithPubsubCode()
    {
        return javaParameters.getWithPubsubCode();
    }

    protected boolean getWithServiceCode()
    {
        return javaParameters.getWithServiceCode();
    }

    protected boolean getWithSqlCode()
    {
        return javaParameters.getWithSqlCode();
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return context;
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioExtensionException
    {
        processTemplate(templateName, templateData, zserioType, zserioType.getName());
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outFileName) throws ZserioExtensionException
    {
        processTemplate(templateName, templateData, zserioType.getPackage(), outFileName);
    }

    protected void processTemplate(String templateName, Object templateData, Package zserioPackage,
            String outFileName) throws ZserioExtensionException
    {
        processTemplate(templateName, templateData, zserioPackage.getPackageName(), outFileName);
    }

    private void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot) throws ZserioExtensionException
    {
        final File outDir = new File(javaParameters.getOutputDir(), packageName.toFilesystemPath());
        final File outputFile = new File(outDir, outFileNameRoot + JAVA_SOURCE_EXTENSION);
        if (outputFileManager.addOutputFile(outputFile))
        {
            FreeMarkerUtil.processTemplate(JAVA_TEMPLATE_LOCATION + templateName, templateData, outputFile,
                    false);
        }
    }

    private static final String JAVA_SOURCE_EXTENSION = ".java";
    private static final String JAVA_TEMPLATE_LOCATION = "java/";

    private final OutputFileManager outputFileManager;
    private final JavaExtensionParameters javaParameters;

    private TemplateDataContext context = null;
}
