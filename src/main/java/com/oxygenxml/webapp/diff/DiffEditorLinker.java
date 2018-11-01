package com.oxygenxml.webapp.diff;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.access.WebappEditingSessionLifecycleListener;
import ro.sync.ecss.extensions.api.webapp.access.WebappPluginWorkspace;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Class that makes server-side links between editors that are compared client-side.
 * 
 * @author ctalau
 */
public class DiffEditorLinker implements WorkspaceAccessPluginExtension {
  /**
   * Context attribute to represent the diff id used to pair editors.
   */
  private static final String DIFF_ID_ATTR = "diff_id";
  /**
   * Context attribute used to identify the other editor.
   */
  public static final String DIFF_PAIR_EDITOR_ATTR = "diff_pair_editor";
  
  /**
   * Map from diffId to the first editor that is loaded.
   */
  private static final Cache<String, AuthorDocumentModel> diffIdToEditor = 
      CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .weakValues()
        .build();
  
  public boolean applicationClosing() {
    return true;
  }
  
  public void applicationStarted(StandalonePluginWorkspace pluginWorkspace) {
    WebappPluginWorkspace webappPluginWorkspace = (WebappPluginWorkspace)pluginWorkspace;
    
    webappPluginWorkspace.addEditingSessionLifecycleListener(new WebappEditingSessionLifecycleListener() {
      @Override
      public void editingSessionStarted(String sessionId, AuthorDocumentModel doc) {
        final AuthorEditorAccess editorAccess = doc.getAuthorAccess().getEditorAccess();
        
        String diffId = (String) editorAccess.getEditingContext().getAttribute(DIFF_ID_ATTR);
      
        // The left editor loads first and populates the caches.
        AuthorDocumentModel firstEditor = diffIdToEditor.getIfPresent(diffId);
        if (firstEditor != null) {
          linkEditors(doc, firstEditor);
          linkEditors(firstEditor, doc);
          diffIdToEditor.invalidate(diffId);
        } else {
          diffIdToEditor.put(diffId, doc);
        }
      }

      /**
       * Links two docs using editing context attributes.
       * @param doc1 The first doc.
       * @param doc2 The second doc.
       */
      private void linkEditors(AuthorDocumentModel doc1, AuthorDocumentModel doc2) {
        doc1.getAuthorAccess().getEditorAccess().getEditingContext()
          .setAttribute(DIFF_PAIR_EDITOR_ATTR, doc2);
      }
    });
  }
}
