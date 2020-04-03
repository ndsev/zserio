package zserio.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import zserio.antlr.ZserioParser;
import zserio.antlr.ZserioParserBaseVisitor;

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
                ? ctx.packageNameDefinition().id(0) : ctx;
        final AstLocation packageLocation = new AstLocation(packageLocationCtx.getStart());
        currentPackage = new Package(packageLocation, packageName, imports, docComment);
        if (packageNameMap.put(currentPackage.getPackageName(), currentPackage) != null)
        {
            // translation unit package already exists, this could happen only for default packages
            throw new ParserException(currentPackage, "Multiple default packages are not allowed!");
        }

        for (ZserioParser.LanguageDirectiveContext languageDirectiveCtx : ctx.languageDirective())
            visitLanguageDirective(languageDirectiveCtx);

        final Package unitPackage = currentPackage;
        currentPackage = null;

        return unitPackage;
    }

    @Override
    public PackageName visitPackageNameDefinition(ZserioParser.PackageNameDefinitionContext ctx)
    {
        if (ctx != null)
            return createPackageName(ctx.id());
        else
            return PackageName.EMPTY; // default package
    }

    @Override
    public Import visitImportDeclaration(ZserioParser.ImportDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id(0).getStart());
        String importedSymbolName = null;
        PackageName importedPackageName = null;

        if (ctx.MULTIPLY() == null)
        {
            importedPackageName = createPackageName(getPackageNameIds(ctx.id()));
            importedSymbolName = getSymbolNameId(ctx.id()).getText();
        }
        else
        {
            importedPackageName = createPackageName(ctx.id());
        }

        return new Import(location, importedPackageName, importedSymbolName);
    }

    @Override
    public ZserioType visitTypeDeclaration(ZserioParser.TypeDeclarationContext ctx)
    {
        ZserioType type = (ZserioType)super.visitTypeDeclaration(ctx);
        currentPackage.addType(type);
        return type;
    }

    @Override
    public Constant visitConstDeclaration(ZserioParser.ConstDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final TypeInstantiation typeInstantiation = visitTypeInstantiation(ctx.typeInstantiation());
        final String name = ctx.id().getText();
        final Expression valueExpression = (Expression)visit(ctx.expression());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final Constant constant = new Constant(location, currentPackage, typeInstantiation, name,
                valueExpression, docComment);

        currentPackage.addSymbol(name, constant);

        return constant;
    }

    @Override
    public Subtype visitSubtypeDeclaration(ZserioParser.SubtypeDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final TypeReference targetTypeReference = visitTypeReference(ctx.typeReference());
        final String name = ctx.id().getText();

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final Subtype subtype = new Subtype(location, currentPackage, targetTypeReference, name, docComment);

        return subtype;
    }

    @Override
    public StructureType visitStructureDeclaration(ZserioParser.StructureDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final String name = ctx.id().getText();

        final List<TemplateParameter> templateParameters = visitTemplateParameters(ctx.templateParameters());

        final List<Parameter> typeParameters = visitTypeParameters(ctx.typeParameters());

        final List<Field> fields = new ArrayList<Field>();
        for (ZserioParser.StructureFieldDefinitionContext fieldCtx : ctx.structureFieldDefinition())
            fields.add(visitStructureFieldDefinition(fieldCtx));

        final List<Function> functions = new ArrayList<Function>();
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
        final FieldTypeId fieldTypeId = visitFieldTypeId(ctx.fieldTypeId());
        final boolean isAutoOptional = ctx.OPTIONAL() != null;

        final Expression alignmentExpr = visitFieldAlignment(ctx.fieldAlignment());
        final Expression offsetExpr = visitFieldOffset(ctx.fieldOffset());
        final Expression initializerExpr = visitFieldInitializer(ctx.fieldInitializer());
        final Expression optionalClauseExpr = visitFieldOptionalClause(ctx.fieldOptionalClause());
        final Expression constraintExpr = visitFieldConstraint(ctx.fieldConstraint());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Field(fieldTypeId.getLocation(), fieldTypeId.getTypeInstantation(), fieldTypeId.getName(),
                isAutoOptional, alignmentExpr, offsetExpr, initializerExpr, optionalClauseExpr, constraintExpr,
                docComment);
    }

    @Override
    public Expression visitFieldAlignment(ZserioParser.FieldAlignmentContext ctx)
    {
        if (ctx == null)
            return null;

        final Token token = ctx.DECIMAL_LITERAL().getSymbol();
        final AstLocation location = new AstLocation(token);

        return new Expression(location, currentPackage, token.getType(), token.getText(),
                Expression.ExpressionFlag.NONE);
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

        final List<TemplateParameter> templateParameters = visitTemplateParameters(ctx.templateParameters());

        final List<Parameter> typeParameters = visitTypeParameters(ctx.typeParameters());

        final Expression selectorExpression = (Expression)visit(ctx.expression());

        final List<ChoiceCase> choiceCases = new ArrayList<ChoiceCase>();
        for (ZserioParser.ChoiceCasesContext choiceCasesCtx : ctx.choiceCases())
            choiceCases.add(visitChoiceCases(choiceCasesCtx));

        final ChoiceDefault choiceDefault = visitChoiceDefault(ctx.choiceDefault());

        final List<Function> functions = new ArrayList<Function>();
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

        final FieldTypeId fieldTypeId = visitFieldTypeId(ctx.fieldTypeId());
        final Expression constraintExpr = visitFieldConstraint(ctx.fieldConstraint());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Field(fieldTypeId.getLocation(), fieldTypeId.getTypeInstantation(), fieldTypeId.getName(),
                constraintExpr, docComment);
    }

    @Override
    public UnionType visitUnionDeclaration(ZserioParser.UnionDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final String name = ctx.id().getText();

        final List<TemplateParameter> templateParameters = visitTemplateParameters(ctx.templateParameters());

        final List<Parameter> typeParameters = visitTypeParameters(ctx.typeParameters());

        final List<Field> fields = new ArrayList<Field>();
        for (ZserioParser.UnionFieldDefinitionContext fieldCtx : ctx.unionFieldDefinition())
            fields.add(visitChoiceFieldDefinition(fieldCtx.choiceFieldDefinition()));

        final List<Function> functions = new ArrayList<Function>();
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
        final TypeInstantiation enumTypeInstantiation = visitTypeInstantiation(ctx.typeInstantiation());
        final String name = ctx.id().getText();
        final List<EnumItem> enumItems = new ArrayList<EnumItem>();
        for (ZserioParser.EnumItemContext enumItemCtx : ctx.enumItem())
            enumItems.add(visitEnumItem(enumItemCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final EnumType enumType = new EnumType(location, currentPackage, enumTypeInstantiation, name, enumItems,
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
    public BitmaskType visitBitmaskDeclaration(ZserioParser.BitmaskDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final TypeInstantiation typeInstantiation = visitTypeInstantiation(ctx.typeInstantiation());
        final String name = ctx.id().getText();
        final List<BitmaskValue> bitmaskValues = new ArrayList<BitmaskValue>();
        for (ZserioParser.BitmaskValueContext bitmaskValueCtx : ctx.bitmaskValue())
            bitmaskValues.add(visitBitmaskValue(bitmaskValueCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final BitmaskType enumType = new BitmaskType(location, currentPackage, typeInstantiation, name,
                bitmaskValues, docComment);

        return enumType;
    }

    @Override
    public BitmaskValue visitBitmaskValue(ZserioParser.BitmaskValueContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final String name = ctx.id().getText();
        final ZserioParser.ExpressionContext exprCtx = ctx.expression();
        final Expression valueExpression = (exprCtx != null) ? (Expression)visit(exprCtx) : null;

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new BitmaskValue(location, name, valueExpression, docComment);
    }

    @Override
    public SqlTableType visitSqlTableDeclaration(ZserioParser.SqlTableDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id(0).getStart());
        final String name = ctx.id(0).getText();

        final List<TemplateParameter> templateParameters = visitTemplateParameters(ctx.templateParameters());

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
        final TypeInstantiation typeInstantiation = visitTypeInstantiation(ctx.typeInstantiation());
        final String name = ctx.id().getText();
        final SqlConstraint sqlConstraint = visitSqlConstraint(ctx.sqlConstraint());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Field(location, typeInstantiation, name, isVirtual, sqlConstraint, docComment);
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

        final AstLocation location = new AstLocation(ctx.expression().getStart());
        final Expression constraintExpr = (Expression)visit(ctx.expression());

        return new SqlConstraint(location, constraintExpr);
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
        final TypeInstantiation fieldTypeInstantiation = visitTypeInstantiation(ctx.typeInstantiation());
        final String name = ctx.id().getText();

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Field(location, fieldTypeInstantiation, name, docComment);
    }

    @Override
    public ServiceType visitServiceDefinition(ZserioParser.ServiceDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final String name = ctx.id().getText();

        List<ServiceMethod> methods = new ArrayList<ServiceMethod>();
        for (ZserioParser.ServiceMethodDefinitionContext methodDefinitionCtx : ctx.serviceMethodDefinition())
            methods.add(visitServiceMethodDefinition(methodDefinitionCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final ServiceType serviceType = new ServiceType(location, currentPackage, name, methods, docComment);

        return serviceType;
    }

    @Override
    public ServiceMethod visitServiceMethodDefinition(ZserioParser.ServiceMethodDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());

        final TypeReference responseTypeReference = visitTypeReference(ctx.typeReference(0));

        final String name = ctx.id().getText();

        final TypeReference requestTypeReference = visitTypeReference(ctx.typeReference(1));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new ServiceMethod(location, name, responseTypeReference, requestTypeReference, docComment);
    }

    @Override
    public PubsubType visitPubsubDefinition(ZserioParser.PubsubDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());

        final String name = ctx.id().getText();

        List<PubsubMessage> messages = new ArrayList<PubsubMessage>();
        for (ZserioParser.PubsubMessageDefinitionContext messageDefinitionCtx : ctx.pubsubMessageDefinition())
            messages.add(visitPubsubMessageDefinition(messageDefinitionCtx));

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        final PubsubType pubsubType = new PubsubType(location, currentPackage, name, messages, docComment);

        return pubsubType;
    }

    @Override
    public PubsubMessage visitPubsubMessageDefinition(ZserioParser.PubsubMessageDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());

        String topicDefinition = ctx.topicDefinition().STRING_LITERAL().getText();
        // strip quotes around the string literal
        topicDefinition = topicDefinition.substring(1, topicDefinition.length() - 1);

        final boolean isPublished =
                ctx.topicDefinition().PUBLISH() != null || ctx.topicDefinition().PUBSUB() != null;

        final boolean isSubscribed =
                ctx.topicDefinition().SUBSCRIBE() != null || ctx.topicDefinition().PUBSUB() != null;

        final TypeReference typeReference = visitTypeReference(ctx.typeReference());

        final String name = ctx.id().getText();

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new PubsubMessage(location, name, typeReference, topicDefinition, isPublished, isSubscribed,
                docComment);
    }

    @Override
    public Function visitFunctionDefinition(ZserioParser.FunctionDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.functionName().getStart());
        final TypeReference returnTypeReference = visitTypeReference(ctx.functionType().typeReference());
        final String name = ctx.functionName().getText();
        final Expression resultExpression = (Expression)visit(ctx.functionBody().expression());

        final DocComment docComment = docCommentManager.findDocComment(ctx);

        return new Function(location, returnTypeReference, name, resultExpression, docComment);
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
        return new Parameter(location, visitTypeReference(ctx.typeReference()), ctx.id().getText());
    }

    @Override
    public List<TemplateParameter> visitTemplateParameters(ZserioParser.TemplateParametersContext ctx)
    {
        final List<TemplateParameter> parameters = new ArrayList<TemplateParameter>();
        if (ctx != null)
        {
            for (ZserioParser.IdContext idCtx : ctx.id())
            {
                final AstLocation location = new AstLocation(idCtx.getStart());
                parameters.add(new TemplateParameter(location, idCtx.getText()));
            }
        }
        return parameters;
    }

    @Override
    public List<TemplateArgument> visitTemplateArguments(ZserioParser.TemplateArgumentsContext ctx)
    {
        final ArrayList<TemplateArgument> templateArguments = new ArrayList<TemplateArgument>();
        if (ctx != null)
        {
            for (ZserioParser.TemplateArgumentContext templateArgumentCtx : ctx.templateArgument())
                templateArguments.add(visitTemplateArgument(templateArgumentCtx));
        }
        return templateArguments;
    }

    @Override
    public TemplateArgument visitTemplateArgument(ZserioParser.TemplateArgumentContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final TypeReference typeReference = visitTypeReference(ctx.typeReference());
        return new TemplateArgument(location, typeReference);
    }

    @Override
    public InstantiateType visitInstantiateDeclaration(ZserioParser.InstantiateDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final TypeReference typeReference = visitTypeReference(ctx.typeReference());
        final String name = ctx.id().getText();
        return new InstantiateType(location, currentPackage, typeReference, name);
    }

    @Override
    public Object visitParenthesizedExpression(ZserioParser.ParenthesizedExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1);
    }

    @Override
    public Object visitFunctionCallExpression(ZserioParser.FunctionCallExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1);
    }

    @Override
    public Object visitArrayExpression(ZserioParser.ArrayExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
    }

    @Override
    public Object visitDotExpression(ZserioParser.DotExpressionContext ctx)
    {
        final Expression.ExpressionFlag expressionFlag = (isInDotExpression) ? Expression.ExpressionFlag.NONE :
            Expression.ExpressionFlag.IS_TOP_LEVEL_DOT;
        isInDotExpression = true;
        final Expression operand1 = (Expression)visit(ctx.expression());
        final Token operand2Token = ctx.id().ID().getSymbol();
        final AstLocation operand2Location = new AstLocation(operand2Token);
        final Expression operand2 = new Expression(operand2Location, currentPackage, operand2Token.getType(),
                operand2Token.getText(), Expression.ExpressionFlag.IS_DOT_RIGHT_OPERAND_ID);
        isInDotExpression = false;

        final AstLocation location = new AstLocation(ctx.getStart());

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                expressionFlag, operand1, operand2);
    }

    @Override
    public Object visitLengthofExpression(ZserioParser.LengthofExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1);
    }

    @Override
    public Object visitValueofExpression(ZserioParser.ValueofExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1);
    }

    @Override
    public Object visitNumbitsExpression(ZserioParser.NumbitsExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1);
    }

    @Override
    public Object visitUnaryExpression(ZserioParser.UnaryExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression());

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1);
    }

    @Override
    public Object visitMultiplicativeExpression(ZserioParser.MultiplicativeExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
    }

    @Override
    public Object visitAdditiveExpression(ZserioParser.AdditiveExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
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

        return new Expression(location, currentPackage, tokenType, tokenText, Expression.ExpressionFlag.NONE,
                operand1, operand2);
    }

    @Override
    public Object visitRelationalExpression(ZserioParser.RelationalExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
    }

    @Override
    public Object visitEqualityExpression(ZserioParser.EqualityExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
    }

    @Override
    public Object visitBitwiseAndExpression(ZserioParser.BitwiseAndExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
    }

    @Override
    public Object visitBitwiseXorExpression(ZserioParser.BitwiseXorExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
    }

    @Override
    public Object visitBitwiseOrExpression(ZserioParser.BitwiseOrExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
    }

    @Override
    public Object visitLogicalAndExpression(ZserioParser.LogicalAndExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
    }

    @Override
    public Object visitLogicalOrExpression(ZserioParser.LogicalOrExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2);
    }

    @Override
    public Object visitTernaryExpression(ZserioParser.TernaryExpressionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final Expression operand1 = (Expression)visit(ctx.expression(0));
        final Expression operand2 = (Expression)visit(ctx.expression(1));
        final Expression operand3 = (Expression)visit(ctx.expression(2));

        return new Expression(location, currentPackage, ctx.operator.getType(), ctx.operator.getText(),
                Expression.ExpressionFlag.NONE, operand1, operand2, operand3);
    }

    @Override
    public Object visitLiteralExpression(ZserioParser.LiteralExpressionContext ctx)
    {
        final Token token = ctx.literal().getStart();
        final AstLocation location = new AstLocation(token);

        return new Expression(location, currentPackage, token.getType(), token.getText(),
                Expression.ExpressionFlag.NONE);
    }

    @Override
    public Object visitIndexExpression(ZserioParser.IndexExpressionContext ctx)
    {
        final Token token = ctx.INDEX().getSymbol();
        final AstLocation location = new AstLocation(token);

        return new Expression(location, currentPackage, token.getType(), token.getText(),
                Expression.ExpressionFlag.NONE);
    }

    @Override
    public Object visitIdentifierExpression(ZserioParser.IdentifierExpressionContext ctx)
    {
        final Token token = ctx.id().ID().getSymbol();
        final AstLocation location = new AstLocation(token);
        final Expression.ExpressionFlag expressionFlag = (isInDotExpression) ?
                Expression.ExpressionFlag.IS_DOT_LEFT_OPERAND_ID : Expression.ExpressionFlag.NONE;

        return new Expression(location, currentPackage, token.getType(), token.getText(), expressionFlag);
    }

    @Override
    public QualifiedName visitQualifiedName(ZserioParser.QualifiedNameContext ctx)
    {
        final PackageName referencedPackageName =
                createPackageName(getPackageNameIds(ctx.id()));
        final String referencedTypeName = getSymbolNameId(ctx.id()).getText();
        return new QualifiedName(referencedPackageName, referencedTypeName);
    }

    @Override
    public TypeReference visitTypeReference(ZserioParser.TypeReferenceContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());

        if (ctx.builtinType() != null)
        {
            return new TypeReference(location, currentPackage,
                    (BuiltInType)visitBuiltinType(ctx.builtinType()));
        }

        final List<TemplateArgument> templateArguments = visitTemplateArguments(ctx.templateArguments());
        final QualifiedName qualifiedName = visitQualifiedName(ctx.qualifiedName());

        return new TypeReference(location, currentPackage, qualifiedName.getReferencedPackageName(),
                qualifiedName.getReferencedTypeName(), templateArguments);
    }

    @Override
    public TypeInstantiation visitTypeInstantiation(ZserioParser.TypeInstantiationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final TypeReference typeReference = visitTypeReference(ctx.typeReference());
        if (ctx.typeArguments() != null)
        {
            return new ParameterizedTypeInstantiation(location, typeReference,
                    visitTypeArguments(ctx.typeArguments()));
        }
        if (ctx.dynamicLengthArgument() != null)
        {
            final Expression lengthExpression = (Expression)visit(ctx.dynamicLengthArgument().expression());
            return new DynamicBitFieldInstantiation(location, typeReference, lengthExpression);
        }
        return new TypeInstantiation(location, typeReference);
    }

    @Override
    public List<Expression> visitTypeArguments(ZserioParser.TypeArgumentsContext ctx)
    {
        final List<Expression> typeArguments = new ArrayList<Expression>();
        for (ZserioParser.TypeArgumentContext typeArgumentCtx : ctx.typeArgument())
            typeArguments.add(visitTypeArgument(typeArgumentCtx));
        return typeArguments;
    }

    @Override
    public Expression visitTypeArgument(ZserioParser.TypeArgumentContext ctx)
    {
        if (ctx.EXPLICIT() != null)
        {
            final AstLocation location = new AstLocation(ctx.getStart());
            final Token expressionToken = ctx.id().ID().getSymbol();

            return new Expression(location, currentPackage, expressionToken.getType(),
                    expressionToken.getText(), Expression.ExpressionFlag.IS_EXPLICIT);
        }
        else
        {
            return (Expression)visit(ctx.expression());
        }
    }

    @Override
    public StdIntegerType visitIntType(ZserioParser.IntTypeContext ctx)
    {
        final Token token = ctx.getStart();
        final AstLocation location = new AstLocation(token);

        return new StdIntegerType(location, token.getText(), token.getType());
    }

    @Override
    public VarIntegerType visitVarintType(ZserioParser.VarintTypeContext ctx)
    {
        final Token token = ctx.getStart();
        final AstLocation location = new AstLocation(token);

        return new VarIntegerType(location, token.getText(), token.getType());
    }

    @Override
    public FixedBitFieldType visitFixedBitFieldType(ZserioParser.FixedBitFieldTypeContext ctx)
    {
        final Token token = ctx.getStart();
        final AstLocation location = new AstLocation(token);
        final boolean isSigned = (ctx.INT_FIELD() != null);

        return new FixedBitFieldType(location, token.getText(), isSigned, ctx.DECIMAL_LITERAL().getText());
    }

    @Override
    public DynamicBitFieldType visitDynamicBitFieldType(ZserioParser.DynamicBitFieldTypeContext ctx)
    {
        final Token token = ctx.getStart();
        final AstLocation location = new AstLocation(token);
        final boolean isSigned = (ctx.INT_FIELD() != null);

        return new DynamicBitFieldType(location, token.getText(), isSigned);
    }

    @Override
    public BooleanType visitBoolType(ZserioParser.BoolTypeContext ctx)
    {
        final Token token = ctx.getStart();
        final AstLocation location = new AstLocation(token);

        return new BooleanType(location, token.getText());
    }

    @Override
    public StringType visitStringType(ZserioParser.StringTypeContext ctx)
    {
        final Token token = ctx.getStart();
        final AstLocation location = new AstLocation(token);

        return new StringType(location, token.getText());
    }

    @Override
    public FloatType visitFloatType(ZserioParser.FloatTypeContext ctx)
    {
        final Token token = ctx.getStart();
        final AstLocation location = new AstLocation(token);

        return new FloatType(location, token.getText(), token.getType());
    }

    @Override
    public ExternType visitExternType(ZserioParser.ExternTypeContext ctx)
    {
        final Token token = ctx.getStart();
        final AstLocation location = new AstLocation(token);

        return new ExternType(location, token.getText());
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

    private ZserioParser.IdContext getSymbolNameId(List<ZserioParser.IdContext> idList)
    {
        return idList.get(idList.size() - 1);
    }

    @Override
    public FieldTypeId visitFieldTypeId(ZserioParser.FieldTypeIdContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        TypeInstantiation typeInstantiation = visitTypeInstantiation(ctx.typeInstantiation());
        final String name = ctx.id().getText();

        final boolean isArray = ctx.fieldArrayRange() != null;
        if (isArray)
        {
            final boolean isImplicit = ctx.IMPLICIT() != null;
            final ZserioParser.ExpressionContext exprCtx = ctx.fieldArrayRange().expression();
            final Expression lengthExpression = (exprCtx != null) ? (Expression)visit(exprCtx) : null;
            final AstLocation arrayTypeLocation = new AstLocation(ctx.getStart());
            final ArrayType arrayType = new ArrayType(arrayTypeLocation);
            final TypeReference arrayTypeReference =
                    new TypeReference(arrayTypeLocation, currentPackage, arrayType);
            typeInstantiation = new ArrayInstantiation(arrayTypeLocation, arrayTypeReference,
                    typeInstantiation, isImplicit, lengthExpression);
        }

        return new FieldTypeId(location, typeInstantiation, name);
    }

    private static class FieldTypeId
    {
        public FieldTypeId(AstLocation location, TypeInstantiation typeInstantiation, String name)
        {
            this.location = location;
            this.typeInstantiation = typeInstantiation;
            this.name = name;
        }

        public AstLocation getLocation()
        {
            return location;
        }

        public TypeInstantiation getTypeInstantation()
        {
            return typeInstantiation;
        }

        public String getName()
        {
            return name;
        }

        private final AstLocation location;
        private final TypeInstantiation typeInstantiation;
        private final String name;
    }

    private static class QualifiedName
    {
        public QualifiedName(PackageName referencedPackageName, String referencedTypeName)
        {
            this.referencedPackageName = referencedPackageName;
            this.referencedTypeName = referencedTypeName;
        }

        public PackageName getReferencedPackageName()
        {
            return referencedPackageName;
        }

        public String getReferencedTypeName()
        {
            return referencedTypeName;
        }

        private final PackageName referencedPackageName;
        private final String referencedTypeName;
    }

    private final DocCommentManager docCommentManager = new DocCommentManager();
    private final LinkedHashMap<PackageName, Package> packageNameMap =
            new LinkedHashMap<PackageName, Package>();

    private Package currentPackage = null;
    private boolean isInDotExpression = false;

    private static final String RSHIFT_OPERATOR = ">>";
}
