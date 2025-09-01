#include <type_traits>

#include "zserio/RebindAlloc.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace without_writer_code
{

using allocator_type = zserio::RebindAlloc<Tile::allocator_type, uint8_t>;

template <typename ALLOC>
struct BasicMethodNames
{
    // TODO[Mi-L@]: Since we don't know allocator name provided by user, we use just the fixed substring here

    // all compounds
    static constexpr const char* TYPE_INFO_DECLARATION = "typeInfo();";
    static constexpr const char* REFLECTABLE_CONST_DECLARATION =
            "reflectable(const allocator_type& allocator = allocator_type()) const;";
    static constexpr const char* REFLECTABLE_DECLARATION =
            "reflectable(const allocator_type& allocator = allocator_type());";

    // ItemType
    static constexpr const char* ITEM_TYPE_TYPE_INFO = "enumTypeInfo<::without_writer_code::ItemType";
    static constexpr const char* ITEM_TYPE_REFLECTABLE =
            "enumReflectable(::without_writer_code::ItemType value,";

    // VersionAvailability
    static constexpr const char* VERSION_AVAILABILITY_TYPE_INFO_DEFINITION = "VersionAvailability::typeInfo()";
    static constexpr const char* VERSION_AVAILABILITY_REFLECTABLE_DECLARATION = "reflectable(const ";
    static constexpr const char* VERSION_AVAILABILITY_REFLECTABLE_DEFINITION =
            "VersionAvailability::reflectable(const ";
    static constexpr const char* TO_STRING_DECLARATION =
            "toString(const zserio::string<"; // ...>::allocator_type...
    static constexpr const char* TO_STRING_DEFINITION =
            "VersionAvailability::toString(const zserio::string<"; // ...>::allocator_type...

    // ExtraParamUnion
    static constexpr const char* EXTRA_PARAM_UNION_TYPE_INFO_DEFINITION = "ExtraParamUnion::typeInfo()";
    static constexpr const char* EXTRA_PARAM_UNION_REFLECTABLE_CONST_DEFINITION =
            "ExtraParamUnion::reflectable(const allocator_type& allocator) const";
    static constexpr const char* EXTRA_PARAM_UNION_REFLECTABLE_DEFINITION =
            "::zserio::IBasicReflectablePtr<"; // return type only to prevent clash with const version

    // Item
    static constexpr const char* ITEM_TYPE_INFO_DEFINITION = "Item::typeInfo()";
    static constexpr const char* ITEM_REFLECTABLE_CONST_DEFINITION =
            "Item::reflectable(const allocator_type& allocator) const";
    static constexpr const char* ITEM_REFLECTABLE_DEFINITION =
            "::zserio::IBasicReflectablePtr<"; // return type only to prevent clash with const version

    // ItemChoice
    static constexpr const char* ITEM_CHOICE_TYPE_INFO_DEFINITION = "ItemChoice::typeInfo()";
    static constexpr const char* ITEM_CHOICE_REFLECTABLE_CONST_DEFINITION =
            "ItemChoice::reflectable(const allocator_type& allocator) const";
    static constexpr const char* ITEM_CHOICE_REFLECTABLE_DEFINITION =
            "::zserio::IBasicReflectablePtr<"; // return type only to prevent clash with const version

    // ItemChoiceHolder
    static constexpr const char* ITEM_CHOICE_HOLDER_TYPE_INFO_DEFINITION = "ItemChoiceHolder::typeInfo()";
    static constexpr const char* ITEM_CHOICE_HOLDER_REFLECTABLE_CONST_DEFINITION =
            "ItemChoiceHolder::reflectable(const allocator_type& allocator) const";
    static constexpr const char* ITEM_CHOICE_HOLDER_REFLECTABLE_DEFINITION =
            "::zserio::IBasicReflectablePtr<"; // return type only to prevent clash with const version

    // Tile
    static constexpr const char* TILE_TYPE_INFO_DEFINITION = "Tile::typeInfo()";
    static constexpr const char* TILE_REFLECTABLE_CONST_DEFINITION =
            "Tile::reflectable(const allocator_type& allocator) const";
    static constexpr const char* TILE_REFLECTABLE_DEFINITION =
            "::zserio::IBasicReflectablePtr<"; // return type only to prevent clash with const version
    static constexpr const char* GET_VERSION_STRING_DECLARATION = "& getVersionString(";
    static constexpr const char* GET_VERSION_STRING_DEFINITION = "& Tile::getVersionString(";

    // GeoMapTable
    static constexpr const char* GEO_MAP_TABLE_TYPE_INFO_DEFINITION = "GeoMapTable::typeInfo()";

    // WorldDb
    static constexpr const char* WORLD_DB_TYPE_INFO_DEFINITION = "WorldDb::typeInfo()";
    static constexpr const char* WORLD_DB_CTOR_DECLARATION = "WorldDb(const ::zserio::string<";
    static constexpr const char* WORLD_DB_CTOR_DEFINITION = "WorldDb::WorldDb(const ::zserio::string<";
};

template <>
struct BasicMethodNames<zserio::pmr::PropagatingPolymorphicAllocator<uint8_t>>
{
    // all compounds
    static constexpr const char* TYPE_INFO_DECLARATION = "static const ::zserio::pmr::ITypeInfo& typeInfo();";
    static constexpr const char* REFLECTABLE_CONST_DECLARATION =
            "::zserio::pmr::IReflectableConstPtr reflectable(";
    static constexpr const char* REFLECTABLE_DECLARATION = "::zserio::pmr::IReflectablePtr reflectable(";

    // ItemType
    static constexpr const char* ITEM_TYPE_TYPE_INFO =
            "const ::zserio::pmr::ITypeInfo& enumTypeInfo<::without_writer_code::ItemType";
    static constexpr const char* ITEM_TYPE_REFLECTABLE =
            ":zserio::pmr::IReflectablePtr enumReflectable(::without_writer_code::ItemType "
            "value,";

    // VersionAvailability
    static constexpr const char* VERSION_AVAILABILITY_TYPE_INFO_DEFINITION =
            "const ::zserio::pmr::ITypeInfo& VersionAvailability::typeInfo()";
    static constexpr const char* VERSION_AVAILABILITY_REFLECTABLE_DECLARATION =
            "::zserio::pmr::IReflectablePtr reflectable(";
    static constexpr const char* VERSION_AVAILABILITY_REFLECTABLE_DEFINITION =
            "::zserio::pmr::IReflectablePtr VersionAvailability::reflectable(";
    static constexpr const char* TO_STRING_DECLARATION =
            "::zserio::pmr::string toString(const ::zserio::pmr::string::allocator_type& allocator =";
    static constexpr const char* TO_STRING_DEFINITION =
            "::zserio::pmr::string VersionAvailability::toString("
            "const ::zserio::pmr::string::allocator_type& allocator) const";

    // ExtraParamUnion
    static constexpr const char* EXTRA_PARAM_UNION_TYPE_INFO_DEFINITION =
            "const ::zserio::pmr::ITypeInfo& ExtraParamUnion::typeInfo()";
    static constexpr const char* EXTRA_PARAM_UNION_REFLECTABLE_CONST_DEFINITION =
            "::zserio::pmr::IReflectableConstPtr ExtraParamUnion::reflectable(";
    static constexpr const char* EXTRA_PARAM_UNION_REFLECTABLE_DEFINITION =
            "::zserio::pmr::IReflectablePtr ExtraParamUnion::reflectable(";

    // Item
    static constexpr const char* ITEM_TYPE_INFO_DEFINITION = "const ::zserio::pmr::ITypeInfo& Item::typeInfo()";
    static constexpr const char* ITEM_REFLECTABLE_CONST_DEFINITION =
            "::zserio::pmr::IReflectableConstPtr Item::reflectable(";
    static constexpr const char* ITEM_REFLECTABLE_DEFINITION =
            "::zserio::pmr::IReflectablePtr Item::reflectable(";

    // ItemChoice
    static constexpr const char* ITEM_CHOICE_TYPE_INFO_DEFINITION =
            "const ::zserio::pmr::ITypeInfo& ItemChoice::typeInfo()";
    static constexpr const char* ITEM_CHOICE_REFLECTABLE_CONST_DEFINITION =
            "::zserio::pmr::IReflectableConstPtr ItemChoice::reflectable(";
    static constexpr const char* ITEM_CHOICE_REFLECTABLE_DEFINITION =
            "::zserio::pmr::IReflectablePtr ItemChoice::reflectable(";

    // ItemChoiceHolder
    static constexpr const char* ITEM_CHOICE_HOLDER_TYPE_INFO_DEFINITION =
            "const ::zserio::pmr::ITypeInfo& ItemChoiceHolder::typeInfo()";
    static constexpr const char* ITEM_CHOICE_HOLDER_REFLECTABLE_CONST_DEFINITION =
            "::zserio::pmr::IReflectableConstPtr ItemChoiceHolder::reflectable(";
    static constexpr const char* ITEM_CHOICE_HOLDER_REFLECTABLE_DEFINITION =
            "::zserio::pmr::IReflectablePtr ItemChoiceHolder::reflectable(";

    // Tile
    static constexpr const char* TILE_TYPE_INFO_DEFINITION = "const ::zserio::pmr::ITypeInfo& Tile::typeInfo()";
    static constexpr const char* TILE_REFLECTABLE_CONST_DEFINITION =
            "::zserio::pmr::IReflectableConstPtr Tile::reflectable(";
    static constexpr const char* TILE_REFLECTABLE_DEFINITION =
            "::zserio::pmr::IReflectablePtr Tile::reflectable(";
    static constexpr const char* GET_VERSION_STRING_DECLARATION =
            "const ::zserio::pmr::string& getVersionString(";
    static constexpr const char* GET_VERSION_STRING_DEFINITION =
            "const ::zserio::pmr::string& Tile::getVersionString(";

    // GeoMapTable
    static constexpr const char* GEO_MAP_TABLE_TYPE_INFO_DEFINITION =
            "const ::zserio::pmr::ITypeInfo& GeoMapTable::typeInfo()";

    // WorldDb
    static constexpr const char* WORLD_DB_TYPE_INFO_DEFINITION =
            "const ::zserio::pmr::ITypeInfo& WorldDb::typeInfo()";
    static constexpr const char* WORLD_DB_CTOR_DECLARATION = "WorldDb(const ::zserio::pmr::string&";
    static constexpr const char* WORLD_DB_CTOR_DEFINITION = "WorldDb::WorldDb(const ::zserio::pmr::string&";
};

template <>
struct BasicMethodNames<std::allocator<uint8_t>>
{
    // all compounds
    static constexpr const char* TYPE_INFO_DECLARATION = "static const ::zserio::ITypeInfo& typeInfo();";
    static constexpr const char* REFLECTABLE_CONST_DECLARATION = "::zserio::IReflectableConstPtr reflectable(";
    static constexpr const char* REFLECTABLE_DECLARATION = "::zserio::IReflectablePtr reflectable(";

    // ItemType
    static constexpr const char* ITEM_TYPE_TYPE_INFO =
            "const ::zserio::ITypeInfo& enumTypeInfo<::without_writer_code::ItemType";
    static constexpr const char* ITEM_TYPE_REFLECTABLE =
            "::zserio::IReflectablePtr enumReflectable(::without_writer_code::ItemType value,";

    // VersionAvailability
    static constexpr const char* VERSION_AVAILABILITY_TYPE_INFO_DEFINITION =
            "const ::zserio::ITypeInfo& VersionAvailability::typeInfo()";
    static constexpr const char* VERSION_AVAILABILITY_REFLECTABLE_DECLARATION =
            "::zserio::IReflectablePtr reflectable(";
    static constexpr const char* VERSION_AVAILABILITY_REFLECTABLE_DEFINITION =
            "::zserio::IReflectablePtr VersionAvailability::reflectable(";
    static constexpr const char* TO_STRING_DECLARATION =
            "::zserio::string<> toString(const ::zserio::string<>::allocator_type& allocator =";
    static constexpr const char* TO_STRING_DEFINITION =
            "::zserio::string<> VersionAvailability::toString("
            "const ::zserio::string<>::allocator_type& allocator) const";

    // ExtraParamUnion
    static constexpr const char* EXTRA_PARAM_UNION_TYPE_INFO_DEFINITION =
            "const ::zserio::ITypeInfo& ExtraParamUnion::typeInfo()";
    static constexpr const char* EXTRA_PARAM_UNION_REFLECTABLE_CONST_DEFINITION =
            "::zserio::IReflectableConstPtr ExtraParamUnion::reflectable(";
    static constexpr const char* EXTRA_PARAM_UNION_REFLECTABLE_DEFINITION =
            "::zserio::IReflectablePtr ExtraParamUnion::reflectable(";

    // Item
    static constexpr const char* ITEM_TYPE_INFO_DEFINITION = "const ::zserio::ITypeInfo& Item::typeInfo()";
    static constexpr const char* ITEM_REFLECTABLE_CONST_DEFINITION =
            "::zserio::IReflectableConstPtr Item::reflectable(";
    static constexpr const char* ITEM_REFLECTABLE_DEFINITION = "::zserio::IReflectablePtr Item::reflectable(";

    // ItemChoice
    static constexpr const char* ITEM_CHOICE_TYPE_INFO_DEFINITION =
            "const ::zserio::ITypeInfo& ItemChoice::typeInfo()";
    static constexpr const char* ITEM_CHOICE_REFLECTABLE_CONST_DEFINITION =
            "::zserio::IReflectableConstPtr ItemChoice::reflectable(";
    static constexpr const char* ITEM_CHOICE_REFLECTABLE_DEFINITION =
            "::zserio::IReflectablePtr ItemChoice::reflectable(";

    // ItemChoiceHolder
    static constexpr const char* ITEM_CHOICE_HOLDER_TYPE_INFO_DEFINITION =
            "const ::zserio::ITypeInfo& ItemChoiceHolder::typeInfo()";
    static constexpr const char* ITEM_CHOICE_HOLDER_REFLECTABLE_CONST_DEFINITION =
            "::zserio::IReflectableConstPtr ItemChoiceHolder::reflectable(";
    static constexpr const char* ITEM_CHOICE_HOLDER_REFLECTABLE_DEFINITION =
            "::zserio::IReflectablePtr ItemChoiceHolder::reflectable(";

    // Tile
    static constexpr const char* TILE_TYPE_INFO_DEFINITION = "const ::zserio::ITypeInfo& Tile::typeInfo()";
    static constexpr const char* TILE_REFLECTABLE_CONST_DEFINITION =
            "::zserio::IReflectableConstPtr Tile::reflectable(";
    static constexpr const char* TILE_REFLECTABLE_DEFINITION = "::zserio::IReflectablePtr Tile::reflectable(";
    static constexpr const char* GET_VERSION_STRING_DECLARATION = "const ::zserio::string<>& getVersionString(";
    static constexpr const char* GET_VERSION_STRING_DEFINITION =
            "const ::zserio::string<>& Tile::getVersionString(";

    // GeoMapTable
    static constexpr const char* GEO_MAP_TABLE_TYPE_INFO_DEFINITION =
            "const ::zserio::ITypeInfo& GeoMapTable::typeInfo()";

    // WorldDb
    static constexpr const char* WORLD_DB_TYPE_INFO_DEFINITION =
            "const ::zserio::ITypeInfo& WorldDb::typeInfo()";
    static constexpr const char* WORLD_DB_CTOR_DECLARATION = "WorldDb(const ::zserio::string<>&";
    static constexpr const char* WORLD_DB_CTOR_DEFINITION = "WorldDb::WorldDb(const ::zserio::string<>&";
};

using MethodNames = BasicMethodNames<allocator_type>;

} // namespace without_writer_code
