package zserio.emit.doc;

import zserio.ast.ArrayType;
import zserio.ast.BitFieldType;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.ConstType;
import zserio.ast.ServiceType;
import zserio.ast.EnumType;
import zserio.ast.FloatType;
import zserio.ast.FunctionType;
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
public class HtmlModuleNameSuffixVisitor extends ZserioAstDefaultVisitor
{
    @Override
    public void visitArrayType(ArrayType type)
    {
        htmlModuleNameSuffix = "ARRAY";
    }

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
    public void visitConstType(ConstType type)
    {
        htmlModuleNameSuffix = "CONSTTYPE";
    }

    @Override
    public void visitEnumType(EnumType type)
    {
        htmlModuleNameSuffix = "ENUM";
    }

    @Override
    public void visitFloatType(FloatType type)
    {
        htmlModuleNameSuffix = "FLOAT";
    }

    @Override
    public void visitFunctionType(FunctionType type)
    {
        htmlModuleNameSuffix = "FUNCTION";
    }

    @Override
    public void visitServiceType(ServiceType type)
    {
        htmlModuleNameSuffix = "SERVICE";
    }

    @Override
    public void visitBitFieldType(BitFieldType type)
    {
        if (type.isSigned())
            htmlModuleNameSuffix = "SIGNED_BIT_FIELD";
        else
            htmlModuleNameSuffix = "BIT";
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
        htmlModuleNameSuffix = "TYPE_INSTANTIATION";
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
