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
    void visitRoot(Root root);

    /**
     * Visits a single package.
     *
     * @param packageNode Package AST node.
     */
    void visitPackage(Package packageNode);

    /**
     * Visits a single import.
     *
     * @param importNode Import AST node.
     */
    void visitImport(Import importNode);

    /**
     * Visits constant type definition.
     *
     * @param constType Constant AST node.
     */
    void visitConstType(ConstType constType);

    /**
     * Visits subtype declaration.
     *
     * @param subtype Subtype AST node.
     */
    void visitSubtype(Subtype subtype);

    /**
     * Visits structure type declaration.
     *
     * @param structureType Structure AST node.
     */
    void visitStructureType(StructureType structureType);

    /**
     * Visits choice type declaration.
     *
     * @param choiceType Choice AST node.
     */
    void visitChoiceType(ChoiceType choiceType);

    /**
     * Visits union type declaration.
     *
     * @param unionType Union AST node.
     */
    void visitUnionType(UnionType unionType);

    /**
     * Visits enum type declaration.
     *
     * @param enumType Enum AST node.
     */
    void visitEnumType(EnumType enumType);

    /**
     * Visits SQL table declaration.
     *
     * @param sqlTableType SQL table AST node.
     */
    void visitSqlTableType(SqlTableType sqlTableType);

    /**
     * Visits SQL database definition.
     *
     * @param sqlDatabaseType SQL database AST node.
     */
    void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType);

    /**
     * Visits RPC service definition.
     *
     * @param serviceType RPC service AST node.
     */
    void visitServiceType(ServiceType serviceType);

    /**
     * Visits field definition.
     *
     * @param field Field AST node.
     */
    void visitField(Field field);

    /**
     * Visits choice case definition. Note that a single case can have multiple choice case expressions.
     *
     * @param choiceCase Choice case AST node.
     */
    void visitChoiceCase(ChoiceCase choiceCase);

    /**
     * Visits choice case expression.
     *
     * @param choiceCaseExpression Choice case expression AST node.
     */
    void visitChoiceCaseExpression(ChoiceCaseExpression choiceCaseExpression);

    /**
     * Visits choice default statement.
     *
     * @param choiceDefault Choice default statement AST node.
     */
    void visitChoiceDefault(ChoiceDefault choiceDefault);

    /**
     * Visits enum item definition.
     *
     * @param enumItem Enum item AST node.
     */
    void visitEnumItem(EnumItem enumItem);

    /**
     * Visits SQL constraint definition.
     *
     * @param sqlConstraint SQL constraint AST node.
     */
    void visitSqlConstraint(SqlConstraint sqlConstraint);

    /**
     * Visits RPC call definition.
     *
     * @param rpc RPC call AST node.
     */
    void visitRpc(Rpc rpc);

    /**
     * Visits function definition.
     *
     * @param functionType Function AST node.
     */
    void visitFunctionType(FunctionType functionType);

    /**
     * Visits parameter definition.
     *
     * @param parameter Parameter AST node.
     */
    void visitParameter(Parameter parameter);

    /**
     * Visits expression.
     *
     * @param expresssion Expression AST node.
     */
    void visitExpression(Expression expresssion);

    /**
     * Visits array definition.
     *
     * @param arrayType Array AST node.
     */
    void visitArrayType(ArrayType arrayType);

    /**
     * Visits type instantiation.
     *
     * @param typeInstantiation Type instantiation AST node.
     */
    void visitTypeInstantiation(TypeInstantiation typeInstantiation);

    /**
     * Visits type reference.
     *
     * @param typeReference Type reference AST node.
     */
    void visitTypeReference(TypeReference typeReference);

    /**
     * Visits reference to built-in standard integer type.
     *
     * @param stdIntegerType Standard integer type AST node.
     */
    void visitStdIntegerType(StdIntegerType stdIntegerType);

    /**
     * Visits reference to built-in variable length integer type.
     *
     * @param varIntegerType Variable length integer type AST node.
     */
    void visitVarIntegerType(VarIntegerType varIntegerType);

    /**
     * Visits reference to built-in bit field type.
     *
     * @param bitFieldType Bit field type AST node.
     */
    void visitBitFieldType(BitFieldType bitFieldType);

    /**
     * Visits reference to built-in boolean type.
     *
     * @param booleanType Boolean type AST node.
     */
    void visitBooleanType(BooleanType booleanType);

    /**
     * Visits reference to built-in string type.
     *
     * @param stringType String type AST node.
     */
    void visitStringType(StringType stringType);

    /**
     * Visits reference to built-in float type.
     *
     * @param floatType Float type AST node.
     */
    void visitFloatType(FloatType floatType);

    /**
     * Visits a documentation comment.
     *
     * @param docComment Documentation comment AST node.
     */
    void visitDocComment(DocComment docComment);

    /**
     * Visits documentation paragraph.
     *
     * @param docParagraph Documentation paragraph AST node.
     */
    void visitDocParagraph(DocParagraph docParagraph);

    /**
     * Visits documentation element.
     *
     * @param docElement Documentation element AST node.
     */
    void visitDocElement(DocElement docElement);

    /**
     * Visits documentation multiline.
     *
     * @param docMultiline Documentation multiline AST node.
     */
    void visitDocMultiline(DocMultiline docMultiline);

    /**
     * Visits a see tag within a documentation comment.
     *
     * @param docTagSee See tag AST node.
     */
    void visitDocTagSee(DocTagSee docTagSee);

    /**
     * Visits a todo tag within a documentation comment.
     *
     * @param docTagTodo Todo tag AST node.
     */
    void visitDocTagTodo(DocTagTodo docTagTodo);

    /**
     * Visits a param tag within a documentation comment.
     *
     * @param docTagParam Param tag AST node.
     */
    void visitDocTagParam(DocTagParam docTagParam);

    /**
     * Visits a deprecated tag within a documentation comment.
     *
     * @param docTagDeprecated Deprecated tag AST node.
     */
    void visitDocTagDeprecated(DocTagDeprecated docTagDeprecated);

    /**
     * Visits a single line of documentation.
     *
     * @param docLine Documentation line AST node.
     */
    void visitDocLine(DocLine docLine);

    /**
     * Visits documentation text wrapper.
     *
     * DocLineElement can be either a text or a see tag.
     *
     * @param docLineElement Documentation line element AST node.
     */
    void visitDocLineElement(DocLineElement docLineElement);

    /**
     * Visits documentation text.
     *
     * @param docText Documentation text AST node.
     */
    void visitDocText(DocText docText);
}
