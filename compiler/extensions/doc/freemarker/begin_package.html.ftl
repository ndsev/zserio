<#ftl output_format="HTML">
<#include "html_common.inc.ftl">
<#include "doc_comment.inc.ftl">
<#include "code.inc.ftl">
<#include "import.inc.ftl">
<#include "symbol.inc.ftl">
<!doctype html>
<html lang="en">
  <head>
    <@html_meta_tags/>

    <@html_stylesheets cssDirectory stylesheetName/>

    <title>${symbol.name} documentation</title>

    <script>
      <@html_js_storage_functions/>

      function isTocHidden() {
        return getItemFromStorage(localStorage, "tocHidden") == "true";
      }

      function toggleTocIcon() {
        let tocIcon = document.getElementById("toc_icon");
        let useTag = tocIcon.getElementsByTagName("use")[0];
        let icon = useTag.getAttribute("xlink:href");
        icon = (icon == "#chevron-left") ? "#chevron-right" : "#chevron-left";
        useTag.setAttribute("xlink:href", icon);
      }
    </script>
  </head>
  <body>
    <!-- svg definitions -->
    <svg style="display: none">
      <symbol viewBox="0 0 16 16" class="bi bi-chevron-left" fill="currentColor" id="chevron-left" xmlns="http://www.w3.org/2000/svg">
        <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 010 .708L5.707 8l5.647 5.646a.5.5 0 01-.708.708l-6-6a.5.5 0 010-.708l6-6a.5.5 0 01.708 0z"/>
      </symbol>
      <symbol viewBox="0 0 16 16" class="bi bi-chevron-right" fill="currentColor" id="chevron-right" xmlns="http://www.w3.org/2000/svg">
        <path fill-rule="evenodd" d="M4.646 1.646a.5.5 0 01.708 0l6 6a.5.5 0 010 .708l-6 6a.5.5 0 01-.708-.708L10.293 8 4.646 2.354a.5.5 0 010-.708z"/>
      </symbol>
      <symbol viewBox="0 0 16 16" class="bi bi-chat-left-text" fill="currentColor" id="chat-left-text" xmlns="http://www.w3.org/2000/svg">
        <path fill-rule="evenodd" d="M14 1H2a1 1 0 00-1 1v11.586l2-2A2 2 0 014.414 11H14a1 1 0 001-1V2a1 1 0 00-1-1zM2 0a2 2 0 00-2 2v12.793a.5.5 0 00.854.353l2.853-2.853A1 1 0 014.414 12H14a2 2 0 002-2V2a2 2 0 00-2-2H2z"/>
        <path fill-rule="evenodd" d="M3 3.5a.5.5 0 01.5-.5h9a.5.5 0 010 1h-9a.5.5 0 01-.5-.5zM3 6a.5.5 0 01.5-.5h9a.5.5 0 010 1h-9A.5.5 0 013 6zm0 2.5a.5.5 0 01.5-.5h5a.5.5 0 010 1h-5a.5.5 0 01-.5-.5z"/></symbol><symbol viewBox="0 0 16 16" class="bi bi-chat-left-text-fill" fill="currentColor" id="chat-left-text-fill" xmlns="http://www.w3.org/2000/svg">
        <path fill-rule="evenodd" d="M0 2a2 2 0 012-2h12a2 2 0 012 2v8a2 2 0 01-2 2H4.414a1 1 0 00-.707.293L.854 15.146A.5.5 0 010 14.793V2zm3.5 1a.5.5 0 000 1h9a.5.5 0 000-1h-9zm0 2.5a.5.5 0 000 1h9a.5.5 0 000-1h-9zm0 2.5a.5.5 0 000 1h5a.5.5 0 000-1h-5z"/>
      </symbol>
      <symbol viewBox="0 0 16 16" class="bi bi-chat-left" fill="currentColor" id="chat-left" xmlns="http://www.w3.org/2000/svg">
        <path fill-rule="evenodd" d="M14 1H2a1 1 0 00-1 1v11.586l2-2A2 2 0 014.414 11H14a1 1 0 001-1V2a1 1 0 00-1-1zM2 0a2 2 0 00-2 2v12.793a.5.5 0 00.854.353l2.853-2.853A1 1 0 014.414 12H14a2 2 0 002-2V2a2 2 0 00-2-2H2z"/>
      </symbol>
    </svg>

    <header id="header" class="navbar navbar-dark bg-dark sticky-top">
      <@html_navbar_navigation headerNavigation/>

      <@html_navbar_zserio_brand/>

      <button id="toc_button" type="button" class="btn navbar-nav navbar-brand shadow-none">
        <svg id="toc_icon" class="bi" width="1em" height="1em"><use xlink:href="#chevron-right"/></svg>
      </button>
    </header>

    <script>
      if (isTocHidden())
        toggleTocIcon();
    </script>

    <div class="container-fluid">
      <div class="row">

        <div id="left_panel" class="col-2 order-1">
          <form id="search_form" role="search">
            <input id="search" class="form-control" type="text" autocomplete="off" spellcheck="false"
              placeholder="Search...">
          </form>
          <nav id="symbol_overview" class="nav flex-column">
<#list packages as pkg>
            <nav class="nav-package">
              <@symbol_overview_package_link pkg.symbol, symbol/><#nt>
    <#if pkg.packageSymbols?has_content>
              <nav class="nav nav-symbols flex-column<#if symbol.name == pkg.symbol.name> active</#if>">
        <#list pkg.packageSymbols as packageSymbol>
                <@symbol_overview_link packageSymbol/><#nt>
        </#list>
              </nav>
    </#if>
            </nav>
</#list>
          </nav>
        </div>

        <div id="toc" class="col-2 order-3">
          <nav class="nav flex-column">
            <@symbol_toc_link symbol/><#nt>
<#list tocSymbols as tocSymbol>
            <@symbol_toc_link tocSymbol/><#nt>
</#list>
          </nav>
        </div>
        <script>
          if (isTocHidden())
            document.getElementById("toc").style.display = "none";
        </script>

        <main id="main" class="col-8 pl-3 order-2">
<#if hasFloatingDocComments(docComments)>
          <@doc_comments_floating docComments, 5/>

</#if>
          <h1 id="${symbol.htmlLink.htmlAnchor}" class="anchor">${symbol.name}</h1>
<#if !isDefaultPackage>

          <@code_table_begin 5/>
            <tbody>
    <#if hasStickyDocComments(docComments)>
              <tr class="doc"><td>
                <@doc_comments_sticky docComments, 8/>
              </td></tr>
    </#if>
              <tr><td>
                package <@symbol_reference symbol/>;
              </td></tr>
    <#if compatibilityVersion??>
              <tr><td>
                zserio_compatiblity_version("${compatibilityVersion}");
              </td></tr>
    </#if>
            </tbody>
          <@code_table_end 5/>
</#if>
          <@imports importNodes, 5/>
