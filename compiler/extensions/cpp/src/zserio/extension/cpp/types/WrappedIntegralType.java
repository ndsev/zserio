package zserio.extension.cpp.types;

import java.math.BigInteger;

import zserio.extension.common.ZserioExtensionException;

/**
 * Native C++ integral type mapping.
 */
public class WrappedIntegralType extends NativeIntegralType
{
    public WrappedIntegralType(int numBits, boolean isSigned, NativeArrayTraits arrayTraits)
    {
    	super(getTypeName(numBits, isSigned, arrayTraits.getName()), 
    			numBits, isSigned, arrayTraits);
        addUserIncludeFile(TYPE_WRAPPERS_INCLUDE);
        //baseType = new NativeIntegralType(numBits, isSigned, arrayTraits);
    }
    
        /*public NativeIntegralType getBaseType()
    {
    	return baseType;
    }*/
    
    private static String getTypeName(int numBits, boolean isSigned, String traitsName)
    {
    	int baseTypeBits;
    	if (numBits <= 8)
    		baseTypeBits = 8;
    	else if (numBits <= 16)
    		baseTypeBits = 16;
    	else if (numBits <= 32)
    		baseTypeBits = 32;
    	else
    		baseTypeBits = 64;
    		
        StringBuilder buffer = new StringBuilder();

        buffer.append("::zserio::Integer<");
        
        if (!isSigned)
            buffer.append('u');
        buffer.append("int");
        buffer.append(baseTypeBits);
        buffer.append("_t");

        if (traitsName.equals("VarIntArrayTraits"))
        {
        	buffer.append(", ::zserio::VAR");
        }
        else if (traitsName.equals("VarSizeArrayTraits"))
        {
        	buffer.append(", ::zserio::VARSIZE");
        }
        else if (traitsName.contains("VarInt"))
        {
        	buffer.append(", ::zserio::VAR");
        	buffer.append(baseTypeBits);
        }
        else if (numBits != 8 && numBits != 16 && numBits != 32 && numBits != 64)
        {
        	buffer.append(", ");
        	buffer.append(numBits);
        }
        
        buffer.append(">");
        
        return buffer.toString();
    }

    private final static String TYPE_WRAPPERS_INCLUDE = "zserio/TypeWrappers.h";
    //private NativeIntegralType baseType = null;
}
