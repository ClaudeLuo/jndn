/**
 * Copyright (C) 2014 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * See COPYING for copyright and distribution information.
 */

package net.named_data.jndn.security.identity;

import java.util.ArrayList;
import java.util.HashMap;
import net.named_data.jndn.Data;
import net.named_data.jndn.Name;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.KeyType;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.certificate.IdentityCertificate;
import net.named_data.jndn.util.Blob;

/**
 * MemoryIdentityStorage extends IdentityStorage and implements its methods to 
 * store identity, public key and certificate objects in memory. The application 
 * must get the objects through its own means and add the objects to the 
 * MemoryIdentityStorage object. To use permanent file-based storage, see 
 * BasicIdentityStorage.
 */
public class MemoryIdentityStorage extends IdentityStorage {
  /**
   * Check if the specified identity already exists.
   * @param identityName The identity name.
   * @return True if the identity exists, otherwise false.
   */
  public boolean 
  doesIdentityExist(Name identityName)
  {
    return identityStore_.contains(identityName.toUri());
  }

  /**
   * Add a new identity. An exception will be thrown if the identity already 
   * exists.
   * @param identityName The identity name to be added.
   * @throws SecurityException if the identityName is already added.
   */
  public void
  addIdentity(Name identityName) throws SecurityException
  {
    String identityUri = identityName.toUri();
    if (identityStore_.contains(identityUri))
      throw new SecurityException("Identity already exists: " + identityUri);

    identityStore_.add(identityUri);
  }
  
  /**
   * Revoke the identity.
   * @return True if the identity was revoked, false if not.
   */
  public boolean
  revokeIdentity()
  {
    throw new UnsupportedOperationException
      ("MemoryIdentityStorage::revokeIdentity not implemented");
  }

  /**
   * Check if the specified key already exists.
   * @param keyName The name of the key.
   * @return true if the key exists, otherwise false.
   */
  public boolean 
  doesKeyExist(Name keyName)
  {
    return keyStore_.containsKey(keyName);
  }

  /**
   * Add a public key to the identity storage.
   * @param keyName The name of the public key to be added.
   * @param keyType Type of the public key to be added.
   * @param publicKeyDer A blob of the public key DER to be added.
   * @throws SecurityException if a key with the keyName already exists.
   */
  public void 
  addKey(Name keyName, KeyType keyType, Blob publicKeyDer) throws SecurityException
  {
    Name identityName = keyName.getSubName(0, keyName.size() - 1);

    if (!doesIdentityExist(identityName))
      addIdentity(identityName);

    if (doesKeyExist(keyName))
      throw new SecurityException("a key with the same name already exists!");

    keyStore_ .put(keyName.toUri(), new KeyRecord(keyType, publicKeyDer));
  }

  /**
   * Get the public key DER blob from the identity storage.
   * @param keyName The name of the requested public key.
   * @return The DER Blob.  If not found, return a Blob with a null pointer.
   */
  public Blob
  getKey(Name keyName)
  {
    KeyRecord keyRecord = (KeyRecord)keyStore_.get(keyName.toUri());
    if (keyRecord == null)
      // Not found.  Silently return a null Blob.
      return new Blob();

    return keyRecord.getKeyDer();
  }

  /**
   * Activate a key.  If a key is marked as inactive, its private part will not 
   * be used in packet signing.
   * @param keyName The name of the key.
   */
  public void 
  activateKey(Name keyName)
  {
    throw new UnsupportedOperationException
      ("MemoryIdentityStorage::activateKey not implemented");
  }

  /**
   * Deactivate a key. If a key is marked as inactive, its private part will not 
   * be used in packet signing.
   * @param keyName The name of the key.
   */
  public void 
  deactivateKey(Name keyName)
  {
    throw new UnsupportedOperationException
      ("MemoryIdentityStorage::deactivateKey not implemented");
  }

  /**
   * Check if the specified certificate already exists.
   * @param certificateName The name of the certificate.
   * @return True if the certificate exists, otherwise false.
   */
  public boolean
  doesCertificateExist(Name certificateName)
  {
    return certificateStore_.containsKey(certificateName.toUri());
  }

