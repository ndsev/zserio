package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.DocComment;
import zserio.ast.Field;
import zserio.ast.SqlTableType;
import zserio.ast.TypeInstantiation;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

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
            final JavaNativeType nullableNativeType = javaNativeMapper.getNullableJavaType(fieldTypeInstantiation);
            nullableTypeInfo = new NativeTypeInfoTemplateData(nullableNativeType, fieldTypeInstantiation);
            typeInfo = new NativeTypeInfoTemplateData(nativeType, fieldTypeInstantiation);

            final List<DocComment> fieldDocComments = field.getDocComments();
            docComments = fieldDocComments.isEmpty() ? null : new DocCommentsTemplateData(fieldDocComments);
        }

        public String getName()
        {
            return name;
        }

        public NativeTypeInfoTemplateData getNullableTypeInfo()
        {
            return nullableTypeInfo;
        }

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
        private final NativeTypeInfoTemplateData nullableTypeInfo;
        private final NativeTypeInfoTemplateData typeInfo;
        private final DocCommentsTemplateData docComments;
    }

    private final String packageName;
    private final String name;
    private final List<FieldTemplateData>fields = new ArrayList<FieldTemplateData>();
}
