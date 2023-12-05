package zserio.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zserio.tools.WarningsConfig;
import zserio.tools.ZserioToolPrinter;

/**
 * Implementation of ZserioAstVisitor which manages checking phase.
 */
public final class ZserioAstChecker extends ZserioAstWalker
{
    /**
     * Constructor.
     *
     * @param warningsConfig Warnings config.
     * @param withGlobalRuleIdCheck Whether to check of rule id uniqueness between all packages.
     */
    public ZserioAstChecker(WarningsConfig warningsConfig, boolean withGlobalRuleIdCheck)
    {
        this.warningsConfig = warningsConfig;
        this.withGlobalRuleIdCheck = withGlobalRuleIdCheck;
    }

    @Override
    public void visitRoot(Root root)
    {
        root.visitChildren(this);
        root.check(withGlobalRuleIdCheck);

        for (ZserioType definedType : definedTypes)
        {
            final String definedTypeName = ZserioTypeUtil.getFullName(definedType);
            if (!usedTypeNames.contains(definedTypeName))
            {
                ZserioToolPrinter.printWarning(definedType, "Type '" + definedTypeName + "' is not used.",
                        warningsConfig, WarningsConfig.UNUSED);
            }
        }
    }

    @Override
    public void visitPackage(Package pkg)
    {
        currentPackage = pkg;
        pkg.visitChildren(this);
        pkg.check();
        checkDocComments(currentPackage, WarningsConfig.DOC_COMMENT_MISSING, "package");
        currentPackage = null;
    }

    @Override
    public void visitCompatibilityVersion(CompatibilityVersion compatibilityVersion)
    {
        compatibilityVersion.visitChildren(this);
        checkDocComments(compatibilityVersion, WarningsConfig.DOC_COMMENT_MISSING, "compatibility version");
    }

    @Override
    public void visitImport(Import unitImport)
    {
        unitImport.visitChildren(this);
        checkDocComments(unitImport, WarningsConfig.DOC_COMMENT_MISSING, "import");
    }

    @Override
    public void visitConstant(Constant constant)
    {
        constant.visitChildren(this);
        constant.check();
        checkDocComments(constant, WarningsConfig.DOC_COMMENT_MISSING, "constant '" + constant.getName() + "'");
    }

    @Override
    public void visitRuleGroup(RuleGroup ruleGroup)
    {
        ruleGroup.visitChildren(this);
        checkDocComments(ruleGroup, WarningsConfig.DOC_COMMENT_MISSING,
                "rule group '" + ruleGroup.getName() + "'");
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        subtype.visitChildren(this);
        definedTypes.add(subtype);
        addUsedType(subtype.getTypeReference().getType());
        checkDocComments(subtype, WarningsConfig.DOC_COMMENT_MISSING, "subtype '" + subtype.getName() + "'");
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        if (structureType.getTemplateParameters().isEmpty())
        {
            structureType.visitChildren(this);
            definedTypes.add(structureType);
            structureType.check();

            final String name = structureType.getTemplate() != null
                    ? structureType.getTemplate().getName() : structureType.getName();
            checkDocComments(structureType, WarningsConfig.DOC_COMMENT_MISSING, "structure '" + name + "'");
        }
        else
        {
            visitInstantiations(structureType);
        }
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        if (choiceType.getTemplateParameters().isEmpty())
        {
            choiceType.visitChildren(this);
            definedTypes.add(choiceType);
            choiceType.check(warningsConfig);

            final String name = choiceType.getTemplate() != null
                    ? choiceType.getTemplate().getName() : choiceType.getName();
            checkDocComments(choiceType, WarningsConfig.DOC_COMMENT_MISSING, "choice '" + name + "'");
        }
        else
        {
            visitInstantiations(choiceType);
        }
    }

    @Override
    public void visitChoiceCaseExpression(ChoiceCaseExpression choiceCaseExpression)
    {
        choiceCaseExpression.visitChildren(this);
        checkDocComments(choiceCaseExpression, WarningsConfig.DOC_COMMENT_MISSING, "choice case expression");
    }

    @Override
    public void visitChoiceDefault(ChoiceDefault choiceDefault)
    {
        choiceDefault.visitChildren(this);
        checkDocComments(choiceDefault, WarningsConfig.DOC_COMMENT_MISSING, "choice default");
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        if (unionType.getTemplateParameters().isEmpty())
        {
            unionType.visitChildren(this);
            definedTypes.add(unionType);
            unionType.check();

            final String name = unionType.getTemplate() != null
                    ? unionType.getTemplate().getName() : unionType.getName();
            checkDocComments(unionType, WarningsConfig.DOC_COMMENT_MISSING, "union '" + name + "'");
        }
        else
        {
            visitInstantiations(unionType);
        }
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        enumType.visitChildren(this);
        definedTypes.add(enumType);
        enumType.check();
        checkDocComments(enumType, WarningsConfig.DOC_COMMENT_MISSING,
                "enumeration '" + enumType.getName() + "'");
    }

    @Override
    public void visitEnumItem(EnumItem enumItem)
    {
        enumItem.visitChildren(this);
        checkDocComments(enumItem, WarningsConfig.DOC_COMMENT_MISSING,
                "enum item '" + enumItem.getName() + "'");
    }

    @Override
    public void visitBitmaskType(BitmaskType bitmaskType)
    {
        bitmaskType.visitChildren(this);
        definedTypes.add(bitmaskType);
        bitmaskType.check();
        checkDocComments(bitmaskType, WarningsConfig.DOC_COMMENT_MISSING,
                "bitmask '" + bitmaskType.getName() + "'");
    }

