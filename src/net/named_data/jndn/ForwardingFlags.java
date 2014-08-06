/**
 * Copyright (C) 2014 Regents of the University of California.
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

package net.named_data.jndn;

/**
 * A ForwardingFlags object holds the flags which specify how the forwarding
 * daemon should forward an interest for a registered prefix.  We use a separate
 * ForwardingFlags object to retain future compatibility if the daemon
 * forwarding bits are changed, amended or deprecated.
 */
public class ForwardingFlags {
  /**
   * Create a new ForwardingFlags with "active" and "childInherit" set and all
   * other flags cleared.
   */
  public ForwardingFlags() {}

  /**
   * Create a new ForwardingFlags as a copy of the given value.
   * @param forwardingFlags The ForwardingFlags to copy.
   */
  public ForwardingFlags(ForwardingFlags forwardingFlags)
  {
    active_ = forwardingFlags.active_;
    childInherit_ = forwardingFlags.childInherit_;
    advertise_ = forwardingFlags.advertise_;
    last_ = forwardingFlags.last_;
    capture_ = forwardingFlags.capture_;
    local_ = forwardingFlags.local_;
    tap_ = forwardingFlags.tap_;
    captureOk_ = forwardingFlags.captureOk_;
  }

  /**
   * Get the value of the "active" flag.
   * @return true if the flag is set, false if it is cleared.
   */
  public final boolean
  getActive() { return active_; }

  /**
   * Get the value of the "childInherit" flag.
   * @return true if the flag is set, false if it is cleared.
   */
  public final boolean
  getChildInherit() { return childInherit_; }

  /**
   * Get the value of the "advertise" flag.
   * @return true if the flag is set, false if it is cleared.
   */
  public final boolean
  getAdvertise() { return advertise_; }

  /**
   * Get the value of the "last" flag.
   * @return true if the flag is set, false if it is cleared.
   */
  public final boolean
  getLast() { return last_; }

  /**
   * Get the value of the "capture" flag.
   * @return true if the flag is set, false if it is cleared.
   */
  public final boolean
  getCapture() { return capture_; }

  /**
   * Get the value of the "local" flag.
   * @return true if the flag is set, false if it is cleared.
   */
  public final boolean
  getLocal() { return local_; }

  /**
   * Get the value of the "tap" flag.
   * @return true if the flag is set, false if it is cleared.
   */
  public final boolean
  getTap() { return tap_; }

  /**
   * Get the value of the "captureOk" flag.
   * @return true if the flag is set, false if it is cleared.
   */
  public final boolean
  getCaptureOk() { return captureOk_; }

  /**
   * Set the value of the "active" flag
   * @param active true to set the flag, false to clear it.
   */
  public final void
  setActive(boolean active) { active_ = active; }

  /**
   * Set the value of the "childInherit" flag
   * @param childInherit true to set the flag, false to clear it.
   */
  public final void
  setChildInherit(boolean childInherit) { childInherit_ = childInherit; }

  /**
   * Set the value of the "advertise" flag
   * @param advertise true to set the flag, false to clear it.
   */
  public final void
  setAdvertise(boolean advertise) { advertise_ = advertise; }

  /**
   * Set the value of the "last" flag
   * @param last true to set the flag, false to clear it.
   */
  public final void
  setLast(boolean last) { last_ = last; }

  /**
   * Set the value of the "capture" flag
   * @param capture true to set the flag, false to clear it.
   */
  public final void
  setCapture(boolean capture) { capture_ = capture; }

  /**
   * Set the value of the "local" flag
   * @param local true to set the flag, false to clear it.
   */
  public final void
  setLocal(boolean local) { local_ = local; }

  /**
   * Set the value of the "tap" flag
   * @param tap true to set the flag, false to clear it.
   */
  public final void
  setTap(boolean tap) { tap_ = tap; }

  /**
   * Set the value of the "captureOk" flag
   * @param captureOk true to set the flag, false to clear it.
   */
  public final void
  setCaptureOk(boolean captureOk) { captureOk_ = captureOk; }

