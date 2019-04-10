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
        print("structure [" + structureType.getPackage().getPackageName() + ", "
                + structureType.getName() + "]");
        ++level;
    }

    @Override
    public void endStructureType(StructureType structureType)
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
    public void enterExpression(Expression expression)
    {
        print("expression [\"" + expression.getExpressionString() + "\"]");
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
    public void enterUnsignedBitFieldType(UnsignedBitFieldType unsignedBitFieldType)
    {
        print("unsignedBitFieldType [" + "]"); // TODO: length expression
    }

    @Override
    public void enterSignedBitFieldType(SignedBitFieldType signedBitFieldType)
    {
        print("signedBitFieldType [" + "]"); // TODO: length expression
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