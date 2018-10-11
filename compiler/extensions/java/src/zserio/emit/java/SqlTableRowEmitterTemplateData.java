package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ZserioType;
import zserio.ast.Field;
import zserio.ast.SqlTableType;
import zserio.ast.TypeReference;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.java.types.JavaNativeType;
import zserio.emit.java.types.NativeBooleanType;

public final class SqlTableRowEmitterTemplateData extends JavaTemplateData
{
    public SqlTableRowEmitterTemplateData(TemplateDataContext context, SqlTableType tableType,
            String tableRowName) throws ZserioEmitException
    {
        super(context);

        final JavaNativeTypeMapper javaNativeTypeMapper = context.getJavaNativeTypeMapper();
        final JavaNativeType javaType = javaNativeTypeMapper.getJavaType(tableType);
        packageName = javaType.getPackageName();

        name = tableRowName;

        for (Field field: tableType.getFields())
        {
            final FieldTemplateData fieldData = new FieldTemplateData(javaNativeTypeMapper, field);
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
        public FieldTemplateData(JavaNativeTypeMapper javaNativeTypeMapper, Field field)
                throws ZserioEmitException
        {
            final ZserioType baseType = TypeReference.resolveBaseType(field.getFieldType());
            name = field.getName();
            final JavaNativeType nativeType = javaNativeTypeMapper.getJavaType(baseType);
            javaTypeName = nativeType.getFullName();
            javaNullableTypeName = javaNativeTypeMapper.getNullableJavaType(baseType).getFullName();
            isBool = nativeType instanceof NativeBooleanType;
        }

        public String getName()
        {
            return name;
        }

        public String getJavaTypeName()
        {
            return javaTypeName;
        }

        public String getJavaNullableTypeName()
        {
            return javaNullableTypeName;
        }

        public boolean getIsBool()
        {
            return isBool;
        }

        private final String    name;
        private final String    javaTypeName;
        private final String    javaNullableTypeName;
        private final boolean   isBool;
    }

    private final String                    packageName;
    private final String                    name;
    private final List<FieldTemplateData>   fields = new ArrayList<FieldTemplateData>();
}
