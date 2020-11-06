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
      <div class="navbar-brand">Documentation for package ${symbol.name}</div>
      <a href="https://zserio.org/" target="_blank">
        <img class="logo" src="${resourcesDirectory}/zserio.png"/>
      </a>
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
              <@symbol_overview_package_link pkg.symbol symbol/><#nt>
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
        <main class="col-8 pl-md-3" role="main">
          <h1 id="${symbol.htmlLink.htmlAnchor}" class="anchor">${symbol.name}</h1>
          <@doc_comments docComments 2, false/>
          <@imports importNodes/>
