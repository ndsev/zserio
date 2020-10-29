<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "symbol.inc.ftl">

    <@doc_comments docComments, 2, false/>
    <div class="docuCode">
      import <@symbol_reference importedPackageSymbol/>.<#if importedSymbol??><@symbol_reference importedSymbol/><#else>*</#if>;
    </div>
