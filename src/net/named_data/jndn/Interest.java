/**
 * Copyright (C) 2013 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * See COPYING for copyright and distribution information.
 */

package net.named_data.jndn;

import java.nio.ByteBuffer;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.util.Blob;
import net.named_data.jndn.encoding.WireFormat;

/**
 * An Interest holds a Name and other fields for an interest.
 */
public class Interest {
  /*
  public Interest(const Name& name, int minSuffixComponents, int maxSuffixComponents, 
    const PublisherPublicKeyDigest& publisherPublicKeyDigest, const Exclude& exclude, int childSelector, int answerOriginKind, 
    int scope, Milliseconds interestLifetimeMilliseconds, const std::vector<uint8_t>& nonce) 
  : name_(name), minSuffixComponents_(minSuffixComponents), maxSuffixComponents_(maxSuffixComponents),
  publisherPublicKeyDigest_(publisherPublicKeyDigest), exclude_(exclude), childSelector_(childSelector), 
  answerOriginKind_(answerOriginKind), scope_(scope), interestLifetimeMilliseconds_(interestLifetimeMilliseconds),
  nonce_(nonce)
  {
  }

  public Interest(const Name& name, int minSuffixComponents, int maxSuffixComponents, 
    const PublisherPublicKeyDigest& publisherPublicKeyDigest, const Exclude& exclude, int childSelector, int answerOriginKind, 
    int scope, Milliseconds interestLifetimeMilliseconds) 
  : name_(name), minSuffixComponents_(minSuffixComponents), maxSuffixComponents_(maxSuffixComponents),
  publisherPublicKeyDigest_(publisherPublicKeyDigest), exclude_(exclude), childSelector_(childSelector), 
  answerOriginKind_(answerOriginKind), scope_(scope), interestLifetimeMilliseconds_(interestLifetimeMilliseconds)
  {
  }
  */
  
  /**
   * Create a new Interest with the given name and interest lifetime and "none" for other values.
   * @param name The name for the interest.
   * @param interestLifetimeMilliseconds The interest lifetime in milliseconds, or -1 for none.
   */
  public 
  Interest(Name name, double interestLifetimeMilliseconds) 
  {
    if (name != null)
      name_ = new Name(name);
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
      name_ = new Name(name);
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
  public static final int MARK_STALE = 16;     // Must have scope 0.  Michael calls this a "hack"

  public static final int DEFAULT_ANSWER_ORIGIN_KIND = ANSWER_CONTENT_STORE | ANSWER_GENERATED;

  /**
   * Encode this Interest for a particular wire format.
   * @param wireFormat A WireFormat object used to decode the input.
   * @return The encoded buffer.
   */
  public Blob 
  wireEncode(WireFormat wireFormat) 
  {
    return wireFormat.encodeInterest(this);
  }

  /**
   * Encode this Interest for the default wire format WireFormat.getDefaultWireFormat().
   * @return The encoded buffer.
   */
  public Blob 
  wireEncode() 
  {
    return this.wireEncode(WireFormat.getDefaultWireFormat());
  }

  /**
   * Decode the input using a particular wire format and update this Interest.
   * @param input The input buffer to decode.  This reads from position() to limit(), but does not change the position.
   * @param wireFormat A WireFormat object used to decode the input.
   * @throws EncodingException For invalid encoding.
   */
  public void 
  wireDecode(ByteBuffer input, WireFormat wireFormat) throws EncodingException
  {
    wireFormat.decodeInterest(this, input);
  }

  /**
   * Decode the input using the default wire format WireFormat.getDefaultWireFormat() and update this Interest.
   * @param input The input buffer to decode.  This reads from position() to limit(), but does not change the position.
   * @throws EncodingException For invalid encoding.
   */
  public void 
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
  public void 
  wireDecode(Blob input, WireFormat wireFormat) throws EncodingException
  {
    wireDecode(input.buf(), wireFormat);
  }

  /**
   * Decode the input using the default wire format WireFormat.getDefaultWireFormat() and update this Interest.
   * @param input The input blob to decode.
   * @throws EncodingException For invalid encoding.
   */
  public void 
  wireDecode(Blob input) throws EncodingException
  {
    wireDecode(input.buf());
  }

  public Name
  getName() { return name_; }
  
  public int 
  getMinSuffixComponents() { return minSuffixComponents_; }
  
  public int 
  getMaxSuffixComponents() { return maxSuffixComponents_; }
  
  public PublisherPublicKeyDigest
  getPublisherPublicKeyDigest() { return publisherPublicKeyDigest_; }
  
  /* TODO
  public Exclude
  getExclude() { return exclude_; }
  */
  
  public int 
  getChildSelector() { return childSelector_; }

  public int 
  getAnswerOriginKind() { return answerOriginKind_; }

  public int 
  getScope() { return scope_; }

  public double 
  getInterestLifetimeMilliseconds() { return interestLifetimeMilliseconds_; }

  public Blob
  getNonce() { return nonce_; }

  public void
  setName(Name name) 
  {
    if (name != null)
      name_ = name; 
    else
      name_ = new Name();
  }
  
  public void 
  setMinSuffixComponents(int value) { minSuffixComponents_ = value; }
  
  public void 
  setMaxSuffixComponents(int value) { maxSuffixComponents_ = value; }
  
  public void 
  setChildSelector(int value) { childSelector_ = value; }

  public void 
  setAnswerOriginKind(int value) { answerOriginKind_ = value; }

  public void 
  setScope(int value) { scope_ = value; }

  public void 
  setInterestLifetimeMilliseconds(double value) { interestLifetimeMilliseconds_ = value; }

  public void 
  setNonce(Blob value) 
  {
    if (value != null)
      nonce_ = value; 
    else
      nonce_ = new Blob();
  }
  
  private Name name_ = new Name();
  private int minSuffixComponents_ = -1;
  private int maxSuffixComponents_ = -1;  
  private final PublisherPublicKeyDigest publisherPublicKeyDigest_ = new PublisherPublicKeyDigest();
  /* TODO
  private Exclude exclude_ = new Exclude();
  */
  private int childSelector_ = -1;
  private int answerOriginKind_ = -1;
  private int scope_ = -1;
  private double interestLifetimeMilliseconds_ = -1;
  private Blob nonce_ = new Blob();
}
