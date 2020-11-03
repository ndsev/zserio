<#ftl output_format="HTML">
<#macro symbol_reference symbol suffix="" attrs="">
    <#if symbol.htmlLink??>
        <a class="${symbol.htmlClass}" <@symbol_reference_href_title symbol/> ${attrs?no_esc}>${symbol.name}${suffix}</a><#t>
    <#else>
        <span class="${symbol.htmlClass}" <@symbol_reference_href_title symbol/>>${symbol.name}</span><#t>
    </#if>
    <#if symbol.templateArguments?has_content>
        &lt;<#t>
       <#list symbol.templateArguments as templateArgument>
            <@symbol_reference templateArgument/><#if templateArgument?has_next>,</#if><#t>
       </#list>
        &gt;<#t>
    </#if>
</#macro>

<#macro symbol_overview_package_link symbol currentSymbol>
    <a class="nav-link<#if symbol.name == currentSymbol.name> active</#if>" <#t>
      <@symbol_reference_href_title symbol/>>${symbol.name}</a><#t>
</#macro>

<#macro symbol_overview_link symbol templateParameters>
    <a class="nav-link" <@symbol_reference_href_title symbol/>>${symbol.name}<#t>
      <@symbol_template_parameters templateParameters/></a><#t>
</#macro>

<#macro symbol_toc_link symbol templateParameters>
    <a class="nav-link" href="#${symbol.htmlLink.htmlAnchor}">${symbol.name}<#t>
      <@symbol_template_parameters templateParameters/></a><#t>
</#macro>

<#macro symbol_template_parameters templateParametes>
    <#if templateParameters?has_content>
        &lt;<#t>
        <#list templateParameters as templateParameter>
            ${templateParameter}<#if templateParameter?has_next>, </#if><#t>
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
        <td><@symbol_reference_label templateArgument align/></td><#t>
            <#if templateArgument?has_next>
        <td>,</td><#t>
            </#if>
        </#list>
        <td>&gt;</td><#t>
    </#if>
    </tr></table><#t>
</#macro>

<#macro symbol_reference_href_title symbol>
    <#if symbol.htmlLink??>
        href="<@symbol_html_link symbol.htmlLink/>" title="${symbol.htmlTitle}"<#t>
    <#else>
        title="${symbol.htmlTitle}"<#t>
    </#if>
</#macro>

<#macro symbol_reference_url symbol>
    <#if symbol.htmlLink??>
        URL="<@symbol_html_link symbol.htmlLink/>"<#t>
    </#if>
</#macro>

<#macro symbol_reference_tooltip symbol>
    tooltip="${symbol.htmlTitle}"<#t>
</#macro>

<#macro symbol_html_link htmlLink>
    <#if htmlLink.htmlPage??>${htmlLink.htmlPage}</#if><#if htmlLink.htmlAnchor??>#${htmlLink.htmlAnchor}</#if><#t>
</#macro>
