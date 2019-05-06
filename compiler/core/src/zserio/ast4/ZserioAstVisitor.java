package zserio.ast4;

public interface ZserioAstVisitor
{
    void visitRoot(Root root);

    void visitPackage(Package unitPackage);

    void visitImport(Import unitImport);

    void visitConstType(ConstType constType);
    void visitSubtype(Subtype subtype);
    void visitStructureType(StructureType structureType);
    void visitChoiceType(ChoiceType choiceType);
    void visitUnionType(UnionType unionType);
    void visitEnumType(EnumType enumType);
    void visitSqlTableType(SqlTableType sqlTableType);
    void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType);
    void visitServiceType(ServiceType serviceType);

    void visitField(Field field);

    void visitChoiceCase(ChoiceCase choiceCase);
    void visitChoiceCaseExpression(ChoiceCaseExpression choiceCaseExpression);
    void visitChoiceDefault(ChoiceDefault choiceDefault);

    void visitEnumItem(EnumItem enumItem);

    void visitSqlConstraint(SqlConstraint sqlConstraint);

    void visitRpc(Rpc rpc);

    void visitFunction(FunctionType functionType);

    void visitParameter(Parameter parameter);

    void visitExpression(Expression expresssion);

    void visitArrayType(ArrayType arrayType);

    void visitTypeInstantiation(TypeInstantiation typeInstantiation);

    void visitTypeReference(TypeReference typeReference);

    void visitStdIntegerType(StdIntegerType stdIntegerType);
    void visitVarIntegerType(VarIntegerType varIntegerType);
    void visitBitFieldType(BitFieldType bitFieldType);
    void visitBooleanType(BooleanType booleanType);
    void visitStringType(StringType stringType);
    void visitFloatType(FloatType floatType);

    void visitDocComment(DocComment docComment);
    void visitDocTagSee(DocTagSee docTagSee);
    void visitDocTagTodo(DocTagTodo docTagTodo);
    void visitDocTagParam(DocTagParam docTagParam);
    void visitDocParagraph(DocParagraph docParagraph);
    void visitDocTextLine(DocTextLine docTextLine);
    void visitDocText(DocText docText);
    void visitDocTextElement(DocTextElement docTextElement);
}
