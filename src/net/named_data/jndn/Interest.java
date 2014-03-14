/**
 * Copyright (C) 2013-2014 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * See COPYING for copyright and distribution information.
 */

package net.named_data.jndn;

import java.nio.ByteBuffer;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.encoding.WireFormat;
import net.named_data.jndn.util.Blob;
import net.named_data.jndn.util.ChangeCounter;
import net.named_data.jndn.util.ChangeCountable;

/**
 * An Interest holds a Name and other fields for an interest.
 */
public class Interest implements ChangeCountable {
  /**
   * Create a new Interest with the given name and interest lifetime and "none" 
   * for other values.
   * @param name The name for the interest.
   * @param interestLifetimeMilliseconds The interest lifetime in milliseconds, 
   * or -1 for none.
   */
  public 
  Interest(Name name, double interestLifetimeMilliseconds) 
  {
    if (name != null)
      name_.set(new Name(name));
    interestLifetimeMilliseconds_ = interestLifetimeMilliseconds;
  }

  /**
   * Create a new Interest with the given name and "none" for other values.
   * @param name The name for the interest.
   */
  public 
  Interest(Name name) 
  {
    if (name != null)
      name_.set(new Name(name));
  }

  /**
   * Create a new Interest with an empty name and "none" for all values.
   */
  public 
  Interest() 
  {
  }

  public static final int CHILD_SELECTOR_LEFT = 0;
  public static final int CHILD_SELECTOR_RIGHT = 1;

  public static final int ANSWER_NO_CONTENT_STORE = 0;
  public static final int ANSWER_CONTENT_STORE = 1;
  public static final int ANSWER_GENERATED = 2;
  public static final int ANSWER_STALE = 4;    // Stale answer OK
  public static final int MARK_STALE = 16;     // Must have scope 0.  
                                               // Michael calls this a "hack"

  public static final int DEFAULT_ANSWER_ORIGIN_KIND = 
    ANSWER_CONTENT_STORE | ANSWER_GENERATED;

  /**
   * Encode this Interest for a particular wire format.
   * @param wireFormat A WireFormat object used to encode this Interest.
   * @return The encoded buffer.
   */
  public final Blob 
  wireEncode(WireFormat wireFormat) 
  {
    return wireFormat.encodeInterest(this);
  }

  /**
   * Encode this Interest for the default wire format 
   * WireFormat.getDefaultWireFormat().
   * @return The encoded buffer.
   */
  public final Blob 
  wireEncode() 
  {
    return this.wireEncode(WireFormat.getDefaultWireFormat());
  }

  /**
   * Decode the input using a particular wire format and update this Interest.
   * @param input The input buffer to decode.  This reads from position() to 
   * limit(), but does not change the position.
   * @param wireFormat A WireFormat object used to decode the input.
   * @throws EncodingException For invalid encoding.
   */
  public final void 
  wireDecode(ByteBuffer input, WireFormat wireFormat) throws EncodingException
  {
    wireFormat.decodeInterest(this, input);
  }

  /**
   * Decode the input using the default wire format 
   * WireFormat.getDefaultWireFormat() and update this Interest.
   * @param input The input buffer to decode.  This reads from position() to 
   * limit(), but does not change the position.
   * @throws EncodingException For invalid encoding.
   */
  public final void 
  wireDecode(ByteBuffer input) throws EncodingException
  {
    wireDecode(input, WireFormat.getDefaultWireFormat());
  }

  /**
   * Decode the input using a particular wire format and update this Interest.
   * @param input The input blob to decode.
   * @param wireFormat A WireFormat object used to decode the input.
   * @throws EncodingException For invalid encoding.
   */
  public final void 
  wireDecode(Blob input, WireFormat wireFormat) throws EncodingException
  {
    wireDecode(input.buf(), wireFormat);
  }

  /**
   * Decode the input using the default wire format 
   * WireFormat.getDefaultWireFormat() and update this Interest.
   * @param input The input blob to decode.
   * @throws EncodingException For invalid encoding.
   */
  public final void 
  wireDecode(Blob input) throws EncodingException
  {
    wireDecode(input.buf());
  }

  /**
   * Encode the name according to the "NDN URI Scheme".  If there are interest 
   * selectors, append "?" and added the selectors as a query string.  For 
   * example "/test/name?ndn.ChildSelector=1".
   * @return The URI string.
   */
  public final String
  toUri()
  {
    StringBuffer selectors = new StringBuffer();

    if (minSuffixComponents_ >= 0)
      selectors.append("&ndn.MinSuffixComponents=").append(minSuffixComponents_);
    if (maxSuffixComponents_ >= 0)
      selectors.append("&ndn.MaxSuffixComponents=").append(maxSuffixComponents_);
    if (childSelector_ >= 0)
      selectors.append("&ndn.ChildSelector=").append(childSelector_);
    if (answerOriginKind_ >= 0)
      selectors.append("&ndn.AnswerOriginKind=").append(answerOriginKind_);
    if (scope_ >= 0)
      selectors.append("&ndn.Scope=").append(scope_);
    if (interestLifetimeMilliseconds_ >= 0)
      selectors.append("&ndn.InterestLifetime=").append
        (interestLifetimeMilliseconds_);
    if (getPublisherPublicKeyDigest().getPublisherPublicKeyDigest().size() > 0) {
      selectors.append("&ndn.PublisherPublicKeyDigest=");
      Name.toEscapedString
        (getPublisherPublicKeyDigest().getPublisherPublicKeyDigest().buf(), 
         selectors);
    }
    if (nonce_.size() > 0) {
      selectors.append("&ndn.Nonce=");
      Name.toEscapedString(nonce_.buf(), selectors);
    }
    if (getExclude().size() > 0)
      selectors.append("&ndn.Exclude=").append(getExclude().toUri());

    StringBuffer result = new StringBuffer();

    result.append(getName().toUri());
    String selectorsString = selectors.toString();
    if (selectorsString.length() > 0)
      // Replace the first & with ?.
      result.append("?").append(selectorsString.substring(1));

    return result.toString();      
  }

