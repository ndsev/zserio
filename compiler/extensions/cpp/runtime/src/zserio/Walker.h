#ifndef ZSERIO_WALKER_H_INC
#define ZSERIO_WALKER_H_INC

#include <functional>
#include <regex>
#include <algorithm>

#include "zserio/IReflectable.h"
#include "zserio/ITypeInfo.h"
#include "zserio/WalkerConst.h"
#include "zserio/IWalkObserver.h"
#include "zserio/IWalkFilter.h"
#include "zserio/TypeInfoUtil.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/String.h"
#include "zserio/Vector.h"

namespace zserio
{

/**
 * Walker through zserio objects, based on generated type info (see -withTypeInfoCode) and
 * reflectable interface (see -withReflectionCode).
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicWalker
{
public:
    /**
     * Constructor.
     *
     * \param walkObserver Observer to use during walking.
     * \param walkFilter Walk filter to use.
     */
    explicit BasicWalker(const IBasicWalkObserverPtr<ALLOC>& walkObserver,
            const IBasicWalkFilterPtr<ALLOC>& walkFilter = nullptr, const ALLOC& allocator = ALLOC());

    /**
     * Walks given relfectable zserio compound object.
     *
     * \param reflectable Zserio compound object to walk.
     */
    void walk(const IBasicReflectablePtr<ALLOC>& reflectable);

private:
    void walkFields(const IBasicReflectablePtr<ALLOC>& compound, const ITypeInfo& typeInfo);
    bool walkField(const IBasicReflectablePtr<ALLOC>& reflectable, const FieldInfo& fieldInfo);
    bool walkFieldValue(const IBasicReflectablePtr<ALLOC>& reflectable, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT);

    IBasicWalkObserverPtr<ALLOC> m_walkObserver;
    IBasicWalkFilterPtr<ALLOC> m_walkFilter;
};

/**
 * Default walk observer which just does nothing.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicDefaultWalkObserver : public IBasicWalkObserver<ALLOC>
{
public:
    virtual void beginRoot(const IBasicReflectablePtr<ALLOC>&) override {}
    virtual void endRoot(const IBasicReflectablePtr<ALLOC>&) override {}

    virtual void beginArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&) override {}
    virtual void endArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&) override {}

    virtual void beginCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t) override {}
    virtual void endCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t) override {}

    virtual void visitValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t) override {}
};

/**
 * Default walk filter which filters nothing.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicDefaultWalkFilter : public IBasicWalkFilter<ALLOC>
{
public:
    virtual bool beforeArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&) override
    {
        return true;
    }

    virtual bool afterArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&) override
    {
        return true;
    }

    virtual bool beforeCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t) override
    {
        return true;
    }

    virtual bool afterCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t) override
    {
        return true;
    }

    virtual bool beforeValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t) override
    {
        return true;
    }

    virtual bool afterValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t) override
    {
        return true;
    }
};

/**
 * Walk filter which allows to walk only to the given maximum depth.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicDepthWalkFilter : public IBasicWalkFilter<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param maxDepth Maximum depth to walk to.
     */
    explicit BasicDepthWalkFilter(size_t maxDepth);

    virtual bool beforeArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;
    virtual bool afterArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;

    virtual bool beforeCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual bool beforeValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    size_t m_maxDepth;
    size_t m_depth;
};

/**
 * Walk filter which allows to walk only paths matching the given regex.
 *
 * The path is constructed from field names within the root object, thus the root object itself
 * is not part of the path.
 *
 * Array elements have the index appended to the path so that e.g. "compound.arrayField[0]" will match
 * only the first element in the array "arrayField".
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicRegexWalkFilter : public IBasicWalkFilter<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param pathRegex Path regex to use for filtering.
     */
    explicit BasicRegexWalkFilter(const char* pathRegex, const ALLOC& allocator = ALLOC());

    virtual bool beforeArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;
    virtual bool afterArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;

    virtual bool beforeCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual bool beforeValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    void appendPath(const FieldInfo& fieldInfo, size_t elementIndex);
    void popPath(const FieldInfo& fieldInfo, size_t elementIndex);
    string<ALLOC> getCurrentPath() const;
    bool matchSubtree(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo) const;

    vector<string<ALLOC>, ALLOC> m_currentPath;
    std::regex m_pathRegex;
    ALLOC m_allocator; // TODO[Mi-L@]: Check how std::regex_match allocates when resutls are omitted!
};

