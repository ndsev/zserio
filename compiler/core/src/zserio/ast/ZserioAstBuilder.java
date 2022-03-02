package zserio.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import zserio.antlr.ZserioParser;
import zserio.antlr.ZserioParserBaseVisitor;
import zserio.tools.InputFileManager;

/**
 * Implementation of ZserioParserBaseVisitor which builds Zserio AST.
 */
public class ZserioAstBuilder extends ZserioParserBaseVisitor<Object>
{
    /**
     * Constructor.
     *
     * @param topLevelPackageNameIds List of top level package name identifiers given by command line option.
     * @param inputFileName Input file name given by command line.
     * @param inputFileManager Input file manager.
     */
    public ZserioAstBuilder(Iterable<String> topLevelPackageNameIds, String inputFileName,
            InputFileManager inputFileManager)
    {
        this.topLevelPackageNameIds = topLevelPackageNameIds;
        this.inputFileName = inputFileName;
        this.inputFileManager = inputFileManager;
    }

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
     * @param tree         Parse tree for a single package.
     * @param tokenStream  Token stream for a single translation unit.
     * @return Object      Result of the main rule of ZserioParser grammar.
     *                     Should be a package if the method was called on a correct parse tree.
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
        final PackageName topLevelPackageName = createTopLevelPackageName();
        final List<DocComment> docComments = docCommentManager.findDocComments(ctx.packageNameDefinition());

        // package compatibility version
        final CompatibilityVersion compatibilityVersion = visitCompatibilityVersionDirective(
                ctx.compatibilityVersionDirective());

        // imports
        final List<Import> imports = new ArrayList<Import>();
        for (ZserioParser.ImportDeclarationContext importCtx : ctx.importDeclaration())
            imports.add(visitImportDeclaration(importCtx));

        // package instance
        final ParserRuleContext packageLocationCtx = ctx.packageNameDefinition() != null
                ? ctx.packageNameDefinition().id(0) : ctx;
        final AstLocation packageLocation = new AstLocation(packageLocationCtx.getStart());
        final List<DocComment> trailingDocComments = docCommentManager.findDocComments(ctx.EOF());

        currentPackage = new Package(packageLocation, packageName, topLevelPackageName, compatibilityVersion,
                imports, docComments, trailingDocComments);
        if (packageNameMap.put(currentPackage.getPackageName(), currentPackage) != null)
        {
            // translation unit package already exists, this could happen only for default packages
            throw new ParserException(currentPackage, "Multiple default packages are not allowed!");
        }
        if (packageName.isEmpty())
        {
            // check if input file directory has been specified for default package
            final String inputFileDirectory = new File(inputFileName).getParent();
            if (inputFileDirectory != null)
                throw new ParserException(currentPackage, "Default package cannot be compiled with path! " +
                        "Consider to specify package name or set source path to '" + inputFileDirectory + "'.");
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
        if (ctx == null)
            return createPackageName(new ArrayList<ZserioParser.IdContext>()); // default package

        // this must be checked now to avoid obscure errors if package is not stored in the same file name
        final PackageName packageName = createPackageName(ctx.id());
        final String expectedFileFullName = inputFileManager.getFileFullName(packageName);
        final String fileFullName = ctx.getStart().getInputStream().getSourceName();
        if (!expectedFileFullName.equals(fileFullName))
            throw new ParserException(ctx.id(0).getStart(), "Package '" + packageName.toString() +
                    "' does not match to the source file name!");

        return packageName;
    }

    @Override
    public CompatibilityVersion visitCompatibilityVersionDirective(
            ZserioParser.CompatibilityVersionDirectiveContext ctx)
    {
        if (ctx == null)
            return null;

        final Token versionToken = ctx.STRING_LITERAL().getSymbol();
        final AstLocation location = new AstLocation(versionToken);
        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new CompatibilityVersion(location, versionToken.getText(), docComments);
    }

