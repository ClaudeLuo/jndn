/**
 * Copyright (C) 2014-2015 Regents of the University of California.
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

import java.io.IOException;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.encoding.WireFormat;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.transport.TcpTransport;
import net.named_data.jndn.transport.Transport;
import net.named_data.jndn.util.Common;

/**
 * The Face class provides the main methods for NDN communication.
 */
public class Face {
  /**
   * Create a new Face for communication with an NDN hub with the given
   * Transport object and connectionInfo.
   * @param transport A Transport object used for communication.
   * @param connectionInfo A Transport.ConnectionInfo to be used to connect to
   * the transport.
   */
  public Face(Transport transport, Transport.ConnectionInfo connectionInfo)
  {
    node_ = new Node(transport, connectionInfo);
  }

  /**
   * Create a new Face for communication with an NDN hub at host:port using the
   * default TcpTransport.
   * @param host The host of the NDN hub.
   * @param port The port of the NDN hub.
   */
  public Face(String host, int port)
  {
    node_ = new Node
      (new TcpTransport(), new TcpTransport.ConnectionInfo(host, port));
  }

  /**
   * Create a new Face for communication with an NDN hub at host using the
   * default port 6363 and the default TcpTransport.
   * @param host The host of the NDN hub.
   */
  public Face(String host)
  {
    node_ = new Node
      (new TcpTransport(), new TcpTransport.ConnectionInfo(host, 6363));
  }

  /**
   * Create a new Face for communication with an NDN hub at "localhost" using the
   * default port 6363 and the default TcpTransport.
   */
  public Face()
  {
    node_ = new Node
      (new TcpTransport(), new TcpTransport.ConnectionInfo("localhost", 6363));
  }

