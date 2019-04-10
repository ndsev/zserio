package zserio.ast4;

public interface ZserioListener
{
    void beginRoot(Root root);
    void endRoot(Root root);

    void beginTranslationUnit(TranslationUnit translationUnit);
    void endTranslationUnit(TranslationUnit translationUnit);

    void enterPackage(Package unitPackage);

    void enterImport(Import unitImport);

    void beginSubtype(Subtype subtypeType);
    void endSubtype(Subtype subtypeType);
    void beginStructureType(StructureType structureType);
    void endStructureType(StructureType structureType);

    void beginField(Field field);
    void endField(Field field);

    void beginFunction(FunctionType functionType);
    void endFunction(FunctionType functionType);

    void beginParameter(Parameter parameter);
    void endParameter(Parameter parameter);

    void enterExpression(Expression expresssion);

    void beginArrayType(ArrayType arrayType);
    void endArrayType(ArrayType arrayType);

    void beginTypeInstantiation(TypeInstantiation typeInstantiation);
    void endTypeInstantiation(TypeInstantiation typeInstantiation);

    void enterTypeReference(TypeReference typeReference);

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

        @Override public void beginSubtype(Subtype subtypeType) {}
        @Override public void endSubtype(Subtype subtypeType) {}
        @Override public void beginStructureType(StructureType structureType) {}
        @Override public void endStructureType(StructureType structureType) {}

        @Override public void beginField(Field field) {}
        @Override public void endField(Field field) {}

        @Override public void beginFunction(FunctionType functionType) {}
        @Override public void endFunction(FunctionType functionType) {}

        @Override public void beginParameter(Parameter parameter) {}
        @Override public void endParameter(Parameter parameter) {}

        @Override public void enterExpression(Expression expresssion) {}

        @Override public void beginArrayType(ArrayType arrayType) {}
        @Override public void endArrayType(ArrayType arrayType) {}

        @Override public void beginTypeInstantiation(TypeInstantiation typeInstantiation) {}
        @Override public void endTypeInstantiation(TypeInstantiation typeInstantiation) {}

        @Override public void enterTypeReference(TypeReference typeReference) {}

        @Override public void enterStdIntegerType(StdIntegerType stdIntegerType) {}
        @Override public void enterVarIntegerType(VarIntegerType varIntegerType) {}
        @Override public void enterUnsignedBitFieldType(UnsignedBitFieldType unsignedBitFieldType) {}
        @Override public void enterSignedBitFieldType(SignedBitFieldType signedBitFieldType) {}
        @Override public void enterBooleanType(BooleanType booleanType) {}
        @Override public void enterStringType(StringType stringType) {}
        @Override public void enterFloatType(FloatType floatType) {}
    }
}
