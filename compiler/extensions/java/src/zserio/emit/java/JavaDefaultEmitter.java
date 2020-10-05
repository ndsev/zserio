package zserio.emit.java;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.emit.common.CodeDefaultEmitter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

abstract class JavaDefaultEmitter extends CodeDefaultEmitter
{
    public JavaDefaultEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(javaParameters.getJavaOutputDir(), extensionParameters, JAVA_TEMPLATE_LOCATION);

        this.extensionParameters = extensionParameters;
        this.javaParameters = javaParameters;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        super.beginPackage(pkg);

        if (context == null)
        {
            context = new TemplateDataContext(extensionParameters, javaParameters,
                    getPackageMapper().getPackageName(pkg), getPackageMapper());
        }
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, JAVA_SOURCE_EXTENSION, false);
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outFileName) throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, outFileName, JAVA_SOURCE_EXTENSION,
                false);
    }

    protected void processTemplate(String templateName, Object templateData, PackageName zserioPackageName,
            String outFileName) throws ZserioEmitException
    {
        super.processTemplate(templateName,  templateData, getPackageMapper().getPackageName(zserioPackageName),
                outFileName, JAVA_SOURCE_EXTENSION, false);
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return context;
    }

    private static final String JAVA_SOURCE_EXTENSION = ".java";
    private static final String JAVA_TEMPLATE_LOCATION = "java/";

    private final Parameters extensionParameters;
    private final JavaExtensionParameters javaParameters;

    private TemplateDataContext context = null;
}
