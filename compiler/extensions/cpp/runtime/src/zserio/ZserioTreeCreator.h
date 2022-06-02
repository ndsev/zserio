#ifndef ZSERIO_ZSERIO_TREE_CREATOR_H_INC
#define ZSERIO_ZSERIO_TREE_CREATOR_H_INC

#include <deque>

#include "IReflectable.h"
#include "ITypeInfo.h"

namespace zserio
{

template <typename ALLOC>
class ZserioTreeCreator
{
public:
    ZserioTreeCreator(const ITypeInfo& typeInfo, const IBasicConstructor<ALLOC>& ctor);

    IBasicReflectablePtr<ALLOC> get() const;

    void beginArray();
    void endArray();

    void beginObject();
    void endObject();

    void beginItem(const std::string& key);
    void endItem(const std::string& key);

private:
    const ITypeInfo& getTypeInfo() const;
    const FieldInfo& findFieldInfo(const ITypeInfo& typeInfo, const std::string& fieldName) const;

    const ITypeInfo& m_typeInfo;
    std::deque<std::reference_wrapper<const FieldInfo>> m_fieldInfoStack;
    std::deque<IReflectablePtr> m_valueStack;
};

} // namespace zserio

#endif // ZSERIO_ZSERIO_TREE_CREATOR_H_INC
