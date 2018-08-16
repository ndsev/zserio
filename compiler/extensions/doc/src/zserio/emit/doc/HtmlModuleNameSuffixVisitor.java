package zserio.emit.doc;

import zserio.ast.ArrayType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.ServiceType;
import zserio.ast.ZserioTypeVisitor;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.FunctionType;
import zserio.ast.StructureType;
import zserio.ast.SignedBitFieldType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.UnsignedBitFieldType;
import zserio.ast.VarIntegerType;

/**
 * The ZserioType visitor which resolves the suffix for HTML module name from ZserioType.
 */
public class HtmlModuleNameSuffixVisitor implements ZserioTypeVisitor
{
    /** {@inheritDoc} */
    @Override
    public void visitArrayType(ArrayType type)
    {
        htmlModuleNameSuffix = "ARRAY";
    }

    /** {@inheritDoc} */
    @Override
    public void visitUnsignedBitFieldType(UnsignedBitFieldType type)
    {
        htmlModuleNameSuffix = "BIT";
    }

    /** {@inheritDoc} */
    @Override
    public void visitBooleanType(BooleanType type)
    {
        htmlModuleNameSuffix = "BOOL";
    }

    /** {@inheritDoc} */
    @Override
    public void visitChoiceType(ChoiceType type)
    {
        htmlModuleNameSuffix = "CHOICE";
    }

    /** {@inheritDoc} */
    @Override
    public void visitConstType(ConstType type)
    {
        htmlModuleNameSuffix = "CONSTTYPE";
    }

    /** {@inheritDoc} */
    @Override
    public void visitEnumType(EnumType type)
    {
        htmlModuleNameSuffix = "ENUM";
    }

    /** {@inheritDoc} */
    @Override
    public void visitFloatType(FloatType type)
    {
        htmlModuleNameSuffix = "FLOAT";
    }

    /** {@inheritDoc} */
    @Override
    public void visitFunctionType(FunctionType type)
    {
        htmlModuleNameSuffix = "FUNCTION";
    }

    /** {@inheritDoc} */
    @Override
    public void visitStructureType(StructureType type)
    {
        htmlModuleNameSuffix = "STRUCTURE";
    }

    /** {@inheritDoc} */
    @Override
    public void visitSignedBitFieldType(SignedBitFieldType type)
    {
        htmlModuleNameSuffix = "SIGNED_BIT_FIELD";
    }

    /** {@inheritDoc} */
    @Override
    public void visitSqlDatabaseType(SqlDatabaseType type)
    {
        htmlModuleNameSuffix = "SQL_DATABASE";
    }

    /** {@inheritDoc} */
    @Override
    public void visitSqlTableType(SqlTableType type)
    {
        htmlModuleNameSuffix = "SQL_TABLE";
    }

    /** {@inheritDoc} */
    @Override
    public void visitStdIntegerType(StdIntegerType type)
    {
        htmlModuleNameSuffix = "STD_INTEGER";
    }

    /** {@inheritDoc} */
    @Override
    public void visitStringType(StringType type)
    {
        htmlModuleNameSuffix = "STRING";
    }

    /** {@inheritDoc} */
    @Override
    public void visitSubtype(Subtype type)
    {
        htmlModuleNameSuffix = "SUBTYPE";
    }

    /** {@inheritDoc} */
    @Override
    public void visitTypeInstantiation(TypeInstantiation type)
    {
        htmlModuleNameSuffix = "TYPE_INSTANTIATION";
    }

    /** {@inheritDoc} */
    @Override
    public void visitTypeReference(TypeReference type)
    {
        htmlModuleNameSuffix = "TYPE_REFERENCE";
    }

    /** {@inheritDoc} */
    @Override
    public void visitUnionType(UnionType type)
    {
        htmlModuleNameSuffix = "UNION";

    }

    /** {@inheritDoc} */
    @Override
    public void visitVarIntegerType(VarIntegerType type)
    {
        htmlModuleNameSuffix = "VAR_INTEGER";

    }

    /** {@inheritDoc} */
    @Override
    public void visitServiceType(ServiceType type) {
        htmlModuleNameSuffix = "SERVICE";
    }

    /**
     * Returns the resolved suffix for HMTL module name.
     *
     * @throws ZserioEmitHtmlException Throws if called without visiting ZserioType.
     */
    public String getSuffix() throws ZserioEmitHtmlException
    {
        if (htmlModuleNameSuffix == null)
            throw new ZserioEmitHtmlException("Bad usage of HtmlModuleNameSuffixVisitor!");

        return htmlModuleNameSuffix;
    }

    private String htmlModuleNameSuffix;
}
