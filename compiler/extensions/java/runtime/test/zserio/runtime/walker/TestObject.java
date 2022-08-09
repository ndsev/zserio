package zserio.runtime.walker;

public class TestObject
{
    public static DummyObject createDummyObject()
    {
        return createDummyObject(13, true);
    }

    public static DummyObject createDummyObject(long identifier, boolean createNested)
    {
        final DummyUnion[] unionArray = new DummyUnion[] {new DummyUnion(), new DummyUnion(), new DummyUnion()};
        unionArray[0].setText("1");
        unionArray[1].setValue(2);
        unionArray[2].setNestedArray(new DummyNested[] {new DummyNested("nestedArray")});
        if (createNested)
        {
            return new DummyObject(identifier, new DummyNested("nested"), "test", unionArray, null);
        }
        else
        {
            return new DummyObject(identifier, null, "test", unionArray, null);
        }
    }

    public static class DummyNested implements zserio.runtime.io.InitializeOffsetsWriter, zserio.runtime.SizeOf
    {
        public DummyNested()
        {
        }

        public DummyNested(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            read(in);
        }

        public DummyNested(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            read(contextNode, in);
        }

        public DummyNested(
                java.lang.String text_)
        {
            setText(text_);
        }

        public static zserio.runtime.typeinfo.TypeInfo typeInfo()
        {
            final java.lang.String templateName = "";
            final java.util.List<zserio.runtime.typeinfo.TypeInfo> templateArguments =
                    new java.util.ArrayList<zserio.runtime.typeinfo.TypeInfo>();
            final java.util.List<zserio.runtime.typeinfo.FieldInfo> fieldList =
                    java.util.Arrays.asList(
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "text", // schemaName
                                    "getText", // getterName
                                    "setText", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getString(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            )
                    );
            final java.util.List<zserio.runtime.typeinfo.ParameterInfo> parameterList =
                    new java.util.ArrayList<zserio.runtime.typeinfo.ParameterInfo>();
            final java.util.List<zserio.runtime.typeinfo.FunctionInfo> functionList =
                    new java.util.ArrayList<zserio.runtime.typeinfo.FunctionInfo>();

            return new zserio.runtime.typeinfo.TypeInfo.StructTypeInfo(
                    "DummyNested", DummyNested.class, templateName, templateArguments,
                    fieldList, parameterList, functionList
            );
        }

        public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.createChild();
        }

