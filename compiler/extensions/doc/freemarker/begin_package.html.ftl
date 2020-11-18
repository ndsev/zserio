<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<#include "import.inc.ftl">
<#include "symbol.inc.ftl">
<!doctype html>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="${cssDirectory}/bootstrap.min.css">

    <!-- Zserio Documentation CSS -->
    <link rel="stylesheet" href="${cssDirectory}/${stylesheetName}">

    <title>${symbol.name} documentation</title>
  </head>
  <body>

    <header id="header" class="navbar navbar-dark bg-dark sticky-top">
      <div class="navbar-nav navbar-left navbar-brand">
        <a href="https://zserio.org/" target="_blank">
          <img class="logo" alt="zserio.org" src="${resourcesDirectory}/zserio.png"/>
        </a>
      </div>
      <div class="navbar-nav navbar-center navbar-brand">Documentation for package ${symbol.name}</div>
      <button id="toc_button" type="button" class="btn navbar-nav navbar-right navbar-brand shadow-none">
        <svg id="toc_collapsed_icon" style="display: none" width="1rem" height="1rem" viewBox="0 0 16 16" class="bi bi-chevron-left" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        <svg id="toc_active_icon" width="1rem" height="1rem" viewBox="0 0 16 16" class="bi bi-chevron-right" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
          <path fill-rule="evenodd" d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708z"/>
        </svg>
      </button>
    </header>

    <div class="container-fluid">
      <div class="row">

        <div id="left_panel" class="col-2">
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

        <main class="col-8 pl-md-3">
          <h1 id="${symbol.htmlLink.htmlAnchor}" class="anchor">${symbol.name}</h1>
          <@doc_comments docComments, 5, false/>
          <@imports importNodes, 5/>
