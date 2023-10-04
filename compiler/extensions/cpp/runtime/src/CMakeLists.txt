# Zserio C++ runtime library.
#
# This CMake file defines a static library target named ZserioCppRuntime that contains the zserio C++ runtime.
#
# This CMake file is designed to be included directly without any further dependencies.
#
# An example application CMakeLists.txt follows:
#
# project(ZserioTestApp)
#
# add_subdirectory(runtime_libs/cpp ZserioCppRuntime)
#
# add_executable(ZserioTestApp HelloWorld.cpp)
# target_link_libraries(ZserioTestApp ZserioCppRuntime)

cmake_minimum_required(VERSION 3.1.0)

project(ZserioCppRuntime)

set(ZSERIO_CPP_RUNTIME_LIB_SRCS
    zserio/pmr/AnyHolder.h
    zserio/pmr/ArrayTraits.h
    zserio/pmr/BitBuffer.h
    zserio/pmr/HeapOptionalHolder.h
    zserio/pmr/IReflectable.h
    zserio/pmr/IService.h
    zserio/pmr/Map.h
    zserio/pmr/MemoryResource.cpp
    zserio/pmr/MemoryResource.h
    zserio/pmr/NewDeleteResource.cpp
    zserio/pmr/NewDeleteResource.h
    zserio/pmr/PolymorphicAllocator.h
    zserio/pmr/Reflectable.h
    zserio/pmr/Set.h
    zserio/pmr/String.h
    zserio/pmr/UniquePtr.h
    zserio/pmr/Vector.h
    zserio/AllocatorHolder.h
    zserio/AllocatorPropagatingCopy.h
    zserio/AnyHolder.h
    zserio/Array.h
    zserio/ArrayTraits.h
    zserio/BitBuffer.h
    zserio/BitFieldUtil.cpp
    zserio/BitFieldUtil.h
    zserio/BitPositionUtil.h
    zserio/BitSizeOfCalculator.cpp
    zserio/BitSizeOfCalculator.h
    zserio/BitStreamReader.cpp
    zserio/BitStreamReader.h
    zserio/BitStreamWriter.cpp
    zserio/BitStreamWriter.h
    zserio/BuiltInOperators.cpp
    zserio/BuiltInOperators.h
    zserio/ConstraintException.h
    zserio/CppRuntimeException.cpp
    zserio/CppRuntimeException.h
    zserio/CppRuntimeVersion.h
    zserio/DebugStringUtil.h
    zserio/DeprecatedAttribute.h
    zserio/Enums.h
    zserio/FloatUtil.cpp
    zserio/FloatUtil.h
    zserio/HashCodeUtil.h
    zserio/FileUtil.cpp
    zserio/FileUtil.h
    zserio/IReflectable.h
    zserio/IPubsub.h
    zserio/IService.h
    zserio/ISqliteDatabase.h
    zserio/ISqliteDatabaseReader.h
    zserio/ITypeInfo.h
    zserio/IValidationObserver.h
    zserio/IWalkFilter.h
    zserio/IWalkObserver.h
    zserio/JsonEncoder.cpp
    zserio/JsonEncoder.h
    zserio/JsonDecoder.h
    zserio/JsonParser.h
    zserio/JsonReader.h
    zserio/JsonTokenizer.cpp
    zserio/JsonTokenizer.h
    zserio/JsonWriter.h
    zserio/OptionalHolder.h
    zserio/DeltaContext.h
    zserio/PubsubException.h
    zserio/Reflectable.h
    zserio/SerializeUtil.h
    zserio/ServiceException.h
    zserio/Span.h
    zserio/SqliteConnection.h
    zserio/SqliteException.h
    zserio/SqliteFinalizer.h
    zserio/StringConvertUtil.h
    zserio/StringView.h
    zserio/TypeInfo.h
    zserio/TypeInfoUtil.cpp
    zserio/TypeInfoUtil.h
    zserio/Types.h
    zserio/UniquePtr.h
    zserio/ValidationSqliteUtil.h
    zserio/SizeConvertUtil.cpp
    zserio/SizeConvertUtil.h
    zserio/Walker.h
    zserio/WalkerConst.h
    zserio/ZserioTreeCreator.h
    zserio/ZserioTreeCreator.cpp
)

add_library(${PROJECT_NAME} STATIC ${ZSERIO_CPP_RUNTIME_LIB_SRCS})

set_target_properties(${PROJECT_NAME} PROPERTIES CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES CXX_EXTENSIONS NO)
target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
