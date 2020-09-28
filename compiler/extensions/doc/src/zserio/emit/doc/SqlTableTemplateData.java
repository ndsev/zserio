package zserio.emit.doc;

import zserio.ast.Expression;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.emit.common.ZserioEmitException;

public class SqlTableTemplateData extends CompoundTypeTemplateData
{
    public SqlTableTemplateData(TemplateDataContext context, SqlTableType sqlTableType)
            throws ZserioEmitException
    {
        super(context, sqlTableType);

        final SqlConstraint sqlConstraintType = sqlTableType.getSqlConstraint();
        final Expression sqlConstraintExpr = (sqlConstraintType == null) ? null :
            sqlConstraintType.getConstraintExpr();
        sqlConstraint = (sqlConstraintExpr == null) ? "" :
            context.getExpressionFormatter().formatGetter(sqlConstraintExpr);
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
