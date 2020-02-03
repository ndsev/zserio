package zserio.emit.cpp;

import java.io.File;
import java.util.Locale;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.emit.common.DefaultEmitter;
import zserio.emit.common.FreeMarkerUtil;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

abstract class CppDefaultEmitter extends DefaultEmitter
{
    public CppDefaultEmitter(String outPathName, Parameters extensionParameters)
    {
        this.outPathName = outPathName;
        this.extensionParameters = extensionParameters;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        packageSourceFileName = pkg.getLocation().getFileName();

        if (packageMapper == null)
            packageMapper = new PackageMapper(pkg, extensionParameters.getTopLevelPackageNameList());
    }

    protected void processSourceTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        processTemplate(templateName, templateData, packageMapper.getPackageName(zserioType),
                zserioType.getName(), CPP_SOURCE_EXTENSION, true);
    }

    protected void processHeaderTemplate(String templateName, Object templateData, ZserioType zserioType)
            throws ZserioEmitException
    {
        processHeaderTemplate(templateName, templateData, zserioType.getPackage().getPackageName(),
                zserioType.getName());
    }

    protected void processHeaderTemplate(String templateName, Object templateData,
            PackageName zserioPackageName, String outFileName) throws ZserioEmitException
    {
        processTemplate(templateName, templateData, packageMapper.getPackageName(zserioPackageName),
                outFileName, CPP_HEADER_EXTENSION, false);
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return new TemplateDataContext(extensionParameters, getPackageMapper());
    }

    protected PackageMapper getPackageMapper()
    {
        return packageMapper;
    }

    protected boolean getWithSourcesAmalgamation()
    {
        return extensionParameters.getWithSourcesAmalgamation();
    }

    protected boolean getWithSqlCode()
    {
        return extensionParameters.getWithSqlCode();
    }

    private void processTemplate(String templateName, Object templateData, PackageName packageName,
            String outFileNameRoot, String outputExtension, boolean requestAmalgamate)
                    throws ZserioEmitException
    {
        final File outDir = new File(outPathName, packageName.toFilesystemPath());
        final boolean amalgamate = (getWithSourcesAmalgamation() && requestAmalgamate);
        final String outFileNameWithoutExtension = (amalgamate) ? getAmalgamFileNameRoot() : outFileNameRoot;
        final File outputFile = new File(outDir, outFileNameWithoutExtension + outputExtension);
        FreeMarkerUtil.processTemplate(CPP_TEMPLATE_LOCATION + templateName, templateData, outputFile,
                amalgamate);
    }

    private String getAmalgamFileNameRoot()
    {
        // strip possible directory
        final int lastSlashIndex = packageSourceFileName.lastIndexOf(File.separatorChar);
        final String sourceFileName = (lastSlashIndex == -1) ? packageSourceFileName :
            packageSourceFileName.substring(lastSlashIndex + 1);

        // strip extensions from source file name
        final int firstDotIndex = sourceFileName.indexOf('.');
        final String sourceFileNameRoot = (firstDotIndex == -1) ? sourceFileName :
            sourceFileName.substring(0, firstDotIndex);

        // convert main part of source file name to camel case
        final String[] words = sourceFileNameRoot.split("_");
        final StringBuilder builder = new StringBuilder();
        for (String word : words)
            builder.append(Character.toString(word.charAt(0)).toUpperCase(Locale.ENGLISH) + word.substring(1));

        return builder.toString();
    }

    private static final String CPP_SOURCE_EXTENSION = ".cpp";
    private static final String CPP_HEADER_EXTENSION = ".h";
    private static final String CPP_TEMPLATE_LOCATION = "cpp/";

    private final String outPathName;
    private final Parameters extensionParameters;

    private PackageMapper packageMapper = null;
    private String packageSourceFileName = "DefaultAmalgam"; // default value should have never been used
}
