package zserio.ast4;

public interface ZserioListener
{
    void beginRoot(Root root);
    void endRoot(Root root);

    void beginTranslationUnit(TranslationUnit translationUnit);
    void endTranslationUnit(TranslationUnit translationUnit);

    void enterPackage(Package unitPackage);

    void enterImport(Import unitImport);

    void beginConstType(ConstType constType);
    void endConstType(ConstType constType);
    void beginSubtype(Subtype subtypeType);
    void endSubtype(Subtype subtypeType);
    void beginStructureType(StructureType structureType);
    void endStructureType(StructureType structureType);
    void beginChoiceType(ChoiceType choiceType);
    void endChoiceType(ChoiceType choiceType);

    void beginField(Field field);
    void endField(Field field);

    void beginChoiceCase(ChoiceCase choiceCase);
    void endChoiceCase(ChoiceCase choiceCase);
    void beginChoiceDefault(ChoiceDefault choiceDefault);
    void endChoiceDefault(ChoiceDefault choiceDefault);

    void beginFunction(FunctionType functionType);
    void endFunction(FunctionType functionType);

    void beginParameter(Parameter parameter);
    void endParameter(Parameter parameter);

    void beginExpression(Expression expresssion);
    void endExpression(Expression expresssion);

    void beginArrayType(ArrayType arrayType);
    void endArrayType(ArrayType arrayType);

    void beginTypeInstantiation(TypeInstantiation typeInstantiation);
    void endTypeInstantiation(TypeInstantiation typeInstantiation);

    void enterTypeReference(TypeReference typeReference);

    void enterStdIntegerType(StdIntegerType stdIntegerType);
    void enterVarIntegerType(VarIntegerType varIntegerType);
    void beginBitFieldType(BitFieldType bitFieldType);
    void endBitFieldType(BitFieldType bitFieldType);
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

        @Override public void beginConstType(ConstType constType) {}
        @Override public void endConstType(ConstType constType) {}
        @Override public void beginSubtype(Subtype subtypeType) {}
        @Override public void endSubtype(Subtype subtypeType) {}
        @Override public void beginStructureType(StructureType structureType) {}
        @Override public void endStructureType(StructureType structureType) {}
        @Override public void beginChoiceType(ChoiceType choiceType) {}
        @Override public void endChoiceType(ChoiceType choiceType) {}

        @Override public void beginField(Field field) {}
        @Override public void endField(Field field) {}

        @Override public void beginChoiceCase(ChoiceCase choiceCase) {}
        @Override public void endChoiceCase(ChoiceCase choiceCase) {}
        @Override public void beginChoiceDefault(ChoiceDefault choiceDefault) {}
        @Override public void endChoiceDefault(ChoiceDefault choiceDefault) {}

        @Override public void beginFunction(FunctionType functionType) {}
        @Override public void endFunction(FunctionType functionType) {}

        @Override public void beginParameter(Parameter parameter) {}
        @Override public void endParameter(Parameter parameter) {}

        @Override public void beginExpression(Expression expresssion) {}
        @Override public void endExpression(Expression expresssion) {}

        @Override public void beginArrayType(ArrayType arrayType) {}
        @Override public void endArrayType(ArrayType arrayType) {}

        @Override public void beginTypeInstantiation(TypeInstantiation typeInstantiation) {}
        @Override public void endTypeInstantiation(TypeInstantiation typeInstantiation) {}

        @Override public void enterTypeReference(TypeReference typeReference) {}

        @Override public void enterStdIntegerType(StdIntegerType stdIntegerType) {}
        @Override public void enterVarIntegerType(VarIntegerType varIntegerType) {}
        @Override public void beginBitFieldType(BitFieldType bitFieldType) {}
        @Override public void endBitFieldType(BitFieldType bitFieldType) {}
        @Override public void enterBooleanType(BooleanType booleanType) {}
        @Override public void enterStringType(StringType stringType) {}
        @Override public void enterFloatType(FloatType floatType) {}
    }
}
