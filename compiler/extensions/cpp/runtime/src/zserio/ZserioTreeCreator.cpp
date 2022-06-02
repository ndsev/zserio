#include "zserio/ZserioTreeCreator.h"

namespace zserio
{

ZserioTreeCreator::ZserioTreeCreator(const ITypeInfo& typeInfo) :
        m_typeInfo(typeInfo)
{}

IReflectablePtr ZserioTreeCreator::get() const
{
    return m_valueStack.empty() ? nullptr : m_valueStack.back();
}

void ZserioTreeCreator::beginObject()
{
    m_valueStack.push_back(getTypeInfo().newInstance());
}

void ZserioTreeCreator::endObject()
{

}

void ZserioTreeCreator::beginItem(const std::string& key)
{
    m_fieldInfoStack.push_back(findFieldInfo(getTypeInfo(), key));
}

void ZserioTreeCreator::endItem(const std::string&)
{
    const FieldInfo& fieldInfo = m_fieldInfoStack.back();
    m_fieldInfoStack.pop_back();

    IReflectablePtr value = m_valueStack.back();
    m_valueStack.pop_back();

    IReflectablePtr parent = m_valueStack.back();
    // TODO[Mi-L@]: setField needs AnyHolder!
    //parent->setField(fieldInfo.schemaName, value);
}

const ITypeInfo& ZserioTreeCreator::getTypeInfo() const
{
    return m_fieldInfoStack.empty() ? m_typeInfo : m_fieldInfoStack.back().get().typeInfo;
}

const FieldInfo& ZserioTreeCreator::findFieldInfo(const ITypeInfo& typeInfo, const std::string& fieldName) const
{
    Span<const FieldInfo> fields = typeInfo.getFields();
    for (const FieldInfo& field : fields)
    {
        if (field.schemaName == zserio::StringView(fieldName))
            return field;
    }
    throw CppRuntimeException("Field '") + fieldName + "' not found in '" + typeInfo.getSchemaName() + "!";
}

} // namespace zserio
