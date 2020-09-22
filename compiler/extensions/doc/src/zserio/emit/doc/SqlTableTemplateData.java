package zserio.emit.doc;

import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;

public class SqlTableTemplateData extends CompoundTypeTemplateData
{
    public SqlTableTemplateData(TemplateDataContext context, SqlTableType sqlTableType)
            throws ZserioEmitException
    {
        super(context, sqlTableType);

        sqlConstraint = context.getDocExpressionFormatter().formatExpression(
                sqlTableType.getSqlConstraint() != null ?
                        sqlTableType.getSqlConstraint().getConstraintExpr() : null);

        virtualTableUsing = sqlTableType.getVirtualTableUsingString();
    }

    public String getSqlConstraint()
    {
        return sqlConstraint;
    }

    public String getVirtualTableUsing()
    {
        return virtualTableUsing;
    }

    private final String sqlConstraint;
    private final String virtualTableUsing;
}