    @Override
    public Import visitImportDeclaration(ZserioParser.ImportDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id(0).getStart());
        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);
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

        return new Import(location, importedPackageName, importedSymbolName, docComments);
    }

    @Override
    public ZserioType visitTypeDeclaration(ZserioParser.TypeDeclarationContext ctx)
    {
        final ZserioType type = (ZserioType)super.visitTypeDeclaration(ctx);
        currentPackage.addSymbol(type);
        return type;
    }

    @Override
    public PackageSymbol visitSymbolDefinition(ZserioParser.SymbolDefinitionContext ctx)
    {
        final PackageSymbol symbol = (PackageSymbol)super.visitSymbolDefinition(ctx);
        currentPackage.addSymbol(symbol);
        return symbol;
    }

    @Override
    public Constant visitConstDefinition(ZserioParser.ConstDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final TypeInstantiation typeInstantiation = visitTypeInstantiation(ctx.typeInstantiation());
        final String name = ctx.id().getText();
        final Expression valueExpression = (Expression)visit(ctx.expression());

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final Constant constant = new Constant(location, currentPackage, typeInstantiation, name,
                valueExpression, docComments);

        return constant;
    }

    @Override
    public RuleGroup visitRuleGroupDefinition(ZserioParser.RuleGroupDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());

        final String name = ctx.id().getText();

        final List<Rule> rules = new ArrayList<Rule>();
        for (ZserioParser.RuleDefinitionContext ruleDefinitionCtx : ctx.ruleDefinition())
            rules.add(visitRuleDefinition(ruleDefinitionCtx));

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new RuleGroup(location, currentPackage, name, rules, docComments);
    }

    @Override
    public Rule visitRuleDefinition(ZserioParser.RuleDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.expression().getStart());
        final Expression ruleIdExpression = (Expression)visit(ctx.expression());
        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new Rule(location, ruleIdExpression, docComments);
    }

    @Override
    public Subtype visitSubtypeDeclaration(ZserioParser.SubtypeDeclarationContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final TypeReference targetTypeReference = visitTypeReference(ctx.typeReference());
        final String name = ctx.id().getText();

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final Subtype subtype = new Subtype(location, currentPackage, targetTypeReference, name, docComments);

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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final StructureType structureType = new StructureType(location, currentPackage, name,
                templateParameters, typeParameters, fields, functions, docComments);

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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new Field(fieldTypeId.getLocation(), fieldTypeId.getTypeInstantation(), fieldTypeId.getName(),
                isAutoOptional, alignmentExpr, offsetExpr, initializerExpr, optionalClauseExpr, constraintExpr,
                docComments);
    }

    @Override
    public Expression visitFieldAlignment(ZserioParser.FieldAlignmentContext ctx)
    {
        if (ctx == null)
            return null;

        return (Expression)visit(ctx.expression());
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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final ChoiceType choiceType = new ChoiceType(location, currentPackage, name,
                templateParameters, typeParameters, selectorExpression, choiceCases, choiceDefault, functions,
                docComments);

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
        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);
        return new ChoiceCaseExpression(location, (Expression)visit(ctx.expression()), docComments);
    }

    @Override
    public ChoiceDefault visitChoiceDefault(ZserioParser.ChoiceDefaultContext ctx)
    {
        if (ctx == null)
            return null;

        final AstLocation location = new AstLocation(ctx.getStart());
        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final Field defaultField = visitChoiceFieldDefinition(ctx.choiceFieldDefinition());
        return new ChoiceDefault(location, defaultField, docComments);
    }

    @Override
    public Field visitChoiceFieldDefinition(ZserioParser.ChoiceFieldDefinitionContext ctx)
    {
        if (ctx == null)
            return null;

        final FieldTypeId fieldTypeId = visitFieldTypeId(ctx.fieldTypeId());
        final Expression constraintExpr = visitFieldConstraint(ctx.fieldConstraint());

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new Field(fieldTypeId.getLocation(), fieldTypeId.getTypeInstantation(), fieldTypeId.getName(),
                constraintExpr, docComments);
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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final UnionType unionType = new UnionType(location, currentPackage, name,
                templateParameters, typeParameters, fields, functions, docComments);

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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final EnumType enumType = new EnumType(location, currentPackage, enumTypeInstantiation, name, enumItems,
                docComments);

        return enumType;
    }

    @Override
    public EnumItem visitEnumItem(ZserioParser.EnumItemContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final String name = ctx.id().getText();
        final ZserioParser.ExpressionContext exprCtx = ctx.expression();
        final Expression valueExpression = (exprCtx != null) ? (Expression)visit(exprCtx) : null;

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new EnumItem(location, name, valueExpression, docComments);
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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final BitmaskType enumType = new BitmaskType(location, currentPackage, typeInstantiation, name,
                bitmaskValues, docComments);

        return enumType;
    }

    @Override
    public BitmaskValue visitBitmaskValue(ZserioParser.BitmaskValueContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.getStart());
        final String name = ctx.id().getText();
        final ZserioParser.ExpressionContext exprCtx = ctx.expression();
        final Expression valueExpression = (exprCtx != null) ? (Expression)visit(exprCtx) : null;

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new BitmaskValue(location, name, valueExpression, docComments);
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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final SqlTableType sqlTableType = new SqlTableType(location, currentPackage, name,
                templateParameters, sqlUsingId, fields, sqlConstraint, sqlWithoutRowId, docComments);

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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new Field(location, typeInstantiation, name, isVirtual, sqlConstraint, docComments);
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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final SqlDatabaseType sqlDatabaseType = new SqlDatabaseType(location, currentPackage, name, fields,
                docComments);

        return sqlDatabaseType;
    }

    @Override
    public Field visitSqlDatabaseFieldDefinition(ZserioParser.SqlDatabaseFieldDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final TypeInstantiation fieldTypeInstantiation = visitTypeInstantiation(ctx.typeInstantiation());
        final String name = ctx.id().getText();

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new Field(location, fieldTypeInstantiation, name, docComments);
    }

    @Override
    public ServiceType visitServiceDefinition(ZserioParser.ServiceDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());
        final String name = ctx.id().getText();

        final List<ServiceMethod> methods = new ArrayList<ServiceMethod>();
        for (ZserioParser.ServiceMethodDefinitionContext methodDefinitionCtx : ctx.serviceMethodDefinition())
            methods.add(visitServiceMethodDefinition(methodDefinitionCtx));

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final ServiceType serviceType = new ServiceType(location, currentPackage, name, methods, docComments);

        return serviceType;
    }

    @Override
    public ServiceMethod visitServiceMethodDefinition(ZserioParser.ServiceMethodDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());

        final TypeReference responseTypeReference = visitTypeReference(ctx.typeReference(0));

        final String name = ctx.id().getText();

        final TypeReference requestTypeReference = visitTypeReference(ctx.typeReference(1));

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new ServiceMethod(location, name, responseTypeReference, requestTypeReference, docComments);
    }

    @Override
    public PubsubType visitPubsubDefinition(ZserioParser.PubsubDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());

        final String name = ctx.id().getText();

        final List<PubsubMessage> messages = new ArrayList<PubsubMessage>();
        for (ZserioParser.PubsubMessageDefinitionContext messageDefinitionCtx : ctx.pubsubMessageDefinition())
            messages.add(visitPubsubMessageDefinition(messageDefinitionCtx));

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final PubsubType pubsubType = new PubsubType(location, currentPackage, name, messages, docComments);

        return pubsubType;
    }

    @Override
    public PubsubMessage visitPubsubMessageDefinition(ZserioParser.PubsubMessageDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.id().getStart());

        final Expression topicDefinitionExpr = (Expression)visit(ctx.topicDefinition().expression());

        final boolean isPublished = ctx.topicDefinition().SUBSCRIBE() == null;
        final boolean isSubscribed = ctx.topicDefinition().PUBLISH() == null;
        final TypeReference typeReference = visitTypeReference(ctx.typeReference());
        final String name = ctx.id().getText();
        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new PubsubMessage(location, name, typeReference, topicDefinitionExpr, isPublished, isSubscribed,
                docComments);
    }

    @Override
    public Function visitFunctionDefinition(ZserioParser.FunctionDefinitionContext ctx)
    {
        final AstLocation location = new AstLocation(ctx.functionName().getStart());
        final TypeReference returnTypeReference = visitTypeReference(ctx.functionType().typeReference());
        final String name = ctx.functionName().getText();
        final Expression resultExpression = (Expression)visit(ctx.functionBody().expression());

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        return new Function(location, returnTypeReference, name, resultExpression, docComments);
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

        final List<DocComment> docComments = docCommentManager.findDocComments(ctx);

        final InstantiateType instantiateType =
                new InstantiateType(location, currentPackage, typeReference, name, docComments);

        return instantiateType;
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
                throw new ParserException(ctx.GT().get(0).getSymbol(), "Operator '>>' cannot contain spaces!");
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
        final List<ZserioParser.IdContext> ids = getPackageNameIds(ctx.id());
        final PackageName referencedPackageName = (ids.isEmpty()) ? PackageName.EMPTY : createPackageName(ids);
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
        packageNameBuilder.addIds(topLevelPackageNameIds);
        for (ZserioParser.IdContext id : ids)
            packageNameBuilder.addId(id.getText());

        return packageNameBuilder.get();
    }

    private PackageName createTopLevelPackageName()
    {
        final PackageName.Builder packageNameBuilder = new PackageName.Builder();
        packageNameBuilder.addIds(topLevelPackageNameIds);

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
            final boolean isPacked = ctx.PACKED() != null;
            final boolean isImplicit = ctx.IMPLICIT() != null;
            final ZserioParser.ExpressionContext exprCtx = ctx.fieldArrayRange().expression();
            final Expression lengthExpression = (exprCtx != null) ? (Expression)visit(exprCtx) : null;
            final AstLocation arrayTypeLocation = new AstLocation(ctx.getStart());
            final ArrayType arrayType = new ArrayType(arrayTypeLocation);
            final TypeReference arrayTypeReference =
                    new TypeReference(arrayTypeLocation, currentPackage, arrayType);
            typeInstantiation = new ArrayInstantiation(arrayTypeLocation, arrayTypeReference,
                    typeInstantiation, isPacked, isImplicit, lengthExpression);
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

    private final Iterable<String> topLevelPackageNameIds;
    private final String inputFileName;
    private final InputFileManager inputFileManager;

    private final DocCommentManager docCommentManager = new DocCommentManager();
    private final LinkedHashMap<PackageName, Package> packageNameMap =
            new LinkedHashMap<PackageName, Package>();

    private Package currentPackage = null;
    private boolean isInDotExpression = false;

    private static final String RSHIFT_OPERATOR = ">>";
}
