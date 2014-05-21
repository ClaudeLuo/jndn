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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * A copy of the GNU General Public License is in the file COPYING.
 */

package net.named_data.jndn.encoding;

import java.nio.ByteBuffer;
import net.named_data.jndn.util.DynamicByteBuffer;
import net.named_data.jndn.encoding.tlv.Tlv;
import net.named_data.jndn.encoding.tlv.TlvStructureDecoder;

/**
 * A ElementReader lets you call onReceivedData multiple times which 
 * uses a BinaryXmlStructureDecoder or TlvStructureDecoder to detect the end of 
 * a binary XML or NDN-TLV element and calls 
 * elementListener.onReceivedElement(element) with the element. This handles the 
 * case where a single call to onReceivedData may contain multiple elements.
 */
public class ElementReader {
  /**
   * Create a new ElementReader with the elementListener.
   * @param elementListener The ElementListener used by onReceivedData.
   */
  public
  ElementReader(ElementListener elementListener)
  {
    elementListener_ = elementListener;
  }

  /**
   * Continue to read data until the end of an element, then call 
   * elementListener.onReceivedElement(element ). The buffer passed to 
   * onReceivedElement is only valid during this call.  If you need the data 
   * later, you must copy.
   * @param data The input data containing bytes of the element to read.  
   * This reads from position() to limit(), but does not change the position.
   * @throws EncodingException For invalid encoding.
   */
  public void 
  onReceivedData(ByteBuffer data) throws EncodingException
  {
    // We may repeatedly set data to a slice as we read elements.
    data = data.slice();
    
    // Process multiple objects in the data.
    while(true) {
      if (!usePartialData_) {
        // This is the beginning of an element. Check whether it is binaryXML or 
        //   TLV.
        if (data.remaining() <= 0)
          // Wait for more data.
          return;

        // The type codes for TLV Interest and Data packets are chosen to not
        //   conflict with the first byte of a binary XML packet, so we can
        //   just look at the first byte.
        int firstByte = (int)data.get(0) & 0xff;
        if (firstByte == Tlv.Interest || firstByte == Tlv.Data || 
            firstByte == 0x80)
          useTlv_ = true;
        else
          // Binary XML.
          useTlv_ = false;
      }
      
      boolean gotElementEnd;
      int offset;
      if (useTlv_) {
        // Scan the input to check if a whole TLV object has been read.
        tlvStructureDecoder_.seek(0);
        gotElementEnd = tlvStructureDecoder_.findElementEnd(data);
        offset = tlvStructureDecoder_.getOffset();
      }
      else {
        // Scan the input to check if a whole binary XML object has been read.
        binaryXmlStructureDecoder_.seek(0);
        gotElementEnd = binaryXmlStructureDecoder_.findElementEnd(data);
        offset = binaryXmlStructureDecoder_.getOffset();
      }
      
      if (gotElementEnd) {
        // Got the remainder of an element.  Report to the caller.
        if (usePartialData_) {
          // We have partial data from a previous call, so append this data and point to partialData.
          partialData_.ensuredPut(data, 0, offset);

          elementListener_.onReceivedElement(partialData_.flippedBuffer());
          // Assume we don't need to use partialData anymore until needed.
          usePartialData_ = false;
        }
        else {
          // We are not using partialData, so just point to the input data buffer.
          ByteBuffer dataDuplicate = data.duplicate();
          dataDuplicate.limit(offset);
          elementListener_.onReceivedElement(dataDuplicate);
        }

        // Need to read a new object.
        data.position(offset);
        data = data.slice();
        binaryXmlStructureDecoder_ = new BinaryXmlStructureDecoder();
        tlvStructureDecoder_ = new TlvStructureDecoder();
        if (data.remaining() <= 0)
          // No more data in the packet.
          return;

        // else loop back to decode.
      }
      else {
        // Save remaining data for a later call.
        if (!usePartialData_) {
          usePartialData_ = true;
          partialData_.position(0);
        }

        partialData_.ensuredPut(data);
        return;
      }
    }      
  }
  
  private ElementListener elementListener_;
  private BinaryXmlStructureDecoder binaryXmlStructureDecoder_ = 
    new BinaryXmlStructureDecoder();
  private TlvStructureDecoder tlvStructureDecoder_ = new TlvStructureDecoder();
  private boolean usePartialData_;
  private DynamicByteBuffer partialData_ = new DynamicByteBuffer(1000);
  private boolean useTlv_;
}
