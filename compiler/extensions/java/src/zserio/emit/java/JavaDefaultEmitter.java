package zserio.emit.java;

import zserio.ast.ZserioType;
import zserio.emit.common.CodeDefaultEmitter;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

abstract class JavaDefaultEmitter extends CodeDefaultEmitter
{
    public JavaDefaultEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(javaParameters.getJavaOutputDir(), extensionParameters, JAVA_TEMPLATE_LOCATION,
                JavaFullNameFormatter.JAVA_PACKAGE_SEPARATOR);

        this.extensionParameters = extensionParameters;
        this.javaParameters = javaParameters;
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

    protected void processTemplateToRootDir(String templateName, Object templateData, String outFileName)
            throws ZserioEmitException
    {
        super.processTemplateToRootDir(templateName, templateData, outFileName, JAVA_SOURCE_EXTENSION, false);
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return new TemplateDataContext(extensionParameters, javaParameters, getPackageMapper());
    }

    private static final String JAVA_SOURCE_EXTENSION = ".java";
    private static final String JAVA_TEMPLATE_LOCATION = "java/";

    private final Parameters extensionParameters;
    private final JavaExtensionParameters javaParameters;
}
