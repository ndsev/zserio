package zserio.extension.cpp;

import java.util.List;

import zserio.ast.CompoundType;

/**
 * FreeMarker template data for compound constructors.
 */
public final class CompoundConstructorTemplateData
{
    public CompoundConstructorTemplateData(CompoundType compoundType,
            CompoundParameterTemplateData compoundParametersData, List<CompoundFieldTemplateData> fieldList)
    {
        compoundName = compoundType.getName();
        this.compoundParametersData = compoundParametersData;
        this.fieldList = fieldList;
    }

    public String getCompoundName()
    {
        return compoundName;
    }

    public CompoundParameterTemplateData getCompoundParametersData()
    {
        return compoundParametersData;
    }

    public Iterable<CompoundFieldTemplateData> getFieldList()
    {
        return fieldList;
    }

    private final String compoundName;
    private final CompoundParameterTemplateData compoundParametersData;
    private final List<CompoundFieldTemplateData> fieldList;
}
