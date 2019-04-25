package zserio.ast4;

public class ZserioAstEvaluator implements ZserioAstVisitor
{
    @Override
    public void visitRoot(Root root)
    {
        evaluate(root);
    }

    @Override
    public void visitPackage(Package unitPackage)
    {
        evaluate(unitPackage);
    }

    @Override
    public void visitImport(Import unitImport)
    {
        evaluate(unitImport);
    }

    @Override
    public void visitConstType(ConstType constType)
    {
        evaluate(constType);
    }

    @Override
    public void visitSubtype(Subtype subtypeType)
    {
        evaluate(subtypeType);
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        evaluate(structureType);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        evaluate(choiceType);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        evaluate(unionType);
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        evaluate(enumType);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        evaluate(sqlTableType);
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        evaluate(sqlDatabaseType);
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        evaluate(serviceType);
    }

    @Override
    public void visitField(Field field)
    {
        evaluate(field);
    }

    @Override
    public void visitChoiceCase(ChoiceCase choiceCase)
    {
        evaluate(choiceCase);
    }

    @Override
    public void visitChoiceDefault(ChoiceDefault choiceDefault)
    {
        evaluate(choiceDefault);
    }

    @Override
    public void visitEnumItem(EnumItem enumItem)
    {
        evaluate(enumItem);
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        evaluate(sqlConstraint);
    }

    @Override
    public void visitRpc(Rpc rpc)
    {
        evaluate(rpc);
    }

    @Override
    public void visitFunction(FunctionType functionType)
    {
        evaluate(functionType);
    }

    @Override
    public void visitParameter(Parameter parameter)
    {
        evaluate(parameter);
    }

    @Override
    public void visitExpression(Expression expresssion)
    {
        // this evaluates whole expression tree at once
        expresssion.evaluate();
    }

    @Override
    public void visitArrayType(ArrayType arrayType)
    {
        evaluate(arrayType);
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        evaluate(typeInstantiation);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        evaluate(typeReference);
    }

    @Override
    public void visitStdIntegerType(StdIntegerType stdIntegerType)
    {
        evaluate(stdIntegerType);
    }

    @Override
    public void visitVarIntegerType(VarIntegerType varIntegerType)
    {
        evaluate(varIntegerType);
    }

    @Override
    public void visitBitFieldType(BitFieldType bitFieldType)
    {
        evaluate(bitFieldType);
    }

    @Override
    public void visitBooleanType(BooleanType booleanType)
    {
        evaluate(booleanType);
    }

    @Override
    public void visitStringType(StringType stringType)
    {
        evaluate(stringType);
    }

    @Override
    public void visitFloatType(FloatType floatType)
    {
        evaluate(floatType);
    }

    private void evaluate(AstNodeBase astNode)
    {
        astNode.visitChildren(this);
        astNode.evaluate();
    }
};
