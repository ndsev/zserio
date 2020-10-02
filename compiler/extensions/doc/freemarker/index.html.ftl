<#ftl output_format="HTML">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <script language="JavaScript">
      function receiveMessage(event)
      {
        var message = event.data;
        if ("messageType" in message) {
          if (message.messageType == "package-loaded") {
            var packageOverview = document.getElementById("package_overview").contentWindow
            packageOverview.postMessage(message.packageName, "*")
          } else if (message.messageType == "package-changed") {
            var symbolOverview = document.getElementById("symbol_overview").contentWindow
            symbolOverview.postMessage(message.styleId, "*")
          }
        } // otherwise it's not our message
      }

      window.addEventListener("message", receiveMessage, false);
    </script>
  </head>

  <frameset cols="20%,*">
    <frameset rows="30%,70%">
      <frame id="package_overview" src="package_overview.html" scrolling="auto" frameborder="0" />
      <frame id="symbol_overview" src="symbol_overview.html" scrolling="auto" frameborder="1" />
    </frameset>
    <frame id="main_window" name="main_window" src="" class="detailedDocu" scrolling="auto" frameborder="0" />

    <noframes>
      <body>
        <p>Your browser software can not handle framesets!</p>
      </body>
    </noframes>
  </frameset>
</html>
