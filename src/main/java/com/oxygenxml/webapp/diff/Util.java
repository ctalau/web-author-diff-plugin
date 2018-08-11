package com.oxygenxml.webapp.diff;

import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.oxygenxml.webapp.diff.IntervalDescriptor.Type;

import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.highlights.AuthorHighlighter;
import ro.sync.ecss.extensions.api.highlights.ColorHighlightPainter;
import ro.sync.ecss.extensions.api.webapp.access.IWebappAuthorEditorAccess;
import ro.sync.exml.view.graphics.Color;

public class Util {
  /**
   * Logger.
   */
  private static final Logger logger = Logger.getLogger(Util.class);

  /**
   * Painters for diff highlights.
   */
  private static final Map<IntervalDescriptor.Type, ColorHighlightPainter> painters = 
      ImmutableMap.of(
          Type.ADDED, new ColorHighlightPainter(),
          Type.REMOVED, new ColorHighlightPainter(),
          Type.CHANGED, new ColorHighlightPainter());
  static {
    painters.get(Type.ADDED).setBgColor(new Color(0, 255, 0));
    painters.get(Type.REMOVED).setBgColor(new Color(255, 0, 0));
    painters.get(Type.CHANGED).setBgColor(new Color(0, 255, 255));
  }

  /**
   * Render highlights.
   * 
   * @param intervals Highlighted intervals.
   * @param editorAccess The editor access.
   */
  public static void renderHighlights(List<IntervalDescriptor> intervals, AuthorEditorAccess editorAccess) {
    AuthorHighlighter highlighter = ((IWebappAuthorEditorAccess)editorAccess).getHighlighter();

    for (IntervalDescriptor intervalDescriptor : intervals) {
      try {
        highlighter.addHighlight(
            intervalDescriptor.start, 
            intervalDescriptor.end, 
            painters.get(intervalDescriptor.type), 
            null);
      } catch (BadLocationException e) {
        logger.warn("Cannot render highlight. ignoring.", e);
      }
    }
  }
}
