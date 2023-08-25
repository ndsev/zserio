<#include "FileHeader.inc.ftl"/>
<#include "CompoundField.inc.ftl"/>
<#include "CompoundParameter.inc.ftl"/>
<#include "DocComment.inc.ftl">
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>

class ${name}:
<#if withCodeComments && docComments??>
<@doc_comments docComments, 1/>

</#if>
<#assign numExtendedFields=num_extended_fields(fieldList)>
<#function extended_field_index numFields numExtendedFields fieldIndex>
    <#return fieldIndex - (numFields - numExtendedFields)>
</#function>
<#assign constructorAnnotatedParamList><@compound_constructor_annotated_parameters compoundParametersData, 3/></#assign>
<#macro field_default_value field>
    <#if field.initializer??>
        ${field.initializer}<#t>
    <#elseif field.optional??>
        None<#t>
    <#else>
        <#if field.typeInfo.isBuiltin>
        ${field.typeInfo.typeFullName}()<#t>
        <#else>
        None<#t>
        </#if>
    </#if>
</#macro>
<#if constructorAnnotatedParamList?has_content || fieldList?has_content>
    def __init__(
            self<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if><#rt>
    <#list fieldList as field>
            <#lt>,
            <#nt><@field_argument_name field/>: <@field_annotation_argument_type_name field, name/> = <#rt>
            <@field_default_value field/>
    </#list>
            <#lt>) -> None:
    <#if withCodeComments>
        """
        Fields constructor.

        <@compound_parameter_doc_comment compoundParametersData/>
        <#list fieldList as field>
        :param <@field_argument_name field/>: Value of the field :attr:`${field.name} <.${field.propertyName}>`.
        </#list>
        """

    </#if>
        <@compound_constructor_parameter_assignments compoundParametersData/>
    <#if (numExtendedFields > 0)>
        self._num_extended_fields = ${fieldList?size}
    </#if>
    <#list fieldList as field>
        <@compound_setter_field field, 2/>
    </#list>

</#if>
<#assign constructorParamList><@compound_constructor_parameters compoundParametersData/></#assign>
    @classmethod
    def from_reader(
            cls: typing.Type['${name}'],
            zserio_reader: zserio.BitStreamReader<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> '${name}':
<#if withCodeComments>
        """
        Returns new object instance constructed from bit stream reader.

        :param zserio_reader: Bit stream reader to use.
        <@compound_parameter_doc_comment compoundParametersData/>
        """

</#if>
        self = object.__new__(cls)
        <@compound_constructor_parameter_assignments compoundParametersData/>
<#if (numExtendedFields > 0)>
        self._num_extended_fields = 0
</#if>

        self.read(zserio_reader)

        return self
<#if isPackable>

    @classmethod
    def from_reader_packed(
            cls: typing.Type['${name}'],
            zserio_context: ${name}.ZserioPackingContext,
            zserio_reader: zserio.BitStreamReader<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> '${name}':
    <#if withCodeComments>
        """
        Returns new object instance constructed from bit stream reader.

        Called only internally if packed arrays are used.

        :param zserio_context: Context for packed arrays.
        :param zserio_reader: Bit stream reader to use.
        <@compound_parameter_doc_comment compoundParametersData/>
        """

    </#if>
        self = object.__new__(cls)
        <@compound_constructor_parameter_assignments compoundParametersData/>
    <#if (numExtendedFields > 0)>
        self._num_extended_fields = 0
    </#if>

        self.read_packed(zserio_context, zserio_reader)

        return self
</#if>
<#if withTypeInfoCode>

    @staticmethod
    def type_info() -> zserio.typeinfo.TypeInfo:
    <#if withCodeComments>
        """
        Gets static information about this Zserio type useful for generic introspection.

        :returns: Zserio type information.
        """

    </#if>
        field_list: typing.List[zserio.typeinfo.MemberInfo] = [
    <#list fieldList as field>
            <@member_info_field field field?has_next/>
    </#list>
        ]
    <#if compoundParametersData.list?has_content>
        parameter_list: typing.List[zserio.typeinfo.MemberInfo] = [
        <#list compoundParametersData.list as parameter>
            <@member_info_parameter parameter parameter?has_next/>
        </#list>
        ]
    </#if>
    <#if compoundFunctionsData.list?has_content>
        function_list: typing.List[zserio.typeinfo.MemberInfo] = [
        <#list compoundFunctionsData.list as function>
            <@member_info_function function function?has_next/>
        </#list>
        ]
    </#if>
        attribute_list = {
            zserio.typeinfo.TypeAttribute.FIELDS : field_list<#rt>
    <#if compoundParametersData.list?has_content>
            <#lt>,
            zserio.typeinfo.TypeAttribute.PARAMETERS : parameter_list<#rt>
    </#if>
    <#if compoundFunctionsData.list?has_content>
            <#lt>,
            zserio.typeinfo.TypeAttribute.FUNCTIONS : function_list<#rt>
    </#if>
    <#if templateInstantiation??>
            <#lt>,
            <@type_info_template_instantiation_attributes templateInstantiation/>
    </#if>

        }

        return zserio.typeinfo.TypeInfo("${schemaTypeFullName}", ${name}, attributes=attribute_list)
</#if>

<#macro structure_compare_fields fieldList indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
    <#list fieldList as field>
        <#if field.isExtended>
(not other.${field.isPresentIndicatorName}() if not self.${field.isPresentIndicatorName}() else <#rt>
(other.${field.isPresentIndicatorName}() and <@compound_compare_field field/>))<#rt>
        <#else>
<@compound_compare_field field/><#rt>
        </#if>
        <#if field?has_next>
 and
${I}<#rt>
        </#if>
    </#list>
</#macro>
    def __eq__(self, other: object) -> bool:
<#if compoundParametersData.list?has_content || fieldList?has_content>
    <#assign eqHasParenthesis = (compoundParametersData.list?size + fieldList?size) gt 1/>
        if isinstance(other, ${name}):
            return <#if eqHasParenthesis>(</#if><#rt>
    <#if compoundParametersData.list?has_content>
                    <#lt><@compound_compare_parameters compoundParametersData, 5/><#if fieldList?has_content> and<#elseif eqHasParenthesis>)</#if>
    </#if>
    <#if fieldList?has_content>
                    <#if compoundParametersData.list?has_content>                    </#if><#t>
                    <#lt><@structure_compare_fields fieldList, 5/><#if eqHasParenthesis>)</#if>
    </#if>

        return False
<#else>
        return isinstance(other, ${name})
</#if>

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
<#list fieldList as field>
        <#if field.isExtended>
        if self.${field.isPresentIndicatorName}():
            <@compound_hashcode_field field, 3/>
        <#else>
        <@compound_hashcode_field field, 2/>
        </#if>
</#list>

        return result
<#list compoundParametersData.list as parameter>

    @property
    def ${parameter.propertyName}(self) -> ${parameter.typeInfo.typeFullName}:
    <#if withCodeComments>
        """
        Gets the value of the parameter ${parameter.name}.

        <#if parameter.docComments??>
        **Description:**

        <@doc_comments_inner parameter.docComments, 2/>

        </#if>
        :returns: Value of the parameter ${parameter.name}.
        """

     </#if>
        <@compound_parameter_accessor parameter/>
</#list>
<#list fieldList as field>

    @property
    def ${field.propertyName}(self) -> <@field_annotation_argument_type_name field, name/>:
    <#if withCodeComments>
        """
        Gets the value of the field ${field.name}.

        <#if field.docComments??>
        **Description:**

        <@doc_comments_inner field.docComments, 2/>

        </#if>
        :returns: Value of the field ${field.name}.
        """

     </#if>
        <@compound_getter_field field/>
    <#if withWriterCode>

    @${field.propertyName}.setter
    def ${field.propertyName}(self, <#rt>
            <#lt><@field_argument_name field/>: <@field_annotation_argument_type_name field, name/>) -> None:
        <#if withCodeComments>
        """
        Sets the field ${field.name}.

            <#if field.docComments??>
        **Description:**

        <@doc_comments_inner field.docComments, 2/>

            </#if>
        :param <@field_argument_name field/>: Value of the field ${field.name} to set.
        """

        </#if>
        <#if field.isExtended>
        if not self.${field.isPresentIndicatorName}():
            self._num_extended_fields = ${numExtendedFields}
        </#if>
        <@compound_setter_field field, 2/>
    </#if>
    <#if field.isExtended>

    def ${field.isPresentIndicatorName}(self) -> bool:
        <#if withCodeComments>
        """
        Checks if the extended field ${field.name} is present.

        :returns: True if the extended field ${field.name} is present, otherwise False.
        """

        </#if>
        return self._num_extended_fields > ${extended_field_index(fieldList?size, numExtendedFields, field?index)}
    </#if>
    <#if field.optional??>

    def ${field.optional.isUsedIndicatorName}(self) -> bool:
        <#if withCodeComments>
        """
        Checks if the optional field ${field.name} is used during serialization and deserialization.

        :returns: True if the optional field ${field.name} is used, otherwise False.
        """

        </#if>
        return <#if field.optional.clause??>${field.optional.clause}<#else>self.${field.optional.isSetIndicatorName}()</#if>
        <#if withWriterCode>

    def ${field.optional.isSetIndicatorName}(self) -> bool:
            <#if withCodeComments>
        """
        Checks if the optional field ${field.name} is set.

        :returns: True if the optional field ${field.name} is set, otherwise False.
        """

            </#if>
        return not self.<@field_member_name field/> is None

    def ${field.optional.resetterName}(self) -> None:
            <#if withCodeComments>
        """
        Resets the optional field ${field.name}.
        """

            </#if>
        <#if field.isExtended>
        if not self.${field.isPresentIndicatorName}():
            self._num_extended_fields = ${numExtendedFields}
        </#if>
        self.<@field_member_name field/> = None
        </#if>
    </#if>
</#list>
<#list compoundFunctionsData.list as function>

    def ${function.functionName}(self) -> ${function.returnTypeInfo.typeFullName}:
    <#if withCodeComments>
        """
        Implementation of the function ${function.schemaName}.

        <#if function.docComments??>
        **Description:**

        <@doc_comments_inner function.docComments, 2/>

        </#if>
        :returns: Result of the function ${function.schemaName}.
        """

    </#if>
        return ${function.resultExpression}
</#list>
<#if isPackable>

    def init_packing_context(self, zserio_context: ${name}.ZserioPackingContext) -> None:
    <#if withCodeComments>
        """
        Initializes context for packed arrays.

        Called only internally if packed arrays are used.

        :param zserio_context: Context for packed arrays.
        """

    </#if>
    <#if uses_packing_context(fieldList)>
        <#list fieldList as field>
        <@compound_init_packing_context_field field, 2/>
        </#list>
    <#else>
        del zserio_context
    </#if>
</#if>

    def bitsizeof(self, bitposition: int = 0) -> int:
<#if withCodeComments>
        """
        Calculates size of the serialized object in bits.

        :param bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Number of bits which are needed to store serialized object.
        """

</#if>
<#if fieldList?has_content>
        end_bitposition = bitposition
    <#list fieldList as field>
        <@compound_bitsizeof_field field, 2/>
    </#list>

        return end_bitposition - bitposition
<#else>
        del bitposition

        return 0
</#if>
<#if isPackable>

    def bitsizeof_packed(self, zserio_context: ${name}.ZserioPackingContext, bitposition: int = 0) -> int:
    <#if withCodeComments>
        """
        Calculates size of the serialized object in bits for packed arrays.

        Called only internally if packed arrays are used.

        :param zserio_context: Context for packed arrays.
        :param bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Number of bits which are needed to store serialized object.
        """

    </#if>
    <#if fieldList?has_content>
        <#if !uses_packing_context(fieldList)>
        del zserio_context

        </#if>
        end_bitposition = bitposition
        <#list fieldList as field>
        <@compound_bitsizeof_field field, 2, true/>
        </#list>

        return end_bitposition - bitposition
    <#else>
        del zserio_context
        del bitposition

        return 0
    </#if>
</#if>
<#if withWriterCode>

    def initialize_offsets(self, bitposition: int = 0) -> int:
    <#if withCodeComments>
        """
        Initializes offsets in this Zserio object and in all its fields.

        This method sets offsets in this Zserio object and in all fields recursively.

        :param bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Bit stream position calculated from zero updated to the first byte after serialized object.
        """

    </#if>
    <#if fieldList?has_content>
        end_bitposition = bitposition
        <#list fieldList as field>
            <@compound_initialize_offsets_field field, 2/>
        </#list>

        return end_bitposition
    <#else>
        return bitposition
    </#if>
    <#if isPackable>

    def initialize_offsets_packed(self, zserio_context: ${name}.ZserioPackingContext, bitposition: int) -> int:
        <#if withCodeComments>
        """
        Initializes offsets in this Zserio type and in all its fields for packed arrays.

        This method sets offsets in this Zserio type and in all fields recursively.
        Called only internally if packed arrays are used.

        :param zserio_context: Context for packed arrays.
        :param bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Bit stream position calculated from zero updated to the first byte after serialized object.
        """

        </#if>
        <#if fieldList?has_content>
            <#if !uses_packing_context(fieldList)>
        del zserio_context

            </#if>
        end_bitposition = bitposition
            <#list fieldList as field>
        <@compound_initialize_offsets_field field, 2, true/>
            </#list>

        return end_bitposition
        <#else>
        del zserio_context
        return bitposition
        </#if>
    </#if>
</#if>

<#assign needsReadNewLines=false/>
<#list fieldList as field>
    <#if has_field_any_read_check_code(field, name, 2)>
        <#assign needsReadNewLines=true/>
        <#break>
    </#if>
</#list>
    def read(self, zserio_reader: zserio.BitStreamReader) -> None:
<#if withCodeComments>
        """
        Deserializes this Zserio object from the bit stream.

        :param zserio_reader: Bit stream reader to use.
        """

</#if>
<#if fieldList?has_content>
    <#list fieldList as field>
        <#if field.isExtended>
        if zserio.bitposition.alignto(8, zserio_reader.bitposition) >= zserio_reader.buffer_bitsize:
            <@field_argument_name field/> = <@field_default_value field/>
            <@compound_setter_field field, 3/>
        else:
            self._num_extended_fields += 1
            zserio_reader.alignto(8)

            <@compound_read_field field, name, 3/>
            <#if field?has_next>

            </#if>
        <#else>
        <@compound_read_field field, name, 2/>
            <#if field?has_next && needsReadNewLines>

            </#if>
        </#if>
    </#list>
<#else>
        del zserio_reader
</#if>
<#if isPackable>

    def read_packed(self, zserio_context: ${name}.ZserioPackingContext, zserio_reader: zserio.BitStreamReader) -> None:
    <#if withCodeComments>
        """
        Deserializes this Zserio object from the bit stream.

        Called only internally if packed arrays are used.

        :param zserio_context: Context for packed arrays.
        :param zserio_reader: Bit stream reader to use.
        """

    </#if>
    <#if fieldList?has_content>
        <#if !uses_packing_context(fieldList)>
        del zserio_context

        </#if>
        <#list fieldList as field>
            <#if field.isExtended>
        if zserio.bitposition.alignto(8, zserio_reader.bitposition) >= zserio_reader.buffer_bitsize:
            <@field_argument_name field/> = <@field_default_value field/>
            <@compound_setter_field field, 3/>
        else:
            self._num_extended_fields += 1
            zserio_reader.alignto(8)

            <@compound_read_field field, name, 3, true/>
            <#else>
        <@compound_read_field field, name, 2, true/>
            </#if>
            <#if field?has_next>

            </#if>
        </#list>
    <#else>
        del zserio_context
        del zserio_reader
    </#if>
</#if>
<#if withWriterCode>

    <#assign needsWriteNewLines=false/>
    <#list fieldList as field>
        <#if has_field_any_write_check_code(field, name, 2)>
            <#assign needsWriteNewLines=true/>
            <#break>
        </#if>
    </#list>
    def write(self, zserio_writer: zserio.BitStreamWriter) -> None:
    <#if withCodeComments>
        """
        Serializes this Zserio object to the bit stream.

        :param zserio_writer: Bit stream writer where to serialize this Zserio object.
        """

    </#if>
    <#if fieldList?has_content>
        <#list fieldList as field>
        <@compound_write_field field, name, 2/>
            <#if field?has_next && needsWriteNewLines>

            </#if>
        </#list>
    <#else>
        del zserio_writer
    </#if>
    <#if isPackable>

    def write_packed(self, zserio_context: ${name}.ZserioPackingContext,
                     zserio_writer: zserio.BitStreamWriter) -> None:
        <#if withCodeComments>
        """
        Serializes this Zserio object to the bit stream.

        Called only internally if packed arrays are used.

        :param zserio_context: Context for packed arrays.
        :param zserio_writer: Bit stream writer where to serialize this Zserio object.
        """

        </#if>
        <#if fieldList?has_content>
            <#if !uses_packing_context(fieldList)>
        del zserio_context

            </#if>
            <#list fieldList as field>
        <@compound_write_field field, name, 2, true/>
                <#if field?has_next>

                </#if>
            </#list>
        <#else>
        del zserio_context
        del zserio_writer
        </#if>
    </#if>
</#if>
    <@define_packing_context isPackable, fieldList/>
<#list fieldList as field>
    <@define_offset_checker field, name/>
    <#if withWriterCode>
    <@define_offset_setter field/>
    </#if>
    <@define_element_factory field, name/>
</#list>
