package zserio.extension.cpp;

import zserio.ast.PackageName;

/**
 * Keeps information about configurable types.
 *
 * Currently only types which depends on allocators are configurable.
 */
public class TypesContext
{
    public TypesContext(AllocatorDefinition allocator)
    {
        this.allocator = allocator;

        if (allocator.equals(STD_ALLOCATOR))
        {
            vector = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "vector",
                    true, false, "zserio/Vector.h");
            array = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "Array",
                    true, false, "zserio/Array.h");
            string = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "string",
                    true, false, "zserio/String.h");
            map = new NativeTypeDefinition(STD_PACKAGE_NAME, "map",
                    true, false, "map");
            set = new NativeTypeDefinition(STD_PACKAGE_NAME, "set",
                    true, false, "set");
            anyHolder = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "AnyHolder",
                    true, false, "zserio/AnyHolder.h");
            uniquePtr = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "unique_ptr",
                    true, false, "zserio/UniquePtr.h");
            heapOptionalHolder = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "HeapOptionalHolder",
                    true, false, "zserio/OptionalHolder.h");
            bitBuffer = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "BitBuffer",
                    false, false, "zserio/BitBuffer.h");
            packingContextNode = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "PackingContextNode",
                    false, false, "zserio/PackingContext.h");
            stringArrayTraits = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "StringArrayTraits",
                    false, false, "zserio/ArrayTraits.h");
            bitBufferArrayTraits = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "BitBufferArrayTraits",
                    false, false, "zserio/ArrayTraits.h");
            reflectableFactory = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "ReflectableFactory",
                    false, false, "zserio/Reflectable.h");
            reflectablePtr = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IReflectablePtr",
                    false, false, "zserio/IReflectable.h");
            reflectableConstPtr = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IReflectableConstPtr",
                    false, false, "zserio/IReflectable.h");
            service = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IService",
                    false, false, "zserio/IService.h");
            serviceClient = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IServiceClient",
                    false, false, "zserio/IService.h");
            serviceDataPtr = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IServiceDataPtr",
                    false, false, "zserio/IService.h");
            serviceData = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "ServiceData",
                    false, false, "zserio/IService.h");
        }
        else if (allocator.equals(PROPAGATING_POLYMORPHIC_ALLOCATOR))
        {
            vector = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "vector",
                    true, false, "zserio/pmr/Vector.h");
            array = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "Array",
                    true, false, "zserio/pmr/Array.h");
            string = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "string",
                    false, false, "zserio/pmr/String.h");
            map = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "map",
                    true, false, "zserio/pmr/Map.h");
            set = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "set",
                    true, false, "zserio/pmr/Set.h");
            anyHolder = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "AnyHolder",
                    false, false, "zserio/pmr/AnyHolder.h");
            uniquePtr = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "unique_ptr",
                    true, false, "zserio/pmr/UniquePtr.h");
            heapOptionalHolder = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "HeapOptionalHolder",
                    true, false, "zserio/pmr/HeapOptionalHolder.h");
            bitBuffer = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "BitBuffer",
                    false, false, "zserio/pmr/BitBuffer.h");
            packingContextNode = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "PackingContextNode",
                    false, false, "zserio/pmr/PackingContext.h");
            stringArrayTraits = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "StringArrayTraits",
                    false, false, "zserio/pmr/ArrayTraits.h");
            bitBufferArrayTraits = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "BitBufferArrayTraits",
                    false, false, "zserio/pmr/ArrayTraits.h");
            reflectableFactory = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "ReflectableFactory",
                    false, false, "zserio/pmr/Reflectable.h");
            reflectablePtr = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "IReflectablePtr",
                    false, false, "zserio/pmr/IReflectable.h");
            reflectableConstPtr = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "IReflectableConstPtr",
                    false, false, "zserio/pmr/IReflectable.h");
            service = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "IService",
                    false, false, "zserio/pmr/IService.h");
            serviceClient = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "IServiceClient",
                    false, false, "zserio/pmr/IService.h");
            serviceDataPtr = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "IServiceDataPtr",
                    false, false, "zserio/pmr/IService.h");
            serviceData = new NativeTypeDefinition(ZSERIO_PMR_PACKAGE_NAME, "ServiceData",
                    false, false, "zserio/pmr/IService.h");
        }
        else
        {
            vector = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "vector",
                    true, true, "vector");
            array = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "BasicArray",
                    true, true, "zserio/Array.h");
            string = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "string",
                    true, true, "zserio/String.h");
            map = new NativeTypeDefinition(STD_PACKAGE_NAME, "map",
                    true, true, "map");
            set = new NativeTypeDefinition(STD_PACKAGE_NAME, "set",
                    true, true, "set");
            anyHolder = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "AnyHolder",
                    true, true, "zserio/AnyHolder.h");
            uniquePtr = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "unique_ptr",
                    true, true, "zserio/UniquePtr.h");
            heapOptionalHolder = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "HeapOptionalHolder",
                    true, true, "zserio/OptionalHolder.h");
            bitBuffer = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "BasicBitBuffer",
                    true, true, "zserio/BitBuffer.h");
            packingContextNode = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "BasicPackingContextNode",
                    true, true, "zserio/Array.h");
            stringArrayTraits = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "BasicStringArrayTraits",
                    true, true, "zserio/ArrayTraits.h");
            bitBufferArrayTraits = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "BasicBitBufferArrayTraits",
                    true, true, "zserio/ArrayTraits.h");
            reflectableFactory = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "BasicReflectableFactory",
                    true, true, "zserio/Reflectable.h");
            reflectablePtr = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IBasicReflectablePtr",
                    true, true, "zserio/IReflectable.h");
            reflectableConstPtr = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IBasicReflectableConstPtr",
                    true, true, "zserio/IReflectable.h");
            service = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IBasicService",
                    true, true, "zserio/IService.h");
            serviceClient = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IBasicServiceClient",
                    true, true, "zserio/IService.h");
            serviceDataPtr = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "IBasicServiceDataPtr",
                    true, true, "zserio/IService.h");
            serviceData = new NativeTypeDefinition(ZSERIO_PACKAGE_NAME, "BasicServiceData",
                    true, true, "zserio/IService.h");
        }
    }

    public static class AllocatorDefinition
    {
        public AllocatorDefinition(String allocatorType, String allocatorSystemInclude)
        {
            this(allocatorType, allocatorSystemInclude, "uint8_t");
        }

        public AllocatorDefinition(String allocatorType, String allocatorSystemInclude,
                String allocatorDefaultType)
        {
            this.allocatorType = allocatorType;
            this.allocatorSystemInclude = allocatorSystemInclude;
            this.allocatorDefaultType = allocatorDefaultType;
        }

        public String getAllocatorType()
        {
            return allocatorType;
        }

        public String getAllocatorSystemInclude()
        {
            return allocatorSystemInclude;
        }

        public String getAllocatorDefaultType()
        {
            return allocatorDefaultType;
        }

        private final String allocatorType;
        private final String allocatorSystemInclude;
        private final String allocatorDefaultType;
    }

    public AllocatorDefinition getAllocatorDefinition()
    {
        return allocator;
    }

    public NativeTypeDefinition getVector()
    {
        return vector;
    }

    public NativeTypeDefinition getArray()
    {
        return array;
    }

    public NativeTypeDefinition getString()
    {
        return string;
    }

    public NativeTypeDefinition getMap()
    {
        return map;
    }

    public NativeTypeDefinition getSet()
    {
        return set;
    }

    public NativeTypeDefinition getHeapOptionalHolder()
    {
        return heapOptionalHolder;
    }

    public NativeTypeDefinition getAnyHolder()
    {
        return anyHolder;
    }

    public NativeTypeDefinition getUniquePtr()
    {
        return uniquePtr;
    }

    public NativeTypeDefinition getBitBuffer()
    {
        return bitBuffer;
    }

    public NativeTypeDefinition getPackingContextNode()
    {
        return packingContextNode;
    }

    public NativeTypeDefinition getStringArrayTraits()
    {
        return stringArrayTraits;
    }

    public NativeTypeDefinition getBitBufferArrayTraits()
    {
        return bitBufferArrayTraits;
    }

    public NativeTypeDefinition getRelectableFactory()
    {
        return reflectableFactory;
    }

    public NativeTypeDefinition getReflectablePtr()
    {
        return reflectablePtr;
    }

    public NativeTypeDefinition getReflectableConstPtr()
    {
        return reflectableConstPtr;
    }

    public NativeTypeDefinition getService()
    {
        return service;
    }

    public NativeTypeDefinition getServiceClient()
    {
        return serviceClient;
    }

    public NativeTypeDefinition getServiceDataPtr()
    {
        return serviceDataPtr;
    }

    public NativeTypeDefinition getServiceData()
    {
        return serviceData;
    }

    public static class NativeTypeDefinition
    {
        public NativeTypeDefinition(PackageName pkg, String name, boolean isTemplate,
                boolean needsAllocatorArgument, String systemInclude)
        {
            this.pkg = pkg;
            this.name = name;
            this.isTemplate = isTemplate;
            this.needsAllocatorArgument = needsAllocatorArgument;
            this.systemInclude = systemInclude;
        }

        public PackageName getPackage()
        {
            return pkg;
        }

        public String getName()
        {
            return name;
        }

        public String getSystemInclude()
        {
            return systemInclude;
        }

        public boolean isTemplate()
        {
            return isTemplate;
        }

        public boolean needsAllocatorArgument()
        {
            return needsAllocatorArgument;
        }

        private final PackageName pkg;
        private final String name;
        private final String systemInclude;
        private final boolean isTemplate;
        private final boolean needsAllocatorArgument;
    }

    private final AllocatorDefinition allocator;
    private final NativeTypeDefinition vector;
    private final NativeTypeDefinition array;
    private final NativeTypeDefinition string;
    private final NativeTypeDefinition map;
    private final NativeTypeDefinition set;
    private final NativeTypeDefinition heapOptionalHolder;
    private final NativeTypeDefinition anyHolder;
    private final NativeTypeDefinition uniquePtr;
    private final NativeTypeDefinition bitBuffer;
    private final NativeTypeDefinition packingContextNode;
    private final NativeTypeDefinition stringArrayTraits;
    private final NativeTypeDefinition bitBufferArrayTraits;
    private final NativeTypeDefinition reflectableFactory;
    private final NativeTypeDefinition reflectablePtr;
    private final NativeTypeDefinition reflectableConstPtr;
    private final NativeTypeDefinition service;
    private final NativeTypeDefinition serviceClient;
    private final NativeTypeDefinition serviceDataPtr;
    private final NativeTypeDefinition serviceData;

    public static final AllocatorDefinition PROPAGATING_POLYMORPHIC_ALLOCATOR = new AllocatorDefinition(
            "::zserio::pmr::PropagatingPolymorphicAllocator", "zserio/pmr/PolymorphicAllocator.h", "");
    public static final AllocatorDefinition STD_ALLOCATOR = new AllocatorDefinition(
            "::std::allocator", "memory");

    private static final PackageName STD_PACKAGE_NAME = new PackageName.Builder().addId("std").get();
    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
    private static final PackageName ZSERIO_PMR_PACKAGE_NAME =
            new PackageName.Builder().addId("zserio").addId("pmr").get();
}
