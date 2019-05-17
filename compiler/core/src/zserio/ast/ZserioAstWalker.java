package zserio.ast;

/**
 * The class provide walker implementation of ZserioAstVisitor.
 *
 * This implementation can be extended to create a visitor which needs override only a subset of available
 * methods. Default methods implementation just traverse all children nodes.
 */
public class ZserioAstWalker implements ZserioAstVisitor
{
    @Override
    public void visitRoot(Root root)
    {
        root.visitChildren(this);
    }

    @Override
    public void visitPackage(Package unitPackage)
    {
        unitPackage.visitChildren(this);
    }

    @Override
    public void visitImport(Import unitImport)
    {
        unitImport.visitChildren(this);
    }

    @Override
    public void visitConstType(ConstType constType)
    {
        constType.visitChildren(this);
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        subtype.visitChildren(this);
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        structureType.visitChildren(this);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        choiceType.visitChildren(this);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        unionType.visitChildren(this);
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        enumType.visitChildren(this);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        sqlTableType.visitChildren(this);
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        sqlDatabaseType.visitChildren(this);
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        serviceType.visitChildren(this);
    }

    @Override
    public void visitField(Field field)
    {
        field.visitChildren(this);
    }

    @Override
    public void visitChoiceCase(ChoiceCase choiceCase)
    {
        choiceCase.visitChildren(this);
    }

    @Override
    public void visitChoiceCaseExpression(ChoiceCaseExpression choiceCaseExpression)
    {
        choiceCaseExpression.visitChildren(this);
    }

    @Override
    public void visitChoiceDefault(ChoiceDefault choiceDefault)
    {
        choiceDefault.visitChildren(this);
    }

    @Override
    public void visitEnumItem(EnumItem enumItem)
    {
        enumItem.visitChildren(this);
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        sqlConstraint.visitChildren(this);
    }

    @Override
    public void visitRpc(Rpc rpc)
    {
        rpc.visitChildren(this);
    }

    @Override
    public void visitFunctionType(FunctionType functionType)
    {
        functionType.visitChildren(this);
    }

    @Override
    public void visitParameter(Parameter parameter)
    {
        parameter.visitChildren(this);
    }

    @Override
    public void visitExpression(Expression expresssion)
    {
        expresssion.visitChildren(this);
    }

    @Override
    public void visitArrayType(ArrayType arrayType)
    {
        arrayType.visitChildren(this);
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        typeInstantiation.visitChildren(this);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        typeReference.visitChildren(this);
    }

    @Override
    public void visitStdIntegerType(StdIntegerType stdIntegerType)
    {
        stdIntegerType.visitChildren(this);
    }

    @Override
    public void visitVarIntegerType(VarIntegerType varIntegerType)
    {
        varIntegerType.visitChildren(this);
    }

    @Override
    public void visitBitFieldType(BitFieldType bitFieldType)
    {
        bitFieldType.visitChildren(this);
    }

    @Override
    public void visitBooleanType(BooleanType booleanType)
    {
        booleanType.visitChildren(this);
    }

    @Override
    public void visitStringType(StringType stringType)
    {
        stringType.visitChildren(this);
    }

    @Override
    public void visitFloatType(FloatType floatType)
    {
        floatType.visitChildren(this);
    }

    @Override
    public void visitDocComment(DocComment docComment)
    {
        docComment.visitChildren(this);
    }

    @Override
    public void visitDocParagraph(DocParagraph docParagraph)
    {
        docParagraph.visitChildren(this);
    }

    @Override
    public void visitDocElement(DocElement docElement)
    {
        docElement.visitChildren(this);
    }

    @Override
    public void visitDocMultiline(DocMultiline docMultiline)
    {
        docMultiline.visitChildren(this);
    }

    @Override
    public void visitDocTagSee(DocTagSee docTagSee)
    {
        docTagSee.visitChildren(this);
    }

    @Override
    public void visitDocTagTodo(DocTagTodo docTagTodo)
    {
        docTagTodo.visitChildren(this);
    }

    @Override
    public void visitDocTagParam(DocTagParam docTagParam)
    {
        docTagParam.visitChildren(this);
    }

    @Override
    public void visitDocTagDeprecated(DocTagDeprecated docTagDeprecated)
    {
        docTagDeprecated.visitChildren(this);
    }

    @Override
    public void visitDocLine(DocLine docLine)
    {
        docLine.visitChildren(this);
    }

    @Override
    public void visitDocLineElement(DocLineElement docLineElement)
    {
        docLineElement.visitChildren(this);
    }

    @Override
    public void visitDocText(DocText docText)
    {
        docText.visitChildren(this);
    }
}
