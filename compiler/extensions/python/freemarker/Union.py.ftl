<#include "FileHeader.inc.ftl"/>
<#include "CompoundParameter.inc.ftl">
<#include "CompoundField.inc.ftl"/>
<#include "DocComment.inc.ftl">
<#if withTypeInfoCode>
    <#include "TypeInfo.inc.ftl"/>
</#if>
<@file_header generatorDescription/>
<@future_annotations/>
<@all_imports packageImports symbolImports typeImports/>
<#macro union_no_match name indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}raise zserio.PythonRuntimeException("No match in union ${name}!")
</#macro>
<#macro union_if memberActionMacroName noMatchMacroName packed=false index=0>
    <#list fieldList as field>
        <#if field?is_first>if <#else>elif </#if>self._choice_tag == self.<@choice_tag_name field/>:
            <@.vars[memberActionMacroName] field, 3, packed, field?index+1/>
    </#list>
        else:
            <@.vars[noMatchMacroName] name, 3/>
</#macro>
<#assign choiceTagArrayTraits="zserio.array.VarSizeArrayTraits">

class ${name}:
<#if withCodeComments && docComments??>
<@doc_comments docComments, 1/>

</#if>
<#assign constructorAnnotatedParamList><@compound_constructor_annotated_parameters compoundParametersData, 3/></#assign>
    def __init__(
            self<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if><#rt>
<#if fieldList?has_content>
            <#lt>,
            *,
    <#list fieldList as field>
            <@field_argument_name field/>: <@field_annotation_argument_type_choice_name field, name/> = None<#rt>
        <#if field?has_next>
            <#lt>,
        </#if>
    </#list>
</#if>
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
        self._choice_tag: int = self.UNDEFINED_CHOICE
        self._choice: typing.Any = None
<#if fieldList?has_content>
    <#list fieldList as field>
        if <@field_argument_name field/> is not None:
        <#if !field?is_first>
            if self._choice_tag != self.UNDEFINED_CHOICE:
                raise zserio.PythonRuntimeException("Calling constructor of union ${name} is ambiguous!")
        </#if>
            self._choice_tag = self.<@choice_tag_name field/>
            <@compound_setter_field field, 3/>
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
        instance = cls(${constructorParamList})
        instance.read(zserio_reader)

        return instance

    @classmethod
    def from_reader_packed(
            cls: typing.Type['${name}'],
            zserio_context_node: zserio.array.PackingContextNode,
            zserio_reader: zserio.BitStreamReader<#if constructorAnnotatedParamList?has_content>,
            <#lt>${constructorAnnotatedParamList}</#if>) -> '${name}':
<#if withCodeComments>
        """
        Returns new object instance constructed from bit stream reader.

        Called only internally if packed arrays are used.

        :param zserio_context_node: Context for packed arrays.
        :param zserio_reader: Bit stream reader to use.
        <@compound_parameter_doc_comment compoundParametersData/>
        """

</#if>
        instance = cls(${constructorParamList})
        instance.read_packed(zserio_context_node, zserio_reader)

        return instance
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
            zserio.typeinfo.TypeAttribute.FIELDS : field_list,
    <#if compoundParametersData.list?has_content>
            zserio.typeinfo.TypeAttribute.PARAMETERS : parameter_list,
    </#if>
    <#if compoundFunctionsData.list?has_content>
            zserio.typeinfo.TypeAttribute.FUNCTIONS : function_list,
    </#if>
            zserio.typeinfo.TypeAttribute.SELECTOR : None<#if templateInstantiation??>,</#if>
    <#if templateInstantiation??>
            <@type_info_template_instantiation_attributes templateInstantiation/>

    </#if>
        }

        return zserio.typeinfo.TypeInfo("${schemaTypeFullName}", ${name}, attributes=attribute_list)
</#if>

    def __eq__(self, other: object) -> bool:
        if isinstance(other, ${name}):
            return (<#rt>
<#if compoundParametersData.list?has_content>
                    <#lt><@compound_compare_parameters compoundParametersData, 5/> and
</#if>
                    <#if compoundParametersData.list?has_content>                    </#if><#t>
                    <#lt>self._choice_tag == other._choice_tag and
                    self._choice == other._choice)

        return False

<#macro union_hashcode_field field indent packed index>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}result = zserio.hashcode.calc_hashcode_${field.typeInfo.hashCodeFunc.suffix}(result, self._choice)
</#macro>
<#macro union_hashcode_no_match name indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}pass
</#macro>
    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        <@compound_hashcode_parameters compoundParametersData/>
        result = zserio.hashcode.calc_hashcode_int32(result, self._choice_tag)
<#if fieldList?has_content>
        <@union_if "union_hashcode_field", "union_hashcode_no_match"/>
</#if>

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
        self._choice_tag = self.<@choice_tag_name field/>
        <@compound_setter_field field, 2/>
    </#if>
</#list>
<#list compoundFunctionsData.list as function>

    def ${function.functionName}(self) -> ${function.returnTypeInfo.typeFullName}:
    <#if withCodeComments>
        """
        Implementation of the function ${function.schemaName}.

        :returns: Result of the function ${function.schemaName}.
        """

    </#if>
        return ${function.resultExpression}
</#list>

    @property
    def choice_tag(self) -> int:
<#if withCodeComments>
        """
        Gets the current choice tag.

        :returns: Choice tag which denotes chosen union field.
        """

</#if>
        return self._choice_tag

    @staticmethod
    def create_packing_context(zserio_context_node: zserio.array.PackingContextNode) -> None:
<#if withCodeComments>
        """
        Creates context for packed arrays.

        Called only internally if packed arrays are used.

        :param zserio_context_node: Context for packed arrays.
        """

</#if>
<#if fieldList?has_content>
        zserio_context_node.create_child().create_context()

    <#list fieldList as field>
        <@compound_create_packing_context_field field/>
    </#list>

<#else>
        del zserio_context_node
</#if>

<#macro union_init_packing_context field indent packed index>
    <#local initCode><@compound_init_packing_context_field field, index, indent/></#local>
    <#if initCode?has_content>
${initCode}<#t>
    <#else>
        <#local I>${""?left_pad(indent * 4)}</#local>
${I}pass
    </#if>
</#macro>
    def init_packing_context(self, zserio_context_node: zserio.array.PackingContextNode) -> None:
<#if withCodeComments>
        """
        Initializes context for packed arrays.

        Called only internally if packed arrays are used.

        :param zserio_context_node: Context for packed arrays.
        """

</#if>
<#if fieldList?has_content>
        zserio_context_node.children[0].context.init(${choiceTagArrayTraits}(),
                                                     self._choice_tag)
        <@union_if "union_init_packing_context", "union_no_match", true/>
<#else>
        del zserio_context_node
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

        end_bitposition += zserio.bitsizeof.bitsizeof_varsize(self._choice_tag)

        <@union_if "compound_bitsizeof_field", "union_no_match"/>

        return end_bitposition - bitposition
<#else>
        del bitposition

        return 0
</#if>

    def bitsizeof_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                         bitposition: int = 0) -> int:
