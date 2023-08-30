package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import zserio.ast.PackageName;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeRuntimeAllocType;

/**
 * Base class for all C++ template data for FreeMarker.
 */
public abstract class CppTemplateData implements IncludeCollector
{
    public CppTemplateData(TemplateDataContext context)
    {
        generatorDescription = context.getGeneratorDescription();

        withWriterCode = context.getWithWriterCode();
        withValidationCode = context.getWithValidationCode();
        withTypeInfoCode = context.getWithTypeInfoCode();
        withReflectionCode = context.getWithReflectionCode();
        withRangeCheckCode = context.getWithRangeCheckCode();
        withCodeComments = context.getWithCodeComments();

        headerSystemIncludes = new TreeSet<String>();
        headerUserIncludes = new TreeSet<String>();
        cppUserIncludes = new TreeSet<String>();
        cppSystemIncludes = new TreeSet<String>();

        types = new TypesTemplateData(context.getTypesContext(), context.getCppNativeMapper());
    }

    public String getGeneratorDescription()
    {
        return generatorDescription;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public boolean getWithTypeInfoCode()
    {
        return withTypeInfoCode;
    }

    public boolean getWithReflectionCode()
    {
        return withReflectionCode;
    }

    public boolean getWithRangeCheckCode()
    {
        return withRangeCheckCode;
    }

    public boolean getWithCodeComments()
    {
        return withCodeComments;
    }

    public Iterable<String> getHeaderSystemIncludes()
    {
        return headerSystemIncludes;
    }

    public Iterable<String> getHeaderUserIncludes()
    {
        return headerUserIncludes;
    }

    public Iterable<String> getCppUserIncludes()
    {
        return cppUserIncludes;
    }

    public Iterable<String> getCppSystemIncludes()
    {
        return cppSystemIncludes;
    }

    @Override
    public void addHeaderIncludesForType(CppNativeType nativeType)
    {
        addHeaderSystemIncludes(nativeType.getSystemIncludeFiles());
        addHeaderUserIncludes(nativeType.getUserIncludeFiles());
    }

    @Override
    public void addHeaderSystemIncludes(Collection<String> systemIncludes)
    {
        headerSystemIncludes.addAll(systemIncludes);
    }

    @Override
    public void addHeaderUserIncludes(Collection<String> userIncludes)
    {
        headerUserIncludes.addAll(userIncludes);
    }

    @Override
    public void addCppIncludesForType(CppNativeType nativeType)
    {
        cppSystemIncludes.addAll(nativeType.getSystemIncludeFiles());
        cppUserIncludes.addAll(nativeType.getUserIncludeFiles());
    }

    @Override
    public void addCppSystemIncludes(Collection<String> systemIncludes)
    {
        cppSystemIncludes.addAll(systemIncludes);
    }

    @Override
    public void addCppUserIncludes(Collection<String> userIncludes)
    {
        cppUserIncludes.addAll(userIncludes);
    }

    public TypesTemplateData getTypes()
    {
        return types;
    }

    public static class PackageTemplateData
    {
        public PackageTemplateData(CppNativeType type)
        {
            this(type.getPackageName());
        }

        public PackageTemplateData(PackageName packageName)
        {
            this.packageName = packageName;
        }

        public String getName()
        {
            return CppFullNameFormatter.getFullName(packageName);
        }

        public Iterable<String> getPath()
        {
            return packageName.getIdList();
        }

        private final PackageName packageName;
    }

    public static class TypesTemplateData
    {
        public TypesTemplateData(TypesContext typesContext, CppNativeMapper nativeMapper)
        {
            allocator = new AllocatorTemplateData(typesContext);
            anyHolder = new TypeTemplateData(nativeMapper.getAnyHolderType());
            final NativeRuntimeAllocType uniquePtrType = nativeMapper.getUniquePtrType();
            uniquePtr = new TemplatedTypeTemplateData(uniquePtrType, uniquePtrType.needsAllocatorArgument());
            final NativeRuntimeAllocType heapOptionalHolderType = nativeMapper.getHeapOptionalHolderType();
            heapOptionalHolder = new TemplatedTypeTemplateData(heapOptionalHolderType,
                    heapOptionalHolderType.needsAllocatorArgument());
            inplaceOptionalHolder = new TypeTemplateData(nativeMapper.getInplaceOptionalHolderType());
            string = new TypeTemplateData(nativeMapper.getStringType());
            final NativeRuntimeAllocType vectorType = nativeMapper.getVectorType();
            vector = new TemplatedTypeTemplateData(vectorType, vectorType.needsAllocatorArgument());
            final NativeRuntimeAllocType mapType = nativeMapper.getMapType();
            map = new TemplatedTypeTemplateData(mapType, mapType.needsAllocatorArgument());
            final NativeRuntimeAllocType setType = nativeMapper.getSetType();
            set = new TemplatedTypeTemplateData(setType, setType.needsAllocatorArgument());
            bitBuffer = new TypeTemplateData(nativeMapper.getBitBufferType());
            typeInfo = new TypeTemplateData(nativeMapper.getTypeInfoType());
            reflectableFactory = new TypeTemplateData(nativeMapper.getReflectableFactoryType());
            reflectablePtr = new TypeTemplateData(nativeMapper.getReflectablePtrType());
            reflectableConstPtr = new TypeTemplateData(nativeMapper.getReflectableConstPtrType());
            service = new TypeTemplateData(nativeMapper.getServiceType());
            serviceClient = new TypeTemplateData(nativeMapper.getServiceClientType());
            serviceDataPtr = new TypeTemplateData(nativeMapper.getServiceDataPtrType());
            reflectableServiceData = new TypeTemplateData(nativeMapper.getReflectableServiceDataType());
            objectServiceData = new TypeTemplateData(nativeMapper.getObjectServiceDataType());
            rawServiceDataHolder = new TypeTemplateData(nativeMapper.getRawServiceDataHolderType());
            rawServiceDataView = new TypeTemplateData(nativeMapper.getRawServiceDataViewType());
        }

        public AllocatorTemplateData getAllocator()
        {
            return allocator;
        }

        public TypeTemplateData getAnyHolder()
        {
            return anyHolder;
        }

        public TemplatedTypeTemplateData getUniquePtr()
        {
            return uniquePtr;
        }

        public TemplatedTypeTemplateData getHeapOptionalHolder()
        {
            return heapOptionalHolder;
        }

        public TypeTemplateData getInplaceOptionalHolder()
        {
            return inplaceOptionalHolder;
        }

        public TypeTemplateData getString()
        {
            return string;
        }

        public TemplatedTypeTemplateData getVector()
        {
            return vector;
        }

        public TemplatedTypeTemplateData getMap()
        {
            return map;
        }

        public TemplatedTypeTemplateData getSet()
        {
            return set;
        }

        public TypeTemplateData getBitBuffer()
        {
            return bitBuffer;
        }

        public TypeTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public TypeTemplateData getReflectableFactory()
        {
            return reflectableFactory;
        }

        public TypeTemplateData getReflectablePtr()
        {
            return reflectablePtr;
        }

        public TypeTemplateData getReflectableConstPtr()
        {
            return reflectableConstPtr;
        }

        public TypeTemplateData getService()
        {
            return service;
        }

        public TypeTemplateData getServiceClient()
        {
            return serviceClient;
        }

        public TypeTemplateData getServiceDataPtr()
        {
            return serviceDataPtr;
        }

        public TypeTemplateData getReflectableServiceData()
        {
            return reflectableServiceData;
        }

        public TypeTemplateData getObjectServiceData()
        {
            return objectServiceData;
        }

        public TypeTemplateData getRawServiceDataHolder()
        {
            return rawServiceDataHolder;
        }

        public TypeTemplateData getRawServiceDataView()
        {
            return rawServiceDataView;
        }

        public static class AllocatorTemplateData
        {
            public AllocatorTemplateData(TypesContext typesContext)
            {
                name = typesContext.getAllocatorDefinition().getAllocatorType();
                defaultType = typesContext.getAllocatorDefinition().getAllocatorDefaultType();
                systemIncludes.add(typesContext.getAllocatorDefinition().getAllocatorSystemInclude());
            }

            public String getName()
            {
                return name;
            }

            public String getDefaultType()
            {
                return defaultType;
            }

            public String getDefault()
            {
                return name + "<" + defaultType + ">";
            }

            public Iterable<String> getSystemIncludes()
            {
                return systemIncludes;
            }

            public Iterable<String> getUserIncludes()
            {
                return userIncludes;
            }

            private final String name;
            private final String defaultType;
            private final List<String> systemIncludes = new ArrayList<String>();
            private final List<String> userIncludes = new ArrayList<String>();
        }

        public static class TypeTemplateData
        {
            public TypeTemplateData(CppNativeType type)
            {
                name = type.getFullName();
                systemIncludes = type.getSystemIncludeFiles();
                userIncludes = type.getUserIncludeFiles();
            }

            public String getName()
            {
                return name;
            }

            public Iterable<String> getSystemIncludes()
            {
                return systemIncludes;
            }

            public Iterable<String> getUserIncludes()
            {
                return userIncludes;
            }

            private final String name;
            private final Iterable<String> systemIncludes;
            private final Iterable<String> userIncludes;
        }

        public static class TemplatedTypeTemplateData extends TypeTemplateData
        {
            public TemplatedTypeTemplateData(CppNativeType type, boolean needsAllocatorArgument)
            {
                super(type);
                this.needsAllocatorArgument = needsAllocatorArgument;
            }

            public boolean getNeedsAllocatorArgument()
            {
                return needsAllocatorArgument;
            }

            private final boolean needsAllocatorArgument;
        }

        private final AllocatorTemplateData allocator;
        private final TypeTemplateData anyHolder;
        private final TemplatedTypeTemplateData uniquePtr;
        private final TemplatedTypeTemplateData heapOptionalHolder;
        private final TypeTemplateData inplaceOptionalHolder;
        private final TypeTemplateData string;
        private final TemplatedTypeTemplateData vector;
        private final TemplatedTypeTemplateData map;
        private final TemplatedTypeTemplateData set;
        private final TypeTemplateData bitBuffer;
        private final TypeTemplateData typeInfo;
        private final TypeTemplateData reflectableFactory;
        private final TypeTemplateData reflectablePtr;
        private final TypeTemplateData reflectableConstPtr;
        private final TypeTemplateData service;
        private final TypeTemplateData serviceClient;
        private final TypeTemplateData serviceDataPtr;
        private final TypeTemplateData reflectableServiceData;
        private final TypeTemplateData objectServiceData;
        private final TypeTemplateData rawServiceDataHolder;
        private final TypeTemplateData rawServiceDataView;
    }

    private final String generatorDescription;

    private final boolean withWriterCode;
    private final boolean withValidationCode;
    private final boolean withTypeInfoCode;
    private final boolean withReflectionCode;
    private final boolean withRangeCheckCode;
    private final boolean withCodeComments;

    private final TreeSet<String> headerSystemIncludes;
    private final TreeSet<String> headerUserIncludes;
    private final TreeSet<String> cppUserIncludes;
    private final TreeSet<String> cppSystemIncludes;
    private final TypesTemplateData types;
}