        @Override
        public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
        }

        @Override
        public int bitSizeOf()
        {
            return bitSizeOf(0);
        }

        @Override
        public int bitSizeOf(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);

            return (int)(endBitPosition - bitPosition);
        }

        @Override
        public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);

            return (int)(endBitPosition - bitPosition);
        }

        public java.lang.String getText()
        {
            return text_;
        }

        public void setText(java.lang.String text_)
        {
            this.text_ = text_;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            if (obj instanceof DummyNested)
            {
                final DummyNested that = (DummyNested)obj;

                return
                        ((text_ == null) ? that.text_ == null : text_.equals(that.text_));
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int result = zserio.runtime.Util.HASH_SEED;

            result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                    ((text_ == null) ? 0 : text_.hashCode());

            return result;
        }

        public void read(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            text_ = in.readString();
        }

        public void read(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            text_ = in.readString();
        }

        @Override
        public long initializeOffsets(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);

            return endBitPosition;
        }

        @Override
        public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);

            return endBitPosition;
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out)
                throws java.io.IOException
        {
            write(out, true);
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
                throws java.io.IOException
        {
            out.writeString(text_);
        }

        @Override
        public void write(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            out.writeString(text_);
        }

        private java.lang.String text_;
    }

    public static class DummyUnion implements zserio.runtime.io.InitializeOffsetsWriter, zserio.runtime.SizeOf
    {
        public DummyUnion()
        {
        }

        public DummyUnion(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            read(in);
        }

        public DummyUnion(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            read(contextNode, in);
        }

        public static zserio.runtime.typeinfo.TypeInfo typeInfo()
        {
            final java.lang.String templateName = "";
            final java.util.List<zserio.runtime.typeinfo.TypeInfo> templateArguments =
                    new java.util.ArrayList<zserio.runtime.typeinfo.TypeInfo>();
            final java.util.List<zserio.runtime.typeinfo.FieldInfo> fieldList =
                    java.util.Arrays.asList(
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "value", // schemaName
                                    "getValue", // getterName
                                    "setValue", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getUInt32(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "text", // schemaName
                                    "getText", // getterName
                                    "setText", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getString(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "nestedArray", // schemaName
                                    "getNestedArray", // getterName
                                    "setNestedArray", // setterName
                                    DummyNested.typeInfo(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    true, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            )
                    );
            final java.util.List<zserio.runtime.typeinfo.ParameterInfo> parameterList =
                    new java.util.ArrayList<zserio.runtime.typeinfo.ParameterInfo>();
            final java.util.List<zserio.runtime.typeinfo.FunctionInfo> functionList =
                    new java.util.ArrayList<zserio.runtime.typeinfo.FunctionInfo>();

            return new zserio.runtime.typeinfo.TypeInfo.UnionTypeInfo(
                    "DummyUnion", DummyUnion.class, templateName, templateArguments,
                    fieldList, parameterList, functionList
            );
        }

        public int choiceTag()
        {
            return choiceTag;
        }

        public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.createChild().createContext();

            contextNode.createChild().createContext();
            contextNode.createChild();
            contextNode.createChild();
        }

        @Override
        public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.getChildren().get(0).getContext().init(
                    new zserio.runtime.array.ArrayTraits.VarSizeArrayTraits(),
                    new zserio.runtime.array.ArrayElement.IntArrayElement(choiceTag));

            switch (choiceTag)
            {
            case CHOICE_value:
                contextNode.getChildren().get(1).getContext().init(
                        new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                        new zserio.runtime.array.ArrayElement.LongArrayElement(getValue()));
                break;
            case CHOICE_text:
                break;
            case CHOICE_nestedArray:
                break;
            default:
                throw new zserio.runtime.ZserioError("No match in union DummyUnion!");
            }
        }

        @Override
        public int bitSizeOf()
        {
            return bitSizeOf(0);
        }

        @Override
        public int bitSizeOf(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfVarSize(choiceTag);

            switch (choiceTag)
            {
            case CHOICE_value:
                endBitPosition += 32;
                break;
            case CHOICE_text:
                endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(getText());
                break;
            case CHOICE_nestedArray:
                endBitPosition += ((zserio.runtime.array.Array)objectChoice).bitSizeOf(endBitPosition);
                break;
            default:
                throw new zserio.runtime.ZserioError("No match in union DummyUnion!");
            }

            return (int)(endBitPosition - bitPosition);
        }

        @Override
        public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.VarSizeArrayTraits(),
                    new zserio.runtime.array.ArrayElement.IntArrayElement(choiceTag));

            switch (choiceTag)
            {
            case CHOICE_value:
                endBitPosition += contextNode.getChildren().get(1).getContext().bitSizeOf(
                        new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                        new zserio.runtime.array.ArrayElement.LongArrayElement(getValue()));
                break;
            case CHOICE_text:
                endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(getText());
                break;
            case CHOICE_nestedArray:
                endBitPosition += ((zserio.runtime.array.Array)objectChoice).bitSizeOfPacked(endBitPosition);
                break;
            default:
                throw new zserio.runtime.ZserioError("No match in union DummyUnion!");
            }

            return (int)(endBitPosition - bitPosition);
        }

        public long getValue()
        {
            return (java.lang.Long)objectChoice;
        }

        public void setValue(long value_)
        {
            choiceTag = CHOICE_value;
            objectChoice = value_;
        }

        public java.lang.String getText()
        {
            return (java.lang.String)objectChoice;
        }

        public void setText(java.lang.String text_)
        {
            choiceTag = CHOICE_text;
            objectChoice = text_;
        }

        public DummyNested[] getNestedArray()
        {
            return ((zserio.runtime.array.Array)objectChoice).getRawArray();
        }

        public void setNestedArray(DummyNested[] nestedArray_)
        {
            choiceTag = CHOICE_nestedArray;
            objectChoice = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyNested.class, nestedArray_),
                    new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyNested>(new ZserioElementFactory_nestedArray()),
                    zserio.runtime.array.ArrayType.AUTO);
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            if (obj instanceof DummyUnion)
            {
                final DummyUnion that = (DummyUnion)obj;

                return
                        choiceTag == that.choiceTag &&
                        (
                            (objectChoice == null && that.objectChoice == null) ||
                            (objectChoice != null && objectChoice.equals(that.objectChoice))
                        );
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int result = zserio.runtime.Util.HASH_SEED;

            result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + choiceTag;
            result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                    ((objectChoice == null) ? 0 : objectChoice.hashCode());

            return result;
        }

        public void read(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
        {
            choiceTag = in.readVarSize();

            switch (choiceTag)
            {
            case CHOICE_value:
                objectChoice = in.readUnsignedInt();
                break;
            case CHOICE_text:
                objectChoice = in.readString();
                break;
            case CHOICE_nestedArray:
                objectChoice = new zserio.runtime.array.Array(
                        new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyNested.class),
                        new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyNested>(new ZserioElementFactory_nestedArray()),
                        zserio.runtime.array.ArrayType.AUTO);
                ((zserio.runtime.array.Array)objectChoice).read(in);
                break;
            default:
                throw new zserio.runtime.ZserioError("No match in union DummyUnion!");
            }
        }

        public void read(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            choiceTag = ((zserio.runtime.array.ArrayElement.IntArrayElement)
                    contextNode.getChildren().get(0).getContext().read(
                            new zserio.runtime.array.ArrayTraits.VarSizeArrayTraits(), in)).get();

            switch (choiceTag)
            {
            case CHOICE_value:
                objectChoice = ((zserio.runtime.array.ArrayElement.LongArrayElement)
                        contextNode.getChildren().get(1).getContext().read(
                                new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)), in)).get();
                break;
            case CHOICE_text:
                objectChoice = in.readString();
                break;
            case CHOICE_nestedArray:
                objectChoice = new zserio.runtime.array.Array(
                        new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyNested.class),
                        new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyNested>(new ZserioElementFactory_nestedArray()),
                        zserio.runtime.array.ArrayType.AUTO);
                ((zserio.runtime.array.Array)objectChoice).readPacked(in);
                break;
            default:
                throw new zserio.runtime.ZserioError("No match in union DummyUnion!");
            }
        }

        @Override
        public long initializeOffsets(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfVarSize(choiceTag);

            switch (choiceTag)
            {
            case CHOICE_value:
                endBitPosition += 32;
                break;
            case CHOICE_text:
                endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(getText());
                break;
            case CHOICE_nestedArray:
                endBitPosition = ((zserio.runtime.array.Array)objectChoice).initializeOffsets(endBitPosition);
                break;
            default:
                throw new zserio.runtime.ZserioError("No match in union DummyUnion!");
            }

            return endBitPosition;
        }

        @Override
        public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.VarSizeArrayTraits(),
                    new zserio.runtime.array.ArrayElement.IntArrayElement(choiceTag));

            switch (choiceTag)
            {
            case CHOICE_value:
                endBitPosition += contextNode.getChildren().get(1).getContext().bitSizeOf(
                        new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                        new zserio.runtime.array.ArrayElement.LongArrayElement(getValue()));
                break;
            case CHOICE_text:
                endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(getText());
                break;
            case CHOICE_nestedArray:
                endBitPosition = ((zserio.runtime.array.Array)objectChoice).initializeOffsetsPacked(endBitPosition);
                break;
            default:
                throw new zserio.runtime.ZserioError("No match in union DummyUnion!");
            }

            return endBitPosition;
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out)
                throws java.io.IOException
        {
            write(out, true);
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
                throws java.io.IOException
        {
            out.writeVarSize(choiceTag);

            switch (choiceTag)
            {
            case CHOICE_value:
                out.writeUnsignedInt(getValue());
                break;
            case CHOICE_text:
                out.writeString(getText());
                break;
            case CHOICE_nestedArray:
                ((zserio.runtime.array.Array)objectChoice).write(out);
                break;
            default:
                throw new zserio.runtime.ZserioError("No match in union DummyUnion!");
            };
        }

        @Override
        public void write(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            contextNode.getChildren().get(0).getContext().write(
                    new zserio.runtime.array.ArrayTraits.VarSizeArrayTraits(), out,
                    new zserio.runtime.array.ArrayElement.IntArrayElement(choiceTag));

            switch (choiceTag)
            {
            case CHOICE_value:
                contextNode.getChildren().get(1).getContext().write(
                        new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)), out,
                        new zserio.runtime.array.ArrayElement.LongArrayElement(getValue()));
                break;
            case CHOICE_text:
                out.writeString(getText());
                break;
            case CHOICE_nestedArray:
                ((zserio.runtime.array.Array)objectChoice).writePacked(out);
                break;
            default:
                throw new zserio.runtime.ZserioError("No match in union DummyUnion!");
            };
        }

        public static final int CHOICE_value = 0;
        public static final int CHOICE_text = 1;
        public static final int CHOICE_nestedArray = 2;
        public static final int UNDEFINED_CHOICE = -1;

        private static final class ZserioElementFactory_nestedArray implements zserio.runtime.array.ElementFactory<DummyNested>
        {
            @Override
            public DummyNested create(zserio.runtime.io.BitStreamReader in, int index)
                    throws java.io.IOException
            {
                return new DummyNested(in);
            }

            @Override
            public void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
            {
                DummyNested.createPackingContext(contextNode);
            }

            @Override
            public DummyNested create(zserio.runtime.array.PackingContextNode contextNode,
                    zserio.runtime.io.BitStreamReader in, int index) throws java.io.IOException
            {
                return new DummyNested(contextNode, in);
            }
        }

        private java.lang.Object objectChoice;
        private int choiceTag = UNDEFINED_CHOICE;
    }

    public static class DummyObject implements zserio.runtime.io.InitializeOffsetsWriter, zserio.runtime.SizeOf
    {
        public DummyObject()
        {
        }

        public DummyObject(java.io.File file)
                throws java.io.IOException
        {
            try (final zserio.runtime.io.FileBitStreamReader in = new zserio.runtime.io.FileBitStreamReader(file))
            {
                read(in);
            }
        }

        public DummyObject(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            read(in);
        }

        public DummyObject(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            read(contextNode, in);
        }

        public DummyObject(
                long identifier_,
                DummyNested nested_,
                java.lang.String text_,
                DummyUnion[] unionArray_,
                DummyUnion[] optionalUnionArray_)
        {
            setIdentifier(identifier_);
            setNested(nested_);
            setText(text_);
            setUnionArray(unionArray_);
            setOptionalUnionArray(optionalUnionArray_);
        }

        public static zserio.runtime.typeinfo.TypeInfo typeInfo()
        {
            final java.lang.String templateName = "";
            final java.util.List<zserio.runtime.typeinfo.TypeInfo> templateArguments =
                    new java.util.ArrayList<zserio.runtime.typeinfo.TypeInfo>();
            final java.util.List<zserio.runtime.typeinfo.FieldInfo> fieldList =
                    java.util.Arrays.asList(
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "identifier", // schemaName
                                    "getIdentifier", // getterName
                                    "setIdentifier", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getUInt32(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "nested", // schemaName
                                    "getNested", // getterName
                                    "setNested", // setterName
                                    DummyNested.typeInfo(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    true, // isOptional
                                    obj -> ((DummyObject)obj).getIdentifier() != 0, // optionalCondition
                                    "isNestedUsed", // isUsedIndicatorName
                                    "isNestedSet", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "text", // schemaName
                                    "getText", // getterName
                                    "setText", // setterName
                                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getString(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    false, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "unionArray", // schemaName
                                    "getUnionArray", // getterName
                                    "setUnionArray", // setterName
                                    DummyUnion.typeInfo(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    false, // isOptional
                                    null, // optionalCondition
                                    "", // isUsedIndicatorName
                                    "", // isSetIndicatorName
                                    null, // constraint
                                    true, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            ),
                            new zserio.runtime.typeinfo.FieldInfo(
                                    "optionalUnionArray", // schemaName
                                    "getOptionalUnionArray", // getterName
                                    "setOptionalUnionArray", // setterName
                                    DummyUnion.typeInfo(), // typeInfo
                                    new java.util.ArrayList<java.util.function.BiFunction<Object, Integer, Object>>(), // typeArguments
                                    null, // alignment
                                    null, // offset
                                    null, // initializer
                                    true, // isOptional
                                    null, // optionalCondition
                                    "isOptionalUnionArrayUsed", // isUsedIndicatorName
                                    "isOptionalUnionArraySet", // isSetIndicatorName
                                    null, // constraint
                                    true, // isArray
                                    null, // arrayLength
                                    false, // isPacked
                                    false // isImplicit
                            )
                    );
            final java.util.List<zserio.runtime.typeinfo.ParameterInfo> parameterList =
                    new java.util.ArrayList<zserio.runtime.typeinfo.ParameterInfo>();
            final java.util.List<zserio.runtime.typeinfo.FunctionInfo> functionList =
                    new java.util.ArrayList<zserio.runtime.typeinfo.FunctionInfo>();

            return new zserio.runtime.typeinfo.TypeInfo.StructTypeInfo(
                    "DummyObject", DummyObject.class, templateName, templateArguments,
                    fieldList, parameterList, functionList
            );
        }

        public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.createChild().createContext();
            DummyNested.createPackingContext(contextNode.createChild());
            contextNode.createChild();
            contextNode.createChild();
            contextNode.createChild();
        }

        @Override
        public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.getChildren().get(0).getContext().init(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(identifier_));
            if (isNestedUsed())
            {
                nested_.initPackingContext(contextNode.getChildren().get(1));
            }
        }

        @Override
        public int bitSizeOf()
        {
            return bitSizeOf(0);
        }

        @Override
        public int bitSizeOf(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += 32;
            if (isNestedUsed())
            {
                endBitPosition += nested_.bitSizeOf(endBitPosition);
            }
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition += unionArray_.bitSizeOf(endBitPosition);
            endBitPosition += 1;
            if (isOptionalUnionArrayUsed())
            {
                endBitPosition += optionalUnionArray_.bitSizeOf(endBitPosition);
            }

            return (int)(endBitPosition - bitPosition);
        }

        @Override
        public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(identifier_));
            if (isNestedUsed())
            {
                endBitPosition += nested_.bitSizeOf(contextNode.getChildren().get(1),
                        endBitPosition);
            }
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition += unionArray_.bitSizeOfPacked(endBitPosition);
            endBitPosition += 1;
            if (isOptionalUnionArrayUsed())
            {
                endBitPosition += optionalUnionArray_.bitSizeOfPacked(endBitPosition);
            }

            return (int)(endBitPosition - bitPosition);
        }

        public long getIdentifier()
        {
            return identifier_;
        }

        public void setIdentifier(long identifier_)
        {
            this.identifier_ = identifier_;
        }

        public DummyNested getNested()
        {
            return nested_;
        }

        public void setNested(DummyNested nested_)
        {
            this.nested_ = nested_;
        }

        public boolean isNestedUsed()
        {
            return (getIdentifier() != 0);
        }

        public boolean isNestedSet()
        {
            return (nested_ != null);
        }

        public java.lang.String getText()
        {
            return text_;
        }

        public void setText(java.lang.String text_)
        {
            this.text_ = text_;
        }

        public DummyUnion[] getUnionArray()
        {
            return unionArray_.getRawArray();
        }

        public void setUnionArray(DummyUnion[] unionArray_)
        {
            this.unionArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyUnion.class, unionArray_),
                    new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyUnion>(new ZserioElementFactory_unionArray()),
                    zserio.runtime.array.ArrayType.AUTO);
        }

        public DummyUnion[] getOptionalUnionArray()
        {
            return (optionalUnionArray_ == null) ? null : optionalUnionArray_.getRawArray();
        }

        public void setOptionalUnionArray(DummyUnion[] optionalUnionArray_)
        {
            if (optionalUnionArray_ == null)
            {
                this.optionalUnionArray_ = null;
            }
            else
            {
                this.optionalUnionArray_ = new zserio.runtime.array.Array(
                        new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyUnion.class, optionalUnionArray_),
                        new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyUnion>(new ZserioElementFactory_optionalUnionArray()),
                        zserio.runtime.array.ArrayType.AUTO);
            }
        }

        public boolean isOptionalUnionArrayUsed()
        {
            return isOptionalUnionArraySet();
        }

        public boolean isOptionalUnionArraySet()
        {
            return (optionalUnionArray_ != null);
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            if (obj instanceof DummyObject)
            {
                final DummyObject that = (DummyObject)obj;

                return
                        identifier_ == that.identifier_ &&
                        ((!isNestedUsed()) ? !that.isNestedUsed() :
                            ((nested_ == null) ? that.nested_ == null : nested_.equals(that.nested_))) &&
                        ((text_ == null) ? that.text_ == null : text_.equals(that.text_)) &&
                        ((unionArray_ == null) ? that.unionArray_ == null : unionArray_.equals(that.unionArray_)) &&
                        ((!isOptionalUnionArrayUsed()) ? !that.isOptionalUnionArrayUsed() :
                            ((optionalUnionArray_ == null) ? that.optionalUnionArray_ == null : optionalUnionArray_.equals(that.optionalUnionArray_)));
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int result = zserio.runtime.Util.HASH_SEED;

            result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + (int)(identifier_ ^ (identifier_ >>> 32));
            if (isNestedUsed())
                result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                        ((nested_ == null) ? 0 : nested_.hashCode());
            result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                    ((text_ == null) ? 0 : text_.hashCode());
            result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                    ((unionArray_ == null) ? 0 : unionArray_.hashCode());
            if (isOptionalUnionArrayUsed())
                result = zserio.runtime.Util.HASH_PRIME_NUMBER * result +
                        ((optionalUnionArray_ == null) ? 0 : optionalUnionArray_.hashCode());

            return result;
        }

        public void read(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            identifier_ = in.readUnsignedInt();

            if (getIdentifier() != 0)
            {
                nested_ = new DummyNested(in);
            }

            text_ = in.readString();

            unionArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyUnion.class),
                    new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyUnion>(new ZserioElementFactory_unionArray()),
                    zserio.runtime.array.ArrayType.AUTO);
            unionArray_.read(in);

            if (in.readBool())
            {
                optionalUnionArray_ = new zserio.runtime.array.Array(
                        new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyUnion.class),
                        new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyUnion>(new ZserioElementFactory_optionalUnionArray()),
                        zserio.runtime.array.ArrayType.AUTO);
                optionalUnionArray_.read(in);
            }
        }

        public void read(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            identifier_ = ((zserio.runtime.array.ArrayElement.LongArrayElement)
                    contextNode.getChildren().get(0).getContext().read(
                            new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)), in)).get();

            if (getIdentifier() != 0)
            {
                nested_ = new DummyNested(contextNode.getChildren().get(1), in);
            }

            text_ = in.readString();

            unionArray_ = new zserio.runtime.array.Array(
                    new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyUnion.class),
                    new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyUnion>(new ZserioElementFactory_unionArray()),
                    zserio.runtime.array.ArrayType.AUTO);
            unionArray_.readPacked(in);

            if (in.readBool())
            {
                optionalUnionArray_ = new zserio.runtime.array.Array(
                        new zserio.runtime.array.RawArray.ObjectRawArray<>(DummyUnion.class),
                        new zserio.runtime.array.ArrayTraits.WriteObjectArrayTraits<DummyUnion>(new ZserioElementFactory_optionalUnionArray()),
                        zserio.runtime.array.ArrayType.AUTO);
                optionalUnionArray_.readPacked(in);
            }
        }

        @Override
        public long initializeOffsets(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += 32;
            if (isNestedUsed())
            {
                endBitPosition = nested_.initializeOffsets(endBitPosition);
            }
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition = unionArray_.initializeOffsets(endBitPosition);
            endBitPosition += 1;
            if (isOptionalUnionArrayUsed())
            {
                endBitPosition = optionalUnionArray_.initializeOffsets(endBitPosition);
            }

            return endBitPosition;
        }

        @Override
        public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += contextNode.getChildren().get(0).getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(identifier_));
            if (isNestedUsed())
            {
                endBitPosition = nested_.initializeOffsets(contextNode.getChildren().get(1),
                        endBitPosition);
            }
            endBitPosition += zserio.runtime.BitSizeOfCalculator.getBitSizeOfString(text_);
            endBitPosition = unionArray_.initializeOffsetsPacked(endBitPosition);
            endBitPosition += 1;
            if (isOptionalUnionArrayUsed())
            {
                endBitPosition = optionalUnionArray_.initializeOffsetsPacked(endBitPosition);
            }

            return endBitPosition;
        }

        public void write(java.io.File file) throws java.io.IOException
        {
            try (final zserio.runtime.io.FileBitStreamWriter out = new zserio.runtime.io.FileBitStreamWriter(file))
            {
                write(out);
            }
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out)
                throws java.io.IOException
        {
            write(out, true);
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
                throws java.io.IOException
        {
            out.writeUnsignedInt(identifier_);

            if (isNestedUsed())
            {
                nested_.write(out, false);
            }

            out.writeString(text_);

            unionArray_.write(out);

            if (isOptionalUnionArrayUsed())
            {
                out.writeBool(true);
                optionalUnionArray_.write(out);
            }
            else
            {
                out.writeBool(false);
            }
        }

        @Override
        public void write(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            contextNode.getChildren().get(0).getContext().write(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits((32)), out,
                    new zserio.runtime.array.ArrayElement.LongArrayElement(identifier_));

            if (isNestedUsed())
            {
                nested_.write(contextNode.getChildren().get(1), out);
            }

            out.writeString(text_);

            unionArray_.writePacked(out);

            if (isOptionalUnionArrayUsed())
            {
                out.writeBool(true);
                optionalUnionArray_.writePacked(out);
            }
            else
            {
                out.writeBool(false);
            }
        }

        private static final class ZserioElementFactory_unionArray implements zserio.runtime.array.ElementFactory<DummyUnion>
        {
            @Override
            public DummyUnion create(zserio.runtime.io.BitStreamReader in, int index)
                    throws java.io.IOException
            {
                return new DummyUnion(in);
            }

            @Override
            public void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
            {
                DummyUnion.createPackingContext(contextNode);
            }

            @Override
            public DummyUnion create(zserio.runtime.array.PackingContextNode contextNode,
                    zserio.runtime.io.BitStreamReader in, int index) throws java.io.IOException
            {
                return new DummyUnion(contextNode, in);
            }
        }

        private static final class ZserioElementFactory_optionalUnionArray implements zserio.runtime.array.ElementFactory<DummyUnion>
        {
            @Override
            public DummyUnion create(zserio.runtime.io.BitStreamReader in, int index)
                    throws java.io.IOException
            {
                return new DummyUnion(in);
            }

            @Override
            public void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
            {
                DummyUnion.createPackingContext(contextNode);
            }

            @Override
            public DummyUnion create(zserio.runtime.array.PackingContextNode contextNode,
                    zserio.runtime.io.BitStreamReader in, int index) throws java.io.IOException
            {
                return new DummyUnion(contextNode, in);
            }
        }

        private long identifier_;
        private DummyNested nested_;
        private java.lang.String text_;
        private zserio.runtime.array.Array unionArray_;
        private zserio.runtime.array.Array optionalUnionArray_;
    }

    public static class DummyBitmask implements zserio.runtime.io.InitializeOffsetsWriter, zserio.runtime.SizeOf
    {
        public DummyBitmask()
        {
            this(0);
        }

        public DummyBitmask(long value)
        {
            if (value < 0L || value > 4294967295L)
            {
                throw new java.lang.IllegalArgumentException(
                        "Value for bitmask 'DummyBitmask' out of bounds: " + value + "!");
            }
            this.value = value;
        }

        public DummyBitmask(zserio.runtime.io.BitStreamReader in) throws java.io.IOException
        {
            value = in.readUnsignedInt();
        }

        public DummyBitmask(zserio.runtime.array.PackingContextNode contextNode, zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException
        {
            value = ((zserio.runtime.array.ArrayElement.LongArrayElement)
                    contextNode.getContext().read(
                            new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits(32), in)).get();
        }

        public static zserio.runtime.typeinfo.TypeInfo typeInfo()
        {
            return new zserio.runtime.typeinfo.TypeInfo.BitmaskTypeInfo(
                    "DummyBitmask", DummyBitmask.class,
                    zserio.runtime.typeinfo.TypeInfo.BuiltinTypeInfo.getUInt32(),
                    new java.util.ArrayList<java.util.function.Supplier<Object>>(),
                    java.util.Arrays.asList(
                            new zserio.runtime.typeinfo.ItemInfo("ZERO", () -> 1)
                    )
            );
        }

        public static void createPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.createContext();
        }

        @Override
        public void initPackingContext(zserio.runtime.array.PackingContextNode contextNode)
        {
            contextNode.getContext().init(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits(32),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value));
        }

        @Override
        public int bitSizeOf()
        {
            return bitSizeOf(0);
        }

        @Override
        public int bitSizeOf(long bitPosition)
        {
            return 32;
        }

        @Override
        public int bitSizeOf(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            return contextNode.getContext().bitSizeOf(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits(32),
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value));
        }

        @Override
        public long initializeOffsets(long bitPosition)
        {
            return bitPosition + bitSizeOf(bitPosition);
        }

        @Override
        public long initializeOffsets(zserio.runtime.array.PackingContextNode contextNode, long bitPosition)
        {
            return bitPosition + bitSizeOf(contextNode, bitPosition);
        }

        @Override
        public boolean equals(java.lang.Object other)
        {
            if (!(other instanceof DummyBitmask))
                return false;

            final DummyBitmask otherDummyBitmask = (DummyBitmask)other;
            return value == otherDummyBitmask.value;
        }

        @Override
        public int hashCode()
        {
            int result = zserio.runtime.Util.HASH_SEED;

            result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + (int)(value ^ (value >>> 32));

            return result;
        }

        @Override
        public java.lang.String toString()
        {
            final java.lang.StringBuilder builder = new java.lang.StringBuilder();

            if (this.and(DummyBitmask.Values.ZERO).equals(DummyBitmask.Values.ZERO))
                builder.append(builder.length() == 0 ? "ZERO" : " | ZERO");

            return java.lang.String.valueOf(value) + "[" + builder.toString() + "]";
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            write(out, false);
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
                throws java.io.IOException
        {
            out.writeUnsignedInt(value);
        }

        @Override
        public void write(zserio.runtime.array.PackingContextNode contextNode,
                zserio.runtime.io.BitStreamWriter out) throws java.io.IOException
        {
            contextNode.getContext().write(
                    new zserio.runtime.array.ArrayTraits.BitFieldLongArrayTraits(32), out,
                    new zserio.runtime.array.ArrayElement.LongArrayElement(value));
        }

        public long getValue()
        {
            return value;
        }

        public DummyBitmask or(DummyBitmask other)
        {
            return new DummyBitmask(value | other.value);
        }

        public DummyBitmask and(DummyBitmask other)
        {
            return new DummyBitmask(value & other.value);
        }

        public DummyBitmask xor(DummyBitmask other)
        {
            return new DummyBitmask(value ^ other.value);
        }

        public DummyBitmask not()
        {
            return new DummyBitmask(~value & 4294967295L);
        }

        public static final class Values
        {
            public static final DummyBitmask ZERO = new DummyBitmask(1L);
        }

        private long value;
    }
}
