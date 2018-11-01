package com.oxygenxml.webapp.diff;

/**
 * Descriptor for a diff interval.
 * 
 * @author ctalau
 */
public class IntervalDescriptor implements Comparable<IntervalDescriptor> {
  /**
   * The type of the diff interval in a document.
   */
  public enum Type {
    ADDED,
    REMOVED, 
    CHANGED
  }
  
  /**
   * The start position.
   */
  final int start;
  /**
   * The end position.
   */
  final int end;
  /**
   * The type of the interval.
   */
  final Type type;
  /**
   * The ID of the interval, used to match intervals on the left with ones on the right.
   */
  final int id;

  /**
   * Constructor.
   * 
   * @param id The ID of the interval, used to match intervals on the left with ones on the right.
   * @param start The start offset in the textual representation of the document.
   * @param end The end offset in the textual representation of the document.
   * @param type The type of the interval: added, deleted or changed.
   */
  public IntervalDescriptor(int id, int start, int end, Type type) {
    this.id = id;
    this.start = start;
    this.end = end;
    this.type = type;
  }

  /**
   * Compares to another interval by position.
   * @param o The other interval.
   * @return the sign of the comparison.
   */
  public int compareTo(IntervalDescriptor o) {
    return this.start - o.start;
  }
  
}
