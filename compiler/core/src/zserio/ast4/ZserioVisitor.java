package zserio.ast4;

public interface ZserioVisitor
{
    void visitRoot(Root root);

    void visitTranslationUnit(TranslationUnit translationUnit);
    void visitPackage(Package unitPackage);
    void visitImport(Import unitImport);
    void visitConstType(ConstType constType);
    void visitSubtype(Subtype subtypeType);
    void visitStructureType(StructureType structureType);
    void visitChoiceType(ChoiceType choiceType);
    void visitUnionType(UnionType unionType);
    void visitEnumType(EnumType enumType);
    void visitSqlTableType(SqlTableType sqlTableType);
    void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType);
    void visitServiceType(ServiceType serviceType);

    void visitField(Field field);

    void visitChoiceCase(ChoiceCase choiceCase);
    void visitChoiceDefault(ChoiceDefault choiceDefault);

    void visitEnumItem(EnumItem enumItem);

    void visitSqlConstraint(SqlConstraint sqlConstraint);

    void visitRpc(Rpc rpc);

    void visitFunction(FunctionType functionType);

    void visitParameter(Parameter parameter);

    void visitExpression(Expression expresssion);

    void visitArrayType(ArrayType arrayType);

    void visitTypeInstantiation(TypeInstantiation typeInstantiation);

    void visitTypeReference(TypeReference typeReference);

    void visitStdIntegerType(StdIntegerType stdIntegerType);
    void visitVarIntegerType(VarIntegerType varIntegerType);
    void visitBitFieldType(BitFieldType bitFieldType);
    void visitBooleanType(BooleanType booleanType);
    void visitStringType(StringType stringType);
    void visitFloatType(FloatType floatType);

    public class Base implements ZserioVisitor
    {
        @Override public void visitRoot(Root root) { root.visitChildren(this); }

        @Override public void visitTranslationUnit(TranslationUnit translationUnit) { translationUnit.visitChildren(this); }

        @Override public void visitPackage(Package unitPackage) { unitPackage.visitChildren(this); }

        @Override public void visitImport(Import unitImport) { unitImport.visitChildren(this); }

        @Override public void visitConstType(ConstType constType) { constType.visitChildren(this); }
        @Override public void visitSubtype(Subtype subtypeType) { subtypeType.visitChildren(this); }
        @Override public void visitStructureType(StructureType structureType) { structureType.visitChildren(this); }
        @Override public void visitChoiceType(ChoiceType choiceType) { choiceType.visitChildren(this); }
        @Override public void visitUnionType(UnionType unionType) { unionType.visitChildren(this); }
        @Override public void visitEnumType(EnumType enumType) { enumType.visitChildren(this); }
        @Override public void visitSqlTableType(SqlTableType sqlTableType) { sqlTableType.visitChildren(this); }
        @Override public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType) { sqlDatabaseType.visitChildren(this); }
        @Override public void visitServiceType(ServiceType serviceType) { serviceType.visitChildren(this); }

        @Override public void visitField(Field field) { field.visitChildren(this); }

        @Override public void visitChoiceCase(ChoiceCase choiceCase) { choiceCase.visitChildren(this); }
        @Override public void visitChoiceDefault(ChoiceDefault choiceDefault) { choiceDefault.visitChildren(this); }

        @Override public void visitEnumItem(EnumItem enumItem) { enumItem.visitChildren(this); }

        @Override public void visitSqlConstraint(SqlConstraint sqlConstraint) { sqlConstraint.visitChildren(this); }

        @Override public void visitRpc(Rpc rpc) { rpc.visitChildren(this); }

        @Override public void visitFunction(FunctionType functionType) { functionType.visitChildren(this); }

        @Override public void visitParameter(Parameter parameter) { parameter.visitChildren(this); }

        @Override public void visitExpression(Expression expresssion) { expresssion.visitChildren(this); }

        @Override public void visitArrayType(ArrayType arrayType) { arrayType.visitChildren(this); }

        @Override public void visitTypeInstantiation(TypeInstantiation typeInstantiation) { typeInstantiation.visitChildren(this); }

        @Override public void visitTypeReference(TypeReference typeReference) { typeReference.visitChildren(this); }

        @Override public void visitStdIntegerType(StdIntegerType stdIntegerType) { stdIntegerType.visitChildren(this); }
        @Override public void visitVarIntegerType(VarIntegerType varIntegerType) { varIntegerType.visitChildren(this); }
        @Override public void visitBitFieldType(BitFieldType bitFieldType) { bitFieldType.visitChildren(this); }
        @Override public void visitBooleanType(BooleanType booleanType) { booleanType.visitChildren(this); }
        @Override public void visitStringType(StringType stringType) { stringType.visitChildren(this); }
        @Override public void visitFloatType(FloatType floatType) { floatType.visitChildren(this); }
    }
}
