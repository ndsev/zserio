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
        return new Root(translationUnits, packageNameMap, checkUnusedTypes);
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
            final PackageName packageName = visitPackageDeclaration(ctx.packageDeclaration());
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
                types.add((ZserioType)typeDeclaration);
        }

        currentPackage = null;

        final TranslationUnit translationUnit = new TranslationUnit(
                ctx.getStart(), unitPackage, imports, types);
        addTranslationUnit(translationUnit);

        return translationUnit;
    }

    @Override
    public PackageName visitPackageDeclaration(Zserio4Parser.PackageDeclarationContext ctx)
    {
        final PackageName packageName = createPackageName(ctx.qualifiedName().id());
        return packageName;
    }

    @Override
    public Import visitImportDeclaration(Zserio4Parser.ImportDeclarationContext ctx)
    {
        String importedTypeName = null;
        PackageName importedPackageName = null;

        if (ctx.MULTIPLY() == null)
        {
            importedPackageName = createPackageName(getPackageNameIds(ctx.id()));
            importedTypeName = getTypeNameId(ctx.id()).getText();
        }
        else
        {
            importedPackageName = createPackageName(ctx.id());
        }

        return new Import(ctx.getStart(), importedPackageName, importedTypeName);
    }

    @Override
    public ConstType visitConstDeclaration(Zserio4Parser.ConstDeclarationContext ctx)
    {
        final ZserioType constType = visitTypeName(ctx.typeName());
        final String name = ctx.id().getText();
        final Expression valueExpression = (Expression)visit(ctx.expression());

        return new ConstType(ctx.getStart(), currentPackage, constType, name, valueExpression);
    }

    @Override
    public Subtype visitSubtypeDeclaration(Zserio4Parser.SubtypeDeclarationContext ctx)
    {
        final ZserioType targetType = visitTypeName(ctx.typeName());
        final String name = ctx.id().getText();

        final Subtype subtype = new Subtype(ctx.getStart(), currentPackage, targetType, name);
        currentPackage.setLocalType(subtype, ctx.id().getStart());

        return subtype;
    }

    @Override
    public StructureType visitStructureDeclaration(Zserio4Parser.StructureDeclarationContext ctx)
    {
        final String name = ctx.id().getText();

        final List<Parameter> parameters = visitParameterList(ctx.parameterList());

        final List<Field> fields = new ArrayList<Field>();
        for (Zserio4Parser.StructureFieldDefinitionContext fieldCtx : ctx.structureFieldDefinition())
            fields.add(visitStructureFieldDefinition(fieldCtx));

        final List<FunctionType> functions = new ArrayList<FunctionType>();
        for (Zserio4Parser.FunctionDefinitionContext functionDefinitionCtx : ctx.functionDefinition())
            functions.add(visitFunctionDefinition(functionDefinitionCtx));

        final StructureType structureType = new StructureType(ctx.getStart(), currentPackage, name,
                parameters, fields, functions);
        currentPackage.setLocalType(structureType, ctx.id().getStart());

        return structureType;
    }

    @Override
    public Field visitStructureFieldDefinition(Zserio4Parser.StructureFieldDefinitionContext ctx)
    {
        final ZserioType type = getFieldType(ctx.fieldTypeId());
        final String name = ctx.fieldTypeId().id().getText();
        final boolean isAutoOptional = ctx.OPTIONAL() != null;

        final Expression alignmentExpr = ctx.fieldAlignment() != null
                ? (Expression)visitFieldAlignment(ctx.fieldAlignment()) : null;
        final Expression offsetExpr = ctx.fieldOffset() != null
                ? (Expression)visitFieldOffset(ctx.fieldOffset()) : null;
        final Expression initializerExpr = ctx.fieldInitializer() != null
                ? (Expression)visitFieldInitializer(ctx.fieldInitializer()) : null;
        final Expression optionalClauseExpr = ctx.fieldOptionalClause() != null
                ? (Expression)visitFieldOptionalClause(ctx.fieldOptionalClause()) : null;
        final Expression constraintExpr = ctx.fieldConstraint() != null
                ? (Expression)visitFieldConstraint(ctx.fieldConstraint()) : null;

        return new Field(ctx.getStart(), type, name, isAutoOptional, alignmentExpr, offsetExpr, initializerExpr,
                optionalClauseExpr, constraintExpr);
    }

    @Override
    public Expression visitFieldAlignment(Zserio4Parser.FieldAlignmentContext ctx)
    {
        return new Expression(ctx.DECIMAL_LITERAL().getSymbol());
    }

    @Override
    public ChoiceType visitChoiceDeclaration(Zserio4Parser.ChoiceDeclarationContext ctx)
    {
        final String name = ctx.id().getText();
        final List<Parameter> parameters = visitParameterList(ctx.parameterList());
        final Expression selectorExpression = (Expression)visit(ctx.expression());
        final List<ChoiceCase> choiceCases = new ArrayList<ChoiceCase>();
        for (Zserio4Parser.ChoiceCasesContext choiceCasesCtx : ctx.choiceCases())
            choiceCases.add(visitChoiceCases(choiceCasesCtx));
        final ChoiceDefault choiceDefault = ctx.choiceDefault() != null
                ? visitChoiceDefault(ctx.choiceDefault()) : null;

        final List<FunctionType> functions = new ArrayList<FunctionType>();
        for (Zserio4Parser.FunctionDefinitionContext functionDefinitionCtx : ctx.functionDefinition())
            functions.add((FunctionType)visitFunctionDefinition(functionDefinitionCtx));

        return new ChoiceType(ctx.getStart(), currentPackage, name, parameters, selectorExpression,
                choiceCases, choiceDefault, functions);
    }

    @Override
    public ChoiceCase visitChoiceCases(Zserio4Parser.ChoiceCasesContext ctx)
    {
        List<Expression> caseExpressions = new ArrayList<Expression>();
        for (Zserio4Parser.ChoiceCaseContext choiceCaseCtx : ctx.choiceCase())
            caseExpressions.add(visitChoiceCase(choiceCaseCtx));

        final Field caseField = (ctx.choiceFieldDefinition() != null)
                ? visitChoiceFieldDefinition(ctx.choiceFieldDefinition()) : null;

        return new ChoiceCase(ctx.getStart(), caseExpressions, caseField);
    }

    @Override
    public Expression visitChoiceCase(Zserio4Parser.ChoiceCaseContext ctx)
    {
        return (Expression)visit(ctx.expression());
    }

    @Override
    public ChoiceDefault visitChoiceDefault(Zserio4Parser.ChoiceDefaultContext ctx)
    {
        return new ChoiceDefault(ctx.getStart(), visitChoiceFieldDefinition(ctx.choiceFieldDefinition()));
    }

    @Override
    public Field visitChoiceFieldDefinition(Zserio4Parser.ChoiceFieldDefinitionContext ctx)
    {
        final ZserioType type = getFieldType(ctx.fieldTypeId());
        final String name = ctx.fieldTypeId().id().getText();
        final boolean isAutoOptional = false;

        final Expression alignmentExpr = null;
        final Expression offsetExpr = null;
        final Expression initializerExpr = null;
        final Expression optionalClauseExpr = null;
        final Expression constraintExpr = ctx.fieldConstraint() != null
                ? (Expression)visitFieldConstraint(ctx.fieldConstraint()) : null;

        return new Field(ctx.getStart(), type, name, isAutoOptional, alignmentExpr, offsetExpr, initializerExpr,
                optionalClauseExpr, constraintExpr);
    }

    @Override
    public FunctionType visitFunctionDefinition(Zserio4Parser.FunctionDefinitionContext ctx)
    {
        final ZserioType returnType = visitTypeName(ctx.functionType().typeName());
        final String name = ctx.functionName().getText();
        final Expression resultExpression = (Expression)visit(ctx.functionBody().expression());

        return new FunctionType(ctx.getStart(), currentPackage, returnType, name, resultExpression);
    }

    @Override
    public List<Parameter> visitParameterList(Zserio4Parser.ParameterListContext ctx)
    {
        List<Parameter> parameters = new ArrayList<Parameter>();
        if (ctx != null)
        {
            for (Zserio4Parser.ParameterDefinitionContext parameterDefinitionCtx : ctx.parameterDefinition())
                parameters.add((Parameter)visitParameterDefinition(parameterDefinitionCtx));
        }
        return parameters;
    }

    @Override
    public Object visitParameterDefinition(Zserio4Parser.ParameterDefinitionContext ctx)
    {
        return new Parameter(ctx.getStart(), (ZserioType)visitTypeName(ctx.typeName()), ctx.id().getText());
    }

    @Override
    public Object visitParenthesizedExpression(Zserio4Parser.ParenthesizedExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(ctx.getStart(), ctx.operator, operand1);
    }

    @Override
    public Object visitFunctionCallExpression(Zserio4Parser.FunctionCallExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(ctx.getStart(), ctx.operator, operand1);
    }

    @Override
    public Object visitArrayExpression(Zserio4Parser.ArrayExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitDotExpression(Zserio4Parser.DotExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression());
        final Expression operand2 = new Expression(ctx.id().ID().getSymbol());
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitLengthofExpression(Zserio4Parser.LengthofExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(ctx.getStart(), ctx.operator, operand1);
    }

    @Override
    public Object visitSumExpression(Zserio4Parser.SumExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(ctx.getStart(), ctx.operator, operand1);
    }

    @Override
    public Object visitValueofExpression(Zserio4Parser.ValueofExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(ctx.getStart(), ctx.operator, operand1);
    }

    @Override
    public Object visitNumbitsExpression(Zserio4Parser.NumbitsExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(ctx.getStart(), ctx.operator, operand1);
    }

    @Override
    public Object visitUnaryExpression(Zserio4Parser.UnaryExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(ctx.getStart(), ctx.operator, operand1);
    }

    @Override
    public Object visitMultiplicativeExpression(Zserio4Parser.MultiplicativeExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitAdditiveExpression(Zserio4Parser.AdditiveExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitShiftExpression(Zserio4Parser.ShiftExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitRelationalExpression(Zserio4Parser.RelationalExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitEqualityExpression(Zserio4Parser.EqualityExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitBitwiseAndExpression(Zserio4Parser.BitwiseAndExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitBitwiseXorExpression(Zserio4Parser.BitwiseXorExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitBitwiseOrExpression(Zserio4Parser.BitwiseOrExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitLogicalAndExpression(Zserio4Parser.LogicalAndExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitLogicalOrExpression(Zserio4Parser.LogicalOrExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitTernaryExpression(Zserio4Parser.TernaryExpressionContext ctx)
    {
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        final Expression operand3 = (Expression)visit(ctx.expression(2));
        return new Expression(ctx.getStart(), ctx.operator, operand1, operand2, operand3);
    }

    @Override
    public Object visitLiteralExpression(Zserio4Parser.LiteralExpressionContext ctx)
    {
        return new Expression(ctx.literal().getStart());
    }

    @Override
    public Object visitIndexExpression(Zserio4Parser.IndexExpressionContext ctx)
    {
        return new Expression(ctx.INDEX().getSymbol());
    }

    @Override
    public Object visitIdentifierExpression(Zserio4Parser.IdentifierExpressionContext ctx)
    {
        return new Expression(ctx.id().ID().getSymbol());
    }

    @Override
    public ZserioType visitTypeName(Zserio4Parser.TypeNameContext ctx)
    {
        if (ctx.builtinType() != null)
            return (ZserioType)visitBuiltinType(ctx.builtinType());

        final PackageName referencedPackageName = createPackageName(
                getPackageNameIds(ctx.qualifiedName().id()));
        final String referencedTypeName = getTypeNameId(ctx.qualifiedName().id()).getText();
        final boolean isParameterized = false;

        final TypeReference typeReference =
                new TypeReference(ctx.getStart(), referencedPackageName, referencedTypeName, isParameterized);
        currentPackage.addTypeReferenceToResolve(typeReference);

        return typeReference;
    }

    @Override
    public ZserioType visitTypeReference(Zserio4Parser.TypeReferenceContext ctx)
    {
        if (ctx.builtinType() != null)
            return (ZserioType)visitBuiltinType(ctx.builtinType());

        final PackageName referencedPackageName = createPackageName(
                getPackageNameIds(ctx.qualifiedName().id()));
        final String referencedTypeName = getTypeNameId(ctx.qualifiedName().id()).getText();
        final boolean isParameterized = ctx.typeArgumentList() != null;

        final TypeReference typeReference =
                new TypeReference(ctx.getStart(), referencedPackageName, referencedTypeName, isParameterized);
        currentPackage.addTypeReferenceToResolve(typeReference);

        if (isParameterized)
        {
            final List<Expression> arguments = new ArrayList<Expression>();
            for (Zserio4Parser.TypeArgumentContext typeArgumentCtx : ctx.typeArgumentList().typeArgument())
                arguments.add((Expression)visitTypeArgument(typeArgumentCtx));
            return new TypeInstantiation(ctx.getStart(), typeReference, arguments);
        }
        else
        {
            return typeReference;
        }
    }

    @Override
    public Expression visitTypeArgument(Zserio4Parser.TypeArgumentContext ctx)
    {
        if (ctx.EXPLICIT() != null)
            return new Expression(ctx.getStart(), ctx.id().ID().getSymbol(), true);
        else
            return (Expression)visit(ctx.expression());
    }

    @Override
    public StdIntegerType visitIntType(Zserio4Parser.IntTypeContext ctx)
    {
        return new StdIntegerType(ctx.getStart());
    }

    @Override
    public VarIntegerType visitVarintType(Zserio4Parser.VarintTypeContext ctx)
    {
        return new VarIntegerType(ctx.getStart());
    }

    @Override
    public UnsignedBitFieldType visitUnsignedBitFieldType(Zserio4Parser.UnsignedBitFieldTypeContext ctx)
    {
        final Expression lengthExpression = (Expression)visitBitFieldLength(ctx.bitFieldLength());
        return new UnsignedBitFieldType(ctx.getStart(), lengthExpression);
    }

    @Override
    public SignedBitFieldType visitSignedBitFieldType(Zserio4Parser.SignedBitFieldTypeContext ctx)
    {
        final Expression lengthExpression = (Expression)visitBitFieldLength(ctx.bitFieldLength());
        return new SignedBitFieldType(ctx.getStart(), lengthExpression);
    }

    @Override
    public Expression visitBitFieldLength(Zserio4Parser.BitFieldLengthContext ctx)
    {
        if (ctx.DECIMAL_LITERAL() != null)
            return new Expression(ctx.DECIMAL_LITERAL().getSymbol());

        return (Expression)visit(ctx.expression());
    }

    @Override
    public BooleanType visitBoolType(Zserio4Parser.BoolTypeContext ctx)
    {
        return new BooleanType(ctx.getStart());
    }

    @Override
    public StringType visitStringType(Zserio4Parser.StringTypeContext ctx)
    {
        return new StringType(ctx.getStart());
    }

    @Override
    public FloatType visitFloatType(Zserio4Parser.FloatTypeContext ctx)
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

    private List<Zserio4Parser.IdContext> getPackageNameIds(List<Zserio4Parser.IdContext> qualifiedName)
    {
        return qualifiedName.subList(0, qualifiedName.size() - 1);
    }

    private Zserio4Parser.IdContext getTypeNameId(List<Zserio4Parser.IdContext> qualifiedName)
    {
        return qualifiedName.get(qualifiedName.size() - 1);
    }

    private ZserioType getFieldType(Zserio4Parser.FieldTypeIdContext ctx)
    {
        final ZserioType type = (ZserioType)visitTypeReference(ctx.typeReference());
        if (ctx.fieldArrayRange() == null)
            return type;

        final Expression lengthExpression = (Expression)visit(ctx.fieldArrayRange().expression());
        return new ArrayType(ctx.getStart(), type, lengthExpression, ctx.IMPLICIT() != null);
    }

    private final boolean checkUnusedTypes;
    private final List<TranslationUnit> translationUnits = new ArrayList<TranslationUnit>();
    private final Map<PackageName, Package> packageNameMap = new LinkedHashMap<PackageName, Package>();

    private Package currentPackage = null;
}
