/**
 * Copyright (C) 2013 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * See COPYING for copyright and distribution information.
 */

package net.named_data.jndn.util;

import java.nio.ByteBuffer;

/**
 * A SignedBlob extends Blob to keep the offsets of a signed portion (e.g., the bytes of Data packet). 
 * This inherits from Blob, including Blob.size and Blob.buf.
 */
public class SignedBlob extends Blob {
  /**
   * Create a new SignedBlob with a null pointer and 0 for the offsets.
   */
  public SignedBlob()
  {  
  }
  
  /**
   * Create a new SignedBlob as a copy of the given signedBlob.
   * @param signedBlob The SignedBlob to copy.
   */
  public SignedBlob(SignedBlob signedBlob)
  {
    super(signedBlob.buf());
    signedPortionBeginOffset_ = signedBlob.signedPortionBeginOffset_;
    signedPortionEndOffset_ = signedBlob.signedPortionEndOffset_;
    setSignedBuffer();
  }
  
  /**
   * Create a new SignedBlob and take another pointer to the given blob's buffer.
   * @param blob The Blob from which we take another pointer to the same buffer.
   * @param signedPortionBeginOffset The offset in the buffer of the beginning of the signed portion.
   * @param signedPortionEndOffset The offset in the buffer of the end of the signed portion.
   */
  public SignedBlob(Blob blob, int signedPortionBeginOffset, int signedPortionEndOffset)
  {
    super(blob);
    signedPortionBeginOffset_ = signedPortionBeginOffset;
    signedPortionEndOffset_ = signedPortionEndOffset;
    setSignedBuffer();
  }
  
  /**
   * Create a new SignedBlob from an existing ByteBuffer.  IMPORTANT: After calling this constructor,
   * if you keep a pointer to the buffer then you must treat it as immutable and promise not to change it.
   * @param buffer The existing ByteBuffer.  This takes another reference by calling buffer.asReadOnlyBuffer(). 
   * @param signedPortionBeginOffset The offset in the buffer of the beginning of the signed portion.
   * @param signedPortionEndOffset The offset in the buffer of the end of the signed portion.
   */
  public SignedBlob(ByteBuffer buffer, int signedPortionBeginOffset, int signedPortionEndOffset)
  {
    super(buffer);
    signedPortionBeginOffset_ = signedPortionBeginOffset;
    signedPortionEndOffset_ = signedPortionEndOffset;
    setSignedBuffer();
  }

  /**
   * Create a new SignedBlob with a copy of the bytes in the array.
   * @param value The byte array to copy.
   * @param signedPortionBeginOffset The offset in the buffer of the beginning of the signed portion.
   * @param signedPortionEndOffset The offset in the buffer of the end of the signed portion.
   */
  public SignedBlob(byte[] value, int signedPortionBeginOffset, int signedPortionEndOffset)
  {
    super(value);
    signedPortionBeginOffset_ = signedPortionBeginOffset;
    signedPortionEndOffset_ = signedPortionEndOffset;
    setSignedBuffer();
  }

  /**
   * Return the length of the signed portion of the immutable byte buffer, or null if the pointer is null.
   */
  public int 
  signedSize()
  {
    if (signedBuffer_ != null)
      return signedBuffer_.limit();
    else
      return 0;
  }

  /**
   * Return a new read-only ByteBuffer for the signed portion of the byte buffer, or null if the pointer is null.
   */
  public ByteBuffer
  signedBuf()
  {
    if (signedBuffer_ != null)
      // We call asReadOnlyBuffer each time because it is still allowed to change the position and
      //   limit on a read-only buffer, and we don't want the caller to modify our buffer_.
      return signedBuffer_.asReadOnlyBuffer();
    else
      return null;
  }
  
  /**
   * Set up signedBuffer_ to a slice of buf() based on signedPortionBeginOffset_ and signedPortionEndOffset_.
   */
  private void setSignedBuffer()
  {
    if (!isNull()) {
      // Note that the result of buf() is already a separate ByteBuffer, so it is OK to change the position.
      ByteBuffer tempBuffer = buf();
      tempBuffer.position(signedPortionBeginOffset_);
      tempBuffer.limit(signedPortionEndOffset_);
      // Get a slice which is limited to the signed portion.
      signedBuffer_ = tempBuffer.slice();
    }
    else
      signedBuffer_ = null;
  }
  
  private ByteBuffer signedBuffer_;
  private int signedPortionBeginOffset_;
  private int signedPortionEndOffset_;
}
