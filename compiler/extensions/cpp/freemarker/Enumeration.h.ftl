<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <string>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/HashCodeUtil.h>
#include <zserio/PreWriteAction.h>
<#if withInspectorCode>
#include <zserio/inspector/BlobInspectorTree.h>
</#if>
<@system_includes headerSystemIncludes, false/>

<@user_includes headerUserIncludes, true/>
<@namespace_begin package.path/>

class ${name}
{
public:
    /**
     * The type this enum is based on.
     *
     * This is the C++ mapping of the original zserio type. It can be
     * wider than the original type. E.g. "enum bit:3" would have uint8_t
     * as its base type.
     */
    typedef ${baseCppTypeName} _base_type;

    enum e_${name}
    {
<#list items as item>
        ${item.name} = ${item.value}<#if item_has_next>,</#if>
</#list>
    };

<#if withWriterCode>
    ${name}();
</#if>
    ${name}(e_${name} value);
    explicit ${name}(zserio::BitStreamReader& _in);
<#if withInspectorCode>
    explicit ${name}(const zserio::BlobInspectorTree& _tree);
</#if>

    operator e_${name}() const;
    ${baseCppTypeName} getValue() const;

    size_t bitSizeOf(size_t _bitPosition = 0) const;
<#if withWriterCode>
    size_t initializeOffsets(size_t _bitPosition = 0) const;
</#if>

    bool operator==(const ${name}& other) const;
    bool operator==(e_${name} other) const;
    int hashCode() const;

    void read(zserio::BitStreamReader& _in);
<#if withInspectorCode>
    void read(const zserio::BlobInspectorTree& _tree);
</#if>
<#if withWriterCode>
    void write(zserio::BitStreamWriter& _out,
            zserio::PreWriteAction _preWriteAction = zserio::ALL_PRE_WRITE_ACTIONS) const;
    <#if withInspectorCode>
    void write(zserio::BitStreamWriter& _out, zserio::BlobInspectorTree& _tree,
            zserio::PreWriteAction _preWriteAction = zserio::ALL_PRE_WRITE_ACTIONS) const;
    </#if>
</#if>

    const char* toString() const;

    static ${name} toEnum(${baseCppTypeName} rawValue);

private:
    e_${name} m_value;
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
