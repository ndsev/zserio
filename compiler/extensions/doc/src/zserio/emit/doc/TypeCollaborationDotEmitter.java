package zserio.emit.doc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.EnumType;
import zserio.ast.Root;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
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

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        emitDotDiagrams();
    }

    @Override
    public void beginConst(ConstType constType)
    {
        storeType(constType);
    }

    @Override
    public void beginStructure(StructureType structureType)
    {
        storeType(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType)
    {
        storeType(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType)
    {
        storeType(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType)
    {
        storeType(enumType);
    }

    @Override
    public void beginSubtype(Subtype subtype)
    {
        storeType(subtype);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType)
    {
        storeType(sqlTableType);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType)
    {
        storeType(sqlDatabaseType);
    }

    @Override
    public void beginService(ServiceType serviceType)
    {
        storeType(serviceType);
    }

    private void emitDotDiagrams() throws ZserioEmitException
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
                    throw new ZserioEmitException("Failure to convert '" + outputFile +
                            "' to SVG format!");
        }
    }

    private void emit(File outputFile, String templateFileName, Object templateData)
                 throws ZserioEmitException
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
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
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
