<#macro compound_constructors compoundConstructorsData>
    <#local name=compoundConstructorsData.compoundName>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData/></#local>
    <#local constructorArgumentList><@compound_constructor_argument_list compoundConstructorsData/></#local>
    <#if compoundConstructorsData.withWriterCode>
    public ${name}(${constructorArgumentTypeList})
    {
        <@compound_constructors_set_parameters compoundConstructorsData false/>
    }

    </#if>
    public ${name}(java.io.File __file<#if constructorArgumentTypeList?has_content>,${constructorArgumentTypeList}</#if>)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
        <@compound_constructors_set_parameters compoundConstructorsData true/>
        final zserio.runtime.io.FileBitStreamReader __in = new zserio.runtime.io.FileBitStreamReader(__file);
        read(__in);
        __in.close();
    }

    public ${name}(zserio.runtime.io.BitStreamReader __in<#if constructorArgumentTypeList?has_content>,${constructorArgumentTypeList}</#if>)
            throws java.io.IOException, zserio.runtime.ZserioError
    {
        <@compound_constructors_set_parameters compoundConstructorsData true/>
        read(__in);
    }

</#macro>

<#macro compound_constructors_set_parameters compoundConstructorsData extraNewLine>
    <#local constructorArgumentTypeList><@compound_constructor_argument_type_list compoundConstructorsData/></#local>
    <#if constructorArgumentTypeList?has_content>
        <#list compoundConstructorsData.compoundParametersData.list as compoundParameter>
        this.${compoundParameter.name} = ${compoundParameter.name};
        </#list>
        <#if extraNewLine>

        </#if>
    </#if>
</#macro>

<#macro compound_constructor_argument_type_list compoundConstructorsData>
    <#list compoundConstructorsData.compoundParametersData.list as compoundParameter>

            ${compoundParameter.javaTypeName} ${compoundParameter.name}<#if compoundParameter_has_next>,</#if><#rt>
    </#list>
</#macro>

<#macro compound_constructor_argument_list compoundConstructorsData>
    <#list compoundConstructorsData.compoundParametersData.list as compoundParameter>
${compoundParameter.name}<#if compoundParameter_has_next>, </#if><#rt>
    </#list>
</#macro>
