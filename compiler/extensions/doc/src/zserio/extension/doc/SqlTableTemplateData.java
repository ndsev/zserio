package zserio.extension.doc;

import zserio.ast.Expression;
import zserio.ast.SqlConstraint;
import zserio.ast.SqlTableType;
import zserio.extension.common.ZserioExtensionException;

public class SqlTableTemplateData extends CompoundTypeTemplateData
{
    public SqlTableTemplateData(PackageTemplateDataContext context, SqlTableType sqlTableType)
            throws ZserioExtensionException
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
