/**
 * Copyright (C) 2013 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * See COPYING for copyright and distribution information.
 */

package net.named_data.jndn.encoding;

import net.named_data.jndn.Name;

public class BinaryXmlWireFormat extends WireFormat {
  private void
  encodeName(Name name, BinaryXmlEncoder encoder)
  {
    encoder.writeElementStartDTag(BinaryXml.DTag_Name);

    for (int i = 0; i < name.size(); ++i)
      encoder.writeBlobDTagElement(BinaryXml.DTag_Component, name.get(i).getValue());

    encoder.writeElementClose();
  }
  
  private void
  decodeName(Name name, BinaryXmlDecoder decoder) throws EncodingException
  {
    decoder.readElementStartDTag(BinaryXml.DTag_Name);
    name.clear();
    while (true) {
      if (!decoder.peekDTag(BinaryXml.DTag_Component))
        // No more components.
        break;

      //name.append(decoder.readBinaryDTagElement(BinaryXml.DTag_Component, false));
    }

    decoder.readElementClose();
  }
}
