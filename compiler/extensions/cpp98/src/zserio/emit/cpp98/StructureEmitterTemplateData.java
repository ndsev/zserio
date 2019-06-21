package zserio.emit.cpp98;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.StructureType;
import zserio.emit.common.ZserioEmitException;

public class StructureEmitterTemplateData extends CompoundTypeTemplateData
{
    public StructureEmitterTemplateData(TemplateDataContext context, StructureType structureType)
            throws ZserioEmitException
    {
        super(context, structureType);

        nonOptionalSimpleFieldList = new ArrayList<CompoundFieldTemplateData>();
        for (CompoundFieldTemplateData fieldTemplateData : getFieldList())
        {
            if (fieldTemplateData.getOptional() == null && fieldTemplateData.getIsSimpleType())
                nonOptionalSimpleFieldList.add(fieldTemplateData);
        }
    }

    public Iterable<CompoundFieldTemplateData> getNonOptionalSimpleFieldList()
    {
        return nonOptionalSimpleFieldList;
    }

    private final List<CompoundFieldTemplateData>   nonOptionalSimpleFieldList;
}
