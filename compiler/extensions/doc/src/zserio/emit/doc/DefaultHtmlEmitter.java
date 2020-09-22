package zserio.emit.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.AstNode;
import zserio.ast.ZserioType;
import zserio.ast.Package;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import freemarker.template.Configuration;

abstract public class DefaultHtmlEmitter extends DefaultDocEmitter
{
    public DefaultHtmlEmitter()
    {
        initConfig();

        expressionFormatter = new ExpressionFormatter(new DocExpressionFormattingPolicy());
    }

    public DefaultHtmlEmitter(String outputPath)
    {
        if (outputPath != null && outputPath.length() > 0)
        {
            directory = new File(outputPath);
        }
        else
        {
            directory = new File("html");
        }

        initConfig();

        expressionFormatter = new ExpressionFormatter(new DocExpressionFormattingPolicy());
    }

    private void initConfig()
    {
        if (cfg != null)
            return;

        Configuration config = new Configuration(Configuration.VERSION_2_3_28);
        config.setClassForTemplateLoading(DefaultHtmlEmitter.class, "/freemarker/");

        cfg = config;
    }

    public void setCurrentFolder(String currentFolder)
    {
        this.currentFolder = currentFolder;
    }

    public String getCurrentFolder()
    {
        return currentFolder;
    }

    public String getPackageName()
    {
        return currentPackage.getPackageName().toString();
    }

    public String getRootPackageName()
    {
        return currentRootPackage.getPackageName().toString();
    }

    public List<CompoundEmitter> getContainers()
    {
        return containers;
    }

    public List<LinkedType> getServices()
    {
        return protocols;
    }

    @Override
    public void beginPackage(Package packageToken) throws ZserioEmitException
    {
        // the first one is root package
        if (currentRootPackage == null)
            currentRootPackage = packageToken;
        currentPackage = packageToken;
    }

    public void openOutputFileFromType(AstNode type) throws ZserioEmitException
    {
        File outputDir = new File(directory, DocEmitterTools.getDirectoryNameFromType(type));
        openOutputFile(outputDir, DocEmitterTools.getHtmlFileNameFromType(type));
    }

    protected ExpressionFormatter getExpressionFormatter()
    {
        return expressionFormatter;
    }

    protected static final String CONTENT_FOLDER = "content";

    protected static final String HTML_EXT = ".html";

    static volatile Configuration cfg;

    protected List<CompoundEmitter> containers = new ArrayList<CompoundEmitter>();
    protected List<LinkedType> protocols = new ArrayList<LinkedType>();

    protected File directory;

    protected ZserioType currentType;

    protected Package currentPackage;
    protected Package currentRootPackage;

    private String currentFolder = "/";

    private final ExpressionFormatter expressionFormatter;
}