  /**
   * Get an integer with the bits set according to the flags as used by the
   * ForwardingEntry message.
   * @return An integer with the bits set.
   */
  public final int
  getForwardingEntryFlags()
  {
    int result = 0;

    if (active_)
      result |= ForwardingEntryFlags_ACTIVE;
    if (childInherit_)
      result |= ForwardingEntryFlags_CHILD_INHERIT;
    if (advertise_)
      result |= ForwardingEntryFlags_ADVERTISE;
    if (last_)
      result |= ForwardingEntryFlags_LAST;
    if (capture_)
      result |= ForwardingEntryFlags_CAPTURE;
    if (local_)
      result |= ForwardingEntryFlags_LOCAL;
    if (tap_)
      result |= ForwardingEntryFlags_TAP;
    if (captureOk_)
      result |= ForwardingEntryFlags_CAPTURE_OK;

    return result;
  }

  /**
   * Set the flags according to the bits in forwardingEntryFlags as used by the
   * ForwardingEntry message.
   * @param forwardingEntryFlags An integer with the bits set.
   */
  public final void
  setForwardingEntryFlags(int forwardingEntryFlags)
  {
    active_ = (forwardingEntryFlags & ForwardingEntryFlags_ACTIVE) != 0;
    childInherit_ = (forwardingEntryFlags & ForwardingEntryFlags_CHILD_INHERIT) != 0;
    advertise_ = (forwardingEntryFlags & ForwardingEntryFlags_ADVERTISE) != 0;
    last_ = (forwardingEntryFlags & ForwardingEntryFlags_LAST) != 0;
    capture_ = (forwardingEntryFlags & ForwardingEntryFlags_CAPTURE) != 0;
    local_ = (forwardingEntryFlags & ForwardingEntryFlags_LOCAL) != 0;
    tap_ = (forwardingEntryFlags & ForwardingEntryFlags_TAP) != 0;
    captureOk_ = (forwardingEntryFlags & ForwardingEntryFlags_CAPTURE_OK) != 0;
  }

  /**
   * Get an integer with the bits set according to the NFD forwarding flags as
   * used in the ControlParameters of the command interest.
   * @return An integer with the bits set.
   */
  public final int
  getNfdForwardingFlags()
  {
    int result = 0;

    if (childInherit_)
      result |= NfdForwardingFlags_CHILD_INHERIT;
    if (capture_)
      result |= NfdForwardingFlags_CAPTURE;

    return result;
  }

  /**
   * Set the flags according to the NFD forwarding flags as used in the
   * ControlParameters of the command interest.
   * @param nfdForwardingFlags An integer with the bits set.
   */
  public final void
  setNfdForwardingFlags(int nfdForwardingFlags)
  {
    childInherit_ = (nfdForwardingFlags & NfdForwardingFlags_CHILD_INHERIT) != 0;
    capture_ = (nfdForwardingFlags & NfdForwardingFlags_CAPTURE) != 0;
  }

  private static final int ForwardingEntryFlags_ACTIVE         = 1;
  private static final int ForwardingEntryFlags_CHILD_INHERIT  = 2;
  private static final int ForwardingEntryFlags_ADVERTISE      = 4;
  private static final int ForwardingEntryFlags_LAST           = 8;
  private static final int ForwardingEntryFlags_CAPTURE       = 16;
  private static final int ForwardingEntryFlags_LOCAL         = 32;
  private static final int ForwardingEntryFlags_TAP           = 64;
  private static final int ForwardingEntryFlags_CAPTURE_OK   = 128;

  private static final int NfdForwardingFlags_CHILD_INHERIT  = 1;
  private static final int NfdForwardingFlags_CAPTURE  =       2;

  private boolean active_ = true;
  private boolean childInherit_ = true;
  private boolean advertise_ = false;
  private boolean last_ = false;
  private boolean capture_ = false;
  private boolean local_ = false;
  private boolean tap_ = false;
  private boolean captureOk_ = false;
}
