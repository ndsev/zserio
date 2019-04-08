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
    public void beginTypeReference(TypeReference typeReference)
    {
        print("typeReference");
        ++level;
    }

    @Override
    public void endTypeReference(TypeReference typeReference)
    {
        --level;
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