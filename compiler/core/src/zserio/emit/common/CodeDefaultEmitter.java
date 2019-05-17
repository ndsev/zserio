package zserio.emit.common;

import java.io.File;
import java.util.Locale;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
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
     */
    public CodeDefaultEmitter(String outPathName, Parameters extensionParameters, String codeTemplateLocation)
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
        packageMapper = null;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        if (packageMapper == null)
            packageMapper = new PackageMapper(pkg, topLevelPackageNameList);
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
        final PackageName packageName = packageMapper.getPackageName(zserioType);
        processTemplate(templateName, templateData, packageName, zserioType.getName(), outputExtension,
                requestAmalgamate);
    }

    protected void processTemplate(String templateName, Object templateData, ZserioType zserioType,
            String outFileNameRoot, String outputExtension, boolean requestAmalgamate)
                    throws ZserioEmitException
    {
        final PackageName packageName = packageMapper.getPackageName(zserioType);
        processTemplate(templateName, templateData, packageName, outFileNameRoot, outputExtension,
                requestAmalgamate);
    }

    protected void processTemplateToRootDir(String templateName, Object templateData, String outFileNameRoot,
            String outputExtension, boolean requestAmalgamate) throws ZserioEmitException
    {
        final PackageName rootPackageName = packageMapper.getRootPackageName();
        processTemplate(templateName, templateData, rootPackageName, outFileNameRoot, outputExtension,
                requestAmalgamate);
    }

    protected void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot, String outputExtension, boolean requestAmalgamate)
                    throws ZserioEmitException
    {
        final File outDir = new File(outPathName, packageName.toFilesystemPath());
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
    private PackageMapper packageMapper;
}
