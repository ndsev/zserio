package zserio.extension.java;

import zserio.ast.CompoundType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for SubtypeEmitter.
 */
public final class SubtypeEmitterTemplateData extends UserTypeTemplateData
{
    public SubtypeEmitterTemplateData(TemplateDataContext context, Subtype subtype)
            throws ZserioExtensionException
    {
        super(context, subtype, subtype);

        final ZserioType referencedType = subtype.getTypeReference().getType();
        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final JavaNativeType referencedNativeType = javaNativeMapper.getJavaType(referencedType);
        referencedTypeFullName = referencedNativeType.getFullName();

        final ZserioType referencedBaseType = subtype.getBaseTypeReference().getType();
        isReferencedTypeSqlTable = (referencedBaseType instanceof SqlTableType);
        isReferencedTypeStructure = (referencedBaseType instanceof StructureType);
        referencedCompoundType = (referencedBaseType instanceof CompoundType)
                ? new CompoundTypeTemplateData(context, (CompoundType)referencedBaseType)
                : null;
    }

    public String getReferencedTypeFullName()
    {
        return referencedTypeFullName;
    }

    public boolean getIsReferencedTypeSqlTable()
    {
        return isReferencedTypeSqlTable;
    }

    public boolean getIsReferencedTypeStructure()
    {
        return isReferencedTypeStructure;
    }

    public CompoundTypeTemplateData getReferencedCompoundType()
    {
        return referencedCompoundType;
    }

    private final String referencedTypeFullName;
    private final boolean isReferencedTypeSqlTable;
    private final boolean isReferencedTypeStructure;
    private final CompoundTypeTemplateData referencedCompoundType;
}
