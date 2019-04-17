package zserio.ast4;

public class ZserioAstPrinter implements ZserioAstVisitor
{
    @Override
    public void visitRoot(Root root)
    {
        print("root");
        visitChildren(root);
    }

    @Override
    public void visitPackage(Package unitPackage)
    {
        print("package [" + unitPackage.getPackageName() + "]");
        visitChildren(unitPackage);
    }

    @Override
    public void visitImport(Import unitImport)
    {
        print("import [" + unitImport.getImportedPackageName() + "," + unitImport.getImportedTypeName() + "]");
        visitChildren(unitImport);
    }

    @Override
    public void visitConstType(ConstType constType)
    {
        print("constType [" + constType.getName() + "]");
        visitChildren(constType);
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        print("subtype [" + subtype.getName() + "]");
        visitChildren(subtype);
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        print("structureType [" + structureType.getName() + "]");
        visitChildren(structureType);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        print("choiceType [" + choiceType.getName() + "]");
        visitChildren(choiceType);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        print("unionType [" + unionType.getName() + "]");
        visitChildren(unionType);
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        print("enumType [" + enumType.getName() + "]");
        visitChildren(enumType);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        print("sqlTableType [" + sqlTableType.getName() + "]");
        visitChildren(sqlTableType);
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        print("sqlDatabaseType [" + sqlDatabaseType.getName() + "]");
        visitChildren(sqlDatabaseType);
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        print("serviceType [" + serviceType.getName() + "]");
        visitChildren(serviceType);
    }

    @Override
    public void visitField(Field field)
    {
        print("field [" + field.getName() + "]");
        visitChildren(field);
    }

    @Override public void visitChoiceCase(ChoiceCase choiceCase)
    {
        print("choiceCase");
        visitChildren(choiceCase);
    }

    @Override public void visitChoiceDefault(ChoiceDefault choiceDefault)
    {
        print("choiceDefault");
        visitChildren(choiceDefault);
    }

    @Override
    public void visitEnumItem(EnumItem enumItem)
    {
        print("enumItem [" + enumItem.getName() + "]");
        visitChildren(enumItem);
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        print("sqlConstraint");
        visitChildren(sqlConstraint);
    }

    @Override
    public void visitRpc(Rpc rpc)
    {
        print("rpc [" + rpc.getName() + "]");
        visitChildren(rpc);
    }

    @Override
    public void visitFunction(FunctionType functionType)
    {
        print("function [" + functionType.getName() + "]");
        visitChildren(functionType);
    }

    @Override
    public void visitParameter(Parameter parameter)
    {
        print("parameter [" + parameter.getName() + "]");
        visitChildren(parameter);
    }

    @Override
    public void visitExpression(Expression expression)
    {
        print("expression [\"" + expression.getExpressionString() + "\"]");
        visitChildren(expression);
    }

    @Override
    public void visitArrayType(ArrayType arrayType)
    {
        print("arrayType");
        visitChildren(arrayType);
    }

    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        print("typeInstantiation");
        visitChildren(typeInstantiation);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        print("typeReference [" + typeReference.getName() + "]");
        visitChildren(typeReference);
    }

    @Override
    public void visitStdIntegerType(StdIntegerType stdIntegerType)
    {
        print("stdIntegerType [" + stdIntegerType.getName() + "]");
        visitChildren(stdIntegerType);
    }

    @Override
    public void visitVarIntegerType(VarIntegerType varIntegerType)
    {
        print("varIntegerType [" + varIntegerType.getName() + "]");
        visitChildren(varIntegerType);
    }

    @Override
    public void visitBitFieldType(BitFieldType bitFieldType)
    {
        print("bitFieldType [" + (bitFieldType.isSigned() ? "signed" : "unsigned") + ", " +
                bitFieldType.getLengthExpression().getExpressionString() + "]");
        visitChildren(bitFieldType);
    }

    @Override
    public void visitBooleanType(BooleanType booleanType)
    {
        print("booleanType");
        visitChildren(booleanType);
    }

    @Override
    public void visitStringType(StringType stringType)
    {
        print("stringType");
        visitChildren(stringType);
    }

    @Override
    public void visitFloatType(FloatType floatType)
    {
        print("floatType [" + floatType.getName() + "]");
        visitChildren(floatType);
    }

    private void print(String text)
    {
        final String indent = new String(new char[level * levelIndentLength]).replace('\0', ' ');
        System.out.println(indent + "- " + text);
    }

    private void visitChildren(AstNode node)
    {
        ++level;
        node.visitChildren(this);
        --level;
    }

    private int level = 0;
    private static int levelIndentLength = 2;
}
