package zserio.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import zserio.antlr.ZserioParser;
import zserio.antlr.ZserioParserBaseVisitor;
import zserio.antlr.util.ParserException;

/**
 * Implementation of ZserioParserBaseVisitor which builds Zserio AST.
 */
public class ZserioAstBuilder extends ZserioParserBaseVisitor<Object>
{
    /**
     * Gets built AST.
     *
     * @return Root AST node.
     */
    public Root getAst()
    {
        return new Root(packageNameMap);
    }

    /**
     * Custom visit overload which should be called on the parse tree of a single package (translation unit).
     *
     * @param tree          Parse tree for a single package.
     * @param tokenStream   Token stream for a single translation unit.
     * @return Object       Result of the main rule of ZserioParser grammar.
     *                      Should be a package if the method was called on a correct parse tree.
     */
    public Object visit(ParseTree tree, BufferedTokenStream tokenStream)
    {
        docCommentManager.setStream(tokenStream);
        final Object result = visit(tree);
        docCommentManager.printWarnings();
        docCommentManager.resetStream();

        return result;
    }

    @Override
    public Package visitPackageDeclaration(ZserioParser.PackageDeclarationContext ctx)
    {
        // package
        final PackageName packageName = visitPackageNameDefinition(ctx.packageNameDefinition());
        final DocComment docComment = ctx.packageNameDefinition() != null ?
                docCommentManager.findDocComment(ctx.packageNameDefinition()) : null;

        // imports
        final List<Import> imports = new ArrayList<Import>();
        for (ZserioParser.ImportDeclarationContext importCtx : ctx.importDeclaration())
            imports.add(visitImportDeclaration(importCtx));

        // package instance
        final ParserRuleContext packageLocationCtx = ctx.packageNameDefinition() != null
                ? ctx.packageNameDefinition().qualifiedName() : ctx;
        final AstLocation packageLocation = new AstLocation(packageLocationCtx.getStart());
        localTypes = new LinkedHashMap<String, ZserioType>();
        currentPackage = new Package(packageLocation, packageName, imports, localTypes,
                docComment);
        if (packageNameMap.put(currentPackage.getPackageName(), currentPackage) != null)
        {
            // translation unit package already exists, this could happen only for default packages
            throw new ParserException(currentPackage, "Multiple default packages are not allowed!");
        }

        // types declarations
        for (ZserioParser.TypeDeclarationContext typeCtx : ctx.typeDeclaration())
        {
            ZserioType type = (ZserioType)visitTypeDeclaration(typeCtx);
            final String typeName = type.getName();
            final ZserioType addedType = localTypes.put(typeName, type);
            if (addedType != null)
                throw new ParserException(type, "'" + typeName + "' is already defined in this package!");
        }

        localTypes = null;
        final Package unitPackage = currentPackage;
        currentPackage = null;

        return unitPackage;
    }

    @Override
    public PackageName visitPackageNameDefinition(ZserioParser.PackageNameDefinitionContext ctx)
    {
        if (ctx != null)
            return createPackageName(ctx.qualifiedName().id());
        else
            return PackageName.EMPTY; // default package
    }

    @Override
    public Import visitImportDeclaration(ZserioParser.ImportDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id(0).getStart());
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

