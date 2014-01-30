/**
 * Copyright (C) 2013 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * See COPYING for copyright and distribution information.
 */

package net.named_data.jndn;

import net.named_data.jndn.util.Blob;

/**
 * A Sha256WithRsaSignature extends Signature and holds the signature bits and other info representing a
 * SHA256-with-RSA signature in a data packet.
 */
public class Sha256WithRsaSignature extends Signature {
  /**
   * Create a new Sha256WithRsaSignature with default values.
   */
  public Sha256WithRsaSignature()
  {  
    publisherPublicKeyDigestChangeCount_ = publisherPublicKeyDigest_.getChangeCount();
    keyLocatorChangeCount_ = keyLocator_.getChangeCount();
  }
  
  /**
   * Create a new Sha256WithRsaSignature with a copy of the fields in the given signature object.
   * @param signature The signature object to copy.
   */
  public Sha256WithRsaSignature(Sha256WithRsaSignature signature)
  {
    digestAlgorithm_ = signature.digestAlgorithm_;
    witness_ = signature.witness_;
    signature_ = signature.signature_;
    publisherPublicKeyDigest_ = new PublisherPublicKeyDigest(signature.publisherPublicKeyDigest_);
    keyLocator_ = new KeyLocator(signature.keyLocator_);
    publisherPublicKeyDigestChangeCount_ = publisherPublicKeyDigest_.getChangeCount();
    keyLocatorChangeCount_ = keyLocator_.getChangeCount();
  }
  
  /**
   * Return a new Signature which is a deep copy of this signature.
   * @return A new Sha256WithRsaSignature.
   * @throws CloneNotSupportedException 
   */
  @Override
  public Object clone() throws CloneNotSupportedException
  {
    return new Sha256WithRsaSignature(this);
  }
  
  public final Blob 
  getDigestAlgorithm() { return digestAlgorithm_; }

  public final Blob 
  getWitness() { return witness_; }

  public final Blob 
  getSignature() { return signature_; }
  
  public final PublisherPublicKeyDigest 
  getPublisherPublicKeyDigest() { return publisherPublicKeyDigest_; }
  
  public final KeyLocator 
  getKeyLocator() { return keyLocator_; }

  public final void 
  setDigestAlgorithm(Blob digestAlgorithm) 
  {
    digestAlgorithm_ = (digestAlgorithm == null ? new Blob() : digestAlgorithm); 
    ++changeCount_;
  }
  
  public final void 
  setWitness(Blob witness) 
  { 
    witness_ = (witness == null ? new Blob() : witness); 
    ++changeCount_;
  }

  public final void 
  setSignature(Blob signature) 
  { 
    signature_ = (signature == null ? new Blob() : signature); 
    ++changeCount_;
  }

  public final void 
  setPublisherPublicKeyDigest(PublisherPublicKeyDigest publisherPublicKeyDigest) 
  { 
    publisherPublicKeyDigest_ = (publisherPublicKeyDigest == null ? new PublisherPublicKeyDigest() : publisherPublicKeyDigest);
    publisherPublicKeyDigestChangeCount_ = publisherPublicKeyDigest_.getChangeCount();
    ++changeCount_;
  }
  
  public final void 
  setKeyLocator(KeyLocator keyLocator) 
  {
    keyLocator_ = (keyLocator == null ? new KeyLocator() : keyLocator); 
    keyLocatorChangeCount_ = keyLocator_.getChangeCount();
    ++changeCount_;
  }

  @Override
  public long getChangeCount()
  {
    if (publisherPublicKeyDigestChangeCount_ != publisherPublicKeyDigest_.getChangeCount()) {
      ++changeCount_;
      publisherPublicKeyDigestChangeCount_ = publisherPublicKeyDigest_.getChangeCount();
    }
    if (keyLocatorChangeCount_ != keyLocator_.getChangeCount()) {
      ++changeCount_;
      keyLocatorChangeCount_ = keyLocator_.getChangeCount();
    }
    
    return changeCount_;    
  }
  
  private Blob digestAlgorithm_ = new Blob(); /**< if empty, the default is 2.16.840.1.101.3.4.2.1 (sha-256) */
  private Blob witness_ = new Blob();
  private Blob signature_ = new Blob();
  private PublisherPublicKeyDigest publisherPublicKeyDigest_ = new PublisherPublicKeyDigest();
  private long publisherPublicKeyDigestChangeCount_;
  private KeyLocator keyLocator_ = new KeyLocator();
  private long keyLocatorChangeCount_;
  private long changeCount_ = 0;
}
