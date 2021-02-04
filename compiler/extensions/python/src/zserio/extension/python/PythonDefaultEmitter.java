package zserio.extension.python;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.ZserioType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.FreeMarkerUtil;
import zserio.extension.common.ZserioExtensionException;

abstract class PythonDefaultEmitter extends DefaultTreeWalker
{
    public PythonDefaultEmitter(PythonExtensionParameters pythonParameters)
    {
        this.outputPathName = pythonParameters.getOutputDir();
        this.pythonParameters = pythonParameters;
        this.context = new TemplateDataContext(pythonParameters);
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return true;
    }

    protected boolean getWithPubsubCode()
    {
        return pythonParameters.getWithPubsubCode();
    }

    protected boolean getWithServiceCode()
    {
        return pythonParameters.getWithServiceCode();
    }

    protected boolean getWithSqlCode()
    {
        return pythonParameters.getWithSqlCode();
    }

    protected boolean getWithPythonProperties()
    {
        return pythonParameters.getWithPythonProperties();
    }

    protected TemplateDataContext getTemplateDataContext()
    {
        return context;
    }

    protected Map<File, Boolean> getOutputFiles()
    {
        return outputFiles;
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
        if (addOutputFile(outputFile))
        {
            FreeMarkerUtil.processTemplate(PYTHON_TEMPLATE_LOCATION + templateName, templateData, outputFile,
                    false);
        }
    }

    protected List<String> readFreemarkerTemplate(String templateName) throws ZserioExtensionException
    {
        return FreeMarkerUtil.readFreemarkerTemplate(PYTHON_TEMPLATE_LOCATION + templateName);
    }

    private boolean addOutputFile(File outputFile)
    {
        final long lastModifiedSourceTime = pythonParameters.getLastModifiedSourceTime();
        final boolean generate = pythonParameters.getIngoreTimestamps() ||
                lastModifiedSourceTime == 0L || lastModifiedSourceTime > outputFile.lastModified();

        outputFiles.put(outputFile, generate);

        return generate;
    }

    private static final String PYTHON_SOURCE_EXTENSION = ".py";
    private static final String PYTHON_TEMPLATE_LOCATION = "python/";

    private final String outputPathName;
    private final PythonExtensionParameters pythonParameters;
    private final TemplateDataContext context;
    private final Map<File, Boolean> outputFiles = new HashMap<File, Boolean>();
}
