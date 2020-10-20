package zserio.emit.java;

import java.io.File;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.emit.common.DefaultEmitter;
import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

abstract class JavaDefaultEmitter extends DefaultEmitter
{
    public JavaDefaultEmitter(JavaExtensionParameters javaParameters, Parameters extensionParameters)
    {
        this.javaParameters = javaParameters;
        this.extensionParameters = extensionParameters;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        super.beginPackage(pkg);

        if (context == null)
        {
            context = new TemplateDataContext(extensionParameters, pkg.getPackageName());
        }
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

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        processTemplate(templateName, templateData, zserioType, zserioType.getName());
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outFileName) throws ZserioEmitException
    {
        processTemplate(templateName, templateData, zserioType.getPackage(), outFileName);
    }

    protected void processTemplate(String templateName, Object templateData, Package zserioPackage,
            String outFileName) throws ZserioEmitException
    {
        processTemplate(templateName, templateData, zserioPackage.getPackageName(), outFileName);
    }

    private void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot) throws ZserioEmitException
    {
        final File outDir = new File(javaParameters.getJavaOutputDir(), packageName.toFilesystemPath());
        final File outputFile = new File(outDir, outFileNameRoot + JAVA_SOURCE_EXTENSION);
        FreeMarkerUtil.processTemplate(JAVA_TEMPLATE_LOCATION + templateName, templateData, outputFile, false);
    }

    private static final String JAVA_SOURCE_EXTENSION = ".java";
    private static final String JAVA_TEMPLATE_LOCATION = "java/";

    private final JavaExtensionParameters javaParameters;
    private final Parameters extensionParameters;

    private TemplateDataContext context = null;
}
