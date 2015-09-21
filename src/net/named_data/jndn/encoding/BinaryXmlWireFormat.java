/**
 * Copyright (C) 2013-2015 Regents of the University of California.
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

package net.named_data.jndn.encoding;

import java.nio.ByteBuffer;
import net.named_data.jndn.ContentType;
import net.named_data.jndn.Data;
import net.named_data.jndn.Exclude;
import net.named_data.jndn.ForwardingEntry;
import net.named_data.jndn.Interest;
import net.named_data.jndn.KeyLocator;
import net.named_data.jndn.KeyLocatorType;
import net.named_data.jndn.KeyNameType;
import net.named_data.jndn.MetaInfo;
import net.named_data.jndn.Name;
import net.named_data.jndn.PublisherPublicKeyDigest;
import net.named_data.jndn.Sha256WithRsaSignature;
import net.named_data.jndn.util.Blob;

public class BinaryXmlWireFormat extends WireFormat {
  public BinaryXmlWireFormat()
  {
    if (!WireFormat.ENABLE_NDNX)
      throw new Error
        ("BinaryXmlWireFormat (NDNx) is deprecated. To enable while you upgrade your code to use NDN-TLV, set WireFormat.ENABLE_NDNX = true");
  }

  /**
   * Encode name in binary XML and return the encoding.
   * @param name The Name object to encode.
   * @return A Blob containing the encoding.
   */
  public Blob
  encodeName(Name name)
  {
    BinaryXmlEncoder encoder = new BinaryXmlEncoder();
    encodeName(name, new int[1], new int[1], encoder);
    return new Blob(encoder.getOutput(), false);
  }

  /**
   * Decode input as a name in binary XML and set the fields of the interest object.
   * @param name The Name object whose fields are updated.
   * @param input The input buffer to decode.  This reads from position() to limit(), but does not change the position.
   * @throws EncodingException For invalid encoding.
   */
  public void
  decodeName(Name name, ByteBuffer input) throws EncodingException
  {
    BinaryXmlDecoder decoder = new BinaryXmlDecoder(input);
    decodeName(name, new int[1], new int[1], decoder);
  }

  /**
   * Encode interest in binary XML and return the encoding.
   * @param interest The Interest object to encode.
   * @param signedPortionBeginOffset Return the offset in the encoding of the
   * beginning of the signed portion. The signed portion starts from the first
   * name component and ends just before the final name component (which is
   * assumed to be a signature for a signed interest).
   * @param signedPortionEndOffset Return the offset in the encoding of the end
   * of the signed portion. The signed portion starts from the first
   * name component and ends just before the final name component (which is
   * assumed to be a signature for a signed interest).
   * @return A Blob containing the encoding.
   */
  public Blob
  encodeInterest(Interest interest, int[] signedPortionBeginOffset, int[] signedPortionEndOffset)
  {
    BinaryXmlEncoder encoder = new BinaryXmlEncoder();
    encodeInterest
      (interest, signedPortionBeginOffset, signedPortionEndOffset, encoder);
    return new Blob(encoder.getOutput(), false);
  }

  /**
   * Decode input as an interest in binary XML and set the fields of the interest object.
   * @param interest The Interest object whose fields are updated.
   * @param input The input buffer to decode.  This reads from position() to limit(), but does not change the position.
   * @param signedPortionBeginOffset Return the offset in the encoding of the
   * beginning of the signed portion. The signed portion starts from the first
   * name component and ends just before the final name component (which is
   * assumed to be a signature for a signed interest).
   * @param signedPortionEndOffset Return the offset in the encoding of the end
   * of the signed portion. The signed portion starts from the first
   * name component and ends just before the final name component (which is
   * assumed to be a signature for a signed interest).
   * @throws EncodingException For invalid encoding.
   */
  public void
  decodeInterest
    (Interest interest, ByteBuffer input, int[] signedPortionBeginOffset,
     int[] signedPortionEndOffset) throws EncodingException
  {
    BinaryXmlDecoder decoder = new BinaryXmlDecoder(input);
    decodeInterest
      (interest, signedPortionBeginOffset, signedPortionEndOffset, decoder);
  }

  /**
   * Encode data and return the encoding.
   * @param data The Data object to encode.
   * @param signedPortionBeginOffset Return the offset in the encoding of the beginning of the signed portion by
   * setting signedPortionBeginOffset[0].
   * If you are not encoding in order to sign, you can call encodeData(data) to ignore this returned value.
   * @param signedPortionEndOffset Return the offset in the encoding of the end of the signed portion by
   * setting signedPortionEndOffset[0].
   * If you are not encoding in order to sign, you can call encodeData(data) to ignore this returned value.
   * @return A Blob containing the encoding.
   */
  public Blob
  encodeData(Data data, int[] signedPortionBeginOffset, int[] signedPortionEndOffset)
  {
    BinaryXmlEncoder encoder = new BinaryXmlEncoder(1500);
    encodeData(data, signedPortionBeginOffset, signedPortionEndOffset, encoder);
    return new Blob(encoder.getOutput(), false);
  }

  /**
   * Decode input as a data packet in binary XML and set the fields in the data object.
   * @param data The Data object whose fields are updated.
   * @param input The input buffer to decode.  This reads from position() to limit(), but does not change the position.
   * @param signedPortionBeginOffset Return the offset in the input buffer of the beginning of the signed portion by
   * setting signedPortionBeginOffset[0].  If you are not decoding in order to verify, you can call
   * decodeData(data, input) to ignore this returned value.
   * @param signedPortionEndOffset Return the offset in the input buffer of the end of the signed portion by
   * setting signedPortionEndOffset[0]. If you are not decoding in order to verify, you can call
   * decodeData(data, input) to ignore this returned value.
   * @throws EncodingException For invalid encoding.
   */
  public void
  decodeData(Data data, ByteBuffer input, int[] signedPortionBeginOffset, int[] signedPortionEndOffset) throws EncodingException
  {
    BinaryXmlDecoder decoder = new BinaryXmlDecoder(input);
    decodeData(data, input, signedPortionBeginOffset, signedPortionEndOffset, decoder);
  }

  /**
   * Encode forwardingEntry in binary XML and return the encoding.
   * @param forwardingEntry The ForwardingEntry object to encode.
   * @return A Blob containing the encoding.
   */
  public Blob
  encodeForwardingEntry(ForwardingEntry forwardingEntry)
  {
    BinaryXmlEncoder encoder = new BinaryXmlEncoder();

    encoder.writeElementStartDTag(BinaryXml.DTag_ForwardingEntry);

    encoder.writeOptionalUDataDTagElement
      (BinaryXml.DTag_Action, new Blob(forwardingEntry.getAction()));
    encodeName(forwardingEntry.getPrefix(), new int[1], new int[1], encoder);
    // This will skip encoding if there is no publisherPublicKeyDigest.
    encodePublisherPublicKeyDigest
      (forwardingEntry.getPublisherPublicKeyDigest(), encoder);
    encoder.writeOptionalUnsignedDecimalIntDTagElement
      (BinaryXml.DTag_FaceID, forwardingEntry.getFaceId());
    encoder.writeUnsignedDecimalIntDTagElement
      (BinaryXml.DTag_ForwardingFlags,
       forwardingEntry.getForwardingFlags().getForwardingEntryFlags());
    if (forwardingEntry.getFreshnessPeriod() >= 0.0)
      // Convert from milliseconds to seconds.
      encoder.writeOptionalUnsignedDecimalIntDTagElement
        (BinaryXml.DTag_FreshnessSeconds,
         (int)Math.round(forwardingEntry.getFreshnessPeriod() / 1000.0));

    encoder.writeElementClose();
    return new Blob(encoder.getOutput(), false);
  }

  /**
   * Decode input as a forwarding entry in binary XML and set the fields of the
   * forwardingEntry object.
   * @param forwardingEntry The ForwardingEntry object whose fields are updated.
   * @param input ByteBuffer input.
   * @throws EncodingException For invalid encoding.
   */
  public void
  decodeForwardingEntry
    (ForwardingEntry forwardingEntry, ByteBuffer input) throws EncodingException
  {
    BinaryXmlDecoder decoder = new BinaryXmlDecoder(input);
    decoder.readElementStartDTag(BinaryXml.DTag_ForwardingEntry);

    ByteBuffer action = decoder.readOptionalUDataDTagElement(BinaryXml.DTag_Action);
    if (action != null)
      forwardingEntry.setAction
        (new String(new Blob(action, true).getImmutableArray()));
    else
      forwardingEntry.setAction("");

    decodeName(forwardingEntry.getPrefix(), new int[1], new int[1], decoder);
    decodeOptionalPublisherPublicKeyDigest
      (forwardingEntry.getPublisherPublicKeyDigest(), decoder);
    forwardingEntry.setFaceId
      (decoder.readOptionalUnsignedIntegerDTagElement(BinaryXml.DTag_FaceID));

    int forwardingEntryFlags = decoder.readOptionalUnsignedIntegerDTagElement
      (BinaryXml.DTag_ForwardingFlags);
    if (forwardingEntryFlags >= 0)
      forwardingEntry.getForwardingFlags().setForwardingEntryFlags(forwardingEntryFlags);
    else
      // This sets the default flags.
      forwardingEntry.setForwardingFlags(null);

    int freshnessSeconds = decoder.readOptionalUnsignedIntegerDTagElement
      (BinaryXml.DTag_FreshnessSeconds);
    // Convert seconds to milliseconds.
    forwardingEntry.setFreshnessPeriod
      (freshnessSeconds >= 0 ? (double)freshnessSeconds * 1000.0 : -1.0);

    decoder.readElementClose();
  }

  /**
   * Get a singleton instance of a BinaryXmlWireFormat.  Assuming that the
   * default wire format was set with
   * WireFormat.setDefaultWireFormat(BinaryXmlWireFormat.get()), you can check
   * if this is the default wire encoding with
   * if (WireFormat.getDefaultWireFormat() == BinaryXmlWireFormat.get()).
   * @return The singleton instance.
   */
  public static BinaryXmlWireFormat
  get()
  {
    return instance_;
  }

  /**
   * Encode the name to the encoder.
   * @param name The name to encode.
   * @param signedPortionBeginOffset Return the offset in the encoding of the
   * beginning of the signed portion. The signed portion starts from the first
   * name component and ends just before the final name component (which is
   * assumed to be a signature for a signed interest).
   * @param signedPortionEndOffset Return the offset in the encoding of the end
   * of the signed portion. The signed portion starts from the first
   * name component and ends just before the final name component (which is
   * assumed to be a signature for a signed interest).
   * @param encoder The TlvEncoder to receive the encoding.
   */
  private static void
  encodeName
    (Name name, int[] signedPortionBeginOffset, int[] signedPortionEndOffset,
     BinaryXmlEncoder encoder)
  {
    encoder.writeElementStartDTag(BinaryXml.DTag_Name);

    signedPortionBeginOffset[0] = encoder.getOffset();

    if (name.size() == 0)
      // There is no "final component", so set signedPortionEndOffset arbitrarily.
      signedPortionEndOffset[0] = signedPortionBeginOffset[0];
    else {
      for (int i = 0; i < name.size(); ++i) {
        if (i == name.size() - 1)
          // We will begin the final component.
          signedPortionEndOffset[0] = encoder.getOffset();

        encoder.writeBlobDTagElement(BinaryXml.DTag_Component, name.get(i).getValue());
      }
    }

    encoder.writeElementClose();
  }

  /**
   * Decode the name as binary XML and set the fields in name.
   * @param name The name object whose fields are set.
   * @param signedPortionBeginOffset Return the offset in the encoding of the
   * beginning of the signed portion. The signed portion starts from the first
   * name component and ends just before the final name component (which is
   * assumed to be a signature for a signed interest).
   * If you are not decoding in order to verify, you can ignore this returned value.
   * @param signedPortionEndOffset Return the offset in the encoding of the end
   * of the signed portion. The signed portion starts from the first
   * name component and ends just before the final name component (which is
   * assumed to be a signature for a signed interest).
   * If you are not decoding in order to verify, you can ignore this returned value.
   * @param decoder The decoder with the input to decode.
   * @throws EncodingException
   */
  private static void
  decodeName
    (Name name, int[] signedPortionBeginOffset, int[] signedPortionEndOffset,
     BinaryXmlDecoder decoder) throws EncodingException
  {
    decoder.readElementStartDTag(BinaryXml.DTag_Name);
    name.clear();

    signedPortionBeginOffset[0] = decoder.getOffset();
    // In case there are no components, set signedPortionEndOffset arbitrarily.
    signedPortionEndOffset[0] = signedPortionBeginOffset[0];

    while (true) {
      if (!decoder.peekDTag(BinaryXml.DTag_Component))
        // No more components.
        break;

      signedPortionEndOffset[0] = decoder.getOffset();

      name.append
        (new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_Component), true));
    }

    decoder.readElementClose();
  }

  private static void
  encodeInterest
    (Interest interest, int[] signedPortionBeginOffset,
     int[] signedPortionEndOffset, BinaryXmlEncoder encoder)
  {
    encoder.writeElementStartDTag(BinaryXml.DTag_Interest);

    encodeName
      (interest.getName(), signedPortionBeginOffset, signedPortionEndOffset, encoder);
    encoder.writeOptionalUnsignedDecimalIntDTagElement(BinaryXml.DTag_MinSuffixComponents, interest.getMinSuffixComponents());
    encoder.writeOptionalUnsignedDecimalIntDTagElement(BinaryXml.DTag_MaxSuffixComponents, interest.getMaxSuffixComponents());

    if (interest.getKeyLocator().getType() == KeyLocatorType.KEY_LOCATOR_DIGEST &&
        interest.getKeyLocator().getKeyData().size() > 0)
      // There is a KEY_LOCATOR_DIGEST. Use this instead of the publisherPublicKeyDigest.
      encoder.writeBlobDTagElement
        (BinaryXml.DTag_PublisherPublicKeyDigest,
         interest.getKeyLocator().getKeyData());

    // This will skip encoding if there is no exclude.
    encodeExclude(interest.getExclude(), encoder);
    encoder.writeOptionalUnsignedDecimalIntDTagElement(BinaryXml.DTag_ChildSelector, interest.getChildSelector());
    if (interest.getAnswerOriginKind() >= 0 && interest.getAnswerOriginKind() != Interest.DEFAULT_ANSWER_ORIGIN_KIND)
      encoder.writeUnsignedDecimalIntDTagElement(BinaryXml.DTag_AnswerOriginKind, interest.getAnswerOriginKind());
    encoder.writeOptionalUnsignedDecimalIntDTagElement(BinaryXml.DTag_Scope, interest.getScope());
    encoder.writeOptionalTimeMillisecondsDTagElement(BinaryXml.DTag_InterestLifetime, interest.getInterestLifetimeMilliseconds());
    encoder.writeOptionalBlobDTagElement(BinaryXml.DTag_Nonce, interest.getNonce());

    encoder.writeElementClose();
  }

  private static void
  decodeInterest
    (Interest interest, int[] signedPortionBeginOffset,
     int[] signedPortionEndOffset, BinaryXmlDecoder decoder) throws EncodingException
  {
    decoder.readElementStartDTag(BinaryXml.DTag_Interest);

    decodeName
      (interest.getName(), signedPortionBeginOffset,signedPortionEndOffset,
       decoder);
    interest.setMinSuffixComponents(decoder.readOptionalUnsignedIntegerDTagElement(BinaryXml.DTag_MinSuffixComponents));
    interest.setMaxSuffixComponents(decoder.readOptionalUnsignedIntegerDTagElement(BinaryXml.DTag_MaxSuffixComponents));

    // Initially clear the keyLocator.
    interest.getKeyLocator().clear();

    if (decoder.peekDTag(BinaryXml.DTag_Exclude))
      decodeExclude(interest.getExclude(), decoder);
    else
      interest.getExclude().clear();

    interest.setChildSelector(decoder.readOptionalUnsignedIntegerDTagElement(BinaryXml.DTag_ChildSelector));
    interest.setAnswerOriginKind(decoder.readOptionalUnsignedIntegerDTagElement(BinaryXml.DTag_AnswerOriginKind));
    interest.setScope(decoder.readOptionalUnsignedIntegerDTagElement(BinaryXml.DTag_Scope));
    interest.setInterestLifetimeMilliseconds(decoder.readOptionalTimeMillisecondsDTagElement(BinaryXml.DTag_InterestLifetime));
    interest.setNonce(new Blob(decoder.readOptionalBinaryDTagElement(BinaryXml.DTag_Nonce), true));

    decoder.readElementClose();
  }

  private static void
  encodeData(Data data, int[] signedPortionBeginOffset, int[] signedPortionEndOffset, BinaryXmlEncoder encoder)
  {
    encoder.writeElementStartDTag(BinaryXml.DTag_ContentObject);
    encodeSignature((Sha256WithRsaSignature)data.getSignature(), encoder);

    signedPortionBeginOffset[0] = encoder.getOffset();

    encodeName(data.getName(), new int[1], new int[1], encoder);
    encodeSignedInfo((Sha256WithRsaSignature)data.getSignature(), data.getMetaInfo(), encoder);
    encoder.writeBlobDTagElement(BinaryXml.DTag_Content, data.getContent());

    signedPortionEndOffset[0] = encoder.getOffset();

    encoder.writeElementClose();
  }

  private static void
  decodeData(Data data, ByteBuffer input, int[] signedPortionBeginOffset, int[] signedPortionEndOffset,
              BinaryXmlDecoder decoder) throws EncodingException
  {
    decoder.readElementStartDTag(BinaryXml.DTag_ContentObject);

    data.setSignature(new Sha256WithRsaSignature());
    if (decoder.peekDTag(BinaryXml.DTag_Signature))
      decodeSignature((Sha256WithRsaSignature)data.getSignature(), decoder);

    signedPortionBeginOffset[0] = decoder.getOffset();

    decodeName(data.getName(), new int[1], new int[1], decoder);
    data.setMetaInfo(new MetaInfo());
    if (decoder.peekDTag(BinaryXml.DTag_SignedInfo))
      decodeSignedInfo((Sha256WithRsaSignature)data.getSignature(), data.getMetaInfo(), decoder);
    // Require a Content element, but set allowNull to allow a missing BLOB.
    data.setContent(new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_Content, true), true));

    signedPortionEndOffset[0] = decoder.getOffset();

    decoder.readElementClose();
  }

  /**
   * Encode the PublisherPublicKeyDigest using Binary XML.  If publisherPublicKeyDigest.getPublisherPublicKeyDigest().size()
   * is 0, then do nothing.
   * @param publisherPublicKeyDigest The PublisherPublicKeyDigest to encode.
   * @param encoder The BinaryXmlEncoder used to encode.
   */
  private static void
  encodePublisherPublicKeyDigest(PublisherPublicKeyDigest publisherPublicKeyDigest, BinaryXmlEncoder encoder)
  {
    if (publisherPublicKeyDigest.getPublisherPublicKeyDigest().size() <= 0)
      return;

    encoder.writeBlobDTagElement(BinaryXml.DTag_PublisherPublicKeyDigest, publisherPublicKeyDigest.getPublisherPublicKeyDigest());
  }

  /**
   * Expect the next element to be a Binary XML PublisherPublicKeyDigest and decode into publisherPublicKeyDigest.
   * @param publisherPublicKeyDigest The PublisherPublicKeyDigest to update.
   * @param decoder The BinaryXmlDecoder used to decode.
   * @throws EncodingException For invalid encoding.
   */
  private static void
  decodePublisherPublicKeyDigest
    (PublisherPublicKeyDigest publisherPublicKeyDigest, BinaryXmlDecoder decoder) throws EncodingException
  {
    publisherPublicKeyDigest.setPublisherPublicKeyDigest
      (new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_PublisherPublicKeyDigest), true));
  }

  /**
   * Peek the next element and if it is a Binary XML PublisherPublicKeyDigest decode into publisherPublicKeyDigest.
   * Otherwise, set publisherPublicKeyDigest to none.
   * @param publisherPublicKeyDigest The PublisherPublicKeyDigest to update.
   * @param decoder The BinaryXmlDecoder used to decode.
   * @throws EncodingException For invalid encoding.
   */
  private static void
  decodeOptionalPublisherPublicKeyDigest
    (PublisherPublicKeyDigest publisherPublicKeyDigest, BinaryXmlDecoder decoder) throws EncodingException
  {
    if (decoder.peekDTag(BinaryXml.DTag_PublisherPublicKeyDigest))
      decodePublisherPublicKeyDigest(publisherPublicKeyDigest, decoder);
    else
      publisherPublicKeyDigest.clear();
  }

  private static void
  encodeExclude(Exclude exclude, BinaryXmlEncoder encoder)
  {
    if (exclude.size() <= 0)
      // Omit.
      return;

    encoder.writeElementStartDTag(BinaryXml.DTag_Exclude);

    // TODO: Do we want to order the components (except for ANY)?
    for (int i = 0; i < exclude.size(); ++i) {
      Exclude.Entry entry = exclude.get(i);

      if (entry.getType() == Exclude.Type.COMPONENT)
        encoder.writeBlobDTagElement(BinaryXml.DTag_Component, entry.getComponent().getValue());
      else {
        // Type is ANY.
        encoder.writeElementStartDTag(BinaryXml.DTag_Any);
        encoder.writeElementClose();
      }
    }

    encoder.writeElementClose();
  }

  private static void
  decodeExclude(Exclude exclude, BinaryXmlDecoder decoder) throws EncodingException
  {
    decoder.readElementStartDTag(BinaryXml.DTag_Exclude);

    exclude.clear();
    while (true) {
      if (decoder.peekDTag(BinaryXml.DTag_Component))
        exclude.appendComponent(new Name.Component(new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_Component), true)));
      else if (decoder.peekDTag(BinaryXml.DTag_Any)) {
        // Read the Any element.
        decoder.readElementStartDTag(BinaryXml.DTag_Any);
        decoder.readElementClose();

        exclude.appendAny();
      }
      else if (decoder.peekDTag(BinaryXml.DTag_Bloom)) {
        // Skip the Bloom and treat it as Any.
        decoder.readBinaryDTagElement(BinaryXml.DTag_Bloom);
        exclude.appendAny();
      }
      else
        // No more entries.
        break;
    }

    decoder.readElementClose();
  }

  private static void
  encodeSignature(Sha256WithRsaSignature signature, BinaryXmlEncoder encoder)
  {
    encoder.writeElementStartDTag(BinaryXml.DTag_Signature);

    // TODO: Check if digestAlgorithm is the same as the default, and skip it, otherwise encode it as UDATA.
    encoder.writeOptionalBlobDTagElement(BinaryXml.DTag_Witness, signature.getWitness());
    // Require a signature.
    encoder.writeBlobDTagElement(BinaryXml.DTag_SignatureBits, signature.getSignature());
    encoder.writeElementClose();
  }

  private static void
  decodeSignature(Sha256WithRsaSignature signature, BinaryXmlDecoder decoder) throws EncodingException
  {
    decoder.readElementStartDTag(BinaryXml.DTag_Signature);
    /* TODO: digestAlgorithm as UDATA */ signature.setDigestAlgorithm(new Blob());
    signature.setWitness(new Blob(decoder.readOptionalBinaryDTagElement(BinaryXml.DTag_Witness), true));
    // Require a signature.
    signature.setSignature(new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_SignatureBits), true));
    decoder.readElementClose();
  }

  /**
   * Encode the KeyLocator using Binary XML.  If keyLocator.getType() == KeyLocatorType.NONE, then do nothing.
   * @param keyLocator The KeyLocator to encode.
   * @param encoder The BinaryXmlEncoder used to encode.
   */
  private static void
  encodeKeyLocator(KeyLocator keyLocator, BinaryXmlEncoder encoder)
  {
    if (keyLocator.getType() == KeyLocatorType.NONE)
      return;

    if (keyLocator.getType() == KeyLocatorType.KEY_LOCATOR_DIGEST)
      // encodeSignedInfo already encoded this as the publisherPublicKeyDigest,
      //   so do nothing here.
      return;

    encoder.writeElementStartDTag(BinaryXml.DTag_KeyLocator);

    if (keyLocator.getType() == KeyLocatorType.KEY)
      encoder.writeBlobDTagElement(BinaryXml.DTag_Key, keyLocator.getKeyData());
    else if (keyLocator.getType() == KeyLocatorType.CERTIFICATE)
      encoder.writeBlobDTagElement(BinaryXml.DTag_Certificate, keyLocator.getKeyData());
    else if (keyLocator.getType() == KeyLocatorType.KEYNAME) {
      encoder.writeElementStartDTag(BinaryXml.DTag_KeyName);
      encodeName(keyLocator.getKeyName(), new int[1], new int[1], encoder);

      if (keyLocator.getKeyNameType() != KeyNameType.NONE && keyLocator.getKeyData().size() > 0) {
        if (keyLocator.getKeyNameType() == KeyNameType.PUBLISHER_PUBLIC_KEY_DIGEST)
          encoder.writeBlobDTagElement(BinaryXml.DTag_PublisherPublicKeyDigest, keyLocator.getKeyData());
        else if (keyLocator.getKeyNameType() == KeyNameType.PUBLISHER_CERTIFICATE_DIGEST)
          encoder.writeBlobDTagElement(BinaryXml.DTag_PublisherCertificateDigest, keyLocator.getKeyData());
        else if (keyLocator.getKeyNameType() == KeyNameType.PUBLISHER_ISSUER_KEY_DIGEST)
          encoder.writeBlobDTagElement(BinaryXml.DTag_PublisherIssuerKeyDigest, keyLocator.getKeyData());
        else if (keyLocator.getKeyNameType() == KeyNameType.PUBLISHER_ISSUER_CERTIFICATE_DIGEST)
          encoder.writeBlobDTagElement(BinaryXml.DTag_PublisherIssuerCertificateDigest, keyLocator.getKeyData());
        else
          // We don't expect this to happen since we use an enum.
          throw new Error("unrecognized KeyNameType");
      }

      encoder.writeElementClose();
    }
    else
      // We don't expect this to happen since we use an enum.
      throw new Error("unrecognized KeyLocatorType");

    encoder.writeElementClose();
  }

  private static void
  decodeKeyNameData(KeyLocator keyLocator, BinaryXmlDecoder decoder) throws EncodingException
  {
    if (decoder.peekDTag(BinaryXml.DTag_PublisherPublicKeyDigest)) {
      keyLocator.setKeyNameType(KeyNameType.PUBLISHER_PUBLIC_KEY_DIGEST);
      keyLocator.setKeyData(new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_PublisherPublicKeyDigest), true));
    }
    else {
      if (decoder.peekDTag(BinaryXml.DTag_PublisherCertificateDigest)) {
        keyLocator.setKeyNameType(KeyNameType.PUBLISHER_CERTIFICATE_DIGEST);
        keyLocator.setKeyData(new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_PublisherCertificateDigest), true));
      }
      else {
        if (decoder.peekDTag(BinaryXml.DTag_PublisherIssuerKeyDigest)) {
          keyLocator.setKeyNameType(KeyNameType.PUBLISHER_ISSUER_KEY_DIGEST);
          keyLocator.setKeyData(new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_PublisherIssuerKeyDigest), true));
        }
        else {
          if (decoder.peekDTag(BinaryXml.DTag_PublisherIssuerCertificateDigest)) {
            keyLocator.setKeyNameType(KeyNameType.PUBLISHER_ISSUER_CERTIFICATE_DIGEST);
            keyLocator.setKeyData(new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_PublisherIssuerCertificateDigest), true));
          }
          else {
            // Key name data is omitted.
            keyLocator.setKeyNameType(KeyNameType.NONE);
            keyLocator.setKeyData(new Blob());
          }
        }
      }
    }
  }

  /**
   * Expect the next element to be a Binary XML KeyLocator and decode into keyLocator.
   * @param keyLocator The KeyLocator to update.
   * @param decoder The BinaryXmlDecoder used to decode.
   * @throws EncodingException For invalid encoding.
   */
  private static void
  decodeKeyLocator(KeyLocator keyLocator, BinaryXmlDecoder decoder) throws EncodingException
  {
    keyLocator.clear();
    decoder.readElementStartDTag(BinaryXml.DTag_KeyLocator);

    if (decoder.peekDTag(BinaryXml.DTag_Key)) {
      keyLocator.setType(KeyLocatorType.KEY);
      keyLocator.setKeyData(new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_Key), true));
    }
    else {
      if (decoder.peekDTag(BinaryXml.DTag_Certificate)) {
        keyLocator.setType(KeyLocatorType.CERTIFICATE);
        keyLocator.setKeyData(new Blob(decoder.readBinaryDTagElement(BinaryXml.DTag_Certificate), true));
      }
      else {
        if (decoder.peekDTag(BinaryXml.DTag_KeyName)) {
          keyLocator.setType(KeyLocatorType.KEYNAME);

          decoder.readElementStartDTag(BinaryXml.DTag_KeyName);
          decodeName(keyLocator.getKeyName(), new int[1], new int[1], decoder);
          decodeKeyNameData(keyLocator, decoder);
          decoder.readElementClose();
        }
        else
           throw new EncodingException("decodeKeyLocator: unrecognized key locator type");
      }
    }

    decoder.readElementClose();
  }

  /**
   * Peek the next element and if it is a Binary XML KeyLocator then decode into the keyLocator.
   * Otherwise, call keyLocator.clear().
   * @param keyLocator The KeyLocator to update.
   * @param decoder The BinaryXmlDecoder used to decode.
   * @throws EncodingException For invalid encoding.
   */
  private static void
  decodeOptionalKeyLocator(KeyLocator keyLocator, BinaryXmlDecoder decoder) throws EncodingException
  {
    if (decoder.peekDTag(BinaryXml.DTag_KeyLocator))
      decodeKeyLocator(keyLocator, decoder);
    else
      keyLocator.clear();
  }

  // Put these in a Blob so we can use ByteBuffer equals.
  private static final Blob DATA_BYTES = new Blob(ByteBuffer.wrap(new byte[] { (byte)0x0C, (byte)0x04, (byte)0xC0 }), false);
  private static final Blob ENCR_BYTES = new Blob(ByteBuffer.wrap(new byte[] { (byte)0x10, (byte)0xD0, (byte)0x91 }), false);
  private static final Blob GONE_BYTES = new Blob(ByteBuffer.wrap(new byte[] { (byte)0x18, (byte)0xE3, (byte)0x44 }), false);
  private static final Blob KEY_BYTES =  new Blob(ByteBuffer.wrap(new byte[] { (byte)0x28, (byte)0x46, (byte)0x3F }), false);
  private static final Blob LINK_BYTES = new Blob(ByteBuffer.wrap(new byte[] { (byte)0x2C, (byte)0x83, (byte)0x4A }), false);
  private static final Blob NACK_BYTES = new Blob(ByteBuffer.wrap(new byte[] { (byte)0x34, (byte)0x00, (byte)0x8A }), false);

  private static void
  encodeSignedInfo(Sha256WithRsaSignature signature, MetaInfo metaInfo, BinaryXmlEncoder encoder)
  {
    encoder.writeElementStartDTag(BinaryXml.DTag_SignedInfo);

    if (signature.getPublisherPublicKeyDigest().getPublisherPublicKeyDigest().size() > 0)
      // We have a publisherPublicKeyDigest, so use it.
      encodePublisherPublicKeyDigest(signature.getPublisherPublicKeyDigest(), encoder);
    else {
      if (signature.getKeyLocator().getType() == KeyLocatorType.KEY_LOCATOR_DIGEST &&
          signature.getKeyLocator().getKeyData().size() > 0)
        // We have a TLV-style KEY_LOCATOR_DIGEST, so encode as the
        //   publisherPublicKeyDigest.
        encoder.writeBlobDTagElement
            (BinaryXml.DTag_PublisherPublicKeyDigest,
             signature.getKeyLocator().getKeyData());
    }

    encoder.writeOptionalTimeMillisecondsDTagElement(BinaryXml.DTag_Timestamp, metaInfo.getTimestampMilliseconds());
    if (!(metaInfo.getType() == ContentType.DATA || metaInfo.getType() == ContentType.BLOB)) {
      // Not the default of DATA, so we need to encode the type.
      Blob typeBytes = null;;
      if (metaInfo.getType() == ContentType.ENCR)
        typeBytes = ENCR_BYTES;
      else if (metaInfo.getType() == ContentType.GONE)
        typeBytes = GONE_BYTES;
      else if (metaInfo.getType() == ContentType.KEY)
        typeBytes = KEY_BYTES;
      else if (metaInfo.getType() == ContentType.LINK)
        typeBytes = LINK_BYTES;
      else if (metaInfo.getType() == ContentType.NACK)
        typeBytes = NACK_BYTES;

      encoder.writeBlobDTagElement(BinaryXml.DTag_Type, typeBytes);
    }

    encoder.writeOptionalUnsignedDecimalIntDTagElement(BinaryXml.DTag_FreshnessSeconds, metaInfo.getFreshnessSeconds());
    encoder.writeOptionalBlobDTagElement(BinaryXml.DTag_FinalBlockID, metaInfo.getFinalBlockId().getValue());
    // This will skip encoding if there is no key locator.
    encodeKeyLocator(signature.getKeyLocator(), encoder);
    encoder.writeElementClose();
  }

  private static void
  decodeSignedInfo(Sha256WithRsaSignature signature, MetaInfo metaInfo, BinaryXmlDecoder decoder) throws EncodingException
  {
    decoder.readElementStartDTag(BinaryXml.DTag_SignedInfo);
    decodeOptionalPublisherPublicKeyDigest(signature.getPublisherPublicKeyDigest(), decoder);
    metaInfo.setTimestampMilliseconds(decoder.readOptionalTimeMillisecondsDTagElement(BinaryXml.DTag_Timestamp));
    ByteBuffer typeBytes = decoder.readOptionalBinaryDTagElement(BinaryXml.DTag_Type);
    if (typeBytes == null)
      // The default Type is BLOB.
      metaInfo.setType(ContentType.BLOB);
    else {
      if (typeBytes.equals(DATA_BYTES.buf()))
        metaInfo.setType(ContentType.BLOB);
      else if (typeBytes.equals(ENCR_BYTES.buf()))
        metaInfo.setType(ContentType.ENCR);
      else if (typeBytes.equals(GONE_BYTES.buf()))
        metaInfo.setType(ContentType.GONE);
      else if (typeBytes.equals(KEY_BYTES.buf()))
        metaInfo.setType(ContentType.KEY);
      else if (typeBytes.equals(LINK_BYTES.buf()))
        metaInfo.setType(ContentType.LINK);
      else if (typeBytes.equals(NACK_BYTES.buf()))
        metaInfo.setType(ContentType.NACK);
      else
        throw new EncodingException("Unrecognized MetaInfo.ContentType");
    }

    metaInfo.setFreshnessSeconds(decoder.readOptionalUnsignedIntegerDTagElement(BinaryXml.DTag_FreshnessSeconds));
    metaInfo.setFinalBlockId(new Name.Component(new Blob(decoder.readOptionalBinaryDTagElement(BinaryXml.DTag_FinalBlockID), true)));
    decodeOptionalKeyLocator(signature.getKeyLocator(), decoder);
    decoder.readElementClose();
  }

  private static BinaryXmlWireFormat instance_ = new BinaryXmlWireFormat();
}
