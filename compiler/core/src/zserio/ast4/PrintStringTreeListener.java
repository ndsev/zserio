package zserio.ast4;

public class PrintStringTreeListener implements ZserioListener
{
    @Override
    public void beginRoot(Root root)
    {
        print("root");
        ++level;
    }

    @Override
    public void endRoot(Root root)
    {
        --level;
    }

    @Override
    public void beginTranslationUnit(TranslationUnit translationUnit)
    {
        print("translationUnit [" + translationUnit.getLocation().getFileName() + "]");
        ++level;
    }

    @Override
    public void endTranslationUnit(TranslationUnit translationUnit)
    {
        --level;
    }

    @Override
    public void enterPackage(Package unitPackage)
    {
        print("package [" + unitPackage.getPackageName() + "]");
    }

    @Override
    public void enterImport(Import unitImport)
    {
        print("import [" + unitImport.getImportedPackageName() + "," + unitImport.getImportedTypeName() + "]");
    }

    @Override
    public void beginConstType(ConstType constType)
    {
        print("constType [" + constType.getName() + "]");
        ++level;
    }

    @Override
    public void endConstType(ConstType constType)
    {
        --level;
    }

    @Override
    public void beginSubtype(Subtype subtype)
    {
        print("subtype [" + subtype.getName() + "]");
        ++level;
    }

    @Override
    public void endSubtype(Subtype subtype)
    {
        --level;
    }

    @Override
    public void beginStructureType(StructureType structureType)
    {
        print("structureType [" + structureType.getName() + "]");
        ++level;
    }

    @Override
    public void endStructureType(StructureType structureType)
    {
        --level;
    }

    @Override
    public void beginChoiceType(ChoiceType choiceType)
    {
        print("choiceType [" + choiceType.getName() + "]");
        ++level;
    }

    @Override
    public void endChoiceType(ChoiceType choiceType)
    {
        --level;
    }

    @Override
    public void beginUnionType(UnionType unionType)
    {
        print("unionType [" + unionType.getName() + "]");
        ++level;
    }

    @Override
    public void endUnionType(UnionType unionType)
    {
        --level;
    }

    @Override
    public void beginEnumType(EnumType enumType)
    {
        print("enumType [" + enumType.getName() + "]");
        ++level;
    }

    @Override
    public void endEnumType(EnumType enumType)
    {
        --level;
    }

    @Override
    public void beginSqlTableType(SqlTableType sqlTableType)
    {
        print("sqlTableType [" + sqlTableType.getName() + "]");
        ++level;
    }

    @Override
    public void endSqlTableType(SqlTableType sqlTableType)
    {
        --level;
    }

    @Override
    public void beginSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        print("sqlDatabaseType [" + sqlDatabaseType.getName() + "]");
        ++level;
    }

    @Override
    public void endSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        --level;
    }

    @Override
    public void beginServiceType(ServiceType serviceType)
    {
        print("serviceType [" + serviceType.getName() + "]");
        ++level;
    }

    @Override
    public void endServiceType(ServiceType serviceType)
    {
        --level;
    }

    @Override
    public void beginField(Field field)
    {
        print("field [" + field.getName() + "]");
        ++level;
    }

    @Override
    public void endField(Field field)
    {
        --level;
    }

    @Override public void beginChoiceCase(ChoiceCase choiceCase)
    {
        print("choiceCase");
        ++level;
    }

    @Override public void endChoiceCase(ChoiceCase choiceCase)
    {
        --level;
    }

    @Override public void beginChoiceDefault(ChoiceDefault choiceDefault)
    {
        print("choiceDefault");
        ++level;
    }

    @Override public void endChoiceDefault(ChoiceDefault choiceDefault)
    {
        --level;
    }

    @Override
    public void beginEnumItem(EnumItem enumItem)
    {
        print("enumItem [" + enumItem.getName() + "]");
        ++level;
    }

    @Override
    public void endEnumItem(EnumItem enumItem)
    {
        --level;
    }

    @Override
    public void beginSqlConstraint(SqlConstraint sqlConstraint)
    {
        print("sqlConstraint");
        ++level;
    }

    @Override
    public void endSqlConstraint(SqlConstraint sqlConstraint)
    {
        --level;
    }

    @Override
    public void beginRpc(Rpc rpc)
    {
        print("rpc [" + rpc.getName() + "]");
        ++level;
    }

    @Override
    public void endRpc(Rpc rpc)
    {
        --level;
    }

    @Override
    public void beginFunction(FunctionType functionType)
    {
        print("function [" + functionType.getName() + "]");
        ++level;
    }

    @Override
    public void endFunction(FunctionType functionType)
    {
        --level;
    }

    @Override
    public void beginParameter(Parameter parameter)
    {
        print("parameter [" + parameter.getName() + "]");
        ++level;
    }

    @Override
    public void endParameter(Parameter parameter)
    {
        --level;
    }

    @Override
    public void beginExpression(Expression expression)
    {
        print("expression [\"" + expression.getExpressionString() + "\"]");
        ++level;
    }

    @Override
    public void endExpression(Expression expression)
    {
        --level;
    }

    @Override
    public void beginArrayType(ArrayType arrayType)
    {
        print("arrayType");
        ++level;
    }

    @Override
    public void endArrayType(ArrayType arrayType)
    {
        --level;
    }

    public void beginTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        print("typeInstantiation");
        ++level;
    }

    public void endTypeInstantiation(TypeInstantiation typeInstantiation)
    {
        --level;
    }

    @Override
    public void enterTypeReference(TypeReference typeReference)
    {
        print("typeReference [" + typeReference.getName() + "]");
    }

    @Override
    public void enterStdIntegerType(StdIntegerType stdIntegerType)
    {
        print("stdIntegerType [" + stdIntegerType.getName() + "]");
    }

    @Override
    public void enterVarIntegerType(VarIntegerType varIntegerType)
    {
        print("varIntegerType [" + varIntegerType.getName() + "]");
    }

    @Override
    public void beginBitFieldType(BitFieldType bitFieldType)
    {
        print("bitFieldType [" + (bitFieldType.isSigned() ? "signed" : "unsigned") + ", " +
                bitFieldType.getLengthExpression().getExpressionString() + "]");
        ++level;
    }

    @Override
    public void endBitFieldType(BitFieldType bitFieldType)
    {
        --level;
    }

    @Override
    public void enterBooleanType(BooleanType booleanType)
    {
        print("booleanType");
    }

    @Override
    public void enterStringType(StringType stringType)
    {
        print("stringType");
    }

    @Override
    public void enterFloatType(FloatType floatType)
    {
        print("floatType [" + floatType.getName() + "]");
    }

    private void print(String text)
    {
        final String indent = new String(new char[level * levelIndentLength]).replace('\0', ' ');
        System.out.println(indent + "- " + text);
    }

    private int level = 0;
    private static int levelIndentLength = 2;
}