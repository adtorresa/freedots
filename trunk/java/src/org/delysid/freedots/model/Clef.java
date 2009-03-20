/* -*- c-basic-offset: 2; -*- */
/*
 * FreeDots -- MusicXML to braille music transcription
 *
 * Copyright 2008-2009 Mario Lang  All Rights Reserved.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details (a copy is included in the LICENSE.txt file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License 
 * along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This software is maintained by Mario Lang <mlang@delysid.org>.
 */
package org.delysid.freedots.model;

public class Clef {
  public enum Sign { G, F, C; };

  Sign sign;
  int line;
  public Clef(Sign sign, int line) {
    this.sign = sign;
    this.line = line;
  }
  public boolean isTreble() { return (sign == Sign.G && line == 2); }
  public boolean isBass() { return (sign == Sign.F && line == 4); }

  public int getChordDirection() {
    switch (sign) {
      case G: switch (line) {
                case 2: return -1;
              }
      case F: switch (line) {
                case 4: return 1;
              }
    }
    return -1;
  }
}