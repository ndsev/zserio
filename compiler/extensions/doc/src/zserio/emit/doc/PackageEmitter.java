package zserio.emit.doc;

import java.io.File;
import java.io.IOException;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.CompoundType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.Package;
import zserio.ast.PubsubType;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;

public class PackageEmitter extends DefaultHtmlEmitter
{
    public PackageEmitter(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPath);
        directory = new File(directory, CONTENT_FOLDER);
        this.withSvgDiagrams = withSvgDiagrams;
        this.usedByCollector = usedByCollector;
        this.outputPath = outputPath;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        ResourceManager.getInstance().setCurrentOutputDir(directory.toString());
        openOutputFile(directory, pkg.getPackageName().toString() + HTML_EXT);

        final PackageTemplateData templateData = new PackageTemplateData(pkg);
        processTemplate("doc/package_begin.html.ftl", templateData);
    }

    @Override
    public void endPackage(Package pkg) throws ZserioEmitException
    {
        processTemplate("doc/package_end.html.ftl", null);

        if (writer != null)
            writer.close();
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        final ConstantEmitter templateData =
                new ConstantEmitter(constant, outputPath, withSvgDiagrams, usedByCollector);
        processTemplate("doc/const.html.ftl", templateData);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioEmitException
    {
        final SubtypeEmitter templateData =
                new SubtypeEmitter(subtype, outputPath, withSvgDiagrams, usedByCollector);
        processTemplate("doc/subtype.html.ftl", templateData);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        final EnumerationEmitter templateData =
                new EnumerationEmitter(enumType, outputPath, withSvgDiagrams, usedByCollector);
        processTemplate("doc/enumeration.html.ftl", templateData);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        final BitmaskEmitter templateData =
                new BitmaskEmitter(bitmaskType, outputPath, withSvgDiagrams, usedByCollector);
        processTemplate("doc/bitmask.html.ftl", templateData);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        processCompound(structureType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        processCompound(unionType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        final CompoundEmitter templateData =
                new CompoundEmitter(choiceType, outputPath, withSvgDiagrams, usedByCollector);
        processTemplate("doc/choice.html.ftl", templateData);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {
        processCompound(sqlDatabaseType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        processCompound(sqlTableType);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        final ServiceTemplateData templateData =
                new ServiceTemplateData(serviceType, outputPath, withSvgDiagrams);
        processTemplate("doc/service.html.ftl", templateData);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        final PubsubTemplateData templateData = new PubsubTemplateData(getExpressionFormatter(), pubsubType,
                outputPath, withSvgDiagrams);
        processTemplate("doc/pubsub.html.ftl", templateData);
    }

    private void processCompound(CompoundType compoundType) throws ZserioEmitException
    {
        final CompoundEmitter templateData =
                new CompoundEmitter(compoundType, outputPath, withSvgDiagrams, usedByCollector);
        processTemplate("doc/compound.html.ftl", templateData);
    }

    private void processTemplate(String templateFile, Object templateData) throws ZserioEmitException
    {
        try
        {
            Template tpl = cfg.getTemplate(templateFile);
            tpl.process(templateData, writer);
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

    public static class PackageTemplateData
    {
        public PackageTemplateData(Package pkg) throws ZserioEmitException
        {
            name = pkg.getPackageName().toString();
            docComment = new DocCommentTemplateData(pkg.getDocComment());
        }

        public String getName()
        {
            return name;
        }

        public DocCommentTemplateData getDocComment()
        {
            return docComment;
        }

        private final String name;
        private final DocCommentTemplateData docComment;
    }

    private final boolean withSvgDiagrams;
    private final UsedByCollector usedByCollector;
    private final String outputPath;
}