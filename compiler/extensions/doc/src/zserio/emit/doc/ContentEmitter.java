package zserio.emit.doc;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.PubsubType;
import zserio.ast.Root;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;

public class ContentEmitter extends DefaultHtmlEmitter
{
    private final CompoundEmitter compoundEmitter;
    private final EnumerationEmitter enumerationEmitter;
    private final BitmaskEmitter bitmaskEmitter;
    private final SubtypeEmitter subtypeEmitter;
    private final ConstantEmitter constantEmitter;
    private final ServiceEmitter serviceEmitter;
    private final PubsubEmitter pubsubEmitter;

    public ContentEmitter(String outputPath, boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPath);

        compoundEmitter = new CompoundEmitter(outputPath, withSvgDiagrams, usedByCollector);
        enumerationEmitter = new EnumerationEmitter(outputPath, withSvgDiagrams, usedByCollector);
        bitmaskEmitter = new BitmaskEmitter(outputPath, withSvgDiagrams, usedByCollector);
        subtypeEmitter = new SubtypeEmitter(outputPath, withSvgDiagrams, usedByCollector);
        constantEmitter = new ConstantEmitter(outputPath, withSvgDiagrams, usedByCollector);
        serviceEmitter = new ServiceEmitter(outputPath, withSvgDiagrams);
        pubsubEmitter = new PubsubEmitter(outputPath, withSvgDiagrams);
    }

    @Override
    public void beginRoot(Root root)
    {
        setCurrentFolder(CONTENT_FOLDER);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        constantEmitter.emit(constant);
    }

    @Override
    public void beginSubtype(Subtype subType) throws ZserioEmitException
    {
        subtypeEmitter.emit(subType);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        compoundEmitter.emit(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        compoundEmitter.emit(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        compoundEmitter.emit(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        enumerationEmitter.emit(enumType);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        bitmaskEmitter.emit(bitmaskType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        compoundEmitter.emit(sqlTableType);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {
        compoundEmitter.emit(sqlDatabaseType);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        serviceEmitter.emit(serviceType);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        pubsubEmitter.emit(pubsubType);
    }
}
