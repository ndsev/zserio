package zserio.emit.doc;

import zserio.ast.ArrayInstantiation;
import zserio.ast.BitmaskType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.ServiceType;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.Function;
import zserio.ast.PubsubType;
import zserio.ast.StructureType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.emit.common.ZserioEmitException;

/**
 * The ZserioType visitor which resolves the suffix for HTML module name from ZserioType.
 */
class HtmlModuleNameSuffixVisitor extends ZserioAstDefaultVisitor
{
    @Override
    public void visitBooleanType(BooleanType type)
    {
        htmlModuleNameSuffix = "BOOL";
    }

    @Override
    public void visitChoiceType(ChoiceType type)
    {
        htmlModuleNameSuffix = "CHOICE";
    }

    @Override
    public void visitConstant(Constant constant)
    {
        htmlModuleNameSuffix = "CONSTANT";
    }

    @Override
    public void visitEnumType(EnumType type)
    {
        htmlModuleNameSuffix = "ENUM";
    }

    @Override
    public void visitBitmaskType(BitmaskType type)
    {
        htmlModuleNameSuffix = "BITMASK";
    }

    @Override
    public void visitFloatType(FloatType type)
    {
        htmlModuleNameSuffix = "FLOAT";
    }

    @Override
    public void visitFunction(Function type)
    {
        htmlModuleNameSuffix = "FUNCTION";
    }

    @Override
    public void visitServiceType(ServiceType type)
    {
        htmlModuleNameSuffix = "SERVICE";
    }

    @Override
    public void visitPubsubType(PubsubType type)
    {
        htmlModuleNameSuffix = "SERVICE";
    }

    @Override
    public void visitFixedBitFieldType(FixedBitFieldType type)
    {
        if (type.isSigned())
            htmlModuleNameSuffix = "SIGNED_FIXED_BIT_FIELD";
        else
            htmlModuleNameSuffix = "UNSIGNED_FIXED_BIT_FIELD";
    }

    @Override
    public void visitDynamicBitFieldType(DynamicBitFieldType type)
    {
        if (type.isSigned())
            htmlModuleNameSuffix = "SIGNED_DYNAMIC_BIT_FIELD";
        else
            htmlModuleNameSuffix = "UNSIGNED_DYNAMIC_BIT_FIELD";
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType type)
    {
        htmlModuleNameSuffix = "SQL_DATABASE";
    }

    @Override
    public void visitSqlTableType(SqlTableType type)
    {
        htmlModuleNameSuffix = "SQL_TABLE";
    }

    @Override
    public void visitStdIntegerType(StdIntegerType type)
    {
        htmlModuleNameSuffix = "STD_INTEGER";
    }

    @Override
    public void visitStringType(StringType type)
    {
        htmlModuleNameSuffix = "STRING";
    }

    @Override
    public void visitStructureType(StructureType type)
    {
        htmlModuleNameSuffix = "STRUCTURE";
    }

    @Override
    public void visitSubtype(Subtype type)
    {
        htmlModuleNameSuffix = "SUBTYPE";
    }

    @Override
    public void visitTypeInstantiation(TypeInstantiation type)
    {
        if (type instanceof ArrayInstantiation)
            type = ((ArrayInstantiation) type).getElementTypeInstantiation();
        type.getType().accept(this);
    }

    @Override
    public void visitTypeReference(TypeReference type)
    {
        htmlModuleNameSuffix = "TYPE_REFERENCE";
    }

    @Override
    public void visitUnionType(UnionType type)
    {
        htmlModuleNameSuffix = "UNION";
    }

    @Override
    public void visitVarIntegerType(VarIntegerType type)
    {
        htmlModuleNameSuffix = "VAR_INTEGER";
    }

    /**
     * Returns the resolved suffix for HMTL module name.
     *
     * @throws ZserioEmitException Throws if called without visiting ZserioType.
     */
    public String getSuffix() throws ZserioEmitException
    {
        if (htmlModuleNameSuffix == null)
            throw new ZserioEmitException("Bad usage of HtmlModuleNameSuffixVisitor!");

        return htmlModuleNameSuffix;
    }

    private String htmlModuleNameSuffix;
}
