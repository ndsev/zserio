<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">

          <@doc_comments docComments 2, false/>
        </main>
        <div id="toc" class="col-2 order-3">
          <nav class="nav flex-column">
            <@symbol_toc_package_link symbol/>
<#list packageSymbols as packageSymbol>
            <@symbol_toc_link packageSymbol.symbol packageSymbol.templateParameters/>
</#list>
          </nav>
        </div>
      </div><!-- row -->
    </div><!-- container -->

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ho+j7jyWK8fNQe+A12Hb8AhRq26LrZ/JpcUGGOn+Y7RsweNrtN/tE3MoK7ZeZDyx" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/anchor-js/anchor.min.js"></script>
    <script>
      // anchorjs setup
      anchors.add(".anchor");

      // bootstrap's scrollspy setup
      var scrollMarginTop = parseInt($('.anchor').css("scroll-margin-top"));
      $('body').scrollspy({
        target: '#toc',
        offset: scrollMarginTop
      });

      // search logic
      $(document).ready(function(){
        $("#search").on("keyup", function() {
          var value = $(this).val().toLowerCase();
          if (value == "") {
            // restore to default state
            $("#symbol_overview .nav").css("display", "");
            $("#symbol_overview .nav-link").css("display", "");
          } else {
            $("#symbol_overview > nav > div > .nav-link").hide(); // hid all packages by default
            $("#symbol_overview .nav .nav .nav-link").filter(function() {
                if ($(this).text().toLowerCase().indexOf(value) > -1) {
                    $(this).parents().siblings(".nav-link").show(); // show parent package
                    $(this).parents(".nav").show(); // show parent nav
                    $(this).show(); // show this item
                } else {
                    $(this).hide();
                }
            });
          }
        });
      });
    </script>
  </body>
</html>
