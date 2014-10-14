/**
 * Copyright (C) 2013-2014 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * A copy of the GNU Lesser General Public License is in the file COPYING.
 */

package net.named_data.jndn;

import java.nio.ByteBuffer;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.encoding.WireFormat;
import net.named_data.jndn.util.Blob;
import net.named_data.jndn.util.ChangeCountable;
import net.named_data.jndn.util.ChangeCounter;
import net.named_data.jndn.util.SignedBlob;

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
   * Create a new interest as a deep copy of the given interest.
   * @param interest The interest to copy.
   */
  public
  Interest(Interest interest)
  {
    name_.set(new Name(interest.getName()));
    minSuffixComponents_ = interest.minSuffixComponents_;
    maxSuffixComponents_ = interest.maxSuffixComponents_;
    publisherPublicKeyDigest_.set
      (new PublisherPublicKeyDigest(interest.getPublisherPublicKeyDigest()));
    keyLocator_.set(new KeyLocator(interest.getKeyLocator()));
    exclude_.set(new Exclude(interest.getExclude()));
    childSelector_ = interest.childSelector_;
    answerOriginKind_ = interest.answerOriginKind_;

    interestLifetimeMilliseconds_ = interest.interestLifetimeMilliseconds_;
    scope_ = interest.scope_;
    nonce_ = interest.getNonce();
    setDefaultWireEncoding(interest.defaultWireEncoding_, null);
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
   * Encode this Interest for a particular wire format. If wireFormat is the
   * default wire format, also set the defaultWireEncoding field to the encoded
   * result.
   * @param wireFormat A WireFormat object used to encode this Interest.
   * @return The encoded buffer.
   */
  public final SignedBlob
  wireEncode(WireFormat wireFormat)
  {
    if (!getDefaultWireEncoding().isNull() &&
        getDefaultWireEncodingFormat() == wireFormat)
      // We already have an encoding in the desired format.
      return getDefaultWireEncoding();

    int[] signedPortionBeginOffset = new int[1];
    int[] signedPortionEndOffset = new int[1];
    Blob encoding = wireFormat.encodeInterest
      (this, signedPortionBeginOffset, signedPortionEndOffset);
    SignedBlob wireEncoding = new SignedBlob
      (encoding, signedPortionBeginOffset[0], signedPortionEndOffset[0]);

    if (wireFormat == WireFormat.getDefaultWireFormat())
      // This is the default wire encoding.
      setDefaultWireEncoding(wireEncoding, WireFormat.getDefaultWireFormat());

    return wireEncoding;
  }

  /**
   * Encode this Interest for the default wire format
   * WireFormat.getDefaultWireFormat().
   * @return The encoded buffer.
   */
  public final SignedBlob
  wireEncode()
  {
    return wireEncode(WireFormat.getDefaultWireFormat());
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
    int[] signedPortionBeginOffset = new int[1];
    int[] signedPortionEndOffset = new int[1];
    wireFormat.decodeInterest
      (this, input, signedPortionBeginOffset, signedPortionEndOffset);

    if (wireFormat == WireFormat.getDefaultWireFormat())
      // This is the default wire encoding.
      setDefaultWireEncoding
        (new SignedBlob(input, true, signedPortionBeginOffset[0],
         signedPortionEndOffset[0]), WireFormat.getDefaultWireFormat());
    else
      setDefaultWireEncoding(new SignedBlob(), null);
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
   * Decode the input using a particular wire format and update this Interest. If
   * wireFormat is the default wire format, also set the defaultWireEncoding
   * field another pointer to the input Blob.
   * @param input The input blob to decode.
   * @param wireFormat A WireFormat object used to decode the input.
   * @throws EncodingException For invalid encoding.
   */
  public final void
  wireDecode(Blob input, WireFormat wireFormat) throws EncodingException
  {
    int[] signedPortionBeginOffset = new int[1];
    int[] signedPortionEndOffset = new int[1];
    wireFormat.decodeInterest
      (this, input.buf(), signedPortionBeginOffset, signedPortionEndOffset);

    if (wireFormat == WireFormat.getDefaultWireFormat())
      // This is the default wire encoding.
      setDefaultWireEncoding
        (new SignedBlob(input, signedPortionBeginOffset[0],
         signedPortionEndOffset[0]), WireFormat.getDefaultWireFormat());
    else
      setDefaultWireEncoding(new SignedBlob(), null);
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
    wireDecode(input, WireFormat.getDefaultWireFormat());
  }

  /**
   * Encode the name according to the "NDN URI Scheme".  If there are interest
   * selectors, append "?" and added the selectors as a query string.  For
   * example "/test/name?ndn.ChildSelector=1".
   * @return The URI string.
   * @note This is an experimental feature.  See the API docs for more detail at
   * http://named-data.net/doc/ndn-ccl-api/interest.html#interest-touri-method .
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
        ((long)Math.round(interestLifetimeMilliseconds_));
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
   * @deprecated The Interest publisherPublicKeyDigest is deprecated.  If you
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

  /**
   * @deprecated Use getMustBeFresh.
   */
  public final int
  getAnswerOriginKind() { return answerOriginKind_; }

  /**
   * Get the must be fresh flag. If not specified, the default is true.
   * @return The must be fresh flag.
   */
  public final boolean
  getMustBeFresh()
  {
    if (answerOriginKind_ < 0)
      return true;
    else
      return (answerOriginKind_ & ANSWER_STALE) == 0;
  }

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
    name_.set(name == null ? new Name() : new Name(name));
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

  /**
   * @deprecated Use setMustBeFresh.
   */
  public final void
  setAnswerOriginKind(int answerOriginKind)
  {
    answerOriginKind_ = answerOriginKind;
    ++changeCount_;
  }

  /**
   * Set the MustBeFresh flag.
   * @param mustBeFresh True if the content must be fresh, otherwise false.
   */
  public final void
  setMustBeFresh(boolean mustBeFresh)
  {
    if (answerOriginKind_ < 0) {
      // It is is already the default where MustBeFresh is true.
      if (!mustBeFresh) {
        // Set answerOriginKind_ so that getMustBeFresh returns false.
        answerOriginKind_ = ANSWER_STALE;
        ++changeCount_;
      }
    }
    else {
      if (mustBeFresh)
        // Clear the stale bit.
        answerOriginKind_ &= ~ANSWER_STALE;
      else
        // Set the stale bit.
        answerOriginKind_ |= ANSWER_STALE;
      ++changeCount_;
    }
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
    keyLocator_.set(keyLocator == null ? new KeyLocator() : new KeyLocator(keyLocator));
    ++changeCount_;
  }

  public final void
  setExclude(Exclude exclude)
  {
    exclude_.set(exclude == null ? new Exclude() : new Exclude(exclude));
    ++changeCount_;
  }

  /**
   * Check if this Interest's name matches the given name (using Name.match)
   * and the given name also conforms to the interest selectors.
   * @param name The name to check.
   * @return True if the name and interest selectors match, otherwise false.
   */
  public final boolean
  matchesName(Name name)
  {
    if (!getName().match(name))
      return false;

    if (minSuffixComponents_ >= 0 &&
        // Add 1 for the implicit digest.
        !(name.size() + 1 - getName().size() >= minSuffixComponents_))
      return false;
    if (maxSuffixComponents_ >= 0 &&
        // Add 1 for the implicit digest.
        !(name.size() + 1 - getName().size() <= maxSuffixComponents_))
      return false;
    if (getExclude().size() > 0 && name.size() > getName().size() &&
        getExclude().matches(name.get(getName().size())))
      return false;

    return true;
  }

  /**
   * Return a pointer to the defaultWireEncoding, which was encoded with
   * getDefaultWireEncodingFormat().
   * @return The default wire encoding. Its pointer may be null.
   */
  public final SignedBlob
  getDefaultWireEncoding()
  {
    if (getDefaultWireEncodingChangeCount_ != getChangeCount()) {
      // The values have changed, so the default wire encoding is invalidated.
      defaultWireEncoding_ = new SignedBlob();
      defaultWireEncodingFormat_ = null;
      getDefaultWireEncodingChangeCount_ = getChangeCount();
    }

    return defaultWireEncoding_;
  }

  /**
   * Get the WireFormat which is used by getDefaultWireEncoding().
   * @return The WireFormat, which is only meaningful if the
   * getDefaultWireEncoding() does not have a null pointer.
   */
  WireFormat
  getDefaultWireEncodingFormat() { return defaultWireEncodingFormat_; }

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

  private void
  setDefaultWireEncoding
    (SignedBlob defaultWireEncoding, WireFormat defaultWireEncodingFormat)
  {
    defaultWireEncoding_ = defaultWireEncoding;
    defaultWireEncodingFormat_ = defaultWireEncodingFormat;
    // Set getDefaultWireEncodingChangeCount_ so that the next call to
    //   getDefaultWireEncoding() won't clear defaultWireEncoding_.
    getDefaultWireEncodingChangeCount_ = getChangeCount();
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
  private SignedBlob defaultWireEncoding_ = new SignedBlob();
  private WireFormat defaultWireEncodingFormat_;
  private long getDefaultWireEncodingChangeCount_ = 0;
  private long changeCount_ = 0;
}
