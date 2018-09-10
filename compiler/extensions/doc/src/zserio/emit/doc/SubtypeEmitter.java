package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.ServiceType;
import zserio.ast.ZserioException;
import zserio.ast.Subtype;
import zserio.ast.ZserioType;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class SubtypeEmitter extends DefaultHtmlEmitter
{
    private Subtype subtype;
    private DocCommentTemplateData docCommentTemplateData;
    private String docPath;
    private boolean withSvgDiagrams;

    public SubtypeEmitter(String outputPath, boolean withSvgDiagrams)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
    }

    public void emit(Subtype s)
    {
        this.subtype = s;
        docCommentTemplateData = new DocCommentTemplateData(subtype.getDocComment());
        containers.clear();
        for (CompoundType compound : subtype.getUsedByCompoundList())
        {
            CompoundEmitter ce = new CompoundEmitter(compound);
            containers.add(ce);
        }
        services.clear();
        for (ServiceType service : subtype.getUsedByServiceList())
        {
            services.add(new LinkedType(service));
        }

        try
        {
            Template tpl = cfg.getTemplate("doc/subtype.html.ftl");
            setCurrentFolder(CONTENT_FOLDER);
            openOutputFileFromType(s);
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
        if (subtype == null)
            throw new RuntimeException("getPackageName() called before emit()!");

        return subtype.getPackage().getPackageName();
    }

    public LinkedType getTargetType()
    {
        if (subtype == null)
            throw new RuntimeException("getTargetType() called before emit()!");

        ZserioType targetType = subtype.getTargetType();
        LinkedType linkedType = new LinkedType(targetType);
        return linkedType;
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

    public List<LinkedType> getConstInstances()
    {
        if (subtype == null)
            throw new RuntimeException("getConstInstances() called before emit()!");

        List<LinkedType> results = new ArrayList<LinkedType>();
        for( ZserioType type : subtype.getUsedByConstList() )
        {
            results.add( new LinkedType(type) );
        }
        return results;
    }

    public Subtype getType()
    {
        return subtype;
    }

    public String getCollaborationDiagramSvgFileName()
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, subtype) : null;
    }
}