<#if withCodeComments>
        """
        Calculates size of the serialized object in bits for packed arrays.

        Called only internally if packed arrays are used.

        :param zserio_context_node: Context for packed arrays.
        :param bitposition: Bit stream position calculated from zero where the object will be serialized.

        :returns: Number of bits which are needed to store serialized object.
        """

</#if>
<#if fieldList?has_content>
        end_bitposition = bitposition

        end_bitposition += zserio_context_node.children[0].context.bitsizeof(${choiceTagArrayTraits}(),
                                                                             self._choice_tag)

        <@union_if "compound_bitsizeof_field", "union_no_match", true/>

        return end_bitposition - bitposition
<#else>
        del zserio_context_node
        del bitposition

        return 0
</#if>
<#if withWriterCode>

    def initialize_offsets(self, bitposition: int) -> int:
    <#if withCodeComments>
        """
        Initializes offsets in this Zserio object and in all its fields.

        This method sets offsets in this Zserio object and in all fields recursively.

        :param bitposition: Bit stream position calculated from zero where the object will be serialized.
        """

    </#if>
    <#if fieldList?has_content>
        end_bitposition = bitposition

        end_bitposition += zserio.bitsizeof.bitsizeof_varsize(self._choice_tag)

        <@union_if "compound_initialize_offsets_field", "union_no_match"/>

        return end_bitposition
    <#else>
        return bitposition
    </#if>

    def initialize_offsets_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                                  bitposition: int) -> int:
    <#if withCodeComments>
        """
        Initializes offsets in this Zserio type and in all its fields for packed arrays.

        This method sets offsets in this Zserio type and in all fields recursively.
        Called only internally if packed arrays are used.

        :param zserio_context_node: Context for packed arrays.
        :param bitposition: Bit stream position calculated from zero where the object will be serialized.
        """

    </#if>
    <#if fieldList?has_content>
        end_bitposition = bitposition

        end_bitposition += zserio_context_node.children[0].context.bitsizeof(${choiceTagArrayTraits}(),
                                                                             self._choice_tag)

        <@union_if "compound_initialize_offsets_field", "union_no_match", true/>

        return end_bitposition
    <#else>
        del zserio_context_node
        return bitposition
    </#if>
