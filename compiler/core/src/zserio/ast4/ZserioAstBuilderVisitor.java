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
            imports.add(visitImportDeclaration(importCtx));

        // package
        Package unitPackage = new Package(ctx.getStart(), visitPackageDeclaration(ctx.packageDeclaration()),
                imports);

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
        if (ctx != null)
            return createPackageName(ctx.qualifiedName().id());
        else
            return PackageName.EMPTY; // default package
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
        final ZserioType type = visitTypeName(ctx.typeName());
        final String name = ctx.id().getText();
        final Expression valueExpression = (Expression)visit(ctx.expression());

        final ConstType constType = new ConstType(ctx.getStart(), currentPackage, type, name,
                valueExpression);
        currentPackage.setLocalType(constType, ctx.id().getStart());

        return constType;
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

        final Expression alignmentExpr = visitFieldAlignment(ctx.fieldAlignment());
        final Expression offsetExpr = visitFieldOffset(ctx.fieldOffset());
        final Expression initializerExpr = visitFieldInitializer(ctx.fieldInitializer());
        final Expression optionalClauseExpr = visitFieldOptionalClause(ctx.fieldOptionalClause());
        final Expression constraintExpr = visitFieldConstraint(ctx.fieldConstraint());

        return new Field(ctx.getStart(), type, name, isAutoOptional, alignmentExpr, offsetExpr, initializerExpr,
                optionalClauseExpr, constraintExpr);
    }

    @Override
    public Expression visitFieldAlignment(Zserio4Parser.FieldAlignmentContext ctx)
    {
        if (ctx == null)
            return null;

        return new Expression(ctx.DECIMAL_LITERAL().getSymbol());
    }

    @Override
    public Expression visitFieldOffset(Zserio4Parser.FieldOffsetContext ctx)
    {
        if (ctx == null)
            return null;

        return (Expression)visit(ctx.expression());
    }

    @Override
    public Expression visitFieldInitializer(Zserio4Parser.FieldInitializerContext ctx)
    {
        if (ctx == null)
            return null;

        return (Expression)visit(ctx.expression());
    }

    @Override
    public Expression visitFieldOptionalClause(Zserio4Parser.FieldOptionalClauseContext ctx)
    {
        if (ctx == null)
            return null;

        return (Expression)visit(ctx.expression());
    }

    @Override
    public Expression visitFieldConstraint(Zserio4Parser.FieldConstraintContext ctx)
    {
        if (ctx == null)
            return null;

        return (Expression)visit(ctx.expression());
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

        final ChoiceDefault choiceDefault = visitChoiceDefault(ctx.choiceDefault());

        final List<FunctionType> functions = new ArrayList<FunctionType>();
        for (Zserio4Parser.FunctionDefinitionContext functionDefinitionCtx : ctx.functionDefinition())
            functions.add(visitFunctionDefinition(functionDefinitionCtx));

        final ChoiceType choiceType = new ChoiceType(ctx.getStart(), currentPackage, name, parameters,
                selectorExpression, choiceCases, choiceDefault, functions);
        currentPackage.setLocalType(choiceType, ctx.id().getStart());

        return choiceType;
    }

    @Override
    public ChoiceCase visitChoiceCases(Zserio4Parser.ChoiceCasesContext ctx)
    {
        List<Expression> caseExpressions = new ArrayList<Expression>();
        for (Zserio4Parser.ChoiceCaseContext choiceCaseCtx : ctx.choiceCase())
            caseExpressions.add(visitChoiceCase(choiceCaseCtx));

        final Field caseField = visitChoiceFieldDefinition(ctx.choiceFieldDefinition());

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
        if (ctx == null)
            return null;

        final Field defaultField = visitChoiceFieldDefinition(ctx.choiceFieldDefinition());
        return new ChoiceDefault(ctx.getStart(), defaultField);
    }

    @Override
    public Field visitChoiceFieldDefinition(Zserio4Parser.ChoiceFieldDefinitionContext ctx)
    {
        if (ctx == null)
            return null;

        final ZserioType type = getFieldType(ctx.fieldTypeId());
        final String name = ctx.fieldTypeId().id().getText();
        final boolean isAutoOptional = false;

        final Expression alignmentExpr = null;
        final Expression offsetExpr = null;
        final Expression initializerExpr = null;
        final Expression optionalClauseExpr = null;
        final Expression constraintExpr = visitFieldConstraint(ctx.fieldConstraint());

        return new Field(ctx.getStart(), type, name, isAutoOptional, alignmentExpr, offsetExpr, initializerExpr,
                optionalClauseExpr, constraintExpr);
    }

    @Override
    public UnionType visitUnionDeclaration(Zserio4Parser.UnionDeclarationContext ctx)
    {
        final String name = ctx.id().getText();

        final List<Parameter> parameters = visitParameterList(ctx.parameterList());

        final List<Field> fields = new ArrayList<Field>();
        for (Zserio4Parser.UnionFieldDefinitionContext fieldCtx : ctx.unionFieldDefinition())
            fields.add(visitChoiceFieldDefinition(fieldCtx.choiceFieldDefinition()));

        final List<FunctionType> functions = new ArrayList<FunctionType>();
        for (Zserio4Parser.FunctionDefinitionContext functionDefinitionCtx : ctx.functionDefinition())
            functions.add(visitFunctionDefinition(functionDefinitionCtx));

        final UnionType unionType = new UnionType(ctx.getStart(), currentPackage, name, parameters, fields,
                functions);
        currentPackage.setLocalType(unionType, ctx.id().getStart());

        return unionType;
    }

    @Override
    public EnumType visitEnumDeclaration(Zserio4Parser.EnumDeclarationContext ctx)
    {
        final ZserioType zserioEnumType = visitTypeName(ctx.typeName());
        final String name = ctx.id().getText();
        final List<EnumItem> enumItems = new ArrayList<EnumItem>();
        for (Zserio4Parser.EnumItemContext enumItemCtx : ctx.enumItem())
            enumItems.add(visitEnumItem(enumItemCtx));

        final EnumType enumType = new EnumType(ctx.getStart(), currentPackage, zserioEnumType, name, enumItems);
        currentPackage.setLocalType(enumType, ctx.id().getStart());

        return enumType;
    }

    @Override
    public EnumItem visitEnumItem(Zserio4Parser.EnumItemContext ctx)
    {
        final String name = ctx.id().getText();
        final Expression valueExpression = (Expression)visit(ctx.expression());

        return new EnumItem(ctx.getStart(), name, valueExpression);
    }

    @Override
    public SqlTableType visitSqlTableDeclaration(Zserio4Parser.SqlTableDeclarationContext ctx)
    {
        final String name = ctx.id(0).getText();
        final String sqlUsingId = ctx.id(1) != null ? ctx.id(1).getText() : null;
        final List<Field> fields = new ArrayList<Field>();
        for (Zserio4Parser.SqlTableFieldDefinitionContext fieldCtx : ctx.sqlTableFieldDefinition())
            fields.add(visitSqlTableFieldDefinition(fieldCtx));
        final SqlConstraint sqlConstraint = visitSqlConstraintDefinition(ctx.sqlConstraintDefinition());
        final boolean sqlWithoutRowId = ctx.sqlWithoutRowId() != null;

        final SqlTableType sqlTableType = new SqlTableType(ctx.getStart(), currentPackage, name, sqlUsingId,
                fields, sqlConstraint, sqlWithoutRowId);
        currentPackage.setLocalType(sqlTableType, ctx.id(0).getStart());

        return sqlTableType;
    }

    @Override
    public Field visitSqlTableFieldDefinition(Zserio4Parser.SqlTableFieldDefinitionContext ctx)
    {
        final boolean isVirtual = ctx.SQL_VIRTUAL() != null;
        final ZserioType type = visitTypeReference(ctx.typeReference());
        final String name = ctx.id().getText();
        final SqlConstraint sqlConstraint = visitSqlConstraint(ctx.sqlConstraint());

        return new Field(ctx.getStart(), type, name, isVirtual, sqlConstraint);
    }

    @Override
    public SqlConstraint visitSqlConstraintDefinition(Zserio4Parser.SqlConstraintDefinitionContext ctx)
    {
        if (ctx == null)
            return null;

        return visitSqlConstraint(ctx.sqlConstraint());
    }

    @Override
    public SqlConstraint visitSqlConstraint(Zserio4Parser.SqlConstraintContext ctx)
    {
        if (ctx == null)
            return null;

        return new SqlConstraint(ctx.getStart(), new Expression(ctx.STRING_LITERAL().getSymbol()));
    }

    @Override
    public SqlDatabaseType visitSqlDatabaseDefinition(Zserio4Parser.SqlDatabaseDefinitionContext ctx)
    {
        final String name = ctx.id().getText();
        final List<Field> fields = new ArrayList<Field>();
        for (Zserio4Parser.SqlDatabaseFieldDefinitionContext fieldCtx : ctx.sqlDatabaseFieldDefinition())
            fields.add(visitSqlDatabaseFieldDefinition(fieldCtx));

        final SqlDatabaseType sqlDatabaseType = new SqlDatabaseType(ctx.getStart(), currentPackage, name,
                fields);
        currentPackage.setLocalType(sqlDatabaseType, ctx.id().getStart());

        return sqlDatabaseType;
    }

    @Override
    public Field visitSqlDatabaseFieldDefinition(Zserio4Parser.SqlDatabaseFieldDefinitionContext ctx)
    {
        final ZserioType type = visitQualifiedName(ctx.sqlTableReference().qualifiedName());
        final String name = ctx.id().getText();

        return new Field(ctx.getStart(), type, name);
    }

    @Override
    public ServiceType visitServiceDefinition(Zserio4Parser.ServiceDefinitionContext ctx)
    {
        final String name = ctx.id().getText();

        List<Rpc> rpcs = new ArrayList<Rpc>();
        for (Zserio4Parser.RpcDeclarationContext rpcDeclarationCtx : ctx.rpcDeclaration())
            rpcs.add(visitRpcDeclaration(rpcDeclarationCtx));

        final ServiceType serviceType = new ServiceType(ctx.getStart(), currentPackage, name, rpcs);
        currentPackage.setLocalType(serviceType, ctx.id().getStart());

        return serviceType;
    }

    @Override
    public Rpc visitRpcDeclaration(Zserio4Parser.RpcDeclarationContext ctx)
    {
        final boolean responseStreaming = ctx.rpcTypeName(0).STREAM() != null;
        final ZserioType responseType = visitQualifiedName(ctx.rpcTypeName(0).qualifiedName());

        final String name = ctx.id().getText();

        final boolean requestStreaming = ctx.rpcTypeName(1).STREAM() != null;
        final ZserioType requestType = visitQualifiedName(ctx.rpcTypeName(1).qualifiedName());

        return new Rpc(ctx.getStart(), name, responseType, responseStreaming, requestType, requestStreaming);
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
        return new Parameter(ctx.getStart(), visitTypeName(ctx.typeName()), ctx.id().getText());
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


        return visitQualifiedName(ctx.qualifiedName());
    }

    @Override
    public ZserioType visitTypeReference(Zserio4Parser.TypeReferenceContext ctx)
    {
        if (ctx.builtinType() != null)
            return (ZserioType)visitBuiltinType(ctx.builtinType());

        final boolean isParameterized = ctx.typeArgumentList() != null;
        final TypeReference typeReference = visitQualifiedName(ctx.qualifiedName(), isParameterized);

        if (isParameterized)
        {
            final List<Expression> arguments = new ArrayList<Expression>();
            for (Zserio4Parser.TypeArgumentContext typeArgumentCtx : ctx.typeArgumentList().typeArgument())
                arguments.add(visitTypeArgument(typeArgumentCtx));
            return new TypeInstantiation(ctx.getStart(), typeReference, arguments);
        }
        else
        {
            return typeReference;
        }
    }

    @Override
    public TypeReference visitQualifiedName(Zserio4Parser.QualifiedNameContext ctx)
    {
        return visitQualifiedName(ctx, false);
    }

    public TypeReference visitQualifiedName(Zserio4Parser.QualifiedNameContext ctx, boolean isParameterized)
    {
        final PackageName referencedPackageName = createPackageName(
                getPackageNameIds(ctx.id()));
        final String referencedTypeName = getTypeNameId(ctx.id()).getText();

        final TypeReference typeReference =
                new TypeReference(ctx.getStart(), referencedPackageName, referencedTypeName, isParameterized);
        currentPackage.addTypeReferenceToResolve(typeReference);

        return typeReference;
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
        final Expression lengthExpression = visitBitFieldLength(ctx.bitFieldLength());
        return new UnsignedBitFieldType(ctx.getStart(), lengthExpression);
    }

    @Override
    public SignedBitFieldType visitSignedBitFieldType(Zserio4Parser.SignedBitFieldTypeContext ctx)
    {
        final Expression lengthExpression = visitBitFieldLength(ctx.bitFieldLength());
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
        final ZserioType type = visitTypeReference(ctx.typeReference());
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
