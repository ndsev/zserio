package zserio.ast4;

import java.util.List;
import java.util.Map;

import zserio.antlr.util.ParserException;
import zserio.ast.PackageName;
import zserio.ast4.Package;
import zserio.emit.common.Emitter;
import zserio.emit.common.ZserioEmitException;

/**
 * The representation of AST ROOT node.
 *
 * This class represents an AST node which has all translation units compiled by zserio.
 */
public class Root extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param checkUnusedTypes Whether to print warnings for unused types.
     */
    public Root(List<TranslationUnit> translationUnits, Map<PackageName, Package> packageNameMap,
            boolean checkUnusedTypes)
    {
        super(null);

        this.translationUnits = translationUnits;
        this.packageNameMap = packageNameMap;
        this.checkUnusedTypes = checkUnusedTypes;
    }

    @Override
    public void accept(ZserioVisitor visitor)
    {
        visitor.visitRoot(this);
    }

    @Override
    public void visitChildren(ZserioVisitor visitor)
    {
        for (TranslationUnit translationUnit : translationUnits)
            translationUnit.accept(visitor);
    }

    /**
     * Resolves all references in all translation units.
     *
     * @throws ParserException In case of wrong import or wrong type reference or if cyclic subtype definition
     *                         is detected.
     */
    public void resolveReferences() throws ParserException
    {
        for (Map.Entry<PackageName, Package> packageNameEntry : packageNameMap.entrySet())
        {
            packageNameEntry.getValue().resolve(packageNameMap);
        }
    }

    /**
     * Walks through AST tree applying given emitter interface.
     *
     * @param emitter Emitter interface to use for walking.
     *
     * @throws ZserioEmitException Throws in case of unknown ZserioType.
     */
    public void walk(Emitter emitter) throws ZserioEmitException
    {
        /*emitter.beginRoot(this);

        for (TranslationUnit translationUnit : translationUnits)
        {
            emitter.beginTranslationUnit(translationUnit);

            final Package unitPackage = translationUnit.getPackage();
            if (unitPackage != null) // translation unit package can be null if input file is empty
                emitter.beginPackage(unitPackage);

            final List<Import> unitImports = translationUnit.getImports();
            for (Import unitImport : unitImports)
                emitter.beginImport(unitImport);

            final List<ZserioType> types = translationUnit.getTypes();
            for (ZserioType type : types)
            {
                if (type instanceof ConstType)
                    emitter.beginConst((ConstType)type);
                else if (type instanceof Subtype)
                    emitter.beginSubtype((Subtype)type);
                else if (type instanceof StructureType)
                    emitter.beginStructure((StructureType)type);
                else if (type instanceof ChoiceType)
                    emitter.beginChoice((ChoiceType)type);
                else if (type instanceof UnionType)
                    emitter.beginUnion((UnionType)type);
                else if (type instanceof EnumType)
                    emitter.beginEnumeration((EnumType)type);
                else if (type instanceof SqlTableType)
                    emitter.beginSqlTable((SqlTableType)type);
                else if (type instanceof SqlDatabaseType)
                    emitter.beginSqlDatabase((SqlDatabaseType)type);
                else if (type instanceof ServiceType)
                    emitter.beginService((ServiceType)type);
                else
                    throw new ZserioEmitException("Unknown type '" + type.getClass() + "' in tree walker!");
            }

            emitter.endTranslationUnit(translationUnit);
        }

        emitter.endRoot(this);*/
    }

    private final List<TranslationUnit> translationUnits;
    private final Map<PackageName, Package> packageNameMap;
    private final boolean checkUnusedTypes; // TODO:
}
