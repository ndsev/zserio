<#ftl output_format="HTML">
<#include "doc_comment.inc.ftl">

          <@doc_comments docComments 2, false/>
        </main>
        <div id="toc" class="col-2 order-3">
          <nav class="nav flex-column">
            <@symbol_toc_link symbol/>
<#list packageSymbols as packageSymbol>
            <@symbol_toc_link packageSymbol/>
</#list>
          </nav>
        </div>
      </div><!-- row -->
    </div><!-- container -->

    <script src="../js/jquery-3.5.1.slim.min.js"></script>
    <script src="../js/bootstrap.bundle.min.js"></script>
    <script src="../js/anchor.min.js"></script>
    <script>
      // anchorjs setup
      anchors.add(".anchor");

      // search logic
      function escapeRegex(string) {
        return string.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
      }

      function buildRegEx(terms) {
        try {
          return new RegExp(terms.join(".*"), "i");
        } catch (e) {
          return undefined;
        }
      }

      function buildSearchPattern(value) {
        let terms = value.split(/\s/);
        let pattern = buildRegEx(terms);
        if (!pattern) {
          let escapedTerms = terms.map(function(term) {
            return escapeRegex(term);
          });
          pattern = buildRegEx(escapedTerms);
        }
        return pattern;
      }

      function searchFilter(value) {
        let symbolOverview = $("#symbol_overview");

        if (value == "") {
          // restore to default state
          symbolOverview.find(".nav-symbols").css("display", "");
          symbolOverview.find(".nav-link").css("display", "");
        } else {
          // show also symbols nav-s which are not in the active package
          symbolOverview.find(".nav-symbols").show();
          symbolOverview.find(".nav-link").hide(); // hide all links

          let searchPattern = buildSearchPattern(value);
          if (!searchPattern) {
            console.warn("Couldn't build a search pattern!")
            return;
          }

          symbolOverview.find(".nav-package").each(function(index, element) {
            let navPackage = $(element);
            let navLinkPackage = navPackage.find(".nav-link-package");
            let packageName = navLinkPackage.text();
            let showPackage = packageName.match(searchPattern);
            navPackage.find(".nav-link-symbol").each(function(index, element) {
              let navLinkSymbol = $(element);
              let symbolName = navLinkSymbol.text();
              let fullSymbolName = packageName + "." + symbolName;

              if (fullSymbolName.match(searchPattern)) {
                navLinkSymbol.show();
                showPackage = true;
              }
            });
            if (showPackage) {
              navLinkPackage.show();
            }
          });
        }
      }

      function scrollParentToChild(parent, child) {
        let parentRect = parent.getBoundingClientRect();
        let parentViewableArea = {
          height: parent.clientHeight,
          width: parent.clientWidth
        };

        let childRect = child.getBoundingClientRect();
        let isViewable = (childRect.top >= parentRect.top) &&
                         (childRect.top <= parentRect.top + parentViewableArea.height);

        if (!isViewable) {
          parent.scrollTop = (childRect.top + parent.scrollTop) - parentRect.top
        }
      }

      // custom hooks
      $(document).ready(function() {
        // re-apply current search value
        let searchValue = sessionStorage.getItem("searchValue");
        if (searchValue) {
          $("#search").val(searchValue);
          searchFilter(searchValue);
        }

        // re-apply current left panel scroll
        let symbolOverviewScrollTop = sessionStorage.getItem("symbolOverviewScrollTop");
        if (symbolOverviewScrollTop) {
          $('#symbol_overview').scrollTop(symbolOverviewScrollTop);
        }

        // bootstrap's scrollspy setup
        var scrollMarginTop = parseInt($('.anchor').css("scroll-margin-top"));
        $('body').scrollspy({
          target: '#toc',
          offset: scrollMarginTop
        });

        $("#search").on("keyup", function(e) {
          if (e.key == "Escape")
            $(this).val("");

          let value = $(this).val();
          sessionStorage.setItem("searchValue", value);
          searchFilter(value);
        });

        // remember current symbol overview scroll position
        $(".nav-link").on("click", function() {
          sessionStorage.setItem("symbolOverviewScrollTop", $('#symbol_overview').scrollTop());
        });

        // allow to collapse active package in the overview
        $(".nav-link-package.active").on("click", function() {
          $(this).siblings(".nav-symbols").toggleClass("collapsed");
        })

        // catch scrollspy's event that the active item has changed
        $(window).on("activate.bs.scrollspy", function() {
          scrollParentToChild($('#toc')[0], $('#toc .active')[0]);
        })
      });
    </script>
  </body>
</html>