  /**
   * Send the Interest through the transport, read the entire response and call
   * onData(interest, data).
   * @param interest The Interest to send.  This copies the Interest.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @param onTimeout If the interest times out according to the interest
   * lifetime, this calls onTimeout.onTimeout(interest) where interest is the
   * interest given to expressInterest. If onTimeout is null, this does not use
   * it.
   * @param wireFormat A WireFormat object used to encode the message.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Interest interest, OnData onData, OnTimeout onTimeout,
     WireFormat wireFormat) throws IOException
  {
    return node_.expressInterest(interest, onData, onTimeout, wireFormat);
  }

  /**
   * Send the Interest through the transport, read the entire response and call
   * onData(interest, data).
   * This uses the default WireFormat.getDefaultWireFormat().
   * @param interest The Interest to send.  This copies the Interest.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @param onTimeout If the interest times out according to the interest
   * lifetime, this calls onTimeout.onTimeout(interest) where interest is the
   * interest given to expressInterest. If onTimeout is null, this does not use
   * it.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Interest interest, OnData onData, OnTimeout onTimeout) throws IOException
  {
    return node_.expressInterest
      (interest, onData, onTimeout, WireFormat.getDefaultWireFormat());
  }

  /**
   * Send the Interest through the transport, read the entire response and call
   * onData(interest, data).  Ignore if the interest times out.
   * @param interest The Interest to send.  This copies the Interest.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @param wireFormat A WireFormat object used to encode the message.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Interest interest, OnData onData, WireFormat wireFormat) throws IOException
  {
    return node_.expressInterest(interest, onData, null, wireFormat);
  }

  /**
   * Send the Interest through the transport, read the entire response and call
   * onData(interest, data).  Ignore if the interest times out.
   * This uses the default WireFormat.getDefaultWireFormat().
   * @param interest The Interest to send.  This copies the Interest.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest(Interest interest, OnData onData) throws IOException
  {
    return node_.expressInterest
      (interest, onData, null, WireFormat.getDefaultWireFormat());
  }

  /**
   * Encode name as an Interest. If interestTemplate is not null, use its
   * interest selectors. Send the interest through the transport, read the
   * entire response and call onData(interest, data).
   * @param name A Name for the interest. This copies the Name.
   * @param interestTemplate If not null, copy interest selectors from the
   * template. This does not keep a pointer to the Interest object.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @param onTimeout If the interest times out according to the interest
   * lifetime, this calls onTimeout.onTimeout(interest) where interest is the
   * interest given to expressInterest. If onTimeout is null, this does not use
   * it.
   * @param wireFormat A WireFormat object used to encode the message.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Name name, Interest interestTemplate, OnData onData, OnTimeout onTimeout,
     WireFormat wireFormat) throws IOException
  {
    Interest interest = new Interest(name);
    if (interestTemplate != null) {
      interest.setMinSuffixComponents(interestTemplate.getMinSuffixComponents());
      interest.setMaxSuffixComponents(interestTemplate.getMaxSuffixComponents());
      interest.setKeyLocator(interestTemplate.getKeyLocator());
      interest.setExclude(interestTemplate.getExclude());
      interest.setChildSelector(interestTemplate.getChildSelector());
      interest.setMustBeFresh(interestTemplate.getMustBeFresh());
      interest.setScope(interestTemplate.getScope());
      interest.setInterestLifetimeMilliseconds(
        interestTemplate.getInterestLifetimeMilliseconds());
      // Don't copy the nonce.
    }
    else
      interest.setInterestLifetimeMilliseconds(4000.0);

    return node_.expressInterest(interest, onData, onTimeout, wireFormat);
  }

  /**
   * Encode name as an Interest. If interestTemplate is not null, use its
   * interest selectors. Send the interest through the transport, read the
   * entire response and call onData(interest, data).
   * Use a default interest lifetime.
   * @param name A Name for the interest. This copies the Name.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @param onTimeout If the interest times out according to the interest
   * lifetime, this calls onTimeout.onTimeout(interest) where interest is the
   * interest given to expressInterest. If onTimeout is null, this does not use
   * it.
   * @param wireFormat A WireFormat object used to encode the message.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Name name, OnData onData, OnTimeout onTimeout,
     WireFormat wireFormat) throws IOException
  {
    return expressInterest(name, null, onData, onTimeout, wireFormat);
  }


  /**
   * Encode name as an Interest. If interestTemplate is not null, use its
   * interest selectors. Send the interest through the transport, read the
   * entire response and call onData(interest, data).
   * Ignore if the interest times out.
   * @param name A Name for the interest. This copies the Name.
   * @param interestTemplate If not null, copy interest selectors from the
   * template. This does not keep a pointer to the Interest object.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @param wireFormat A WireFormat object used to encode the message.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Name name, Interest interestTemplate, OnData onData,
     WireFormat wireFormat) throws IOException
  {
    return expressInterest(name, interestTemplate, onData, null, wireFormat);
  }

  /**
   * Encode name as an Interest. If interestTemplate is not null, use its
   * interest selectors. Send the interest through the transport, read the
   * entire response and call onData(interest, data).
   * This uses the default WireFormat.getDefaultWireFormat().
   * @param name A Name for the interest. This copies the Name.
   * @param interestTemplate If not null, copy interest selectors from the
   * template. This does not keep a pointer to the Interest object.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @param onTimeout If the interest times out according to the interest
   * lifetime, this calls onTimeout.onTimeout(interest) where interest is the
   * interest given to expressInterest. If onTimeout is null, this does not use
   * it.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Name name, Interest interestTemplate, OnData onData,
     OnTimeout onTimeout) throws IOException
  {
    return expressInterest
      (name, interestTemplate, onData, onTimeout,
       WireFormat.getDefaultWireFormat());
  }

  /**
   * Encode name as an Interest. If interestTemplate is not null, use its
   * interest selectors. Send the interest through the transport, read the
   * entire response and call onData(interest, data).
   * Ignore if the interest times out.
   * This uses the default WireFormat.getDefaultWireFormat().
   * @param name A Name for the interest. This copies the Name.
   * @param interestTemplate If not null, copy interest selectors from the
   * template. This does not keep a pointer to the Interest object.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Name name, Interest interestTemplate, OnData onData) throws IOException
  {
    return expressInterest
      (name, interestTemplate, onData, null, WireFormat.getDefaultWireFormat());
  }

  /**
   * Encode name as an Interest. If interestTemplate is not null, use its
   * interest selectors. Send the interest through the transport, read the
   * entire response and call onData(interest, data).
   * Use a default interest lifetime.
   * This uses the default WireFormat.getDefaultWireFormat().
   * @param name A Name for the interest. This copies the Name.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @param onTimeout If the interest times out according to the interest
   * lifetime, this calls onTimeout.onTimeout(interest) where interest is the
   * interest given to expressInterest. If onTimeout is null, this does not use
   * it.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Name name, OnData onData, OnTimeout onTimeout) throws IOException
  {
    return expressInterest
      (name, null, onData, onTimeout, WireFormat.getDefaultWireFormat());
  }

  /**
   * Encode name as an Interest. If interestTemplate is not null, use its
   * interest selectors. Send the interest through the transport, read the
   * entire response and call onData(interest, data).
   * Use a default interest lifetime.
   * Ignore if the interest times out.
   * @param name A Name for the interest. This copies the Name.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @param wireFormat A WireFormat object used to encode the message.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest
    (Name name, OnData onData, WireFormat wireFormat) throws IOException
  {
    return expressInterest(name, null, onData, null, wireFormat);
  }

  /**
   * Encode name as an Interest. If interestTemplate is not null, use its
   * interest selectors. Send the interest through the transport, read the
   * entire response and call onData(interest, data).
   * Use a default interest lifetime.
   * Ignore if the interest times out.
   * This uses the default WireFormat.getDefaultWireFormat().
   * @param name A Name for the interest. This copies the Name.
   * @param onData  When a matching data packet is received, this calls
   * onData.onData(interest, data) where interest is the interest given to
   * expressInterest and data is the received Data object. NOTE: You must not
   * change the interest object - if you need to change it then make a copy.
   * @return The pending interest ID which can be used with
   * removePendingInterest.
   * @throws IOException For I/O error in sending the interest.
   * @throws Error If the encoded interest size exceeds getMaxNdnPacketSize().
   */
  public long
  expressInterest(Name name, OnData onData) throws IOException
  {
    return expressInterest
      (name, null, onData, null, WireFormat.getDefaultWireFormat());
  }