  /**
   * Add a certificate to the identity storage.
   * @param certificate The certificate to be added.  This makes a copy of the 
   * certificate.
   * @throws SecurityException if the certificate is already installed.
   */
  public void 
  addCertificate(IdentityCertificate certificate) throws SecurityException
  {
    throw new UnsupportedOperationException
      ("MemoryIdentityStorage::addCertificate not implemented");
    /*
    Name certificateName = certificate.getName();
    Name keyName = certificate.getPublicKeyName();

    if (!doesKeyExist(keyName))
      throw new SecurityException
        ("No corresponding Key record for certificate! " + keyName.toUri() + 
         " " + certificateName.toUri());

    // Check if certificate has already existed!
    if (doesCertificateExist(certificateName))
      throw new SecurityException("Certificate has already been installed!");

    // Check if the public key of certificate is the same as the key record. 
    Blob keyBlob = getKey(keyName);
    if (keyBlob.isNull() || 
        !keyBlob.equals(certificate.getPublicKeyInfo().getKeyDer()))
      throw new SecurityException("Certificate does not match the public key!");

    // Insert the certificate.
    certificateStore_.put(certificateName.toUri(), certificate.wireEncode());
    */
  }

  /**
   * Get a certificate from the identity storage.
   * @param certificateName The name of the requested certificate.
   * @param allowAny If false, only a valid certificate will be 
   * returned, otherwise validity is disregarded.
   * @return The requested certificate. If not found, return null.
   */
  public Data
  getCertificate(Name certificateName, boolean allowAny)
  {
    Blob certificateDer = (Blob)certificateStore_.get(certificateName.toUri());
    if (certificateDer == null)
      // Not found.  Silently return null.
      return new Data();

    Data data = new Data();
    try {
      data.wireDecode(certificateDer);
    } 
    catch (EncodingException ex) {
      // Don't expect this to happen. Silently return null.
      return new Data();
    }
    return data;
  }

  /*****************************************
   *           Get/Set Default             *
   *****************************************/

  /**
   * Get the default identity. 
   * @return The name of default identity.
   * @throws SecurityException if the default identity is not set.
   */
  public Name 
  getDefaultIdentity() throws SecurityException
  {
    if (defaultIdentity_.length() == 0)
      throw new SecurityException("MemoryIdentityStorage.getDefaultIdentity: The default identity is not defined");
  
    return new Name(defaultIdentity_);
  }

  /**
   * Get the default key name for the specified identity.
   * @param identityName The identity name.
   * @return The default key name.
   */
  public Name 
  getDefaultKeyNameForIdentity(Name identityName)
  {
    throw new UnsupportedOperationException
      ("MemoryIdentityStorage::getDefaultKeyNameForIdentity not implemented");
  }

  /**
   * Get the default certificate name for the specified key.
   * @param keyName The key name.
   * @return The default certificate name.
   */
  public Name 
  getDefaultCertificateNameForKey(Name keyName)
  {
    throw new UnsupportedOperationException
      ("MemoryIdentityStorage::getDefaultCertificateNameForKey not implemented");
  }

  /**
   * Set the default identity.  If the identityName does not exist, then clear 
   * the default identity so that getDefaultIdentity() throws an exception.
   * @param identityName The default identity name.
   */
  public void 
  setDefaultIdentity(Name identityName)
  {
    String identityUri = identityName.toUri();
    if (identityStore_.contains(identityUri))
      defaultIdentity_ = identityUri;
    else
      // The identity doesn't exist, so clear the default.
      defaultIdentity_ = "";
  }

  /**
   * Set the default key name for the specified identity.
   * @param keyName The key name.
   * @param identityNameCheck The identity name to check the keyName.
   */
  public void 
  setDefaultKeyNameForIdentity(Name keyName, Name identityNameCheck)
  {
    throw new UnsupportedOperationException
      ("MemoryIdentityStorage::setDefaultKeyNameForIdentity not implemented");
  }

  /**
   * Set the default key name for the specified identity.
   * @param keyName The key name.
   * @param certificateName The certificate name.
   */
  public void 
  setDefaultCertificateNameForKey(Name keyName, Name certificateName)
  {
    throw new UnsupportedOperationException
      ("MemoryIdentityStorage::setDefaultCertificateNameForKey not implemented");
  }  

  private static class KeyRecord {
    public KeyRecord(KeyType keyType, Blob keyDer)
    {
      keyType_ = keyType;
      keyDer_ = keyDer;
    }
    
    KeyType getKeyType() { return keyType_; }
    
    Blob getKeyDer() { return keyDer_; }
    
    private KeyType keyType_;
    private Blob keyDer_;
  };
  
  private final ArrayList identityStore_ = 
    new ArrayList(); /**< A list of String name URI. */
  private String defaultIdentity_ = 
    ""; /**< The default identity in identityStore_, or "" if not defined. */
  private final HashMap keyStore_ = 
    new HashMap(); /**< The map key is the keyName.toUri(). The value is a KeyRecord. */
  private final HashMap certificateStore_ = 
    new HashMap(); /**< The map key is the certificateName.toUri(). The value is the certificate Blob. */
}
