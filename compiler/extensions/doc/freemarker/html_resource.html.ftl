<#ftl output_format="HTML">
<!doctype html>
<html lang="en">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>${title}</title>
  </head>
  <body>
<#list bodyContent?split("\r?\n", "rm") as htmlLine>
    ${htmlLine?no_esc}
</#list>
  </body>
</html>
