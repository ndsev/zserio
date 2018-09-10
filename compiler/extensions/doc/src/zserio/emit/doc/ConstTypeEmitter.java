package zserio.emit.doc;

import java.io.File;
import java.io.IOException;

import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.ZserioException;
import zserio.ast.ZserioType;
import zserio.ast.Expression;
import zserio.emit.common.ExpressionFormatter;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ConstTypeEmitter extends DefaultHtmlEmitter
{
    private ConstType consttype;
    private DocCommentTemplateData docCommentTemplateData;
    private String docPath;
    private boolean withSvgDiagrams;

    public ConstTypeEmitter(String outputPath, boolean withSvgDiagrams)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
    }

    public void emit(ConstType constType)
    {
        this.consttype = constType;
        docCommentTemplateData = new DocCommentTemplateData(consttype.getDocComment());
        containers.clear();
        for (CompoundType compound : consttype.getUsedByCompoundList())
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

    @Override
    public String getPackageName()
    {
        if( consttype != null )
        {
            return consttype.getPackage().getPackageName();
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

    public String getTypeValue()
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

    public String getCollaborationDiagramSvgFileName()
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, consttype) : null;
    }
}