    @Override
    public void visitBitmaskValue(BitmaskValue bitmaskValue)
    {
        bitmaskValue.visitChildren(this);
        checkDocComments(bitmaskValue, WarningsConfig.DOC_COMMENT_MISSING,
                "bitmask value '" + bitmaskValue.getName() + "'");
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        if (sqlTableType.getTemplateParameters().isEmpty())
        {
            sqlTableType.visitChildren(this);
            definedTypes.add(sqlTableType);
            sqlTableType.check(warningsConfig);

            final String name = sqlTableType.getTemplate() != null
                    ? sqlTableType.getTemplate().getName() : sqlTableType.getName();
            checkDocComments(sqlTableType, WarningsConfig.DOC_COMMENT_MISSING, "SQL table '" + name + "'");
        }
        else
        {
            visitInstantiations(sqlTableType);
        }
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        sqlDatabaseType.visitChildren(this);
        sqlDatabaseType.check();
        checkDocComments(sqlDatabaseType, WarningsConfig.DOC_COMMENT_MISSING,
                "SQL database '" + sqlDatabaseType.getName() + "'");
    }

    @Override
    public void visitField(Field field)
    {
        field.visitChildren(this);
        field.check(currentPackage, warningsConfig);
        checkDocComments(field, WarningsConfig.DOC_COMMENT_MISSING, "field '" + field.getName() + "'");
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        serviceType.visitChildren(this);
        serviceType.check();
        checkDocComments(serviceType, WarningsConfig.DOC_COMMENT_MISSING,
                "service '" + serviceType.getName() + "'");
    }

    @Override
    public void visitServiceMethod(ServiceMethod serviceMethod)
    {
        serviceMethod.visitChildren(this);
        serviceMethod.check();
        checkDocComments(serviceMethod, WarningsConfig.DOC_COMMENT_MISSING,
                "method '" + serviceMethod.getName() + "'");
    }

    @Override
    public void visitPubsubType(PubsubType pubsubType)
    {
        pubsubType.visitChildren(this);
        pubsubType.check();
        checkDocComments(pubsubType, WarningsConfig.DOC_COMMENT_MISSING,
                "pubsub '" + pubsubType.getName() + "'");
    }

    @Override
    public void visitPubsubMessage(PubsubMessage pubsubMessage)
    {
        pubsubMessage.visitChildren(this);
        pubsubMessage.check();
        checkDocComments(pubsubMessage, WarningsConfig.DOC_COMMENT_MISSING,
                "message '" + pubsubMessage.getName() + "'");
    }

    @Override
    public void visitRule(Rule rule)
    {
        rule.visitChildren(this);
        checkDocComments(rule, WarningsConfig.DOC_COMMENT_MISSING, "rule '" + rule.getRuleId() + "'");
    }

    @Override
    public void visitFunction(Function function)
    {
        function.visitChildren(this);
        function.check(warningsConfig);
        checkDocComments(function, WarningsConfig.DOC_COMMENT_MISSING, "function '" + function.getName() + "'");
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        typeInstantiation.visitChildren(this);
        typeInstantiation.check(warningsConfig, currentTemplateInstantiation);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        typeReference.visitChildren(this);
        addUsedType(typeReference.getType());
    }

    @Override
    public void visitInstantiateType(InstantiateType instantiateType)
    {
        instantiateType.visitChildren(this);
        checkDocComments(instantiateType, WarningsConfig.DOC_COMMENT_MISSING,
                "instantiate type '" + instantiateType.getName() + "'");
    }

    private void visitInstantiations(ZserioTemplatableType template)
    {
        for (ZserioTemplatableType instantiation : template.getInstantiations())
        {
            try
            {
                currentTemplateInstantiation = instantiation;
                instantiation.accept(this);
                currentTemplateInstantiation = null;
            }
            catch (ParserException e)
            {
                throw new InstantiationException(e, instantiation.getInstantiationReferenceStack());
            }
        }
    }

    private void checkDocComments(DocumentableAstNode documentableAstNode, String warningSpecifier,
            String schemaElementDescription)
    {
        // check if documentable AST node has assigned some sticky comment
        for (DocComment docComment : documentableAstNode.getDocComments())
        {
            if (docComment.isSticky())
                return;
        }

        // report each warning only once - simple way to solve repeated warnings in template instantiations
        final AstLocation location = documentableAstNode.getLocation();
        if (!reportedDocCommentsWarnings.contains(location))
        {
            ZserioToolPrinter.printWarning(location,
                    "Missing documentation comment for " + schemaElementDescription + ".",
                    warningsConfig, warningSpecifier);
            reportedDocCommentsWarnings.add(location);
        }
    }

    private void addUsedType(ZserioType usedType)
    {
        if (!(usedType instanceof BuiltInType))
            usedTypeNames.add(ZserioTypeUtil.getFullName(usedType));
    }

    private final WarningsConfig warningsConfig;
    private final boolean withGlobalRuleIdCheck;

    private final Set<AstLocation> reportedDocCommentsWarnings = new HashSet<AstLocation>();
    private final Set<String> usedTypeNames = new HashSet<String>();
    private final List<ZserioType> definedTypes = new ArrayList<ZserioType>();

    private Package currentPackage = null;
    private ZserioTemplatableType currentTemplateInstantiation = null;
};
