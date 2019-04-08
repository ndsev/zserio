package zserio.ast4;

public interface ZserioListener
{
    void beginRoot(Root root);
    void endRoot(Root root);

    void beginTranslationUnit(TranslationUnit translationUnit);
    void endTranslationUnit(TranslationUnit translationUnit);

    void enterPackage(Package unitPackage);

    void enterImport(Import unitImport);

    void beginSubtype(Subtype subtype);
    void endSubtype(Subtype subtype);

    void beginTypeReference(TypeReference typeReference);
    void endTypeReference(TypeReference typeReference);

    void enterStdIntegerType(StdIntegerType stdIntegerType);
    void enterVarIntegerType(VarIntegerType varIntegerType);
    void enterUnsignedBitFieldType(UnsignedBitFieldType unsignedBitFieldType);
    void enterSignedBitFieldType(SignedBitFieldType signedBitFieldType);
    void enterBooleanType(BooleanType booleanType);
    void enterStringType(StringType stringType);
    void enterFloatType(FloatType floatType);

    public class Base implements ZserioListener
    {
        @Override public void beginRoot(Root root) {}
        @Override public void endRoot(Root root) {}

        @Override public void beginTranslationUnit(TranslationUnit translationUnit) {}
        @Override public void endTranslationUnit(TranslationUnit translationUnit) {}

        @Override public void enterPackage(Package unitPackage) {}

        @Override public void enterImport(Import unitImport) {}

        @Override public void beginSubtype(Subtype subtype) {}
        @Override public void endSubtype(Subtype subtype) {}

        @Override public void beginTypeReference(TypeReference typeReference) {}
        @Override public void endTypeReference(TypeReference typeReference) {}

        @Override public void enterStdIntegerType(StdIntegerType stdIntegerType) {}
        @Override public void enterVarIntegerType(VarIntegerType varIntegerType) {}
        @Override public void enterUnsignedBitFieldType(UnsignedBitFieldType unsignedBitFieldType) {}
        @Override public void enterSignedBitFieldType(SignedBitFieldType signedBitFieldType) {}
        @Override public void enterBooleanType(BooleanType booleanType) {}
        @Override public void enterStringType(StringType stringType) {}
        @Override public void enterFloatType(FloatType floatType) {}
    }
}