/**
 * Walk filter which allows to walk only to the given maximum array length.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicArrayLengthWalkFilter : public IBasicWalkFilter<ALLOC>
{
public:
    /**
     * Constructor.
     *
     * \param maxArrayLength Maximum array length to walk to.
     */
    explicit BasicArrayLengthWalkFilter(size_t maxArrayLength);

    virtual bool beforeArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;
    virtual bool afterArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;

    virtual bool beforeCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual bool beforeValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    bool filterArrayElement(size_t elementIndex);

    size_t m_maxArrayLength;
};

/**
 * Walk filter which implements composition of particular filters.
 *
 * The filters are called sequentially and logical and is applied on theirs results.
 * Note that all filters are always called.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class BasicAndWalkFilter : public IBasicWalkFilter<ALLOC>
{
public:
    using WalkFilters = vector<IBasicWalkFilterPtr<ALLOC>, ALLOC>;

    /**
     * Constructor.
     *
     * \param walkFilters List of filters to use in composition.
     */
    explicit BasicAndWalkFilter(const WalkFilters& walkFilters);

    virtual bool beforeArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;
    virtual bool afterArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo) override;

    virtual bool beforeCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterCompound(const IBasicReflectablePtr<ALLOC>& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual bool beforeValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    template <typename FilterFunc, typename ...Args>
    bool applyFilters(FilterFunc filterFunc, Args... args)
    {
        bool result = true;
        for (const IWalkFilterPtr& walkFilter : m_walkFilters)
            result &= ((*walkFilter).*filterFunc)(args...);
        return result;
    }

    WalkFilters m_walkFilters;
};

/** Typedefs to walker related classes provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using Walker = BasicWalker<>;
using DefaultWalkObserver = BasicDefaultWalkObserver<>;
using DefaultWalkFilter = BasicDefaultWalkFilter<>;
using DepthWalkFilter = BasicDepthWalkFilter<>;
using RegexWalkFilter = BasicRegexWalkFilter<>;
using ArrayLengthWalkFilter = BasicArrayLengthWalkFilter<>;
using AndWalkFilter = BasicAndWalkFilter<>;
/** \} */

template <typename ALLOC>
BasicWalker<ALLOC>::BasicWalker(const IBasicWalkObserverPtr<ALLOC>& walkObserver,
        const IBasicWalkFilterPtr<ALLOC>& walkFilter, const ALLOC& allocator) :
        m_walkObserver(walkObserver),
        m_walkFilter(walkFilter ? walkFilter : std::allocate_shared<BasicDefaultWalkFilter<ALLOC>>(allocator))
{}

template <typename ALLOC>
void BasicWalker<ALLOC>::walk(const IBasicReflectablePtr<ALLOC>& compound)
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

template <typename ALLOC>
void BasicWalker<ALLOC>::walkFields(const IBasicReflectablePtr<ALLOC>& compound, const ITypeInfo& typeInfo)
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
        for (const FieldInfo& fieldInfo : typeInfo.getFields())
        {
            if (!walkField(compound->getField(fieldInfo.schemaName), fieldInfo))
                break;
        }
    }
}

template <typename ALLOC>
bool BasicWalker<ALLOC>::walkField(const IBasicReflectablePtr<ALLOC>& reflectable, const FieldInfo& fieldInfo)
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

template <typename ALLOC>
bool BasicWalker<ALLOC>::walkFieldValue(const IBasicReflectablePtr<ALLOC>& reflectable,
        const FieldInfo& fieldInfo, size_t elementIndex)
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

template <typename ALLOC>
BasicDepthWalkFilter<ALLOC>::BasicDepthWalkFilter(size_t maxDepth) :
        m_maxDepth(maxDepth), m_depth(1)
{}

template <typename ALLOC>
bool BasicDepthWalkFilter<ALLOC>::beforeArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&)
{
    const bool enter = m_depth <= m_maxDepth;
    m_depth += 1;
    return enter;
}

