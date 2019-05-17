package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.ServiceType;
import zserio.ast.Subtype;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class SubtypeEmitter extends DefaultHtmlEmitter
{
    private Subtype subtype;
    private DocCommentTemplateData docCommentTemplateData;
    private final String docPath;
    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;

    public SubtypeEmitter(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPath);
        docPath = outputPath;
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
    }

    public void emit(Subtype s) throws ZserioEmitException
    {
        this.subtype = s;
        docCommentTemplateData = new DocCommentTemplateData(subtype.getDocComment());
        containers.clear();
        for (CompoundType compound : usedByCollector.getUsedByTypes(subtype, CompoundType.class))
        {
            CompoundEmitter ce = new CompoundEmitter(compound);
            containers.add(ce);
        }
        services.clear();
        for (ServiceType service : usedByCollector.getUsedByTypes(subtype, ServiceType.class))
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
        if (subtype == null)
            throw new RuntimeException("getPackageName() called before emit()!");

        return subtype.getPackage().getPackageName().toString();
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
        for (ZserioType type : usedByCollector.getUsedByTypes(subtype, ConstType.class))
        {
            results.add( new LinkedType(type) );
        }
        return results;
    }

    public Subtype getType()
    {
        return subtype;
    }

    public String getCollaborationDiagramSvgFileName() throws ZserioEmitException
    {
        return (withSvgDiagrams) ? DocEmitterTools.getTypeCollaborationSvgUrl(docPath, subtype) : null;
    }
}
