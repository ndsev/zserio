package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import antlr.collections.AST;
import zserio.ast.ZserioType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Emits type collaboration diagrams in DOT format per each Zserio type.
 */
public class TypeCollaborationDotEmitter extends DefaultDocEmitter
{
    /**
     * Constructor.
     *
     * @param docPath              Path to the root of generated documentation.
     * @param dotLinksPrefix       Prefix for doc links or null to use links to locally generated doc.
     * @param withSvgDiagrams True to enable dot files conversion to svg format.
     * @param dotExecutable        Dot executable to use for conversion or null to use dot exe on path.
     */
    public TypeCollaborationDotEmitter(String docPath, String dotLinksPrefix, boolean withSvgDiagrams,
                                       String dotExecutable)
    {
        usedByTypeMap = new HashMap<ZserioType, Set<ZserioType>>();
        this.docPath = docPath;
        this.dotLinksPrefix = (dotLinksPrefix == null) ? "../.." : dotLinksPrefix;
        this.withSvgDiagrams = withSvgDiagrams;
        this.dotExecutable = dotExecutable;
    }

    // implementation of Emitter interface (used to gather all the data for the emitter)

    @Override
    public void endRoot()
    {
        emitDotDiagrams();
    }

    @Override
    public void beginConst(AST c)
    {
        if (!(c instanceof ZserioType))
            throw new ZserioEmitHtmlException("Unexpected token type in beginConst!");
        storeType((ZserioType)c);
    }

    @Override
    public void beginStructure(AST s)
    {
        if (!(s instanceof ZserioType))
            throw new ZserioEmitHtmlException("Unexpected token type in beginStructure!");
        storeType((ZserioType)s);
    }

    @Override
    public void beginChoice(AST c)
    {
        if (!(c instanceof ZserioType))
            throw new ZserioEmitHtmlException("Unexpected token type in beginChoice!");
        storeType((ZserioType)c);
    }

    @Override
    public void beginUnion(AST u)
    {
        if (!(u instanceof ZserioType))
            throw new ZserioEmitHtmlException("Unexpected token type in beginUnion!");
        storeType((ZserioType)u);
    }

    @Override
    public void beginEnumeration(AST e)
    {
        if (!(e instanceof ZserioType))
            throw new ZserioEmitHtmlException("Unexpected token type in beginEnumeration!");
        storeType((ZserioType)e);
    }

    @Override
    public void beginSubtype(AST s)
    {
        if (!(s instanceof ZserioType))
            throw new ZserioEmitHtmlException("Unexpected token type in beginSubtype!");
        storeType((ZserioType)s);
    }

    @Override
    public void beginService(AST s)
    {
        if (!(s instanceof ZserioType))
            throw new ZserioEmitHtmlException("Unexpected token type in beginServiceType");
        storeType((ZserioType)s);
    }

    @Override
    public void beginSqlDatabase(AST s)
    {
        if (!(s instanceof ZserioType))
            throw new ZserioEmitHtmlException("Unexpected token type in beginSqlDatabase!");
        storeType((ZserioType)s);
    }

    @Override
    public void beginSqlTable(AST s)
    {
        if (!(s instanceof ZserioType))
            throw new ZserioEmitHtmlException("Unexpected token type in beginSqlTable!");
        storeType((ZserioType)s);
    }

    private void emitDotDiagrams()
    {
        for (Map.Entry<ZserioType, Set<ZserioType> > entry : usedByTypeMap.entrySet())
        {
            final ZserioType type = entry.getKey();
            final File outputFile = DocEmitterTools.getTypeCollaborationDotFile(docPath, type);

            TypeCollaborationDotTemplateData templateData = new TypeCollaborationDotTemplateData(type,
                    entry.getValue(), dotLinksPrefix);
            emit(outputFile, "doc/type_collaboration.dot.ftl", templateData);
            if (withSvgDiagrams)
                if (!DotFileConvertor.convertToSvg(dotExecutable, outputFile,
                                      DocEmitterTools.getTypeCollaborationSvgFile(docPath, type)))
                    throw new ZserioEmitHtmlException("Failure to convert '" + outputFile +
                            "' to SVG format!");
        }
    }

    private void emit(File outputFile, String templateFileName, Object templateData)
                 throws ZserioEmitHtmlException
    {
        try
        {
            Configuration fmConfig = new Configuration(Configuration.VERSION_2_3_28);
            fmConfig.setClassForTemplateLoading(TypeCollaborationDotEmitter.class, "/freemarker/");

            Template fmTemplate = fmConfig.getTemplate(templateFileName);

            openOutputFile(outputFile);
            fmTemplate.process(templateData, writer);
            writer.close();
        }
        catch (IOException exc)
        {
            throw new ZserioEmitHtmlException(exc);
        }
        catch (TemplateException exc)
        {
            throw new ZserioEmitHtmlException(exc);
        }
    }

    private void storeType(ZserioType type)
    {
        boolean isEmpty = true;
        for (ZserioType usedType : type.getUsedTypeList())
        {
            final Set<ZserioType> usedByTypeSet = createUsedByTypeSet(usedType);
            usedByTypeSet.add(type);
            isEmpty = false;
        }

        if (!isEmpty)
            createUsedByTypeSet(type);
    }

    private Set<ZserioType> createUsedByTypeSet(ZserioType type)
    {
        Set<ZserioType> usedByTypeSet = usedByTypeMap.get(type);
        if (usedByTypeSet == null)
        {
            usedByTypeSet = new LinkedHashSet<ZserioType>();
            usedByTypeMap.put(type, usedByTypeSet);
        }

        return usedByTypeSet;
    }

    private final Map<ZserioType, Set<ZserioType> > usedByTypeMap;
    private final String docPath;
    private final String dotLinksPrefix;
    private final boolean withSvgDiagrams;
    private final String dotExecutable;
}
