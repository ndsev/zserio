package zserio.ast4;

public class ZserioAstChecker implements ZserioAstVisitor
{
    @Override
    public void visitRoot(Root root)
    {
        check(root);
    }

    @Override
    public void visitTranslationUnit(TranslationUnit translationUnit)
    {
        check(translationUnit);
    }

    @Override
    public void visitPackage(Package unitPackage)
    {
        check(unitPackage);
    }

    @Override
    public void visitImport(Import unitImport)
    {
        check(unitImport);
    }

    @Override
    public void visitConstType(ConstType constType)
    {
        check(constType);
    }

    @Override
    public void visitSubtype(Subtype subtypeType)
    {
        check(subtypeType);
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        check(structureType);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        check(choiceType);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        check(unionType);
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        check(enumType);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        check(sqlTableType);
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        check(sqlDatabaseType);
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        check(serviceType);
    }

    @Override
    public void visitField(Field field)
    {
        check(field);
    }

    @Override
    public void visitChoiceCase(ChoiceCase choiceCase)
    {
        check(choiceCase);
    }

    @Override
    public void visitChoiceDefault(ChoiceDefault choiceDefault)
    {
        check(choiceDefault);
    }

    @Override
    public void visitEnumItem(EnumItem enumItem)
    {
        check(enumItem);
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        check(sqlConstraint);
    }

    @Override
    public void visitRpc(Rpc rpc)
    {
        check(rpc);
    }

    @Override
    public void visitFunction(FunctionType functionType)
    {
        check(functionType);
    }

    @Override
    public void visitParameter(Parameter parameter)
    {
        check(parameter);
    }

    @Override
    public void visitExpression(Expression expresssion)
    {
        check(expresssion);
    }
    
    @Override
    public void visitArrayType(ArrayType arrayType)
    {
        check(arrayType);
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        check(typeInstantiation);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        check(typeReference);
    }

    @Override
    public void visitStdIntegerType(StdIntegerType stdIntegerType)
    {
        check(stdIntegerType);
    }

    @Override
    public void visitVarIntegerType(VarIntegerType varIntegerType)
    {
        check(varIntegerType);
    }

    @Override
    public void visitBitFieldType(BitFieldType bitFieldType)
    {
        check(bitFieldType);
    }

    @Override
    public void visitBooleanType(BooleanType booleanType)
    {
        check(booleanType);
    }

    @Override
    public void visitStringType(StringType stringType)
    {
        check(stringType);
    }

    @Override
    public void visitFloatType(FloatType floatType)
    {
        check(floatType);
    }

    private void check(AstNodeBase astNode)
    {
        astNode.visitChildren(this);
        astNode.check();
    }
};
