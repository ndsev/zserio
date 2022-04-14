#include <algorithm>

#include "zserio/TypeInfoUtil.h"
#include "zserio/Walker.h"

namespace zserio
{

Walker::Walker(IWalkObserver& walkObserver, IWalkFilter& walkFilter) :
        m_walkObserver(walkObserver), m_walkFilter(walkFilter)
{}

void Walker::walk(const IReflectablePtr& compound)
{
    // TODO[Mi-L@]: Add various checks!

    m_walkObserver.beginRoot(compound);
    walkFields(compound);
    m_walkObserver.endRoot(compound);
}

void Walker::walkFields(const IReflectablePtr& compound)
{
    const ITypeInfo& typeInfo = compound->getTypeInfo();
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        if (TypeInfoUtil::hasChoice(typeInfo.getSchemaType()))
        {
            StringView compoundChoice = compound->getChoice();
            if (!compoundChoice.empty())
            {
                Span<const FieldInfo> fields = typeInfo.getFields();
                auto fieldsIt = std::find_if(fields.begin(), fields.end(),
                        [compoundChoice](const FieldInfo& fieldInfo) {
                                return fieldInfo.schemaName == compoundChoice; });
                if (fieldsIt != fields.end())
                {
                    walkField(compound->getField(compoundChoice), *fieldsIt);
                }
            }
            // else uninitialized or empty branch
        }
        else
        {
            for (const FieldInfo& field : typeInfo.getFields())
            {
                if (!walkField(compound->getField(field.schemaName), field))
                    break;
            }
        }
    }
}

bool Walker::walkField(const IReflectablePtr& reflectable, const FieldInfo& fieldInfo)
{
    if (reflectable->isArray())
    {
        if (m_walkFilter.beforeArray(reflectable, fieldInfo))
        {
            m_walkObserver.beginArray(reflectable, fieldInfo);
            for (size_t i = 0; i < reflectable->size(); ++i)
            {
                if (!walkFieldValue(reflectable->at(i), fieldInfo, i))
                    break;
            }
            m_walkObserver.endArray(reflectable, fieldInfo);

        }
        return m_walkFilter.afterArray(reflectable, fieldInfo);
    }
    else
    {
        return walkFieldValue(reflectable, fieldInfo);
    }
}

bool Walker::walkFieldValue(const IReflectablePtr& reflectable, const FieldInfo& fieldInfo, size_t elementIndex)
{
    const ITypeInfo& typeInfo = fieldInfo.typeInfo;
    if (TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        if (m_walkFilter.beforeCompound(reflectable, fieldInfo, elementIndex))
        {
            m_walkObserver.beginCompound(reflectable, fieldInfo, elementIndex);
            walkFields(reflectable);
            m_walkObserver.endCompound(reflectable, fieldInfo, elementIndex);
        }
        return m_walkFilter.afterCompound(reflectable, fieldInfo, elementIndex);
    }
    else
    {
        if (m_walkFilter.beforeValue(reflectable, fieldInfo, elementIndex))
            m_walkObserver.visitValue(reflectable, fieldInfo, elementIndex);
        return m_walkFilter.afterValue(reflectable, fieldInfo, elementIndex);
    }
}

DepthWalkFilter::DepthWalkFilter(size_t maxDepth) :
        m_maxDepth(maxDepth), m_depth(1)
{}

bool DepthWalkFilter::beforeArray(const IReflectablePtr&, const FieldInfo&)
{
    const bool enter = m_depth <= m_maxDepth;
    m_depth += 1;
    return enter;
}

bool DepthWalkFilter::afterArray(const IReflectablePtr&, const FieldInfo&)
{
    m_depth -= 1;
    return true;
}

bool DepthWalkFilter::beforeCompound(const IReflectablePtr&, const FieldInfo&, size_t)
{
    const bool enter = m_depth <= m_maxDepth;
    m_depth += 1;
    return enter;
}

bool DepthWalkFilter::afterCompound(const IReflectablePtr&, const FieldInfo&, size_t)
{
    m_depth -= 1;
    return true;
}

bool DepthWalkFilter::beforeValue(const IReflectablePtr&, const FieldInfo&, size_t)
{
    return m_depth <= m_maxDepth;
}

bool DepthWalkFilter::afterValue(const IReflectablePtr&, const FieldInfo&, size_t)
{
    return true;
}

ArrayLengthWalkFilter::ArrayLengthWalkFilter(size_t maxArrayLength) :
        m_maxArrayLength(maxArrayLength)
{}

bool ArrayLengthWalkFilter::beforeArray(const IReflectablePtr&, const FieldInfo&)
{
    return true;
}

bool ArrayLengthWalkFilter::afterArray(const IReflectablePtr&, const FieldInfo&)
{
    return true;
}

bool ArrayLengthWalkFilter::beforeCompound(const IReflectablePtr&, const FieldInfo&, size_t elementIndex)
{
    return filterArrayElement(elementIndex);
}

bool ArrayLengthWalkFilter::afterCompound(const IReflectablePtr&, const FieldInfo&, size_t elementIndex)
{
    return filterArrayElement(elementIndex);
}

bool ArrayLengthWalkFilter::beforeValue(const IReflectablePtr&, const FieldInfo&, size_t elementIndex)
{
    return filterArrayElement(elementIndex);
}

bool ArrayLengthWalkFilter::afterValue(const IReflectablePtr&, const FieldInfo&, size_t elementIndex)
{
    return filterArrayElement(elementIndex);
}

bool ArrayLengthWalkFilter::filterArrayElement(size_t elementIndex)
{
    return elementIndex == WALKER_NOT_ELEMENT ? true : elementIndex < m_maxArrayLength;
}

} // namespace
