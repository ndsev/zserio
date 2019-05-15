package zserio.emit.doc;

import java.io.File;
import java.io.IOException;

import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ConstTypeEmitter extends DefaultHtmlEmitter
{
    private ConstType consttype;
    private DocCommentTemplateData docCommentTemplateData;
    private final String docPath;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;

    public ConstTypeEmitter(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
    }

    public void emit(ConstType constType) throws ZserioEmitException
    {
        this.consttype = constType;
        docCommentTemplateData = new DocCommentTemplateData(consttype.getDocComment());
        containers.clear();
        for (CompoundType compound : usedByCollector.getUsedByTypes(consttype, CompoundType.class))
        {
            CompoundEmitter ce = new CompoundEmitter(compound);
            containers.add(ce);
        }
        services.clear();

        try
        {
            Template tpl = cfg.getTemplate("doc/const.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(consttype);
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
        if( consttype != null )
        {
            return consttype.getPackage().getPackageName().toString();
        }

        return "";
    }

    public String getTypeName()
    {
        if( consttype != null )
        {
            return consttype.getName();
        }

        return "";
    }

    public String getTypeValue() throws ZserioEmitException
    {
        if( consttype != null )
        {
            final Expression valueExpression = consttype.getValueExpression();
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

    public LinkedType getConstType()
    {
        if (consttype == null)
            throw new RuntimeException("getConstType() called before emit()!");

        ZserioType constType = consttype.getConstType();
        LinkedType linkedType = new LinkedType(constType);
        return linkedType;
    }

    public String getCollaborationDiagramSvgFileName() throws ZserioEmitException
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, consttype) : null;
    }
}