  /**
   * Remove the pending interest entry with the pendingInterestId from the
   * pending interest table. This does not affect another pending interest with
   * a different pendingInterestId, even if it has the same interest name.
   * If there is no entry with the pendingInterestId, do nothing.
   * @param pendingInterestId The ID returned from expressInterest.
   */
  public final void
  removePendingInterest(long pendingInterestId)
  {
    node_.removePendingInterest(pendingInterestId);
  }

  /**
   * Set the KeyChain and certificate name used to sign command interests
   * (e.g. for registerPrefix).
   * @param keyChain The KeyChain object for signing interests, which
   * must remain valid for the life of this Face. You must create the KeyChain
   * object and pass it in. You can create a default KeyChain for your
   * system with the default KeyChain constructor.
   * @param certificateName The certificate name for signing interests.
   * This makes a copy of the Name. You can get the default certificate name
   * with keyChain.getDefaultCertificateName() .
   */
  public final void
  setCommandSigningInfo(KeyChain keyChain, Name certificateName)
  {
    commandKeyChain_ = keyChain;
    commandCertificateName_ = new Name(certificateName);
  }

  /**
   * Set the certificate name used to sign command interest (e.g. for
   * registerPrefix), using the KeyChain that was set with setCommandSigningInfo.
   * @param certificateName The certificate name for signing interest.
   * This makes a copy of the Name.
   */
  public final void
  setCommandCertificateName(Name certificateName)
  {
    commandCertificateName_ = new Name(certificateName);
  }

  /**
   * Append a timestamp component and a random value component to interest's
   * name. Then use the keyChain and certificateName from setCommandSigningInfo
   * to sign the interest. If the interest lifetime is not set, this sets it.
   * @param interest The interest whose name is appended with components.
   * @param wireFormat A WireFormat object used to encode the SignatureInfo and
   * to encode the interest name for signing.
   * @throws SecurityException If cannot find the private key for the
   * certificateName.
   * @note This method is an experimental feature. See the API docs for more detail at
   * http://named-data.net/doc/ndn-ccl-api/face.html#face-makecommandinterest-method .
   */
  public final void
  makeCommandInterest(Interest interest, WireFormat wireFormat) throws SecurityException
  {
    node_.makeCommandInterest
      (interest, commandKeyChain_, commandCertificateName_, wireFormat);
  }

