package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import zserio.ast.ZserioException;
import zserio.ast.ZserioType;
import zserio.ast.Package;
import zserio.emit.common.ExpressionFormatter;
import zserio.tools.PackageManager;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

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
        return currentPackage.getPackageName();
    }

    public String getRootPackageName()
    {
        return PackageManager.get().getRoot().getPackageName();
    }

    public List<CompoundEmitter> getContainers()
    {
        return containers;
    }

    public List<LinkedType> getServices()
    {
        return services;
    }

    @Override
    public void beginPackage(AST p)
    {
        currentPackage = PackageManager.get().lookup(p);
    }

    public void emitStylesheet()
    {
        emit("doc/webStyles.css.ftl", "webStyles.css");
    }

    public void emitFrameset()
    {
        emit("doc/index.html.ftl", "index.html");
    }

    public void openOutputFileFromType(ZserioType type)
    {
        File outputDir = new File(directory, DocEmitterTools.getDirectoryNameFromType(type));
        openOutputFile(outputDir, DocEmitterTools.getHtmlFileNameFromType(type));
    }

    protected ExpressionFormatter getExpressionFormatter()
    {
        return expressionFormatter;
    }

    private void emit(String template, String outputName)
    {
        try
        {
            Template tpl = cfg.getTemplate(template);
            openOutputFile(directory, outputName);

            tpl.process(this, writer);
            writer.close();
        }
        catch (IOException exc)
        {
            throw new ZserioException(exc);
        }
        catch (TemplateException exc)
        {
            throw new ZserioException(exc);
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    protected static final String CONTENT_FOLDER = "content";

    protected static final String HTML_EXT = ".html";

    static volatile Configuration cfg;

    protected List<CompoundEmitter> containers = new ArrayList<CompoundEmitter>();
    protected List<LinkedType> services = new ArrayList<LinkedType>();

    protected File directory;

    protected ZserioType currentType;

    protected Package currentPackage;

    private String currentFolder = "/";

    private final ExpressionFormatter expressionFormatter;
}
