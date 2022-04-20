#include <algorithm>

#include "zserio/TypeInfoUtil.h"
#include "zserio/Walker.h"
#include "zserio/StringConvertUtil.h"

namespace zserio
{

Walker::Walker(const IWalkObserverPtr& walkObserver, const IWalkFilterPtr& walkFilter) :
        m_walkObserver(walkObserver), m_walkFilter(walkFilter)
{}

void Walker::walk(const IReflectablePtr& compound)
{
    const ITypeInfo& typeInfo = compound->getTypeInfo();
    if (!TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        throw CppRuntimeException("Walker: Root object '") + typeInfo.getSchemaName() +
                "' is not a compound type!";
    }

    m_walkObserver->beginRoot(compound);
    walkFields(compound, typeInfo);
    m_walkObserver->endRoot(compound);
}

void Walker::walkFields(const IReflectablePtr& compound, const ITypeInfo& typeInfo)
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

bool Walker::walkField(const IReflectablePtr& reflectable, const FieldInfo& fieldInfo)
{
    if (reflectable && reflectable->isArray())
    {
        if (m_walkFilter->beforeArray(reflectable, fieldInfo))
        {
            m_walkObserver->beginArray(reflectable, fieldInfo);
            for (size_t i = 0; i < reflectable->size(); ++i)
            {
                if (!walkFieldValue(reflectable->at(i), fieldInfo, i))
                    break;
            }
            m_walkObserver->endArray(reflectable, fieldInfo);

        }
        return m_walkFilter->afterArray(reflectable, fieldInfo);
    }
    else
    {
        return walkFieldValue(reflectable, fieldInfo);
    }
}

bool Walker::walkFieldValue(const IReflectablePtr& reflectable, const FieldInfo& fieldInfo, size_t elementIndex)
{
    const ITypeInfo& typeInfo = fieldInfo.typeInfo;
    if (reflectable && TypeInfoUtil::isCompound(typeInfo.getSchemaType()))
    {
        if (m_walkFilter->beforeCompound(reflectable, fieldInfo, elementIndex))
        {
            m_walkObserver->beginCompound(reflectable, fieldInfo, elementIndex);
            walkFields(reflectable, typeInfo);
            m_walkObserver->endCompound(reflectable, fieldInfo, elementIndex);
        }
        return m_walkFilter->afterCompound(reflectable, fieldInfo, elementIndex);
    }
    else
    {
        if (m_walkFilter->beforeValue(reflectable, fieldInfo, elementIndex))
            m_walkObserver->visitValue(reflectable, fieldInfo, elementIndex);
        return m_walkFilter->afterValue(reflectable, fieldInfo, elementIndex);
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

namespace
{

std::string getCurrentPathImpl(const std::vector<std::string>& currentPath)
{
    std::string currentPathStr;
    for (auto it = currentPath.begin(); it != currentPath.end(); ++it)
        {
            if (!currentPathStr.empty())
                currentPathStr += ".";
            currentPathStr += *it;
        }
        return currentPathStr;
}

void appendPathImpl(std::vector<std::string>& currentPath, const FieldInfo& fieldInfo, size_t elementIndex)
{
    if (elementIndex == WALKER_NOT_ELEMENT)
        currentPath.emplace_back(fieldInfo.schemaName.data(), fieldInfo.schemaName.size());
    else
        currentPath.back() = toString(fieldInfo.schemaName) + "[" + toString(elementIndex) + "]";
}

void popPathImpl(std::vector<std::string>& currentPath, const FieldInfo& fieldInfo, size_t elementIndex)
{
    if (elementIndex == WALKER_NOT_ELEMENT)
        currentPath.pop_back();
    else
        currentPath.back() = toString(fieldInfo.schemaName);
}

class SubtreeRegexWalkFilter : public IWalkFilter
{
public:
    SubtreeRegexWalkFilter(const std::vector<std::string> currentPath, const std::regex& pathRegex) :
            m_currentPath(currentPath), m_pathRegex(pathRegex)
    {}

    bool matches()
    {
        return m_matches;
    }

    virtual bool beforeArray(const IReflectablePtr&, const FieldInfo& fieldInfo) override
    {
        m_currentPath.emplace_back(fieldInfo.schemaName.data(), fieldInfo.schemaName.size());
        m_matches = std::regex_match(getCurrentPath(), m_pathRegex);

        // terminate when the match is already found (note that array is never null here)
        return !m_matches;
    }

    virtual bool afterArray(const IReflectablePtr&, const FieldInfo&) override
    {
        m_currentPath.pop_back();
        return !m_matches; // terminate when the match is already found
    }

    virtual bool beforeCompound(const IReflectablePtr&, const FieldInfo& fieldInfo,
            size_t elementIndex) override
    {
        appendPath(fieldInfo, elementIndex);
        m_matches = std::regex_match(getCurrentPath(), m_pathRegex);

        // terminate when the match is already found (note that compound is never null here)
        return !m_matches;
    }

    virtual bool afterCompound(const IReflectablePtr&, const FieldInfo& fieldInfo,
            size_t elementIndex) override
    {
        popPath(fieldInfo, elementIndex);
        return !m_matches; // terminate when the match is already found
    }

    virtual bool beforeValue(const IReflectablePtr&, const FieldInfo& fieldInfo,
            size_t elementIndex) override
    {
        appendPath(fieldInfo, elementIndex);
        m_matches = std::regex_match(getCurrentPath(), m_pathRegex);

        return !m_matches; // terminate when the match is already found
    }

    virtual bool afterValue(const IReflectablePtr&, const FieldInfo& fieldInfo,
            size_t elementIndex) override
    {
        popPath(fieldInfo, elementIndex);
        return !m_matches; // terminate when the match is already found
    }

private:
    std::string getCurrentPath() const
    {
        return getCurrentPathImpl(m_currentPath);
    }

    void appendPath(const FieldInfo& fieldInfo, size_t elementIndex)
    {
        appendPathImpl(m_currentPath, fieldInfo, elementIndex);
    }

    void popPath(const FieldInfo& fieldInfo, size_t elementIndex)
    {
        popPathImpl(m_currentPath, fieldInfo, elementIndex);
    }

    std::vector<std::string> m_currentPath;
    std::regex m_pathRegex;
    bool m_matches = false;
};

} // namespace

RegexWalkFilter::RegexWalkFilter(const char* pathRegex) :
        m_pathRegex(pathRegex)
{}

bool RegexWalkFilter::beforeArray(const IReflectablePtr& array, const FieldInfo& fieldInfo)
{
    m_currentPath.emplace_back(fieldInfo.schemaName.data(), fieldInfo.schemaName.size());

    if (std::regex_match(getCurrentPath(), m_pathRegex))
        return true; // the array itself matches

    for (size_t i = 0; i < array->size(); ++i)
    {
        m_currentPath.back() = toString(fieldInfo.schemaName) + "[" + toString(i) + "]";

        if (matchSubtree(array->at(i), fieldInfo))
            return true;

        m_currentPath.back() = toString(fieldInfo.schemaName);
    }

    return false;
}

bool RegexWalkFilter::afterArray(const IReflectablePtr&, const FieldInfo&)
{
    m_currentPath.pop_back();
    return true;
}

bool RegexWalkFilter::beforeCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    appendPath(fieldInfo, elementIndex);
    if (std::regex_match(getCurrentPath(), m_pathRegex))
        return true; // the compound itself matches

    return matchSubtree(compound, fieldInfo);
}

bool RegexWalkFilter::afterCompound(const IReflectablePtr&, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    popPath(fieldInfo, elementIndex);
    return true;
}

bool RegexWalkFilter::beforeValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    appendPath(fieldInfo, elementIndex);
    return matchSubtree(value, fieldInfo);
}

bool RegexWalkFilter::afterValue(const IReflectablePtr&, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    popPath(fieldInfo, elementIndex);
    return true;
}

void RegexWalkFilter::appendPath(const FieldInfo& fieldInfo, size_t elementIndex)
{
    appendPathImpl(m_currentPath, fieldInfo, elementIndex);
}

void RegexWalkFilter::popPath(const FieldInfo& fieldInfo, size_t elementIndex)
{
    popPathImpl(m_currentPath, fieldInfo, elementIndex);
}

std::string RegexWalkFilter::getCurrentPath() const
{
    return getCurrentPathImpl(m_currentPath);
}

bool RegexWalkFilter::matchSubtree(const IReflectablePtr& value, const FieldInfo& fieldInfo) const
{
    if (value != nullptr && TypeInfoUtil::isCompound(fieldInfo.typeInfo.getSchemaType()))
    {
        // is a not null compound, try to find match within its subtree
        auto subtreeFilter = std::make_shared<SubtreeRegexWalkFilter>(m_currentPath, m_pathRegex);
        DefaultWalkObserver defaultObserver;
        Walker walker(std::make_shared<DefaultWalkObserver>(), subtreeFilter);
        walker.walk(value);
        return subtreeFilter->matches();
    }
    else
    {
        // try to match a simple value or null compound
        return std::regex_match(getCurrentPath(), m_pathRegex);
    }
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

AndWalkFilter::AndWalkFilter(const std::vector<IWalkFilterPtr>& walkFilters) :
        m_walkFilters(walkFilters)
{}

bool AndWalkFilter::beforeArray(const IReflectablePtr& array, const FieldInfo& fieldInfo)
{
    return applyFilters(&IWalkFilter::beforeArray, array, fieldInfo);
}

bool AndWalkFilter::afterArray(const IReflectablePtr& array, const FieldInfo& fieldInfo)
{
    return applyFilters(&IWalkFilter::afterArray, array, fieldInfo);
}

bool AndWalkFilter::beforeCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    return applyFilters(&IWalkFilter::beforeCompound, compound, fieldInfo, elementIndex);
}

bool AndWalkFilter::afterCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    return applyFilters(&IWalkFilter::afterCompound, compound, fieldInfo, elementIndex);
}

bool AndWalkFilter::beforeValue(const IReflectablePtr& value, const FieldInfo& fieldInfo, size_t elementIndex)
{
    return applyFilters(&IWalkFilter::beforeValue, value, fieldInfo, elementIndex);
}

bool AndWalkFilter::afterValue(const IReflectablePtr& value, const FieldInfo& fieldInfo, size_t elementIndex)
{
    return applyFilters(&IWalkFilter::afterValue, value, fieldInfo, elementIndex);
}

} // namespace zserio
