package zserio.ast;

/**
 * Interface to visitor which allows to walk Zserio AST.
 */
public interface ZserioAstVisitor
{
    /**
     * Visits root node.
     *
     * @param root Root node of zserio AST.
     */
    public void visitRoot(Root root);

    /**
     * Visits a single package.
     *
     * @param packageNode Package AST node.
     */
    public void visitPackage(Package packageNode);

    /**
     * Visits compatibility version.
     *
     * @param compatibilityVersion Compatibility version AST node.
     */
    public void visitCompatibilityVersion(CompatibilityVersion compatibilityVersion);

    /**
     * Visits a single import.
     *
     * @param importNode Import AST node.
     */
    public void visitImport(Import importNode);

    /**
     * Visits constant definition.
     *
     * @param constant Constant AST node.
     */
    public void visitConstant(Constant constant);

    /**
     * Visits rule group definition.
     *
     * @param ruleGroup Rule group AST node.
     */
    public void visitRuleGroup(RuleGroup ruleGroup);

    /**
     * Visits subtype declaration.
     *
     * @param subtype Subtype AST node.
     */
    public void visitSubtype(Subtype subtype);

    /**
     * Visits structure type declaration.
     *
     * @param structureType Structure AST node.
     */
    public void visitStructureType(StructureType structureType);

    /**
     * Visits choice type declaration.
     *
     * @param choiceType Choice AST node.
     */
    public void visitChoiceType(ChoiceType choiceType);

    /**
     * Visits union type declaration.
     *
     * @param unionType Union AST node.
     */
    public void visitUnionType(UnionType unionType);

    /**
     * Visits enum type declaration.
     *
     * @param enumType Enum AST node.
     */
    public void visitEnumType(EnumType enumType);

    /**
     * Visits bitmask type declaration.
     *
     * @param bitmaskType Bitmask AST node.
     */
    public void visitBitmaskType(BitmaskType bitmaskType);

    /**
     * Visits SQL table declaration.
     *
     * @param sqlTableType SQL table AST node.
     */
    public void visitSqlTableType(SqlTableType sqlTableType);

