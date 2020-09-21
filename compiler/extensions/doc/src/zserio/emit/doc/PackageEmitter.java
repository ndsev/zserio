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
    public PackageEmitter(TemplateDataContext context)
    {
        super(context.getOutputPath());
        directory = new File(directory, CONTENT_FOLDER);
        this.context = context;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        ResourceManager.getInstance().setCurrentOutputDir(directory.toString());
        openOutputFile(directory, pkg.getPackageName().toString() + HTML_EXT);

        final BeginPackageTemplateData templateData = new BeginPackageTemplateData(pkg);
        processTemplate("doc/begin_package.html.ftl", templateData);
    }

    @Override
    public void endPackage(Package pkg) throws ZserioEmitException
    {
        processTemplate("doc/end_package.html.ftl", null);

        if (writer != null)
            writer.close();
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        final ConstantTemplateData templateData = new ConstantTemplateData(context, constant);
        processTemplate("doc/constant.html.ftl", templateData);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioEmitException
    {
        final SubtypeEmitter templateData = new SubtypeEmitter(subtype,
                context.getOutputPath(), context.getWithSvgDiagrams(), context.getUsedByCollector());
        processTemplate("doc/subtype.html.ftl", templateData);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        final EnumerationTemplateData templateData = new EnumerationTemplateData(context, enumType);
        processTemplate("doc/enumeration.html.ftl", templateData);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        final BitmaskTemplateData templateData = new BitmaskTemplateData(context, bitmaskType);
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
        final CompoundEmitter templateData = new CompoundEmitter(choiceType,
                context.getOutputPath(), context.getWithSvgDiagrams(), context.getUsedByCollector());
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
                new ServiceTemplateData(serviceType, context);
        processTemplate("doc/service.html.ftl", templateData);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        final PubsubTemplateData templateData =
                new PubsubTemplateData(getExpressionFormatter(), pubsubType, context);
        processTemplate("doc/pubsub.html.ftl", templateData);
    }

    private void processCompound(CompoundType compoundType) throws ZserioEmitException
    {
        final CompoundEmitter templateData = new CompoundEmitter(compoundType,
                context.getOutputPath(), context.getWithSvgDiagrams(), context.getUsedByCollector());
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

    private final TemplateDataContext context;
}