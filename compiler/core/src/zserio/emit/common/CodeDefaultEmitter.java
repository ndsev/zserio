package zserio.emit.common;

import java.io.File;
import java.util.Locale;

import zserio.ast.ZserioType;
import zserio.ast.TranslationUnit;
import zserio.ast.Package;
import zserio.tools.Parameters;

/**
 * Implements default emitter common for C++ and Java.
 */
public abstract class CodeDefaultEmitter extends DefaultEmitter
{
    /**
     * Constructor.
     *
     * @param outPathName          Path where to store all generated outputs.
     * @param extensionParameters  Command line parameter for the extension.
     * @param codeTemplateLocation Path where are all FreeMarker templates.
     * @param codePackageSeparator Separator which must be used in package names.
     */
    public CodeDefaultEmitter(String outPathName, Parameters extensionParameters, String codeTemplateLocation,
            String codePackageSeparator)
    {
        this.outPathName = outPathName;
        withInspectorCode = extensionParameters.getWithInspectorCode();
        withRangeCheckCode = extensionParameters.getWithRangeCheckCode();
        withSourcesAmalgamation = extensionParameters.getWithSourcesAmalgamation();
        withSqlCode = extensionParameters.getWithSqlCode();
        withGrpcCode = extensionParameters.getWithGrpcCode();
        withValidationCode = extensionParameters.getWithValidationCode();
        withWriterCode = extensionParameters.getWithWriterCode();

        this.codeTemplateLocation = codeTemplateLocation;

        topLevelPackageNameList = extensionParameters.getTopLevelPackageNameList();
        this.codePackageSeparator = codePackageSeparator;
        packageMapper = null;
    }

    @Override
    public void beginTranslationUnit(TranslationUnit translationUnit) throws ZserioEmitException
    {
        if (packageMapper == null)
        {
            final Package rootPackage = translationUnit.getPackage();
            if (rootPackage != null)
            {
                // root package can be null for empty files
                packageMapper = new PackageMapper(rootPackage, topLevelPackageNameList, codePackageSeparator);
            }
        }
    }

    protected boolean getWithInspectorCode()
    {
        return withInspectorCode;
    }

    protected boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    protected boolean getWithSqlCode()
    {
        return withSqlCode;
    }

    protected boolean getWithGrpcCode()
    {
        return withGrpcCode;
    }

    protected boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    protected boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    protected PackageMapper getPackageMapper()
    {
        return packageMapper;
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outputExtension, boolean requestAmalgamate) throws ZserioEmitException
    {
        final String codePackagePath = packageMapper.getPackageFilePath(zserioType);
        processTemplate(templateName, templateData, codePackagePath, zserioType.getName(), outputExtension,
                requestAmalgamate);
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outFileNameRoot, String outputExtension, boolean requestAmalgamate)
                    throws ZserioEmitException
    {
        final String codePackagePath = packageMapper.getPackageFilePath(zserioType);
        processTemplate(templateName, templateData, codePackagePath, outFileNameRoot, outputExtension,
                requestAmalgamate);
    }

    protected void processTemplateToRootDir(String templateName, Object templateData, String outFileNameRoot,
            String outputExtension, boolean requestAmalgamate) throws ZserioEmitException
    {
        final String rootPackagePath = packageMapper.getRootPackageFilePath();
        processTemplate(templateName, templateData, rootPackagePath, outFileNameRoot, outputExtension,
                requestAmalgamate);
    }

    private void processTemplate(String templateName, Object templateData, String packagePath,
            String outFileNameRoot, String outputExtension, boolean requestAmalgamate)
                    throws ZserioEmitException
    {
        final File outDir = new File(outPathName, packagePath);
        final String outDirName = outDir.getName();
        final boolean amalgamate = (withSourcesAmalgamation && requestAmalgamate);
        final String outFileNameWithoutExtension = (amalgamate) ? convertDirNameToCamelCase(outDirName) :
            outFileNameRoot;
        final File outputFile = new File(outDir, outFileNameWithoutExtension + outputExtension);
        FreeMarkerUtil.processTemplate(codeTemplateLocation + templateName, templateData, outputFile,
                amalgamate);
    }

    private String convertDirNameToCamelCase(String dirName)
    {
        final String[] words = dirName.split("_");
        final StringBuilder builder = new StringBuilder();
        for (String word : words)
            builder.append(Character.toString(word.charAt(0)).toUpperCase(Locale.ENGLISH) + word.substring(1));

        return builder.toString();
    }

    private final String outPathName;

    private final boolean withInspectorCode;
    private final boolean withRangeCheckCode;
    private final boolean withValidationCode;
    private final boolean withSourcesAmalgamation;
    private final boolean withSqlCode;
    private final boolean withGrpcCode;
    private final boolean withWriterCode;

    private final String codeTemplateLocation;
    private final Iterable<String> topLevelPackageNameList;
    private final String codePackageSeparator;
    private PackageMapper packageMapper;
}
