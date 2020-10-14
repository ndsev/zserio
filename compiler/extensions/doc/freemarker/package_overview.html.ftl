<#ftl output_format="HTML">
<html>
  <head>
    <title>Zserio Package Overview</title>

    <link rel="stylesheet" type="text/css" href="web_styles.css">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <script language="JavaScript">
        var oldSelectedElement = null;

        function highlightElement(selectedElement, scroll) {
          if (oldSelectedElement) {
            oldSelectedElement.className = "packagelist";
          }
          oldSelectedElement = selectedElement;
          selectedElement.className = "selectedpackagelist";
          if (scroll)
            selectedElement.scrollIntoView();
        }

        function showPackage(element, scroll) {
          var selectedElement = element.parentElement.parentElement;
          if (oldSelectedElement == selectedElement)
            return;

          highlightElement(selectedElement, scroll);
          var clickedStyleItemId = "style_" + element.text.replace(/\./g, '_');
          parent.postMessage({ messageType: "package-changed", styleId: clickedStyleItemId }, "*");
        }

        function showAllPackages(element) {
          highlightElement(element, false);
          parent.postMessage({ messageType: "package-changed", styleId: ".all" }, "*");
        }

        function receiveMessage(event) {
          if (oldSelectedElement && oldSelectedElement.id == "all_packages")
            return;

          var packageName = event.data;
          var packageLinkElement = document.getElementById(packageName);
          showPackage(packageLinkElement, true);
        }

        window.addEventListener("message", receiveMessage, false);
    </script>
  </head>

  <body>
    <h2>Packages</h2>

    <ul class="packagelist" id="all_packages" title="All packages in one" onclick="showAllPackages(this);"><#rt>
      <li><#t>
        all packages<#t>
      </li><#t>
    <#lt></ul>
<#list packageNames as packageName>
    <ul class="packagelist"><#rt>
      <li><#t>
        <a href="content/${packageName}.html" id="${packageName}" title="Package: ${packageName}" <#t>
          target="main_window" onclick="showPackage(this, false);">${packageName}</a><#t>
      </li><#t>
    <#lt></ul>
</#list>

    <script language="JavaScript">
      showAllPackages(document.getElementById("all_packages"));
    </script>
  </body>
</html>
