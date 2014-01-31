/**
 * Copyright (C) 2013 Regents of the University of California.
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
import net.named_data.jndn.util.SignedBlob;


public class Data implements ChangeCountable {
  /**
   * Create a new Data object with default values and where the signature is a blank Sha256WithRsaSignature.
   */
  public Data()
  {  
  }
  
  /**
   * Create a new Data object with the given name and default values and where the signature is a blank Sha256WithRsaSignature.
   * @param name The name which is copied.
   */
  public Data(Name name)
  {
    name_.set(new Name(name));
  }
  
  /**
   * Create a deep copy of the given data object, including a clone of the signature object.
   * @param data The data object to copy.
   */
  public Data(Data data)
  {
    try {
      signature_.set(data.signature_ == null ? new Sha256WithRsaSignature() : (Signature)data.signature_.get().clone());
    } 
    catch (CloneNotSupportedException e) {
      // We don't expect this to happen, so just treat it as if we got a null pointer.
      throw new NullPointerException("Data.setSignature: unexpected exception in clone(): " + e.getMessage());
    }

    name_.set(new Name(data.name_.get()));
    metaInfo_.set(new MetaInfo(data.metaInfo_.get()));
    content_ = data.content_;
    setDefaultWireEncoding(data.defaultWireEncoding_);
  }
  
  /**
   * Encode this Data for a particular wire format. If wireFormat is the default wire format, also set the defaultWireEncoding 
   * field to the encoded result.
   * @param wireFormat A WireFormat object used to decode the input.
   * @return The encoded buffer.
   */
  public final SignedBlob 
  wireEncode(WireFormat wireFormat)
  {
    int[] signedPortionBeginOffset = new int[1];
    int[] signedPortionEndOffset = new int[1];
    Blob encoding = wireFormat.encodeData(this, signedPortionBeginOffset, signedPortionEndOffset);
    SignedBlob wireEncoding = new SignedBlob(encoding, signedPortionBeginOffset[0], signedPortionEndOffset[0]);

    if (wireFormat == WireFormat.getDefaultWireFormat())
      // This is the default wire encoding.
      setDefaultWireEncoding(wireEncoding);

    return wireEncoding;
  }

  /**
   * Encode this Data for the default wire format WireFormat.getDefaultWireFormat(). Also set the defaultWireEncoding 
   * field to the encoded result.
   * @return The encoded buffer.
   */
  public final SignedBlob 
  wireEncode()
  {
    return wireEncode(WireFormat.getDefaultWireFormat());
  }
    
  /**
   * Decode the input using a particular wire format and update this Data. If wireFormat is the default wire format, also 
   * set the defaultWireEncoding field to the input.
   * @param input The input buffer to decode.  This reads from position() to limit(), but does not change the position.
   * @param wireFormat A WireFormat object used to decode the input.
   * @throws EncodingException For invalid encoding.
   */
  public final void 
  wireDecode(ByteBuffer input, WireFormat wireFormat) throws EncodingException
  {
    int[] signedPortionBeginOffset = new int[1];
    int[] signedPortionEndOffset = new int[1];
    wireFormat.decodeData(this, input, signedPortionBeginOffset, signedPortionEndOffset);

    if (wireFormat == WireFormat.getDefaultWireFormat())
      // This is the default wire encoding.
      setDefaultWireEncoding(new SignedBlob(input, true, signedPortionBeginOffset[0], signedPortionEndOffset[0]));
    else
      setDefaultWireEncoding(new SignedBlob());
  }

  /**
   * Decode the input using the default wire format WireFormat.getDefaultWireFormat() and update this Data. Also 
   * set the defaultWireEncoding field to the input.
   * @param input The input buffer to decode.  This reads from position() to limit(), but does not change the position.
   * @throws EncodingException For invalid encoding.
   */
  public final void 
  wireDecode(ByteBuffer input) throws EncodingException
  {
    wireDecode(input, WireFormat.getDefaultWireFormat());
  }

  public final Signature 
  getSignature() { return signature_.get(); }
  
  public final Name 
  getName() { return name_.get(); }
  
  public final MetaInfo 
  getMetaInfo() { return metaInfo_.get(); }
  
  public final Blob 
  getContent() { return content_; }

  /**
   * Return a pointer to the defaultWireEncoding.
   * @return The default wire encoding. Its pointer may be null.
   */
  public final SignedBlob
  getDefaultWireEncoding() 
  { 
    if (getDefaultWireEncodingChangeCount_ != getChangeCount()) {
      // The values have changed, so the default wire encoding is invalidated.
      defaultWireEncoding_ = new SignedBlob();
      getDefaultWireEncodingChangeCount_ = getChangeCount();
    }
    
    return defaultWireEncoding_; 
  }
  
  /**
   * Set the signature to a copy of the given signature.
   * @param signature The signature object which is cloned.
   * @return This Data so that you can chain calls to update values.
   */
  public final Data 
  setSignature(Signature signature) 
  { 
    try {
      signature_.set(signature == null ? new Sha256WithRsaSignature() : (Signature)signature.clone());
    } 
    catch (CloneNotSupportedException e) {
      // We don't expect this to happen, so just treat it as if we got a null pointer.
      throw new NullPointerException("Data.setSignature: unexpected exception in clone(): " + e.getMessage());
    }
    
    ++changeCount_;
    return this;
  }
  
  /**
   * Set name to a copy of the given Name.  This is not final so that a subclass can override to validate the name.
   * @param name The Name which is copied.
   * @return This Data so that you can chain calls to update values.
   */
  public Data 
  setName(Name name)
  { 
    name_.set((name == null ? new Name() : name)); 
    ++changeCount_;
    return this;
  }
  
  /**
   * Set metaInfo to a copy of the given MetaInfo.
   * @param metaInfo The MetaInfo which is copied.
   * @return This Data so that you can chain calls to update values.
   */
  public final Data 
  setMetaInfo(MetaInfo metaInfo) 
  { 
    metaInfo_.set(metaInfo == null ? new MetaInfo(metaInfo) : metaInfo); 
    ++changeCount_;
    return this;
  }

  public final Data 
  setContent(Blob content) 
  { 
    content_ = (content == null ? new Blob() : content);
    ++changeCount_;
    return this;
  }

  /**
   * Get the change count, which is incremented each time this object (or a child object) is changed.
   * @return The change count.
   */
  @Override
  public final long 
  getChangeCount()
  {
    // Make sure each of the checkChanged is called.
    boolean changed = signature_.checkChanged();
    changed = name_.checkChanged() || changed;
    changed = metaInfo_.checkChanged() || changed;
    if (changed)
      // A child object has changed, so update the change count.
      ++changeCount_;
    
    return changeCount_;
  }
  
  private void
  setDefaultWireEncoding(SignedBlob defaultWireEncoding)
  {
    defaultWireEncoding_ = defaultWireEncoding;
    // Set getDefaultWireEncodingChangeCount_ so that the next call to getDefaultWireEncoding() won't clear defaultWireEncoding_.
    getDefaultWireEncodingChangeCount_ = getChangeCount();
  }

  private final ChangeCounter<Signature> signature_ = new ChangeCounter<Signature>(new Sha256WithRsaSignature());
  private final ChangeCounter<Name> name_ = new ChangeCounter<Name>(new Name());
  private final ChangeCounter<MetaInfo> metaInfo_ = new ChangeCounter<MetaInfo>(new MetaInfo());
  private Blob content_ = new Blob();
  private SignedBlob defaultWireEncoding_ = new SignedBlob();
  private long getDefaultWireEncodingChangeCount_ = 0;
  private long changeCount_ = 0;
}