  public final Name
  getName() { return (Name)name_.get(); }
  
  public final int 
  getMinSuffixComponents() { return minSuffixComponents_; }
  
  public final int 
  getMaxSuffixComponents() { return maxSuffixComponents_; }
  
  /**
   * @deprecated. The Interest publisherPublicKeyDigest is deprecated.  If you 
   * need a publisher public key digest, set the keyLocator keyLocatorType to 
   * KEY_LOCATOR_DIGEST and set its key data to the digest.
   */
  public final PublisherPublicKeyDigest
  getPublisherPublicKeyDigest() 
  { 
    return (PublisherPublicKeyDigest)publisherPublicKeyDigest_.get(); 
  }
  
  public final KeyLocator
  getKeyLocator() { return (KeyLocator)keyLocator_.get(); }

  public final Exclude
  getExclude() { return (Exclude)exclude_.get(); }
  
  public final int 
  getChildSelector() { return childSelector_; }

  public final int 
  getAnswerOriginKind() { return answerOriginKind_; }

  public final int 
  getScope() { return scope_; }

  public final double 
  getInterestLifetimeMilliseconds() { return interestLifetimeMilliseconds_; }

  /**
   * Return the nonce value from the incoming interest.  If you change any of 
   * the fields in this Interest object, then the nonce value is cleared.
   * @return The nonce.
   */
  public final Blob
  getNonce() 
  { 
    if (getNonceChangeCount_ != getChangeCount()) {
      // The values have changed, so the existing nonce is invalidated.
      nonce_ = new Blob();
      getNonceChangeCount_ = getChangeCount();
    }

    return nonce_; 
  }

  public final void
  setName(Name name) 
  { 
    name_.set(name == null ? new Name() : name);
    ++changeCount_;
  }
  
  public final void 
  setMinSuffixComponents(int minSuffixComponents) 
  {
    minSuffixComponents_ = minSuffixComponents;
    ++changeCount_;
  }
  
  public final void 
  setMaxSuffixComponents(int maxSuffixComponents) 
  { 
    maxSuffixComponents_ = maxSuffixComponents; 
    ++changeCount_;
  }
  
  public final void 
  setChildSelector(int childSelector) 
  { 
    childSelector_ = childSelector; 
    ++changeCount_;
  }

  public final void 
  setAnswerOriginKind(int answerOriginKind) 
  { 
    answerOriginKind_ = answerOriginKind; 
    ++changeCount_;
  }

  public final void 
  setScope(int scope) 
  { 
    scope_ = scope; 
    ++changeCount_;
  }

  public final void 
  setInterestLifetimeMilliseconds(double interestLifetimeMilliseconds) 
  { 
    interestLifetimeMilliseconds_ = interestLifetimeMilliseconds; 
    ++changeCount_;
  }

  /**
   * @deprecated You should let the wire encoder generate a random nonce 
   * internally before sending the interest.
   */
  public final void 
  setNonce(Blob nonce) 
  { 
    nonce_ = (nonce == null ? new Blob() : nonce); 
    // Set getNonceChangeCount_ so that the next call to getNonce() won't 
    //   clear nonce_.
    ++changeCount_;
    getNonceChangeCount_ = getChangeCount();
  }

  public final void
  setKeyLocator(KeyLocator keyLocator) 
  { 
    keyLocator_.set(keyLocator == null ? new KeyLocator() : keyLocator);
    ++changeCount_;
  }
  
  /**
   * Get the change count, which is incremented each time this object 
   * (or a child object) is changed.
   * @return The change count.
   */
  public final long 
  getChangeCount()
  {
    // Make sure each of the checkChanged is called.
    boolean changed = name_.checkChanged();
    changed = publisherPublicKeyDigest_.checkChanged() || changed;
    changed = keyLocator_.checkChanged() || changed;
    changed = exclude_.checkChanged() || changed;
    if (changed)
      // A child object has changed, so update the change count.
      ++changeCount_;
    
    return changeCount_;
  }

  private final ChangeCounter name_ = new ChangeCounter(new Name());
  private int minSuffixComponents_ = -1;
  private int maxSuffixComponents_ = -1;  
  /** @deprecated. The Interest publisherPublicKeyDigest is deprecated. If you 
   * need a publisher public key digest, set the keyLocator keyLocatorType to 
   * KEY_LOCATOR_DIGEST and set its key data to the digest. */
  private final ChangeCounter publisherPublicKeyDigest_ = 
    new ChangeCounter(new PublisherPublicKeyDigest());
  private final ChangeCounter keyLocator_ = new ChangeCounter(new KeyLocator());
  private final ChangeCounter exclude_ = new ChangeCounter(new Exclude());
  private int childSelector_ = -1;
  private int answerOriginKind_ = -1;
  private int scope_ = -1;
  private double interestLifetimeMilliseconds_ = -1;
  private Blob nonce_ = new Blob();
  private long getNonceChangeCount_ = 0;
  private long changeCount_ = 0;
}