  /**
   * Append a timestamp component and a random value component to interest's
   * name. Then use the keyChain and certificateName from setCommandSigningInfo
   * to sign the interest. If the interest lifetime is not set, this sets it.
   * Use the default WireFormat to encode the SignatureInfo and to encode the
   * interest name for signing.
   * @param interest The interest whose name is appended with components.
   * @throws SecurityException If cannot find the private key for the
   * certificateName.
   * @note This method is an experimental feature. See the API docs for more detail at
   * http://named-data.net/doc/ndn-ccl-api/face.html#face-makecommandinterest-method .
   */
  public final void
  makeCommandInterest(Interest interest) throws SecurityException
  {
    node_.makeCommandInterest
      (interest, commandKeyChain_, commandCertificateName_,
       WireFormat.getDefaultWireFormat());
  }

  /**
   * Register prefix with the connected NDN hub and call onInterest when a
   * matching interest is received. If you have not called setCommandSigningInfo,
   * this assumes you are connecting to NDNx. If you have called
   * setCommandSigningInfo, this first sends an NFD registration request, and if
   * that times out then this sends an NDNx registration request. If you need to
   * register a prefix with NFD, you must first call setCommandSigningInfo.
   * @param prefix A Name for the prefix to register. This copies the Name.
   * @param onInterest When an interest is received which matches the name
   * prefix, this calls
   * onInterest.onInterest(prefix, interest, transport, registeredPrefixId).
   * NOTE: You must not change the prefix object - if you need to change it then
   * make a copy.
   * @param onRegisterFailed If register prefix fails for any reason, this
   * calls onRegisterFailed.onRegisterFailed(prefix).
   * @param flags The flags for finer control of which interests are forwarded
   * to the application.
   * @param wireFormat A WireFormat object used to encode the message.
   * @return The registered prefix ID which can be used with
   * removeRegisteredPrefix.
   * @throws IOException For I/O error in sending the registration request.
   * @throws SecurityException If signing a command interest for NFD and cannot
   * find the private key for the certificateName.
   */
  public long
  registerPrefix
    (Name prefix, OnInterest onInterest, OnRegisterFailed onRegisterFailed,
     ForwardingFlags flags, WireFormat wireFormat) throws IOException, SecurityException
  {
    return node_.registerPrefix
      (prefix, onInterest, onRegisterFailed, flags, wireFormat, commandKeyChain_,
     commandCertificateName_);
  }

  /**
   * Register prefix with the connected NDN hub and call onInterest when a
   * matching interest is received.
   * This uses the default WireFormat.getDefaultWireFormat().
   * @param prefix A Name for the prefix to register. This copies the Name.
   * @param onInterest When an interest is received which matches the name
   * prefix, this calls
   * onInterest.onInterest(prefix, interest, transport, registeredPrefixId).
   * NOTE: You must not change the prefix object - if you need to change it then
   * make a copy.
   * @param onRegisterFailed If register prefix fails for any reason, this
   * calls onRegisterFailed.onRegisterFailed(prefix).
   * @param flags The flags for finer control of which interests are forwarded
   * to the application.
   * @return The registered prefix ID which can be used with
   * removeRegisteredPrefix.
   * @throws IOException For I/O error in sending the registration request.
   */
  public long
  registerPrefix
    (Name prefix, OnInterest onInterest, OnRegisterFailed onRegisterFailed,
     ForwardingFlags flags) throws IOException, SecurityException
  {
    return registerPrefix
      (prefix, onInterest, onRegisterFailed, flags,
       WireFormat.getDefaultWireFormat());
  }

