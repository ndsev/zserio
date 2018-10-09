package zserio.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import antlr.CommonHiddenStreamToken;
import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.FileNameLexerToken;
import zserio.antlr.util.ParserException;
import zserio.emit.common.Emitter;
import zserio.emit.common.ZserioEmitException;

/**
 * The representation of AST ROOT node.
 *
 * This class represents an AST node which has all translation unit compiled by zserio.
 */
public class Root extends TokenAST
{
    /**
     * Constructor.
     *
     * @param checkUnusedTypes Whether to print warnings for unused types.
     */
    public Root(boolean checkUnusedTypes)
    {
        this(null, checkUnusedTypes);
    }

    /**
     * Creates root node with hidden token. Needed for comment parser.
     *
     * @param hiddenTokenBefore Hidden token which should be stored before created root node.
     *
     * @return Created root node.
     */
    public Root(CommonHiddenStreamToken hiddenTokenBefore)
    {
        this(hiddenTokenBefore, false);
    }

    /**
     * Adds translation unit to this root node.
     *
     * @param translationUnit Translation unit to add.
     */
    public void addTranslationUnit(TranslationUnit translationUnit)
    {
        addChild(translationUnit);
        translationUnits.add(translationUnit);

        final Package unitPackage = translationUnit.getPackage();
        if (unitPackage != null)
        {
            // translation unit package can be null if input file is empty
            packageNameMap.put(unitPackage.getPackageName(), unitPackage);
        }
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
     * Gets all translation units defined in this root.
     *
     * @return All translation units defined in this root.
     */
    public List<TranslationUnit> getTranslationUnits()
    {
        return translationUnits;
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
        emitter.beginRoot(this);

        for (TranslationUnit translationUnit : translationUnits)
        {
            emitter.beginTranslationUnit(translationUnit);

            final Package unitPackage = translationUnit.getPackage();
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

        emitter.endRoot(this);
    }

    @Override
    protected void check() throws ParserException
    {
        // check if zserio type (const, substype, structure, union, choice, enum, etc...) is used
        final List<ZserioType> checkedTypes = new ArrayList<ZserioType>();
        for (TranslationUnit translationUnit : translationUnits)
            checkedTypes.addAll(translationUnit.getTypes());

        // call visitor for all checked types
        final ZserioTypeCheckerVisitor zserioCheckerVisitor =
                new ZserioTypeCheckerVisitor(printUnusedWarnings);
        for (ZserioType checkedType : checkedTypes)
            checkedType.callVisitor(zserioCheckerVisitor);
        zserioCheckerVisitor.throwErrors();
        zserioCheckerVisitor.printWarnings();
    }

    private Root(CommonHiddenStreamToken hiddenTokenBefore, boolean printUnusedWarnings)
    {
        super(new FileNameLexerToken(ZserioParserTokenTypes.ROOT, "ROOT", hiddenTokenBefore));
        this.printUnusedWarnings = printUnusedWarnings;
    }

    private static final long serialVersionUID = -1L;

    private final List<TranslationUnit> translationUnits = new ArrayList<TranslationUnit>();
    private final Map<PackageName, Package> packageNameMap = new LinkedHashMap<PackageName, Package>();
    private final boolean printUnusedWarnings;
}