template <typename ALLOC>
bool BasicDepthWalkFilter<ALLOC>::afterArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&)
{
    m_depth -= 1;
    return true;
}

template <typename ALLOC>
bool BasicDepthWalkFilter<ALLOC>::beforeCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t)
{
    const bool enter = m_depth <= m_maxDepth;
    m_depth += 1;
    return enter;
}

template <typename ALLOC>
bool BasicDepthWalkFilter<ALLOC>::afterCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t)
{
    m_depth -= 1;
    return true;
}

template <typename ALLOC>
bool BasicDepthWalkFilter<ALLOC>::beforeValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t)
{
    return m_depth <= m_maxDepth;
}

template <typename ALLOC>
bool BasicDepthWalkFilter<ALLOC>::afterValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&, size_t)
{
    return true;
}

namespace detail
{

template <typename ALLOC>
string<ALLOC> getCurrentPathImpl(
        const vector<string<ALLOC>, ALLOC>& currentPath,
        const ALLOC& allocator)
{
    string<ALLOC> currentPathStr(allocator);
    for (auto it = currentPath.begin(); it != currentPath.end(); ++it)
        {
            if (!currentPathStr.empty())
                currentPathStr += ".";
            currentPathStr += *it;
        }
        return currentPathStr;
}

template <typename ALLOC>
void appendPathImpl(vector<string<ALLOC>, ALLOC>& currentPath,
        const FieldInfo& fieldInfo, size_t elementIndex, const ALLOC& allocator)
{
    if (elementIndex == WALKER_NOT_ELEMENT)
    {
        currentPath.emplace_back(fieldInfo.schemaName.data(), fieldInfo.schemaName.size(), allocator);
    }
    else
    {
        currentPath.back() = toString(fieldInfo.schemaName, allocator) +
                "[" + toString(elementIndex, allocator) + "]";
    }
}

template <typename ALLOC>
void popPathImpl(vector<string<ALLOC>, ALLOC>& currentPath,
        const FieldInfo& fieldInfo, size_t elementIndex, const ALLOC& allocator)
{
    if (elementIndex == WALKER_NOT_ELEMENT)
        currentPath.pop_back();
    else
        currentPath.back() = toString(fieldInfo.schemaName, allocator);
}

template <typename ALLOC>
class SubtreeRegexWalkFilter : public IBasicWalkFilter<ALLOC>
{
public:
    SubtreeRegexWalkFilter(const vector<string<ALLOC>, ALLOC>& currentPath,
            const std::regex& pathRegex, const ALLOC& allocator) :
            m_currentPath(currentPath), m_pathRegex(pathRegex), m_allocator(allocator)
    {}

    bool matches()
    {
        return m_matches;
    }

    virtual bool beforeArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo& fieldInfo) override
    {
        m_currentPath.emplace_back(fieldInfo.schemaName.data(), fieldInfo.schemaName.size());
        m_matches = std::regex_match(getCurrentPath(), m_pathRegex);

        // terminate when the match is already found (note that array is never null here)
        return !m_matches;
    }

    virtual bool afterArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&) override
    {
        m_currentPath.pop_back();
        return !m_matches; // terminate when the match is already found
    }

    virtual bool beforeCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo& fieldInfo,
            size_t elementIndex) override
    {
        appendPath(fieldInfo, elementIndex);
        m_matches = std::regex_match(getCurrentPath(), m_pathRegex);

        // terminate when the match is already found (note that compound is never null here)
        return !m_matches;
    }

    virtual bool afterCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo& fieldInfo,
            size_t elementIndex) override
    {
        popPath(fieldInfo, elementIndex);
        return !m_matches; // terminate when the match is already found
    }

    virtual bool beforeValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo& fieldInfo,
            size_t elementIndex) override
    {
        appendPath(fieldInfo, elementIndex);
        m_matches = std::regex_match(getCurrentPath(), m_pathRegex);

        return !m_matches; // terminate when the match is already found
    }

    virtual bool afterValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo& fieldInfo,
            size_t elementIndex) override
    {
        popPath(fieldInfo, elementIndex);
        return !m_matches; // terminate when the match is already found
    }

