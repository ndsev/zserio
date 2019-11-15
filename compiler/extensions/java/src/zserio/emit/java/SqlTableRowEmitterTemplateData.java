package zserio.emit.java;

import java.util.ArrayList;
import java.util.List;

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
                throws ZserioEmitException
        {
            final TypeReference fieldTypeReference = field.getTypeInstantiation().getTypeReference();
            name = field.getName();
            final JavaNativeType nativeType = javaNativeMapper.getJavaType(fieldTypeReference);
            javaTypeName = nativeType.getFullName();
            javaNullableTypeName = javaNativeMapper.getNullableJavaType(fieldTypeReference).getFullName();
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
