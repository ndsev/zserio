package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.Field;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;
import zserio.extension.java.types.NativeBooleanType;

/**
 * FreeMarker template data for SQL table rows.
 */
public final class SqlTableRowEmitterTemplateData extends JavaTemplateData
{
    public SqlTableRowEmitterTemplateData(TemplateDataContext context, SqlTableType tableType,
            String tableRowName) throws ZserioExtensionException
    {
        super(context);

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final JavaNativeType javaType = javaNativeMapper.getJavaType(tableType);
        packageName = JavaFullNameFormatter.getFullName(javaType.getPackageName());

        name = tableRowName;

        for (Field field: tableType.getFields())
        {
            final FieldTemplateData fieldData = new FieldTemplateData(javaNativeMapper, field);
            fields.add(fieldData);
        }
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getName()
    {
        return name;
    }

    public Iterable<FieldTemplateData> getFields()
    {
        return fields;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(JavaNativeMapper javaNativeMapper, Field field)
                throws ZserioExtensionException
        {
            final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
            name = field.getName();
            final JavaNativeType nativeType = javaNativeMapper.getJavaType(fieldTypeInstantiation);
            javaTypeFullName = nativeType.getFullName();
            javaNullableTypeFullName = javaNativeMapper.getNullableJavaType(fieldTypeInstantiation).getFullName();
            isBool = nativeType instanceof NativeBooleanType;
        }

        public String getName()
        {
            return name;
        }

        public String getJavaTypeFullName()
        {
            return javaTypeFullName;
        }

        public String getJavaNullableTypeFullName()
        {
            return javaNullableTypeFullName;
        }

        public boolean getIsBool()
        {
            return isBool;
        }

        private final String    name;
        private final String    javaTypeFullName;
        private final String    javaNullableTypeFullName;
        private final boolean   isBool;
    }

    private final String                    packageName;
    private final String                    name;
    private final List<FieldTemplateData>   fields = new ArrayList<FieldTemplateData>();
}
