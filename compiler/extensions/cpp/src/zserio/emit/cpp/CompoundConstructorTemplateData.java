package zserio.emit.cpp;

import java.util.List;

import zserio.ast.CompoundType;

public class CompoundConstructorTemplateData
{
    public CompoundConstructorTemplateData(CppNativeTypeMapper cppNativeTypeMapper, CompoundType compoundType,
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

    private final String                            compoundName;
    private final CompoundParameterTemplateData     compoundParametersData;
    private final List<CompoundFieldTemplateData>   fieldList;
}