  /**
   * Register prefix with the connected NDN hub and call onInterest when a
   * matching interest is received.
   * Use default ForwardingFlags.
   * @param prefix A Name for the prefix to register. This copies the Name.
   * @param onInterest When an interest is received which matches the name
   * prefix, this calls
   * onInterest.onInterest(prefix, interest, transport, registeredPrefixId).
   * NOTE: You must not change the prefix object - if you need to change it then
   * make a copy.
   * @param onRegisterFailed If register prefix fails for any reason, this
   * calls onRegisterFailed.onRegisterFailed(prefix).
   * @param wireFormat A WireFormat object used to encode the message.
   * @return The registered prefix ID which can be used with
   * removeRegisteredPrefix.
   * @throws IOException For I/O error in sending the registration request.
   * @throws SecurityException If signing a command interest for NFD and cannot
   * find the private key for the certificateName.
   */
  public long
  registerPrefix
    (Name prefix, OnInterest onInterest, OnRegisterFailed onRegisterFailed,
     WireFormat wireFormat) throws IOException, SecurityException
  {
    return registerPrefix
      (prefix, onInterest, onRegisterFailed, new ForwardingFlags(), wireFormat);
  }

  /**
   * Register prefix with the connected NDN hub and call onInterest when a
   * matching interest is received.
   * This uses the default WireFormat.getDefaultWireFormat().
   * Use default ForwardingFlags.
   * @param prefix A Name for the prefix to register. This copies the Name.
   * @param onInterest When an interest is received which matches the name
   * prefix, this calls
   * onInterest.onInterest(prefix, interest, transport, registeredPrefixId).
   * NOTE: You must not change the prefix object - if you need to change it then
   * make a copy.
   * @param onRegisterFailed If register prefix fails for any reason, this
   * calls onRegisterFailed.onRegisterFailed(prefix).
   * @return The registered prefix ID which can be used with
   * removeRegisteredPrefix.
   * @throws IOException For I/O error in sending the registration request.
   * @throws SecurityException If signing a command interest for NFD and cannot
   * find the private key for the certificateName.
   */
  public long
  registerPrefix
    (Name prefix, OnInterest onInterest,
     OnRegisterFailed onRegisterFailed) throws IOException, SecurityException
  {
    return registerPrefix
      (prefix, onInterest, onRegisterFailed, new ForwardingFlags(),
       WireFormat.getDefaultWireFormat());
  }

  /**
   * Remove the registered prefix entry with the registeredPrefixId from the
   * registered prefix table. This does not affect another registered prefix with
   * a different registeredPrefixId, even if it has the same prefix name.
   * If there is no entry with the registeredPrefixId, do nothing.
   * @param registeredPrefixId The ID returned from registerPrefix.
   */
  public final void
  removeRegisteredPrefix(long registeredPrefixId)
  {
    node_.removeRegisteredPrefix(registeredPrefixId);
  }

  /**
   * Process any packets to receive and call callbacks such as onData,
   * onInterest or onTimeout. This returns immediately if there is no data to
   * receive. This blocks while calling the callbacks. You should repeatedly
   * call this from an event loop, with calls to sleep as needed so that the
   * loop doesn’t use 100% of the CPU. Since processEvents modifies the pending
   * interest table, your application should make sure that it calls
   * processEvents in the same thread as expressInterest (which also modifies
   * the pending interest table).
   * This may throw an exception for reading data or in the callback for
   * processing the data. If you call this from an main event loop, you may want
   * to catch and log/disregard all exceptions.
   */
  public void
  processEvents() throws IOException, EncodingException
  {
    // Just call Node's processEvents.
    node_.processEvents();
  }
  
  /**
   * Check if the face is local based on the current connection through the
   * Transport; some Transport may cause network IO (e.g. an IP host name lookup).
   * @return True if the face is local, false if not.
   * @throws IOException 
   * @note This is an experimental feature. This API may change in the future.
   */
  public boolean
  isLocal() throws IOException
  {
    return node_.isLocal();
  }

  /**
   * Shut down and disconnect this Face.
   */
  public void
  shutdown()
  {
    node_.shutdown();
  }

  /**
   * Get the practical limit of the size of a network-layer packet. If a packet
   * is larger than this, the library or application MAY drop it.
   * @return The maximum NDN packet size.
   */
  static int
  getMaxNdnPacketSize() { return Common.MAX_NDN_PACKET_SIZE; }

  private Node node_;
  KeyChain commandKeyChain_ = null;
  Name commandCertificateName_ = new Name();
}
