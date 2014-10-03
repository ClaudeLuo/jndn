/**
 * Copyright (C) 2014 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * From PyNDN unit-tests by Adeola Bannis.
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

package net.named_data.jndn.tests.unit_tests;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import net.named_data.jndn.Interest;
import net.named_data.jndn.KeyLocatorType;
import net.named_data.jndn.Name;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.util.Blob;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class TestInterestMethods {
  // Convert the int array to a ByteBuffer.
  private static ByteBuffer
  toBuffer(int[] array)
  {
    ByteBuffer result = ByteBuffer.allocate(array.length);
    for (int i = 0; i < array.length; ++i)
      result.put((byte)(array[i] & 0xff));

    result.flip();
    return result;
  }

  private static final ByteBuffer codedInterest = toBuffer(new int[] {
0x05, 0x53, // Interest
  0x07, 0x0A, 0x08, 0x03, 0x6E, 0x64, 0x6E, 0x08, 0x03, 0x61, 0x62, 0x63, // Name
  0x09, 0x38, // Selectors
    0x0D, 0x01, 0x04, // MinSuffixComponents
    0x0E, 0x01, 0x06, // MaxSuffixComponents
    0x0F, 0x22, // KeyLocator
      0x1D, 0x20, // KeyLocatorDigest
                  0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                  0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F,
    0x10, 0x07, // Exclude
      0x08, 0x03, 0x61, 0x62, 0x63, // NameComponent
      0x13, 0x00, // Any
    0x11, 0x01, 0x01, // ChildSelector
    0x12, 0x00, // MustBeFesh
  0x0A, 0x04, 0x61, 0x62, 0x61, 0x62,   // Nonce
  0x0B, 0x01, 0x02, // Scope
  0x0C, 0x02, 0x75, 0x30, // InterestLifetime
1
  });

  static String dump(Object s1) { return s1.toString(); }
  static String dump(Object s1, Object s2) { return s1.toString() + " " + s2.toString(); }

  private static ArrayList initialDump = new ArrayList(Arrays.asList(new Object[] {
    "name: /ndn/abc",
    "minSuffixComponents: 4",
    "maxSuffixComponents: 6",
    "keyLocator: KeyLocatorDigest: 000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f",
    "exclude: abc,*",
    "childSelector: 1",
    "mustBeFresh: true",
    "nonce: 61626162",
    "scope: 2",
    "lifetimeMilliseconds: 30000.0"}));

  private static ArrayList
  dumpInterest(Interest interest)
  {
    ArrayList result = new ArrayList();
    result.add(dump("name:", interest.getName().toUri()));
    result.add(dump("minSuffixComponents:",
      interest.getMinSuffixComponents() >= 0 ?
        interest.getMinSuffixComponents() : "<none>"));
    result.add(dump("maxSuffixComponents:",
      interest.getMaxSuffixComponents() >= 0 ?
        interest.getMaxSuffixComponents() : "<none>"));
    if (interest.getKeyLocator().getType() != KeyLocatorType.NONE) {
      if (interest.getKeyLocator().getType() == KeyLocatorType.KEY_LOCATOR_DIGEST)
        result.add(dump("keyLocator: KeyLocatorDigest:",
          interest.getKeyLocator().getKeyData().toHex()));
      else if (interest.getKeyLocator().getType() == KeyLocatorType.KEYNAME)
        result.add(dump("keyLocator: KeyName:",
          interest.getKeyLocator().getKeyName().toUri()));
      else
        result.add(dump("keyLocator: <unrecognized KeyLocatorType"));
    }
    else
      result.add(dump("keyLocator: <none>"));
    result.add(dump("exclude:",
      interest.getExclude().size() > 0 ? interest.getExclude().toUri() :"<none>"));
    result.add(dump("childSelector:",
      interest.getChildSelector() >= 0 ? interest.getChildSelector() : "<none>"));
    result.add(dump("mustBeFresh:", interest.getMustBeFresh()));
    result.add(dump("nonce:", interest.getNonce().size() == 0 ?
      "<none>" : interest.getNonce().toHex()));
    result.add(dump("scope:", interest.getScope() < 0 ?
      "<none>" : interest.getScope()));
    result.add(dump("lifetimeMilliseconds:",
      interest.getInterestLifetimeMilliseconds() < 0 ?
        "<none>" : interest.getInterestLifetimeMilliseconds()));
    return result;
  }

  /**
   * Return a copy of the strings array, removing any string that start with prefix.
   */
  private static ArrayList
  removeStartingWith(ArrayList strings, String prefix)
  {
    ArrayList result = new ArrayList();
    for (int i = 0; i < strings.size(); ++i) {
      if (!((String)strings.get(i)).startsWith(prefix))
        result.add(strings.get(i));
    }

    return result;
  }

  // ignoring nonce, check that the dumped interests are equal
  private static boolean
  interestDumpsEqual(ArrayList dump1, ArrayList dump2)
  {
    String prefix = "nonce:";
    return Arrays.equals
      (removeStartingWith(dump1, prefix).toArray(),
       removeStartingWith(dump2, prefix).toArray());
  }

  private static Interest
  createFreshInterest()
  {
    Interest freshInterest = new Interest(new Name("/ndn/abc"));
    freshInterest.setMustBeFresh(false);
    freshInterest.setMinSuffixComponents(4);
    freshInterest.setMaxSuffixComponents(6);
    freshInterest.getKeyLocator().setType(KeyLocatorType.KEY_LOCATOR_DIGEST);
    freshInterest.getKeyLocator().setKeyData
      (new Blob(new int[] {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
        0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F }));
    freshInterest.getExclude().appendComponent(new Name("abc").get(0)).appendAny();
    freshInterest.setInterestLifetimeMilliseconds(30000);
    freshInterest.setChildSelector(1);
    freshInterest.setMustBeFresh(true);
    freshInterest.setScope(2);

    return freshInterest;
  }

  private Interest referenceInterest;

  @Before
  public void
  setUp()
  {
    referenceInterest = new Interest();
    try {
      referenceInterest.wireDecode(new Blob(codedInterest, false));
    } catch (EncodingException ex) {
      // We don't expect this to happen.
      referenceInterest = null;
    }
  }

  @Test
  public void
  testDump()
  {
    // see if the dump format is the same as we expect
    ArrayList decodedDump = dumpInterest(referenceInterest);
    assertArrayEquals("Initial dump does not have expected format",
                      initialDump.toArray(), decodedDump.toArray());
  }

  @Test
  public void
  testRedecode()
  {
    // check that we encode and decode correctly
    Blob encoding = referenceInterest.wireEncode();
    Interest reDecodedInterest = new Interest();
    try {
      reDecodedInterest.wireDecode(encoding);
    } catch (EncodingException ex) {
      fail("Can't decode reDecodedInterest");
    }
    ArrayList redecodedDump = dumpInterest(reDecodedInterest);
    assertArrayEquals("Re-decoded interest does not match original",
                      initialDump.toArray(), redecodedDump.toArray());
  }

  @Test
  public void
  testCreateFresh()
  {
    Interest freshInterest = createFreshInterest();
    ArrayList freshDump = dumpInterest(freshInterest);
    assertTrue("Fresh interest does not match original",
               interestDumpsEqual(initialDump, freshDump));

    Interest reDecodedFreshInterest = new Interest();
    try {
      reDecodedFreshInterest.wireDecode(freshInterest.wireEncode());
    } catch (EncodingException ex) {
      fail("Can't decode reDecodedFreshInterest");
    }
    ArrayList reDecodedFreshDump = dumpInterest(reDecodedFreshInterest);

    assertTrue("Redecoded fresh interest does not match original",
               interestDumpsEqual(freshDump, reDecodedFreshDump));
  }

  @Test
  public void
  testCopyConstructor()
  {
    Interest interest = new Interest(referenceInterest);
    assertTrue("Interest constructed as deep copy does not match original",
               interestDumpsEqual(dumpInterest(interest), dumpInterest(referenceInterest)));
  }

  @Test
  public void
  testEmptyNonce()
  {
    // make sure a freshly created interest has no nonce
    Interest freshInterest = createFreshInterest();
    assertTrue("Freshly created interest should not have a nonce",
               freshInterest.getNonce().isNull());
  }

  @Test
  public void
  testSetRemovesNonce()
  {
    // Ensure that changing a value on an interest clears the nonce.
    assertFalse(referenceInterest.getNonce().isNull());
    Interest interest = new Interest(referenceInterest);
    // Change a child object.
    interest.getExclude().clear();
    assertTrue("Interest should not have a nonce after changing fields",
               interest.getNonce().isNull());
  }
}
