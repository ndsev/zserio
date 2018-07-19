package zserio.emit.java;

import zserio.ast.ZserioType;
import zserio.emit.common.CodeDefaultEmitter;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.common.PackageMapper;
import zserio.tools.Parameters;

abstract class JavaDefaultEmitter extends CodeDefaultEmitter
{
    public JavaDefaultEmitter(Parameters extensionParameters, JavaExtensionParameters javaParameters)
    {
        super(javaParameters.getJavaOutputDir(), extensionParameters, JAVA_TEMPLATE_LOCATION,
                JavaFullNameFormatter.JAVA_PACKAGE_SEPARATOR);

        final PackageMapper javaPackageMapper = getPackageMapper();
        final JavaNativeTypeMapper javaNativeTypeMapper = new JavaNativeTypeMapper(javaPackageMapper);
        templateDataContext = new TemplateDataContext(javaNativeTypeMapper,
                javaPackageMapper, getWithWriterCode(), getWithValidationCode(), getWithRangeCheckCode(),
                javaParameters.getJavaMajorVersion());
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, JAVA_SOURCE_EXTENSION, false);
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outFileName) throws ZserioEmitException
    {
        super.processTemplate(templateName, templateData, zserioType, outFileName, JAVA_SOURCE_EXTENSION, false);
    }

    protected void processTemplateToRootDir(String templateName, Object templateData, String outFileName)
            throws ZserioEmitException
    {
        super.processTemplateToRootDir(templateName, templateData, outFileName, JAVA_SOURCE_EXTENSION, false);
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return templateDataContext;
    }

    private final TemplateDataContext templateDataContext;

    private static final String JAVA_SOURCE_EXTENSION = ".java";
    private static final String JAVA_TEMPLATE_LOCATION = "java/";
}
