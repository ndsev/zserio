package zserio.ast;

/**
 * The interface for ZserioType visitors.
 *
 * These visitors are used to extend functionality of ZserioType. This approach
 *
 * - avoids 'instanceof' calling which must be called in correct order in case of inheritance
 * - allows compile-time checking of implementation if some new ZserioType is added
 */
public interface ZserioTypeVisitor
{
    /**
     * Visitor called from ArrayType class.
     *
     * @param type ArrayType instance which calls this visitor.
     */
    void visitArrayType(ArrayType type);

    /**
     * Visitor called from BitFieldType class.
     *
     * @param type BitFieldType instance which calls this visitor.
     */
    void visitUnsignedBitFieldType(UnsignedBitFieldType type);

    /**
     * Visitor called from BooleanType class.
     *
     * @param type BooleanType instance which calls this visitor.
     */
    void visitBooleanType(BooleanType type);

    /**
     * Visitor called from ChoiceType class.
     *
     * @param type ChoiceType instance which calls this visitor.
     */
    void visitChoiceType(ChoiceType type);

    /**
     * Visitor called from ConstType class.
     *
     * @param type ConstType instance which calls this visitor.
     */
    void visitConstType(ConstType type);

    /**
     * Visitor called from EnumType class.
     *
     * @param type EnumType instance which calls this visitor.
     */
    void visitEnumType(EnumType type);

    /**
     * Visitor called from FloatType class.
     *
     * @param type FloatType instance which calls this visitor.
     */
    void visitFloatType(FloatType type);

    /**
     * Visitor called from FunctionType class.
     *
     * @param type FunctionType instance which calls this visitor.
     */
    void visitFunctionType(FunctionType type);

    /**
     * Visitor called from RpcType class.
     *
     * @param type RpcType instance which calls this visitor.
     */
    void visitRpcType(RpcType type);

    /**
     * Visitor called from StructureType class.
     *
     * @param type StructureType instance which calls this visitor.
     */
    void visitStructureType(StructureType type);

    /**
     * Visitor called from SignedBitFieldType class.
     *
     * @param type SignedBitFieldType instance which calls this visitor.
     */
    void visitSignedBitFieldType(SignedBitFieldType type);

    /**
     * Visitor called from SqlDatabaseType class.
     *
     * @param type SqlDatabaseType instance which calls this visitor.
     */
    void visitSqlDatabaseType(SqlDatabaseType type);

    /**
     * Visitor called from SqlTableType class.
     *
     * @param type SqlTableType instance which calls this visitor.
     */
    void visitSqlTableType(SqlTableType type);

    /**
     * Visitor called from StdIntegerType class.
     *
     * @param type StdIntegerType instance which calls this visitor.
     */
    void visitStdIntegerType(StdIntegerType type);

    /**
     * Visitor called from StringType class.
     *
     * @param type StringType instance which calls this visitor.
     */
    void visitStringType(StringType type);

    /**
     * Visitor called from Subtype class.
     *
     * @param type Subtype instance which calls this visitor.
     */
    void visitSubtype(Subtype type);

    /**
     * Visitor called from TypeInstantiation class.
     *
     * @param type TypeInstantiation instance which calls this visitor.
     */
    void visitTypeInstantiation(TypeInstantiation type);

    /**
     * Visitor called from TypeReference class.
     *
     * @param type TypeReference instance which calls this visitor.
     */
    void visitTypeReference(TypeReference type);

    /**
     * Visitor called from UnionType class.
     *
     * @param type UnionType instance which calls this visitor.
     */
    void visitUnionType(UnionType type);

    /**
     * Visitor called from VarIntegerType class.
     *
     * @param type VarIntegerType instance which calls this visitor.
     */
    void visitVarIntegerType(VarIntegerType type);
}
