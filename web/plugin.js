(function () {
  // load custom.css file
  var cssFile;
  cssFile = goog.dom.createDom('link');
  cssFile.rel = "stylesheet";
  cssFile.type = "text/css";
  cssFile.href = "../plugin-resources/web-author-diff-plugin/custom.css";
  goog.dom.appendChild(document.head, cssFile);

  goog.events.listenOnce(workspace, sync.api.Workspace.EventType.BEFORE_EDITOR_LOADED, function(e) {
    // Embedded editors do not need an app-bar.
    workspace.getViewManager().hideAppBar();

    // The right editor is read-only
    if (e.options.side === 'right') {
      goog.events.listenOnce(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
        var editor = e.editor;
        editor.setReadOnlyState({readOnly: true, code: 'diff'});
      });
    } else {
      goog.events.listenOnce(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
        // The left editor is loaded, so the user is authenticated.
        // Only now we can start loading the right editor to avoid
        // having the user login twice.
        window.parent.loadRightEditor();
      });

    }
  });
})();
