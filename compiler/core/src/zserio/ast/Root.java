package zserio.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import antlr.CommonHiddenStreamToken;
import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.FileNameLexerToken;
import zserio.antlr.util.ParserException;

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
