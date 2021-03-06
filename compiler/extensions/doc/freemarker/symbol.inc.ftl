<#ftl output_format="HTML">
<#include "html_common.inc.ftl">
<#macro symbol_reference symbol>
    <#if symbol.htmlLink??>
        <a <@symbol_reference_class symbol/> <@symbol_reference_href_title symbol/>><#t>
          ${symbol.name}</a><#t>
    <#else>
        <span <@symbol_reference_class symbol/> <@symbol_reference_href_title symbol/>>${symbol.name}</span><#t>
    </#if>
    <#if symbol.templateArguments?has_content>
        &lt;<#t>
        <#list symbol.templateArguments as templateArgument>
          <@symbol_reference templateArgument/><#if templateArgument?has_next>, </#if><#t>
        </#list>
        &gt;<#t>
    </#if>
</#macro>

<#macro symbol_overview_package_link symbol currentSymbol>
    <a class="nav-link nav-link-package<#if symbol.name == currentSymbol.name> active</#if>" <#t>
      <@symbol_reference_href_title symbol, false/>>${symbol.name}</a><#t>
</#macro>

<#macro symbol_overview_link symbol>
    <a class="nav-link nav-link-symbol" <@symbol_reference_href_title symbol/>>${symbol.name}</a><#t>
</#macro>

<#macro symbol_toc_link symbol>
    <a class="nav-link" href="#${symbol.htmlLink.htmlAnchor?url}" title="${symbol.htmlTitle}">${symbol.name}</a><#t>
</#macro>

<#macro symbol_template_parameters templateParameters>
    <#if templateParameters?has_content>
        &lt;<#t>
        <#list templateParameters as templateParameter>
            ${templateParameter}<#if templateParameter?has_next>, </#if><#t>
        </#list>
        &gt;<#t>
    </#if>
</#macro>

<#macro symbol_node_name symbol>
    ${symbol.name}<#t>
    <#if symbol.templateArguments?has_content>
        &lt;<#t>
        <#list symbol.templateArguments as templateArgument>
            <@symbol_node_name templateArgument/><#if templateArgument?has_next>,</#if><#t>
        </#list>
        &gt;<#t>
    </#if>
</#macro>

<#macro symbol_reference_label symbol align>
    <table align="${align}" border="0" cellspacing="0" cellpadding="0"><tr><#t>
        <td href="<#if symbol.htmlLink??><@symbol_html_link symbol.htmlLink/><#else>javascript:;</#if>" <#t>
            title="${symbol.htmlTitle}">${symbol.name}</td><#t>
    <#if symbol.templateArguments?has_content>
        <td>&lt;</td><#t>
        <#list symbol.templateArguments as templateArgument>
        <td><@symbol_reference_label templateArgument, align/></td><#t>
            <#if templateArgument?has_next>
        <td>,</td><#t>
            </#if>
        </#list>
        <td>&gt;</td><#t>
    </#if>
    </tr></table><#t>
</#macro>

<#macro symbol_reference_class symbol>
    class="${symbol.typeName?lower_case}-token"<#t>
</#macro>

<#macro symbol_reference_href_title symbol useAnchor=true>
    <#if symbol.htmlLink??>
        href="<@symbol_html_link symbol.htmlLink, useAnchor/>" title="${symbol.htmlTitle}"<#t>
    <#else>
        title="${symbol.htmlTitle}"<#t>
    </#if>
</#macro>

<#macro symbol_html_link htmlLink useAnchor=true>
    <#if htmlLink.htmlPage??><@html_path htmlLink.htmlPage/></#if><#rt>
      <#lt><#if useAnchor && htmlLink.htmlAnchor??>#${htmlLink.htmlAnchor?url}</#if><#t>
</#macro>
