/**
 * Copyright (C) 2017 Regents of the University of California.
 * @author: Jeff Thompson <jefft0@remap.ucla.edu>
 * From ndn-cxx unit tests:
 * https://github.com/named-data/ndn-cxx/blob/master/tests/unit-tests/security/pib/detail/identity-impl.t.cpp
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

import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.pib.Pib;
import net.named_data.jndn.security.pib.detail.PibIdentityImpl;
import net.named_data.jndn.security.pib.PibImpl;
import net.named_data.jndn.security.pib.PibKey;
import net.named_data.jndn.security.pib.PibMemory;
import net.named_data.jndn.security.v2.CertificateV2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.Before;

public class TestPibIdentityImpl {
  @Before
  public void
  setUp() throws EncodingException, CertificateV2.Error
  {
    fixture = new PibDataFixture();
  }

  PibDataFixture fixture;

  @Test
  public void
  testBasic() throws PibImpl.Error, Pib.Error
  {
    PibMemory pibImpl = new PibMemory();
    PibIdentityImpl identity1 = new PibIdentityImpl(fixture.id1, pibImpl, true);

    assertTrue(fixture.id1.equals(identity1.getName()));
  }

  @Test
  public void
  testKeyOperation() throws PibImpl.Error, Pib.Error
  {
    PibMemory pibImpl = new PibMemory();
    PibIdentityImpl identity1 = new PibIdentityImpl(fixture.id1, pibImpl, true);
    try {
      new PibIdentityImpl(fixture.id1, pibImpl, false);
    } catch (Throwable ex) {
      fail("Unexpected exception: " + ex.getMessage());
    }

    // The identity should not have any key.
    assertEquals(0, identity1.getKeys_().size());

    // Getting non-existing key should throw Pib.Error.
    try {
      identity1.getKey(fixture.id1Key1Name);
      fail("Did not throw the expected exception");
    }
    catch (Pib.Error ex) {}
    catch (Exception ex) { fail("Did not throw the expected exception"); }
    // Getting the default key should throw Pib.Error.
    try {
      identity1.getDefaultKey();
      fail("Did not throw the expected exception");
    }
    catch (Pib.Error ex) {}
    catch (Exception ex) { fail("Did not throw the expected exception"); }
    // Setting a non-existing key as the default key should throw Pib.Error.
    try {
      identity1.setDefaultKey(fixture.id1Key1Name);
      fail("Did not throw the expected exception");
    }
    catch (Pib.Error ex) {}
    catch (Exception ex) { fail("Did not throw the expected exception"); }

    // Add a key.
    identity1.addKey(fixture.id1Key1.buf(), fixture.id1Key1Name);
    try {
      identity1.getKey(fixture.id1Key1Name);
    } catch (Throwable ex) {
      fail("Unexpected exception: " + ex.getMessage());
    }

    // A new key should become the default key when there is no default.
    try {
      identity1.getDefaultKey();
    } catch (Throwable ex) {
      fail("Unexpected exception: " + ex.getMessage());
    }
    PibKey defaultKey0 = identity1.getDefaultKey();
    assertTrue(fixture.id1Key1Name.equals(defaultKey0.getName()));
    assertTrue(defaultKey0.getPublicKey().equals(fixture.id1Key1));

    // Remove a key.
    identity1.removeKey(fixture.id1Key1Name);
    try {
      identity1.setDefaultKey(fixture.id1Key1Name);
      fail("Did not throw the expected exception");
    }
    catch (Pib.Error ex) {}
    catch (Exception ex) { fail("Did not throw the expected exception"); }

    try {
      identity1.getDefaultKey();
      fail("Did not throw the expected exception");
    }
    catch (Pib.Error ex) {}
    catch (Exception ex) { fail("Did not throw the expected exception"); }

    // Set the default key directly.
    try {
      identity1.setDefaultKey(fixture.id1Key1.buf(), fixture.id1Key1Name);
    } catch (Throwable ex) {
      fail("Unexpected exception: " + ex.getMessage());
    }
    try {
      identity1.getDefaultKey();
    } catch (Throwable ex) {
      fail("Unexpected exception: " + ex.getMessage());
    }
    try {
      identity1.getKey(fixture.id1Key1Name);
    } catch (Throwable ex) {
      fail("Unexpected exception: " + ex.getMessage());
    }

    // Check for a default key.
    PibKey defaultKey1 = identity1.getDefaultKey();
    assertTrue(fixture.id1Key1Name.equals(defaultKey1.getName()));
    assertTrue(defaultKey1.getPublicKey().equals(fixture.id1Key1));

    // Add another key.
    identity1.addKey(fixture.id1Key2.buf(), fixture.id1Key2Name);
    assertEquals(2, identity1.getKeys_().size());

    // Set the default key using a name.
    try {
      identity1.setDefaultKey(fixture.id1Key2Name);
    } catch (Throwable ex) {
      fail("Unexpected exception: " + ex.getMessage());
    }
    try {
      identity1.getDefaultKey();
    } catch (Throwable ex) {
      fail("Unexpected exception: " + ex.getMessage());
    }
    PibKey defaultKey2 = identity1.getDefaultKey();
    assertTrue(fixture.id1Key2Name.equals(defaultKey2.getName()));
    assertTrue(defaultKey2.getPublicKey().equals(fixture.id1Key2));

    // Remove a key.
    identity1.removeKey(fixture.id1Key1Name);
    try {
      identity1.getKey(fixture.id1Key1Name);
      fail("Did not throw the expected exception");
    }
    catch (Pib.Error ex) {}
    catch (Exception ex) { fail("Did not throw the expected exception"); }

    assertEquals(1, identity1.getKeys_().size());

    // Seting the default key directly again should change the default.
    try {
      identity1.setDefaultKey(fixture.id1Key1.buf(), fixture.id1Key1Name);
    } catch (Throwable ex) {
      fail("Unexpected exception: " + ex.getMessage());
    }
    PibKey defaultKey3 = identity1.getDefaultKey();
    assertTrue(fixture.id1Key1Name.equals(defaultKey3.getName()));
    assertTrue(defaultKey3.getPublicKey().equals(fixture.id1Key1));
    assertEquals(2, identity1.getKeys_().size());

    // Remove all keys.
    identity1.removeKey(fixture.id1Key1Name);
    try {
      identity1.getKey(fixture.id1Key1Name);
      fail("Did not throw the expected exception");
    }
    catch (Pib.Error ex) {}
    catch (Exception ex) { fail("Did not throw the expected exception"); }

    assertEquals(1, identity1.getKeys_().size());
    identity1.removeKey(fixture.id1Key2Name);
    try {
      identity1.getKey(fixture.id1Key2Name);
      fail("Did not throw the expected exception");
    }
    catch (Pib.Error ex) {}
    catch (Exception ex) { fail("Did not throw the expected exception"); }

    assertEquals(0, identity1.getKeys_().size());
    try {
      identity1.getDefaultKey();
      fail("Did not throw the expected exception");
    }
    catch (Pib.Error ex) {}
    catch (Exception ex) { fail("Did not throw the expected exception"); }
  }
}