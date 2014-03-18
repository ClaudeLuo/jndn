/**
 * Copyright (C) 2013-2014 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * See COPYING for copyright and distribution information.
 */

package net.named_data.jndn;
import net.named_data.jndn.util.ChangeCountable;

public class MetaInfo implements ChangeCountable {
  /**
   * Create a new MetaInfo with default values.
   */
  public MetaInfo()
  {
  }

  /**
   * Create a new MetaInfo with a copy of the fields in the given metaInfo.
   * @param metaInfo The MetaInfo to copy.
   */
  public MetaInfo(MetaInfo metaInfo)
  {
    timestampMilliseconds_ = metaInfo.timestampMilliseconds_;
    type_ = metaInfo.type_; 
    freshnessPeriod_ = metaInfo.freshnessPeriod_; 
    // Name.Component is read-only, so we don't need a deep copy.
    finalBlockID_ = metaInfo.finalBlockID_;
  }

  /**
   * @deprecated Use the application-specific content to store a timestamp.
   */
  public final double 
  getTimestampMilliseconds() { return timestampMilliseconds_; }
  
  public final ContentType 
  getType() { return type_; }
  
  public final double 
  getFreshnessPeriod() { return freshnessPeriod_; }
  
  /**
   * @deprecated Use getFreshnessPeriod.
   */
  public final int 
  getFreshnessSeconds() 
  { 
    return freshnessPeriod_ < 0 ? -1 : (int)Math.round(freshnessPeriod_ / 1000.0); 
  }
  
  public final Name.Component 
  getFinalBlockID() { return finalBlockID_; }
  
  /**
   * @deprecated Use the application-specific content to store a timestamp.
   */
  public final void 
  setTimestampMilliseconds(double timestampMilliseconds)
  { 
    timestampMilliseconds_ = timestampMilliseconds; 
    ++changeCount_;
  }
  
  public final void 
  setType(ContentType type)
  { 
    type_ = type; 
    ++changeCount_;
  }
  
  public final void 
  setFreshnessPeriod(double freshnessPeriod) 
  { 
    freshnessPeriod_ = freshnessPeriod; 
    ++changeCount_;
  }
  
  /**
   * @deprecated Use setFreshnessPeriod.
   */
  public final void 
  setFreshnessSeconds(int freshnessSeconds) 
  { 
    setFreshnessPeriod
      (freshnessSeconds < 0 ? -1.0 : (double)freshnessSeconds * 1000.0); 
  }

  public final void 
  setFinalBlockID(Name.Component finalBlockID) 
  { 
    finalBlockID_ = (finalBlockID == null ? new Name.Component() : finalBlockID); 
    ++changeCount_;
  }

  /**
   * Get the change count, which is incremented each time this object is changed.
   * @return The change count.
   */
  public final long 
  getChangeCount() { return changeCount_; }
  
  private double timestampMilliseconds_; /**< milliseconds since 1/1/1970. -1 for none */
  private ContentType type_ = ContentType.BLOB; /**< default is ContentType.BLOB. */
  private double freshnessPeriod_; /**< -1 for none */
  private Name.Component finalBlockID_ = new Name.Component(); /**< size 0 for none */
  private long changeCount_ = 0;
}
