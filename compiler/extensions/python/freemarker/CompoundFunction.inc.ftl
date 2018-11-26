<#macro compound_functions compoundFunctionsData>
    <#list compoundFunctionsData.list as function>
    def ${function.name}(self):
        return ${function.resultExpression}

    </#list>
</#macro>
