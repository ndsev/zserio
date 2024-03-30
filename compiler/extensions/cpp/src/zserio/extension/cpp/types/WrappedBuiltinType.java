package zserio.extension.cpp.types;

import zserio.ast.PackageName;

/**
 * Native C++ built-in type mapping for compund fields.
 */
public class WrappedBuiltinType extends NativeBuiltinType implements CppNativeArrayableType
{
    public WrappedBuiltinType(String builtinTypeName, NativeArrayTraits arrayTraits)
    {
        super(getTypeName(builtinTypeName, arrayTraits.getName()), arrayTraits);
        addUserIncludeFile(TYPE_WRAPPERS_INCLUDE);
        //baseType = new NativeBuiltinType(builtinTypeName, arrayTraits);
    }
    
    /*public NativeBuiltinType getBaseType()
    {
    	return baseType;
    }*/
    
    public static String getTypeName(String name, String traitsName)
    {
    	if (name.equals("bool"))
    	{
    		return "::zserio::Boolean";
    	}
    	else if (name.equals("float"))
    	{
    		if (traitsName.contains("Float16"))
    			return "::zserio::Float<float, 16>";
    		else
    			return "::zserio::Float<float>";
    	}
    	else if (name.equals("double"))
    	{
    		return "::zserio::Float<double>";
    	}
    	return name;
    }
    
    private final static String TYPE_WRAPPERS_INCLUDE = "zserio/TypeWrappers.h";
    //private NativeBuiltinType baseType = null;
}
