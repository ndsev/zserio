package zserio.ast4;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import zserio.antlr.Zserio4Parser;
import zserio.antlr.Zserio4ParserBaseVisitor;
import zserio.ast.PackageName;

public class ZserioAstBuilderVisitor extends Zserio4ParserBaseVisitor<Object>
{
    public ZserioAstBuilderVisitor(boolean checkUnusedTypes)
    {
        this.checkUnusedTypes = checkUnusedTypes;
    }

    public Root getAst()
    {
        // TODO: add also package set?
        return new Root(translationUnits, checkUnusedTypes);
    }

    @Override
    public Object visitTranslationUnit(Zserio4Parser.TranslationUnitContext ctx)
    {
        // imports
        final List<Import> imports = new ArrayList<Import>();
        for (Zserio4Parser.ImportDeclarationContext importCtx : ctx.importDeclaration())
            imports.add((Import)visitImportDeclaration(importCtx));

        // package
        Package unitPackage = null;
        if (ctx.packageDeclaration() != null)
        {
            final PackageName packageName = (PackageName)visitPackageDeclaration(ctx.packageDeclaration());
            unitPackage = new Package(ctx.packageDeclaration().getStart(), packageName, imports);
        }
        else
        {
            unitPackage = new Package(ctx.getStart(), PackageName.EMPTY, imports); // default package
        }

        currentPackage = unitPackage; // set current package for types

        // types declarations
        final List<ZserioType> types = new ArrayList<ZserioType>();
        for (Zserio4Parser.TypeDeclarationContext typeCtx : ctx.typeDeclaration())
        {
            final Object typeDeclaration = visitTypeDeclaration(typeCtx);
            if (typeDeclaration != null) // TODO: remove null check when all types are implemented
                types.add((ZserioType)visitTypeDeclaration(typeCtx));
        }

        currentPackage = null;

        final TranslationUnit translationUnit = new TranslationUnit(
                ctx.getStart(), unitPackage, imports, types);
        addTranslationUnit(translationUnit);

        return translationUnit;
    }

    @Override
    public Object visitPackageDeclaration(Zserio4Parser.PackageDeclarationContext ctx)
    {
        final PackageName packageName = createPackageName(ctx.qualifiedName().id());
        return packageName;
    }

    @Override
    public Object visitImportDeclaration(Zserio4Parser.ImportDeclarationContext ctx)
    {
        String importedTypeName = null;
        PackageName importedPackageName = null;

        if (ctx.MULTIPLY() == null)
        {
            importedPackageName = createPackageName(ctx.id().subList(0, ctx.id().size() - 1));
            importedTypeName = ctx.id().get(ctx.id().size() - 1).getText();
        }
        else
        {
            importedPackageName = createPackageName(ctx.id());
        }

        return new Import(ctx.getStart(), importedPackageName, importedTypeName);
    }

    @Override
    public Object visitConstDeclaration(Zserio4Parser.ConstDeclarationContext ctx)
    {
        return null; // TODO
    }

    @Override
    public Object visitSubtypeDeclaration(Zserio4Parser.SubtypeDeclarationContext ctx)
    {
        final String name = ctx.id().getText();
        final ZserioType targetType = (ZserioType)visitTypeName(ctx.typeName());

        return new Subtype(ctx.getStart(), name, targetType);
    }

    @Override
    public Object visitTypeName(Zserio4Parser.TypeNameContext ctx)
    {
        if (ctx.builtinType() != null)
            return visitBuiltinType(ctx.builtinType());

        return new TypeReference(ctx.getStart()); // TODO: handle referenced type
    }

    @Override
    public Object visitIntType(Zserio4Parser.IntTypeContext ctx)
    {
        return new StdIntegerType(ctx.getStart());
    }

    @Override
    public Object visitVarintType(Zserio4Parser.VarintTypeContext ctx)
    {
        return new VarIntegerType(ctx.getStart());
    }

    @Override
    public Object visitUnsignedBitFieldType(Zserio4Parser.UnsignedBitFieldTypeContext ctx)
    {
        // final Expression expression = ctx.bitfieldLength();
        // TODO: use length expression
        return new UnsignedBitFieldType(ctx.getStart());
    }

    @Override
    public Object visitSignedBitFieldType(Zserio4Parser.SignedBitFieldTypeContext ctx)
    {
        // TODO: use length expression
        return new SignedBitFieldType(ctx.getStart());
    }

    @Override
    public Object visitBoolType(Zserio4Parser.BoolTypeContext ctx)
    {
        return new BooleanType(ctx.getStart());
    }

    @Override
    public Object visitStringType(Zserio4Parser.StringTypeContext ctx)
    {
        return new StringType(ctx.getStart());
    }

    @Override
    public Object visitFloatType(Zserio4Parser.FloatTypeContext ctx)
    {
        return new FloatType(ctx.getStart());
    }

    /**
     * Adds translation unit to this root node.
     *
     * @param translationUnit Translation unit to add.
     */
    private void addTranslationUnit(TranslationUnit translationUnit)
    {
        translationUnits.add(translationUnit);

        final Package unitPackage = translationUnit.getPackage();
        if (packageNameMap.put(unitPackage.getPackageName(), unitPackage) != null)
        {
            // translation unit package already exists, this could happen only for default packages
            throw new ParserException(translationUnit, "Multiple default packages are not allowed!");
        }
    }

    private PackageName createPackageName(List<Zserio4Parser.IdContext> ids)
    {
        final PackageName.Builder packageNameBuilder = new PackageName.Builder();
        for (Zserio4Parser.IdContext id : ids)
            packageNameBuilder.addId(id.getText());
        return packageNameBuilder.get();
    }

    private final boolean checkUnusedTypes;
    private final List<TranslationUnit> translationUnits = new ArrayList<TranslationUnit>();
    private final Map<PackageName, Package> packageNameMap = new LinkedHashMap<PackageName, Package>();

    private Package currentPackage = null;
}
