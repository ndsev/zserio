<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">
<html>
  <head>
    <title>${name}</title>
    <link rel="stylesheet" type="text/css" href="../webStyles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  <body>
    <h1>${name}</h1>
    <@doc_comments docComments false/>