    /**
     * Visits SQL database definition.
     *
     * @param sqlDatabaseType SQL database AST node.
     */
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType);

    /**
     * Visits service definition.
     *
     * @param serviceType Service AST node.
     */
    public void visitServiceType(ServiceType serviceType);

    /**
     * Visits Pub/Sub definition.
     *
     * @param pubsubType Pub/Sub AST node.
     */
    public void visitPubsubType(PubsubType pubsubType);

    /**
     * Visits field definition.
     *
     * @param field Field AST node.
     */
    public void visitField(Field field);

    /**
     * Visits choice case definition. Note that a single case can have multiple choice case expressions.
     *
     * @param choiceCase Choice case AST node.
     */
    public void visitChoiceCase(ChoiceCase choiceCase);

    /**
     * Visits choice case expression.
     *
     * @param choiceCaseExpression Choice case expression AST node.
     */
    public void visitChoiceCaseExpression(ChoiceCaseExpression choiceCaseExpression);

    /**
     * Visits choice default statement.
     *
     * @param choiceDefault Choice default statement AST node.
     */
    public void visitChoiceDefault(ChoiceDefault choiceDefault);

    /**
     * Visits enum item definition.
     *
     * @param enumItem Enum item AST node.
     */
    public void visitEnumItem(EnumItem enumItem);

    /**
     * Visits bitmask named value definition.
     *
     * @param bitmaskValue Bitmask named value AST node.
     */
    public void visitBitmaskValue(BitmaskValue bitmaskValue);

    /**
     * Visits SQL constraint definition.
     *
     * @param sqlConstraint SQL constraint AST node.
     */
    public void visitSqlConstraint(SqlConstraint sqlConstraint);

    /**
     * Visits service method definition.
     *
     * @param serviceMethod Service method AST node.
     */
    public void visitServiceMethod(ServiceMethod serviceMethod);

    /**
     * Visits Pub/Sub message definition.
     *
     * @param pubsubMessage Pub/Sub message AST node.
     */
    public void visitPubsubMessage(PubsubMessage pubsubMessage);

    /**
     * Visits a single rule.
     *
     * @param rule Rule AST node.
     */
    public void visitRule(Rule rule);

    /**
     * Visits function definition.
     *
     * @param function Function AST node.
     */
    public void visitFunction(Function function);

    /**
     * Visits parameter definition.
     *
     * @param parameter Parameter AST node.
     */
    public void visitParameter(Parameter parameter);

    /**
     * Visits expression.
     *
     * @param expresssion Expression AST node.
     */
    public void visitExpression(Expression expresssion);

    /**
     * Visits type reference.
     *
     * @param typeReference Type reference AST node.
     */
    public void visitTypeReference(TypeReference typeReference);

    /**
     * Visits type instantiation.
     *
     * @param typeInstantiation Type instantiation AST node.
     */
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation);

    /**
     * Visits array type.
     *
     * @param arrayType Array type AST node.
     */
    public void visitArrayType(ArrayType arrayType);

    /**
     * Visits reference to built-in standard integer type.
     *
     * @param stdIntegerType Standard integer type AST node.
     */
    public void visitStdIntegerType(StdIntegerType stdIntegerType);

    /**
     * Visits reference to built-in variable length integer type.
     *
     * @param varIntegerType Variable length integer type AST node.
     */
    public void visitVarIntegerType(VarIntegerType varIntegerType);

    /**
     * Visits reference to built-in fixed bit field type.
     *
     * @param fixedBitFieldType Fixed bit field type AST node.
     */
    public void visitFixedBitFieldType(FixedBitFieldType fixedBitFieldType);

    /**
     * Visits reference to built-in dynamic bit field type.
     *
     * @param dynamicBitFieldType Dynamic bit field type AST node.
     */
    public void visitDynamicBitFieldType(DynamicBitFieldType dynamicBitFieldType);

    /**
     * Visits reference to built-in boolean type.
     *
     * @param booleanType Boolean type AST node.
     */
    public void visitBooleanType(BooleanType booleanType);

    /**
     * Visits reference to built-in string type.
     *
     * @param stringType String type AST node.
     */
    public void visitStringType(StringType stringType);

    /**
     * Visits reference to built-in float type.
     *
     * @param floatType Float type AST node.
     */
    public void visitFloatType(FloatType floatType);

    /**
     * Visits reference to built-in extern type.
     *
     * @param externType Extern type AST node.
     */
    public void visitExternType(ExternType externType);

    /**
     * Visits template parameter.
     *
     * @param templateParameter Template parameter AST node.
     */
    public void visitTemplateParameter(TemplateParameter templateParameter);

    /**
     * Visits template argument.
     *
     * @param templateArgument Template argument AST node.
     */
    public void visitTemplateArgument(TemplateArgument templateArgument);

    /**
     * Visits template instantiation.
     *
     * @param instantiateType Instantiate type AST node.
     */
    public void visitInstantiateType(InstantiateType instantiateType);

    /**
     * Visits a classic-style documentation comment.
     *
     * @param docComment Classic-style documentation comment AST node.
     */
    public void visitDocCommentClassic(DocCommentClassic docComment);

    /**
     * Visits a markdown-style documentation comment.
     *
     * @param docComment Markdown-style documentation comment AST node.
     */
    public void visitDocCommentMarkdown(DocCommentMarkdown docComment);

    /**
     * Visits documentation paragraph.
     *
     * @param docParagraph Documentation paragraph AST node.
     */
    public void visitDocParagraph(DocParagraph docParagraph);

    /**
     * Visits documentation element.
     *
     * @param docElement Documentation element AST node.
     */
    public void visitDocElement(DocElement docElement);

    /**
     * Visits documentation multiline.
     *
     * @param docMultiline Documentation multiline AST node.
     */
    public void visitDocMultiline(DocMultiline docMultiline);

    /**
     * Visits a see tag within a documentation comment.
     *
     * @param docTagSee See tag AST node.
     */
    public void visitDocTagSee(DocTagSee docTagSee);

    /**
     * Visits a todo tag within a documentation comment.
     *
     * @param docTagTodo Todo tag AST node.
     */
    public void visitDocTagTodo(DocTagTodo docTagTodo);

    /**
     * Visits a param tag within a documentation comment.
     *
     * @param docTagParam Param tag AST node.
     */
    public void visitDocTagParam(DocTagParam docTagParam);

    /**
     * Visits a deprecated tag within a documentation comment.
     *
     * @param docTagDeprecated Deprecated tag AST node.
     */
    public void visitDocTagDeprecated(DocTagDeprecated docTagDeprecated);

    /**
     * Visits a single line of documentation.
     *
     * @param docLine Documentation line AST node.
     */
    public void visitDocLine(DocLine docLine);

    /**
     * Visits documentation text wrapper.
     *
     * DocLineElement can be either a text or a see tag.
     *
     * @param docLineElement Documentation line element AST node.
     */
    public void visitDocLineElement(DocLineElement docLineElement);

    /**
     * Visits documentation text.
     *
     * @param docText Documentation text AST node.
     */
    public void visitDocText(DocText docText);
}
