package zserio.emit.doc;

import java.io.File;
import java.io.IOException;

import zserio.ast.CompoundType;
import zserio.ast.Constant;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ConstantEmitter extends DefaultHtmlEmitter
{
    private Constant constant;
    private DocCommentTemplateData docCommentTemplateData;
    private final String docPath;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;

    public ConstantEmitter(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
    }

    public ConstantEmitter(Constant constant, String outputPath, boolean withSvgDiagrams,
            UsedByCollector usedByCollector) throws ZserioEmitException
    {
        this(outputPath, withSvgDiagrams, usedByCollector);
        this.constant = constant;
        prepareForEmit();
    }

    public void emit(Constant constant) throws ZserioEmitException
    {
        this.constant = constant;
        ResourceManager.getInstance().setCurrentOutputDir(DocEmitterTools.getDirectoryNameFromType(constant));
        prepareForEmit();

        try
        {
            Template tpl = cfg.getTemplate("doc/const.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(constant);
            tpl.process(this, writer);
            writer.close();
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    @Override
    public String getPackageName()
    {
        if( constant != null )
        {
            return constant.getPackage().getPackageName().toString();
        }

        return "";
    }

    public LinkedType getLinkedType() throws ZserioEmitException
    {
        if (constant == null)
            return null;
        return new LinkedType(constant);
    }

    public String getTypeName()
    {
        if( constant != null )
        {
            return constant.getName();
        }

        return "";
    }

    public String getTypeValue() throws ZserioEmitException
    {
        if( constant != null )
        {
            final Expression valueExpression = constant.getValueExpression();
            final DocExpressionFormattingPolicy policy = new DocExpressionFormattingPolicy();
            final ExpressionFormatter expressionFormatter = new ExpressionFormatter(policy);

            return expressionFormatter.formatGetter(valueExpression);
        }

        return "";
    }

    public DocCommentTemplateData getDocComment()
    {
        return docCommentTemplateData;
    }

    public boolean getIsDeprecated()
    {
        if (docCommentTemplateData == null)
            throw new RuntimeException("getIsDeprecated() called before emit()!");

        return docCommentTemplateData.getIsDeprecated();
    }

    public LinkedType getConstType() throws ZserioEmitException
    {
        if (constant == null)
            throw new RuntimeException("getConstType() called before emit()!");

        LinkedType linkedType = new LinkedType(constant.getTypeInstantiation());
        return linkedType;
    }

    public String getCollaborationDiagramSvgFileName() throws ZserioEmitException
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, constant) : null;
    }

    private void prepareForEmit() throws ZserioEmitException
    {
        docCommentTemplateData = new DocCommentTemplateData(constant.getDocComment());
        containers.clear();
        for (CompoundType compound : usedByCollector.getUsedByTypes(constant, CompoundType.class))
        {
            CompoundEmitter ce = new CompoundEmitter(compound);
            containers.add(ce);
        }
        protocols.clear();
    }
}