</#if>

<#macro union_read_field field indent packed index>
    <@compound_read_field field, name, indent, packed, index/>
</#macro>
    def read(self, zserio_reader: zserio.BitStreamReader) -> None:
<#if withCodeComments>
        """
        Deserializes this Zserio object from the bit stream.

        :param zserio_reader: Bit stream reader to use.
        """

</#if>
<#if fieldList?has_content>
        self._choice_tag = zserio_reader.read_varsize()

        <@union_if "union_read_field", "union_no_match"/>
<#else>
        del zserio_reader
</#if>

    def read_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                    zserio_reader: zserio.BitStreamReader) -> None:
<#if withCodeComments>
        """
        Deserializes this Zserio object from the bit stream.

        Called only internally if packed arrays are used.

        :param zserio_context_node: Context for packed arrays.
        :param zserio_reader: Bit stream reader to use.
        """

</#if>
<#if fieldList?has_content>
        self._choice_tag = zserio_context_node.children[0].context.read(${choiceTagArrayTraits}(),
                                                                        zserio_reader)

        <@union_if "union_read_field", "union_no_match", true/>
<#else>
        del zserio_context_node
        del zserio_reader
</#if>
<#if withWriterCode>

<#macro union_write_field field indent packed index>
    <@compound_write_field field, name, indent, packed, index/>
</#macro>
    def write(self, zserio_writer: zserio.BitStreamWriter, *,
              zserio_call_initialize_offsets: bool = True) -> None:
    <#if withCodeComments>
        """
        Serializes this Zserio object to the bit stream.

        :param zserio_writer: Bit stream writer where to serialize this Zserio object.
        :param zserio_call_initialize_offsets: True to call automatically initialize_offsets method before
               writing, otherwise false.
        """

    </#if>
    <#if fieldList?has_content>
        <#if hasFieldWithOffset>
        if zserio_call_initialize_offsets:
            self.initialize_offsets(zserio_writer.bitposition)
        <#else>
        del zserio_call_initialize_offsets
        </#if>

        zserio_writer.write_varsize(self._choice_tag)

        <@union_if "union_write_field", "union_no_match"/>
    <#else>
        del zserio_writer
        del zserio_call_initialize_offsets
    </#if>

    def write_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                    zserio_writer: zserio.BitStreamWriter) -> None:
    <#if withCodeComments>
        """
        Serializes this Zserio object to the bit stream.

        Called only internally if packed arrays are used.

        :param zserio_writer: Bit stream writer where to serialize this Zserio object.
        :param zserio_call_initialize_offsets: True to call automatically initialize_offsets method before
               writing, otherwise false.
        """

    </#if>
    <#if fieldList?has_content>
        zserio_context_node.children[0].context.write(${choiceTagArrayTraits}(),
                                                      zserio_writer, self._choice_tag)

        <@union_if "union_write_field", "union_no_match", true/>
    <#else>
        del zserio_context_node
        del zserio_writer
    </#if>
</#if>
<#list fieldList as field>
    <@define_element_creator field, name/>
</#list>

<#list fieldList as field>
    <@choice_tag_name field/> = ${field?index}
    <#if withCodeComments>
    """ Choice tag which denotes chosen union field ${field.name}. """
    </#if>
</#list>
    <#-- don't use CHOICE_UNDEFINED to prevent clashing with generated choice tags -->
    UNDEFINED_CHOICE = -1
<#if withCodeComments>
    """ Choice tag which is used if no union field has been set yet. """
</#if>