        return new Import(location, importedPackageName, importedTypeName);
    }

    @Override
    public ConstType visitConstDeclaration(ZserioParser.ConstDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final ZserioType type = visitTypeName(ctx.typeName());
        final String name = ctx.id().getText();
        final Expression valueExpression = (Expression)visit(ctx.expression());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final ConstType constType = new ConstType(location, currentPackage, type, name, valueExpression,
                docComment);

        return constType;
    }

    @Override
    public Subtype visitSubtypeDeclaration(ZserioParser.SubtypeDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final ZserioType targetType = visitTypeName(ctx.typeName());
        final String name = ctx.id().getText();

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final Subtype subtype = new Subtype(location, currentPackage, targetType, name, docComment);

        return subtype;
    }

    @Override
    public StructureType visitStructureDeclaration(ZserioParser.StructureDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final String name = ctx.id().getText();

        final List<String> templateParameters = visitTemplateParameters(ctx.templateParameters());

        final List<Parameter> typeParameters = visitTypeParameters(ctx.typeParameters());

        final List<Field> fields = new ArrayList<Field>();
        for (ZserioParser.StructureFieldDefinitionContext fieldCtx : ctx.structureFieldDefinition())
            fields.add(visitStructureFieldDefinition(fieldCtx));

        final List<FunctionType> functions = new ArrayList<FunctionType>();
        for (ZserioParser.FunctionDefinitionContext functionDefinitionCtx : ctx.functionDefinition())
            functions.add(visitFunctionDefinition(functionDefinitionCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final StructureType structureType = new StructureType(location, currentPackage, name,
                templateParameters, typeParameters, fields, functions, docComment);

        return structureType;
    }

    @Override
    public Field visitStructureFieldDefinition(ZserioParser.StructureFieldDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.fieldTypeId().id().getStart());
        final ZserioType type = getFieldType(ctx.fieldTypeId());
        final String name = ctx.fieldTypeId().id().getText();
        final boolean isAutoOptional = ctx.OPTIONAL() != null;

        final Expression alignmentExpr = visitFieldAlignment(ctx.fieldAlignment());
        final Expression offsetExpr = visitFieldOffset(ctx.fieldOffset());
        final Expression initializerExpr = visitFieldInitializer(ctx.fieldInitializer());
        final Expression optionalClauseExpr = visitFieldOptionalClause(ctx.fieldOptionalClause());
        final Expression constraintExpr = visitFieldConstraint(ctx.fieldConstraint());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Field(location, type, name, isAutoOptional, alignmentExpr, offsetExpr, initializerExpr,
                optionalClauseExpr, constraintExpr, docComment);
    }

    @Override
    public Expression visitFieldAlignment(ZserioParser.FieldAlignmentContext ctx)
    {
        if (ctx == null)
            return null;

        return new Expression(ctx.DECIMAL_LITERAL().getSymbol(), currentPackage);
    }

    @Override
    public Expression visitFieldOffset(ZserioParser.FieldOffsetContext ctx)
    {
        if (ctx == null)
            return null;

        return (Expression)visit(ctx.expression());
    }

    @Override
    public Expression visitFieldInitializer(ZserioParser.FieldInitializerContext ctx)
    {
        if (ctx == null)
            return null;

        return (Expression)visit(ctx.expression());
    }

    @Override
    public Expression visitFieldOptionalClause(ZserioParser.FieldOptionalClauseContext ctx)
    {
        if (ctx == null)
            return null;

        return (Expression)visit(ctx.expression());
    }

    @Override
    public Expression visitFieldConstraint(ZserioParser.FieldConstraintContext ctx)
    {
        if (ctx == null)
            return null;

        return (Expression)visit(ctx.expression());
    }

    @Override
    public ChoiceType visitChoiceDeclaration(ZserioParser.ChoiceDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final String name = ctx.id().getText();

        final List<String> templateParameters = visitTemplateParameters(ctx.templateParameters());

        final List<Parameter> typeParameters = visitTypeParameters(ctx.typeParameters());

        final Expression selectorExpression = (Expression)visit(ctx.expression());

        final List<ChoiceCase> choiceCases = new ArrayList<ChoiceCase>();
        for (ZserioParser.ChoiceCasesContext choiceCasesCtx : ctx.choiceCases())
            choiceCases.add(visitChoiceCases(choiceCasesCtx));

        final ChoiceDefault choiceDefault = visitChoiceDefault(ctx.choiceDefault());

        final List<FunctionType> functions = new ArrayList<FunctionType>();
        for (ZserioParser.FunctionDefinitionContext functionDefinitionCtx : ctx.functionDefinition())
            functions.add(visitFunctionDefinition(functionDefinitionCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final ChoiceType choiceType = new ChoiceType(location, currentPackage, name,
                templateParameters, typeParameters, selectorExpression, choiceCases, choiceDefault, functions,
                docComment);

        return choiceType;
    }

    @Override
    public ChoiceCase visitChoiceCases(ZserioParser.ChoiceCasesContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        List<ChoiceCaseExpression> caseExpressions = new ArrayList<ChoiceCaseExpression>();
        for (ZserioParser.ChoiceCaseContext choiceCaseCtx : ctx.choiceCase())
            caseExpressions.add(visitChoiceCase(choiceCaseCtx));

        final Field caseField = visitChoiceFieldDefinition(ctx.choiceFieldDefinition());

        return new ChoiceCase(location, caseExpressions, caseField);
    }

    @Override
    public ChoiceCaseExpression visitChoiceCase(ZserioParser.ChoiceCaseContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final DocComment docComment = docCommentManager.findDocComment(ctx);
        return new ChoiceCaseExpression(location, (Expression)visit(ctx.expression()), docComment);
    }

    @Override
    public ChoiceDefault visitChoiceDefault(ZserioParser.ChoiceDefaultContext ctx)
    {
        if (ctx == null)
            return null;

        final AstLocation location = new AstLocation(ctx.getStart());
        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final Field defaultField = visitChoiceFieldDefinition(ctx.choiceFieldDefinition());
        return new ChoiceDefault(location, defaultField, docComment);
    }

    @Override
    public Field visitChoiceFieldDefinition(ZserioParser.ChoiceFieldDefinitionContext ctx)
    {
        if (ctx == null)
            return null;

        final ZserioType type = getFieldType(ctx.fieldTypeId());
        final ParserRuleContext nameCtx = ctx.fieldTypeId().id();
        final AstLocation location = new AstLocation(nameCtx.getStart());
        final String name = nameCtx.getText();
        final Expression constraintExpr = visitFieldConstraint(ctx.fieldConstraint());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Field(location, type, name, constraintExpr, docComment);
    }

    @Override
    public UnionType visitUnionDeclaration(ZserioParser.UnionDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final String name = ctx.id().getText();

        final List<String> templateParameters = visitTemplateParameters(ctx.templateParameters());

        final List<Parameter> typeParameters = visitTypeParameters(ctx.typeParameters());

        final List<Field> fields = new ArrayList<Field>();
        for (ZserioParser.UnionFieldDefinitionContext fieldCtx : ctx.unionFieldDefinition())
            fields.add(visitChoiceFieldDefinition(fieldCtx.choiceFieldDefinition()));

        final List<FunctionType> functions = new ArrayList<FunctionType>();
        for (ZserioParser.FunctionDefinitionContext functionDefinitionCtx : ctx.functionDefinition())
            functions.add(visitFunctionDefinition(functionDefinitionCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final UnionType unionType = new UnionType(location, currentPackage, name,
                templateParameters, typeParameters, fields, functions, docComment);

        return unionType;
    }

    @Override
    public EnumType visitEnumDeclaration(ZserioParser.EnumDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final ZserioType zserioEnumType = visitTypeName(ctx.typeName());
        final String name = ctx.id().getText();
        final List<EnumItem> enumItems = new ArrayList<EnumItem>();
        for (ZserioParser.EnumItemContext enumItemCtx : ctx.enumItem())
            enumItems.add(visitEnumItem(enumItemCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final EnumType enumType = new EnumType(location, currentPackage, zserioEnumType, name, enumItems,
                docComment);

        return enumType;
    }

    @Override
    public EnumItem visitEnumItem(ZserioParser.EnumItemContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final String name = ctx.id().getText();
        final ZserioParser.ExpressionContext exprCtx = ctx.expression();
        final Expression valueExpression = (exprCtx != null) ? (Expression)visit(exprCtx) : null;

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new EnumItem(location, name, valueExpression, docComment);
    }

    @Override
    public SqlTableType visitSqlTableDeclaration(ZserioParser.SqlTableDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id(0).getStart());
        final String name = ctx.id(0).getText();

        final List<String> templateParameters = visitTemplateParameters(ctx.templateParameters());

        final String sqlUsingId = ctx.id(1) != null ? ctx.id(1).getText() : null;
        final List<Field> fields = new ArrayList<Field>();
        for (ZserioParser.SqlTableFieldDefinitionContext fieldCtx : ctx.sqlTableFieldDefinition())
            fields.add(visitSqlTableFieldDefinition(fieldCtx));

        final SqlConstraint sqlConstraint = visitSqlConstraintDefinition(ctx.sqlConstraintDefinition());
        final boolean sqlWithoutRowId = ctx.sqlWithoutRowId() != null;

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final SqlTableType sqlTableType = new SqlTableType(location, currentPackage, name,
                templateParameters, sqlUsingId, fields, sqlConstraint, sqlWithoutRowId, docComment);

        return sqlTableType;
    }

    @Override
    public Field visitSqlTableFieldDefinition(ZserioParser.SqlTableFieldDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final boolean isVirtual = ctx.SQL_VIRTUAL() != null;
        final ZserioType type = visitTypeReference(ctx.typeReference());
        final String name = ctx.id().getText();
        final SqlConstraint sqlConstraint = visitSqlConstraint(ctx.sqlConstraint());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Field(location, type, name, isVirtual, (sqlConstraint == null) ?
                SqlConstraint.createDefaultFieldConstraint(currentPackage) : sqlConstraint, docComment);
    }

    @Override
    public SqlConstraint visitSqlConstraintDefinition(ZserioParser.SqlConstraintDefinitionContext ctx)
    {
        if (ctx == null)
            return null;

        return visitSqlConstraint(ctx.sqlConstraint());
    }

    @Override
    public SqlConstraint visitSqlConstraint(ZserioParser.SqlConstraintContext ctx)
    {
        if (ctx == null)
            return null;

        final AstLocation location = new AstLocation(ctx.getStart());
        return new SqlConstraint(location, new Expression(ctx.STRING_LITERAL().getSymbol(),
                currentPackage));
    }

    @Override
    public SqlDatabaseType visitSqlDatabaseDefinition(ZserioParser.SqlDatabaseDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final String name = ctx.id().getText();
        final List<Field> fields = new ArrayList<Field>();
        for (ZserioParser.SqlDatabaseFieldDefinitionContext fieldCtx : ctx.sqlDatabaseFieldDefinition())
            fields.add(visitSqlDatabaseFieldDefinition(fieldCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final SqlDatabaseType sqlDatabaseType = new SqlDatabaseType(location, currentPackage, name, fields,
                docComment);

        return sqlDatabaseType;
    }

    @Override
    public Field visitSqlDatabaseFieldDefinition(ZserioParser.SqlDatabaseFieldDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final ZserioType type = visitSqlTableReference(ctx.sqlTableReference());
        final String name = ctx.id().getText();

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Field(location, type, name, docComment);
    }

    @Override
    public TypeReference visitSqlTableReference(ZserioParser.SqlTableReferenceContext ctx)
    {
        final List<ZserioType> templateArguments = visitTemplateArguments(ctx.templateArguments());
        return visitQualifiedName(ctx.qualifiedName(), templateArguments);
    }

    @Override
    public ServiceType visitServiceDefinition(ZserioParser.ServiceDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final String name = ctx.id().getText();

        List<Rpc> rpcs = new ArrayList<Rpc>();
        for (ZserioParser.RpcDefinitionContext rpcDefinitionCtx : ctx.rpcDefinition())
            rpcs.add(visitRpcDefinition(rpcDefinitionCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final ServiceType serviceType = new ServiceType(location, currentPackage, name, rpcs, docComment);

        return serviceType;
    }

    @Override
    public Rpc visitRpcDefinition(ZserioParser.RpcDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());

        final boolean responseStreaming = ctx.rpcTypeName(0).STREAM() != null;
        final List<ZserioType> responseTemplateArguments =
                visitTemplateArguments(ctx.rpcTypeName(0).templateArguments());
        final ZserioType responseType =
                visitQualifiedName(ctx.rpcTypeName(0).qualifiedName(), responseTemplateArguments);

        final String name = ctx.id().getText();

        final boolean requestStreaming = ctx.rpcTypeName(1).STREAM() != null;
        final List<ZserioType> requestTemplateArguments =
                visitTemplateArguments(ctx.rpcTypeName(1).templateArguments());
        final ZserioType requestType =
                visitQualifiedName(ctx.rpcTypeName(1).qualifiedName(), requestTemplateArguments);

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Rpc(location, name, responseType, responseStreaming, requestType,
                requestStreaming, docComment);
    }

    @Override
    public FunctionType visitFunctionDefinition(ZserioParser.FunctionDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.functionName().getStart());
        final ZserioType returnType = visitTypeName(ctx.functionType().typeName());
        final String name = ctx.functionName().getText();
        final Expression resultExpression = (Expression)visit(ctx.functionBody().expression());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new FunctionType(location, currentPackage, returnType, name,
                resultExpression, docComment);
    }

    @Override
    public List<Parameter> visitTypeParameters(ZserioParser.TypeParametersContext ctx)
    {
        List<Parameter> parameters = new ArrayList<Parameter>();
        if (ctx != null)
        {
            for (ZserioParser.ParameterDefinitionContext parameterDefinitionCtx : ctx.parameterDefinition())
                parameters.add((Parameter)visitParameterDefinition(parameterDefinitionCtx));
        }
        return parameters;
    }

    @Override
    public Object visitParameterDefinition(ZserioParser.ParameterDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        return new Parameter(location, visitTypeName(ctx.typeName()), ctx.id().getText());
    }

    @Override
    public List<String> visitTemplateParameters(ZserioParser.TemplateParametersContext ctx)
    {
        final List<String> parameters = new ArrayList<String>();
        if (ctx != null)
        {
            for (ZserioParser.IdContext idCtx : ctx.id())
            {
                parameters.add(idCtx.getText());
            }
        }
        return parameters;
    }

    @Override
    public List<ZserioType> visitTemplateArguments(ZserioParser.TemplateArgumentsContext ctx)
    {
        final ArrayList<ZserioType> templateArguments = new ArrayList<ZserioType>();
        if (ctx != null)
        {
            isTemplateArgument = true;
            for (ZserioParser.TypeNameContext typeNameCtx : ctx.typeName())
                templateArguments.add(visitTypeName(typeNameCtx));
            isTemplateArgument = false;
        }
        return templateArguments;
    }

    @Override
    public Object visitParenthesizedExpression(ZserioParser.ParenthesizedExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(location, currentPackage, ctx.operator, operand1);
    }

    @Override
    public Object visitFunctionCallExpression(ZserioParser.FunctionCallExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(location, currentPackage, ctx.operator, operand1);
    }

    @Override
    public Object visitArrayExpression(ZserioParser.ArrayExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitDotExpression(ZserioParser.DotExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression.ExpressionFlag expressionFlag = (isInDotExpression) ? Expression.ExpressionFlag.NONE :
            Expression.ExpressionFlag.IS_TOP_LEVEL_DOT;
        isInDotExpression = true;
        final Expression operand1 = (Expression)visit(ctx.expression());
        final Expression operand2 = new Expression(ctx.id().ID().getSymbol(), currentPackage,
                Expression.ExpressionFlag.IS_DOT_RIGHT_OPERAND_ID);
        isInDotExpression = false;

        return new Expression(location, currentPackage, ctx.operator, expressionFlag, operand1, operand2);
    }

    @Override
    public Object visitLengthofExpression(ZserioParser.LengthofExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(location, currentPackage, ctx.operator, operand1);
    }

    @Override
    public Object visitValueofExpression(ZserioParser.ValueofExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(location, currentPackage, ctx.operator, operand1);
    }

    @Override
    public Object visitNumbitsExpression(ZserioParser.NumbitsExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(location, currentPackage, ctx.operator, operand1);
    }

    @Override
    public Object visitUnaryExpression(ZserioParser.UnaryExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());
        return new Expression(location, currentPackage, ctx.operator, operand1);
    }

    @Override
    public Object visitMultiplicativeExpression(ZserioParser.MultiplicativeExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitAdditiveExpression(ZserioParser.AdditiveExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitShiftExpression(ZserioParser.ShiftExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        int tokenType = ctx.operator.getType();
        if (tokenType == ZserioParser.GT)
        {
            tokenType = ZserioParser.RSHIFT;

            // check that there is not space between the two '>'
            if (ctx.GT(0).getSymbol().getCharPositionInLine() + 1 !=
                    ctx.GT(1).getSymbol().getCharPositionInLine())
                throw new ParserException(ctx.GT().get(0).getSymbol(), "Operator >> cannot contain spaces!");
        }
        final String tokenText = tokenType == ZserioParser.RSHIFT ? RSHIFT_OPERATOR : ctx.operator.getText();

        return new Expression(location, currentPackage, tokenType, tokenText, operand1, operand2);
    }

    @Override
    public Object visitRelationalExpression(ZserioParser.RelationalExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitEqualityExpression(ZserioParser.EqualityExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitBitwiseAndExpression(ZserioParser.BitwiseAndExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitBitwiseXorExpression(ZserioParser.BitwiseXorExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitBitwiseOrExpression(ZserioParser.BitwiseOrExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitLogicalAndExpression(ZserioParser.LogicalAndExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitLogicalOrExpression(ZserioParser.LogicalOrExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2);
    }

    @Override
    public Object visitTernaryExpression(ZserioParser.TernaryExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        final Expression operand3 = (Expression)visit(ctx.expression(2));
        return new Expression(location, currentPackage, ctx.operator, operand1, operand2, operand3);
    }

    @Override
    public Object visitLiteralExpression(ZserioParser.LiteralExpressionContext ctx)
    {
        return new Expression(ctx.literal().getStart(), currentPackage);
    }

    @Override
    public Object visitIndexExpression(ZserioParser.IndexExpressionContext ctx)
    {
        return new Expression(ctx.INDEX().getSymbol(), currentPackage);
    }

    @Override
    public Object visitIdentifierExpression(ZserioParser.IdentifierExpressionContext ctx)
    {
        final Expression.ExpressionFlag expressionFlag = (isInDotExpression) ?
                Expression.ExpressionFlag.IS_DOT_LEFT_OPERAND_ID : Expression.ExpressionFlag.NONE;

        return new Expression(ctx.id().ID().getSymbol(), currentPackage, expressionFlag);
    }

    @Override
    public ZserioType visitTypeName(ZserioParser.TypeNameContext ctx)
    {
        if (ctx.builtinType() != null)
            return (ZserioType)visitBuiltinType(ctx.builtinType());

        final List<ZserioType> templateArguments = visitTemplateArguments(ctx.templateArguments());

        return visitQualifiedName(ctx.qualifiedName(), templateArguments);
    }

    @Override
    public ZserioType visitTypeReference(ZserioParser.TypeReferenceContext ctx)
    {
        if (ctx.builtinType() != null)
            return (ZserioType)visitBuiltinType(ctx.builtinType());

        final List<ZserioType> templateArguments = visitTemplateArguments(ctx.templateArguments());

        final boolean hasArguments = ctx.typeArguments() != null;
        final TypeReference typeReference = visitQualifiedName(ctx.qualifiedName(), templateArguments,
                !hasArguments);

        if (hasArguments)
        {
            final AstLocation location = new AstLocation(ctx.getStart());
            final List<Expression> arguments = new ArrayList<Expression>();
            for (ZserioParser.TypeArgumentContext typeArgumentCtx : ctx.typeArguments().typeArgument())
                arguments.add(visitTypeArgument(typeArgumentCtx));
            return new TypeInstantiation(location, typeReference, arguments);
        }
        else
        {
            return typeReference;
        }
    }

    @Override
    public TypeReference visitQualifiedName(ZserioParser.QualifiedNameContext ctx)
    {
        return visitQualifiedName(ctx, new ArrayList<ZserioType>());
    }

    public TypeReference visitQualifiedName(ZserioParser.QualifiedNameContext ctx,
            List<ZserioType> templateArguments)
    {
        return visitQualifiedName(ctx, templateArguments, false);
    }

    public TypeReference visitQualifiedName(ZserioParser.QualifiedNameContext ctx,
            List<ZserioType> templateArguments, boolean checkIfNeedsParameters)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final PackageName referencedPackageName = createPackageName(
                getPackageNameIds(ctx.id()));
        final String referencedTypeName = getTypeNameId(ctx.id()).getText();

        final TypeReference typeReference = new TypeReference(location, currentPackage, referencedPackageName,
                referencedTypeName, templateArguments, isTemplateArgument, checkIfNeedsParameters);

        return typeReference;
    }

    @Override
    public Expression visitTypeArgument(ZserioParser.TypeArgumentContext ctx)
    {
        if (ctx.EXPLICIT() != null)
        {
            final AstLocation location = new AstLocation(ctx.getStart());
            return new Expression(location, currentPackage, ctx.id().ID().getSymbol(),
                    Expression.ExpressionFlag.IS_EXPLICIT);
        }
        else
        {
            return (Expression)visit(ctx.expression());
        }
    }

    @Override
    public StdIntegerType visitIntType(ZserioParser.IntTypeContext ctx)
    {
        return new StdIntegerType(ctx.getStart());
    }

    @Override
    public VarIntegerType visitVarintType(ZserioParser.VarintTypeContext ctx)
    {
        return new VarIntegerType(ctx.getStart());
    }

    @Override
    public UnsignedBitFieldType visitUnsignedBitFieldType(ZserioParser.UnsignedBitFieldTypeContext ctx)
    {
        final Expression lengthExpression = visitBitFieldLength(ctx.bitFieldLength());
        return new UnsignedBitFieldType(ctx.getStart(), lengthExpression);
    }

    @Override
    public SignedBitFieldType visitSignedBitFieldType(ZserioParser.SignedBitFieldTypeContext ctx)
    {
        final Expression lengthExpression = visitBitFieldLength(ctx.bitFieldLength());
        return new SignedBitFieldType(ctx.getStart(), lengthExpression);
    }

    @Override
    public Expression visitBitFieldLength(ZserioParser.BitFieldLengthContext ctx)
    {
        if (ctx.DECIMAL_LITERAL() != null)
            return new Expression(ctx.DECIMAL_LITERAL().getSymbol(), currentPackage);

        return (Expression)visit(ctx.expression());
    }

    @Override
    public BooleanType visitBoolType(ZserioParser.BoolTypeContext ctx)
    {
        return new BooleanType(ctx.getStart());
    }

    @Override
    public StringType visitStringType(ZserioParser.StringTypeContext ctx)
    {
        return new StringType(ctx.getStart());
    }

    @Override
    public FloatType visitFloatType(ZserioParser.FloatTypeContext ctx)
    {
        return new FloatType(ctx.getStart());
    }

    private PackageName createPackageName(List<ZserioParser.IdContext> ids)
    {
        final PackageName.Builder packageNameBuilder = new PackageName.Builder();
        for (ZserioParser.IdContext id : ids)
            packageNameBuilder.addId(id.getText());
        return packageNameBuilder.get();
    }

    private List<ZserioParser.IdContext> getPackageNameIds(List<ZserioParser.IdContext> qualifiedName)
    {
        return qualifiedName.subList(0, qualifiedName.size() - 1);
    }

    private ZserioParser.IdContext getTypeNameId(List<ZserioParser.IdContext> qualifiedName)
    {
        return qualifiedName.get(qualifiedName.size() - 1);
    }

    private ZserioType getFieldType(ZserioParser.FieldTypeIdContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final ZserioType type = visitTypeReference(ctx.typeReference());
        if (ctx.fieldArrayRange() == null)
            return type;

        final ZserioParser.ExpressionContext exprCtx = ctx.fieldArrayRange().expression();
        final Expression lengthExpression = (exprCtx != null) ? (Expression)visit(exprCtx) : null;
        return new ArrayType(location, type, lengthExpression, ctx.IMPLICIT() != null);
    }

    private final DocCommentManager docCommentManager = new DocCommentManager();
    private final LinkedHashMap<PackageName, Package> packageNameMap =
            new LinkedHashMap<PackageName, Package>();

    private Package currentPackage = null;
    private LinkedHashMap<String, ZserioType> localTypes = null;
    private boolean isInDotExpression = false;
    private boolean isTemplateArgument = false;

    private static final String RSHIFT_OPERATOR = ">>";
}
