package zserio.runtime.typeinfo;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Type information for SQL table column.
 */
public class ColumnInfo
{
    /**
     * Constructor.
     *
     * @param schemaName Column schema name.
     * @param typeInfo Column type info.
     * @param typeArguments Column type arguments.
     * @param sqlTypeName Column SQL type name.
     * @param sqlConstraint Column SQL constraint.
     * @param isVirtual Flag whether the column is virtual.
     */
    public ColumnInfo(String schemaName, TypeInfo typeInfo, List<Function<Object, Object>> typeArguments,
            String sqlTypeName, String sqlConstraint, boolean isVirtual)
    {
        this.schemaName = schemaName;
        this.typeInfo = typeInfo;
        this.typeArguments = typeArguments;
        this.sqlTypeName = sqlTypeName;
        this.sqlConstraint = sqlConstraint;
        this.isVirtual = isVirtual;
    }

    /**
     * Name of the column as is defined in zserio schema.
     *
     * @return Column schema name.
     */
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Gets type information for the column type.
     *
     * @return Column type info.
     */
    public TypeInfo getTypeInfo()
    {
        return typeInfo;
    }

    /**
     * Gets sequence of column type arguments.
     *
     * @return Unmodifiable list of type arguments.
     */
    public List<Function<Object, Object>> getTypeArguments()
    {
        return Collections.unmodifiableList(typeArguments);
    }

    /**
     * Gets SQL type name of the column.
     *
     * @return Column SQL type name.
     */
    public String getSqlTypeName()
    {
        return sqlTypeName;
    }

    /**
     * Gets column SQL constraint expression.
     *
     * @return Column SQL constraint or empty if column does not have any constraint.
     */
    public String getSqlConstraint()
    {
        return sqlConstraint;
    }

    /**
     * Gets whether the column is a virtual column.
     *
     * @return True if SQL table is virtual, false otherwise.
     */
    public boolean isVirtual()
    {
        return isVirtual;
    }

    private final String schemaName;
    private final TypeInfo typeInfo;
    private final List<Function<Object, Object>> typeArguments;
    private final String sqlTypeName;
    private final String sqlConstraint;
    private final boolean isVirtual;
}