private:
    string<ALLOC> getCurrentPath() const
    {
        return detail::getCurrentPathImpl(m_currentPath, m_allocator);
    }

    void appendPath(const FieldInfo& fieldInfo, size_t elementIndex)
    {
        detail::appendPathImpl(m_currentPath, fieldInfo, elementIndex, m_allocator);
    }

    void popPath(const FieldInfo& fieldInfo, size_t elementIndex)
    {
        detail::popPathImpl(m_currentPath, fieldInfo, elementIndex, m_allocator);
    }

    vector<string<ALLOC>, ALLOC> m_currentPath;
    std::regex m_pathRegex;
    ALLOC m_allocator;
    bool m_matches = false;
};

} // namespace detail

template <typename ALLOC>
BasicRegexWalkFilter<ALLOC>::BasicRegexWalkFilter(const char* pathRegex, const ALLOC& allocator) :
        m_pathRegex(pathRegex), m_allocator(allocator)
{}

template <typename ALLOC>
bool BasicRegexWalkFilter<ALLOC>::beforeArray(const IBasicReflectablePtr<ALLOC>& array,
        const FieldInfo& fieldInfo)
{
    m_currentPath.emplace_back(fieldInfo.schemaName.data(), fieldInfo.schemaName.size(), m_allocator);

    if (std::regex_match(getCurrentPath(), m_pathRegex))
        return true; // the array itself matches

    for (size_t i = 0; i < array->size(); ++i)
    {
        m_currentPath.back() = toString(fieldInfo.schemaName, m_allocator) +
                "[" + toString(i, m_allocator) + "]";

        if (matchSubtree(array->at(i), fieldInfo))
            return true;

        m_currentPath.back() = toString(fieldInfo.schemaName, m_allocator);
    }

    return false;
}

template <typename ALLOC>
bool BasicRegexWalkFilter<ALLOC>::afterArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&)
{
    m_currentPath.pop_back();
    return true;
}

template <typename ALLOC>
bool BasicRegexWalkFilter<ALLOC>::beforeCompound(const IBasicReflectablePtr<ALLOC>& compound,
        const FieldInfo& fieldInfo, size_t elementIndex)
{
    appendPath(fieldInfo, elementIndex);
    if (std::regex_match(getCurrentPath(), m_pathRegex))
        return true; // the compound itself matches

    return matchSubtree(compound, fieldInfo);
}

template <typename ALLOC>
bool BasicRegexWalkFilter<ALLOC>::afterCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    popPath(fieldInfo, elementIndex);
    return true;
}

template <typename ALLOC>
bool BasicRegexWalkFilter<ALLOC>::beforeValue(const IBasicReflectablePtr<ALLOC>& value,
        const FieldInfo& fieldInfo, size_t elementIndex)
{
    appendPath(fieldInfo, elementIndex);
    return matchSubtree(value, fieldInfo);
}

template <typename ALLOC>
bool BasicRegexWalkFilter<ALLOC>::afterValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    popPath(fieldInfo, elementIndex);
    return true;
}

template <typename ALLOC>
void BasicRegexWalkFilter<ALLOC>::appendPath(const FieldInfo& fieldInfo, size_t elementIndex)
{
    detail::appendPathImpl(m_currentPath, fieldInfo, elementIndex, m_allocator);
}

template <typename ALLOC>
void BasicRegexWalkFilter<ALLOC>::popPath(const FieldInfo& fieldInfo, size_t elementIndex)
{
    detail::popPathImpl(m_currentPath, fieldInfo, elementIndex, m_allocator);
}

template <typename ALLOC>
string<ALLOC> BasicRegexWalkFilter<ALLOC>::getCurrentPath() const
{
    return detail::getCurrentPathImpl(m_currentPath, m_allocator);
}

