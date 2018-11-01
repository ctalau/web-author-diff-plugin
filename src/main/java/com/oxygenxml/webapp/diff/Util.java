package com.oxygenxml.webapp.diff;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.oxygenxml.webapp.diff.IntervalDescriptor.Type;

import ro.sync.ecss.common.WebappAccess;
import ro.sync.ecss.common.WebappTextModeState;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;

public class Util {
  /**
   * Logger.
   */
  private static Logger logger = Logger.getLogger(Util.class);
 
  /**
   * Render highlights.
   * 
   * @param intervals Highlighted intervals.
   * @param docModel The editor model.
   */
  public static void renderHighlights(List<IntervalDescriptor> intervals, AuthorDocumentModel docModel) {
    String content = getNormalizedContent(docModel);
    StringBuilder contentWithHighlights = insertHighlights(intervals, content);
    WebappAccess.setTextModeState(docModel, new WebappTextModeState(
        contentWithHighlights.toString(), 1));
  }

  /**
   * Get the normalized content of the document.
   * 
   * @param docModel The document model.
   * @return The content, with end-of-lines normalized.
   */
  private static String getNormalizedContent(AuthorDocumentModel docModel) {
    String content;
    try {
      AuthorEditorAccess editorAccess = docModel.getAuthorAccess().getEditorAccess();
      content = IOUtils.toString(editorAccess.createContentReader());
    } catch (IOException e1) {
      // Cannot happen.
      throw new IllegalStateException();
    }
    // Normalize EOL
    content.replaceAll("\\r(\\n)?", "\n");
    return content;
  }

  /**
   * Insert highlights for the given intervals.
   * 
   * @param intervals The diff intervals.
   * @param content The content of the document.
   * 
   * @return The content with highlights.
   */
  private static StringBuilder insertHighlights(List<IntervalDescriptor> intervals, String content) {
    // Make sure intervals are sorted according to their offsets.
    Collections.sort(intervals);
    
    // Keep a cursor at the latest position copied to the output content.
    int inputCursor = 0;
    StringBuilder contentWithHighlights = new StringBuilder();
    for (IntervalDescriptor interval : intervals) {
      // Copy the part between intervals.
      contentWithHighlights.append(content.substring(inputCursor, interval.start));
      // Add the start highlight marker.
      contentWithHighlights.append(
          "<?oxy_custom_start " + 
              "id=\"" + interval.id + "\" " + 
              "role=\"" + interval.type + "\" " + 
              "type=\"oxy_content_highlight\"" + 
              "color=\"" + roleToColor(interval.type) + "\"?>");
      // Add the content of the interval.
      contentWithHighlights.append(content.substring(interval.start, interval.end));
      // Add the end highlight marker.
      contentWithHighlights.append(
          "<?oxy_custom_end?>");

      // Advance the cursor.
      inputCursor = interval.end;
    }
    // Add the part after the last marker.
    contentWithHighlights.append(content.substring(inputCursor, content.length()));
    
    logger.debug(contentWithHighlights);
    
    return contentWithHighlights;
  }

  /**
   * Gets the corresponding color for a given interval role.
   * 
   * @param type The type of interval.
   * 
   * @return The color.
   */
  private static String roleToColor(Type type) {
    switch (type) {
    case ADDED:
      return "0,255,0";
    case REMOVED:
      return "255,0,0";
    case CHANGED:
      return "0,0,255";
    }
    // Can't happen.
    return "";
  }
}
