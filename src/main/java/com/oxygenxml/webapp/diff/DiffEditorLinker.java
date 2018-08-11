package com.oxygenxml.webapp.diff;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
   * Context attribute to represent the side of the editor.
   */
  private static final String DIFF_SIDE_ATTR = "side";
  /**
   * Context attribute to represent the diff id used to pair editors.
   */
  private static final String DIFF_ID_ATTR = "diff_id";
  /**
   * Context attribute used to identify the other editor.
   */
  public static final String DIFF_PAIR_EDITOR_ATTR = "diff_pair_editor";
  
  /**
   * Map from diffId to left editor.
   */
  private static final Cache<String, AtomicReference<AuthorEditorAccess>> diffIdToLeftEditor = 
      CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .weakValues()
        .build();
  /**
   * Map from diffId to right editor.
   */
  private static final Cache<String, AtomicReference<AuthorEditorAccess>> diffIdToRightEditor = 
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
        
        String side = (String) editorAccess.getEditingContext().getAttribute(DIFF_SIDE_ATTR);
        String diffId = (String) editorAccess.getEditingContext().getAttribute(DIFF_ID_ATTR);
        
        if ("left".equals(side)) {
          // The left editor loads first and populates the caches.
          diffIdToLeftEditor.put(diffId, new AtomicReference<AuthorEditorAccess>(editorAccess));
          AtomicReference<AuthorEditorAccess> rightEditorRef = new AtomicReference<AuthorEditorAccess>();
          diffIdToRightEditor.put(diffId, rightEditorRef);
          editorAccess.getEditingContext().setAttribute(DIFF_PAIR_EDITOR_ATTR, rightEditorRef);
        } else if ("right".equals(side)) {
          // Right editor loads second and finds the left editor already there.
          diffIdToRightEditor.getIfPresent(diffId).set(editorAccess);
          AtomicReference<AuthorEditorAccess> leftEditorRef = diffIdToLeftEditor.getIfPresent(diffId);
          editorAccess.getEditingContext().setAttribute(DIFF_PAIR_EDITOR_ATTR, leftEditorRef);
          
          // And clears the caches.
          diffIdToLeftEditor.invalidate(diffId);
          diffIdToRightEditor.invalidate(diffId);
        }
      }
    });
  }
}