template <typename ALLOC>
bool BasicRegexWalkFilter<ALLOC>::matchSubtree(const IBasicReflectablePtr<ALLOC>& value,
        const FieldInfo& fieldInfo) const
{
    if (value != nullptr && TypeInfoUtil::isCompound(fieldInfo.typeInfo.getSchemaType()))
    {
        // is a not null compound, try to find match within its subtree
        auto subtreeFilter = std::allocate_shared<detail::SubtreeRegexWalkFilter<ALLOC>>(
                m_allocator, m_currentPath, m_pathRegex, m_allocator);
        Walker walker(std::allocate_shared<BasicDefaultWalkObserver<ALLOC>>(m_allocator), subtreeFilter);
        walker.walk(value);
        return subtreeFilter->matches();
    }
    else
    {
        // try to match a simple value or null compound
        return std::regex_match(getCurrentPath(), m_pathRegex);
    }
}

template <typename ALLOC>
BasicArrayLengthWalkFilter<ALLOC>::BasicArrayLengthWalkFilter(size_t maxArrayLength) :
        m_maxArrayLength(maxArrayLength)
{}

template <typename ALLOC>
bool BasicArrayLengthWalkFilter<ALLOC>::beforeArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&)
{
    return true;
}

template <typename ALLOC>
bool BasicArrayLengthWalkFilter<ALLOC>::afterArray(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&)
{
    return true;
}

template <typename ALLOC>
bool BasicArrayLengthWalkFilter<ALLOC>::beforeCompound(const IBasicReflectablePtr<ALLOC>&,
        const FieldInfo&, size_t elementIndex)
{
    return filterArrayElement(elementIndex);
}

template <typename ALLOC>
bool BasicArrayLengthWalkFilter<ALLOC>::afterCompound(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&,
        size_t elementIndex)
{
    return filterArrayElement(elementIndex);
}

template <typename ALLOC>
bool BasicArrayLengthWalkFilter<ALLOC>::beforeValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&,
        size_t elementIndex)
{
    return filterArrayElement(elementIndex);
}

template <typename ALLOC>
bool BasicArrayLengthWalkFilter<ALLOC>::afterValue(const IBasicReflectablePtr<ALLOC>&, const FieldInfo&,
        size_t elementIndex)
{
    return filterArrayElement(elementIndex);
}

template <typename ALLOC>
bool BasicArrayLengthWalkFilter<ALLOC>::filterArrayElement(size_t elementIndex)
{
    return elementIndex == WALKER_NOT_ELEMENT ? true : elementIndex < m_maxArrayLength;
}

template <typename ALLOC>
BasicAndWalkFilter<ALLOC>::BasicAndWalkFilter(const WalkFilters& walkFilters) :
        m_walkFilters(walkFilters)
{}

template <typename ALLOC>
bool BasicAndWalkFilter<ALLOC>::beforeArray(const IBasicReflectablePtr<ALLOC>& array,
        const FieldInfo& fieldInfo)
{
    return applyFilters(&IWalkFilter::beforeArray, array, fieldInfo);
}

template <typename ALLOC>
bool BasicAndWalkFilter<ALLOC>::afterArray(const IBasicReflectablePtr<ALLOC>& array, const FieldInfo& fieldInfo)
{
    return applyFilters(&IWalkFilter::afterArray, array, fieldInfo);
}

template <typename ALLOC>
bool BasicAndWalkFilter<ALLOC>::beforeCompound(const IBasicReflectablePtr<ALLOC>& compound,
        const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    return applyFilters(&IWalkFilter::beforeCompound, compound, fieldInfo, elementIndex);
}

template <typename ALLOC>
bool BasicAndWalkFilter<ALLOC>::afterCompound(const IBasicReflectablePtr<ALLOC>& compound,
        const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    return applyFilters(&IWalkFilter::afterCompound, compound, fieldInfo, elementIndex);
}

template <typename ALLOC>
bool BasicAndWalkFilter<ALLOC>::beforeValue(const IBasicReflectablePtr<ALLOC>& value,
        const FieldInfo& fieldInfo, size_t elementIndex)
{
    return applyFilters(&IWalkFilter::beforeValue, value, fieldInfo, elementIndex);
}

template <typename ALLOC>
bool BasicAndWalkFilter<ALLOC>::afterValue(const IBasicReflectablePtr<ALLOC>& value, const FieldInfo& fieldInfo,
        size_t elementIndex)
{
    return applyFilters(&IWalkFilter::afterValue, value, fieldInfo, elementIndex);
}

} // namespace zserio

#endif // ZSERIO_WALKER_H_INC
