/* -*- c-basic-offset: 2; indent-tabs-mode: nil; -*- */
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
 * This file is maintained by Mario Lang <mlang@delysid.org>.
 */
package freedots;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A enum of all the braille signs (and a few utility methods).
 */
public enum Braille {
  dot(3), wholeRest(134),
  doubleFlat(126, 126), flat(126),
  natural(16),
  sharp(146), doubleSharp(146, 146),

  valueDistinction(126, 2),

  numberSign(3456), simileSign(2356),

  tie(4, 14), slur(14),
  accent(46, 236), martellato(56, 236), breathMark(6, 34),
  staccato(236), mezzoStaccato(5, 236), staccatissimo(6, 236),
  tenuto(456, 236),

  grace(5, 26), mordent(5, 235, 123), trill(235), turn(6, 256),

  fullMeasureInAccord(126, 345),
  partMeasureInAccord(46, 13), partMeasureInAccordDivision(5, 2),

  octave1(4, 4), octave2(4), octave3(45), octave4(456), octave5(5),
  octave6(46), octave7(56), octave8(6), octave9(6, 6),

  second(34), third(346), fourth(3456), fifth(35), sixth(356), seventh(25),
  octave(36),

  rightHandPart(46, 345), soloPart(5, 345), leftHandPart(456, 345),

  hyphen(5),

  postDottedDoubleBar(126, 2356), dottedDoubleBar(126, 23), doubleBar(126, 13),
  fermata(126, 123), fermataSquare(56, 126, 123), fermataTent(45, 126, 123),

  finger1(1), finger2(12), finger3(123), finger4(2), finger5(13),

  digit0(245), digit1(1), digit2(12), digit3(14), digit4(145),
  digit5(15), digit6(124), digit7(1245), digit8(125), digit9(24),
  lowerDigit0(356), lowerDigit1(2), lowerDigit2(23), lowerDigit3(25),
  lowerDigit4(256), lowerDigit5(26), lowerDigit6(235), lowerDigit7(2356),
  lowerDigit8(236), lowerDigit9(35);

  private int[] dots;
  private String cachedString;
  private boolean needsAdditionalDot3IfOneOfDot123Follows = false;

  Braille(final int dots) { this(new int[] {dots}); }
  Braille(final int dots1, final int dots2) { this(new int[] {dots1, dots2}); }
  Braille(final int dots1, final int dots2, final int dots3) {
    this(new int[] {dots1, dots2, dots3});
  }
  private Braille(final int[] dots) {
    this.dots = dots;
    cachedString = "";
    for (int element : dots)
      cachedString += String.valueOf(unicodeBraille(dotsToBits(element)));
  }
  @Override
  public String toString() {
    return cachedString;
  }

  /**
   * Concatenate a number of repetitions of this braille symbol according to
   * braille music rules.
   * @param amount number of repetitions
   * @return the concatenated repetition as a String
   */
  public String repeat(final int amount) {
    if (amount <= 0) return "";

    String atom = toString();
    if (amount == 1) return atom;
    else if (amount == 2) return atom + atom;
    else if (amount == 3) return atom + atom + atom;
    else {
      return numberSign.toString() + upperNumber(amount) + atom;
    }
  }

  /**
   * @return true if this braille music symbol needs an additional dot 3
   * if one of dots 1, 2 or 3 is following.
   */
  public boolean needsAdditionalDot3IfOneOfDot123Follows() {
    return needsAdditionalDot3IfOneOfDot123Follows;
  }
  private void needsAdditionalDot3IfOneOfDot123Follows(boolean newValue) {
    needsAdditionalDot3IfOneOfDot123Follows = newValue;
  }
  static {
    leftHandPart.needsAdditionalDot3IfOneOfDot123Follows(true);
    soloPart.needsAdditionalDot3IfOneOfDot123Follows(true);
    rightHandPart.needsAdditionalDot3IfOneOfDot123Follows(true);
  }

  /** Get an octave sign for a particular octave.
   * @param number indicates the octave
   * @return braille music octave sign
   */
  public static Braille octave(final int number) { return OCTAVES[number]; }
  public static Braille upperDigit(final int digit) { return DIGITS[digit]; }
  public static String upperNumber(int number) {
    String string = "";
    while (number > 0) {
      int digit = number % 10;
      string = upperDigit(digit) + string;
      number = number / 10;
    }
    return string;
  }
  public static Braille lowerDigit(final int digit) {
    return LOWER_DIGITS[digit];
  }
  public static Braille interval(final int interval) {
    return INTERVALS[interval - 1];
  }
  public static Braille finger(int finger) { return fingers[finger - 1]; }

  public static final Map<Character, Character>
  brfTable = Collections.unmodifiableMap(new HashMap<Character, Character>() {
      {
        put(createCharacter(0),     new Character(' '));
        put(createCharacter(1),     new Character('A'));
        put(createCharacter(2),     new Character('1'));
        put(createCharacter(3),     new Character((char)0X27));
        put(createCharacter(4),     new Character('@'));
        put(createCharacter(5),     new Character('"'));
        put(createCharacter(6),     new Character(','));
        put(createCharacter(12),    new Character('B'));
        put(createCharacter(13),    new Character('K'));
        put(createCharacter(14),    new Character('C'));
        put(createCharacter(15),    new Character('E'));
        put(createCharacter(16),    new Character('*'));
        put(createCharacter(23),    new Character('2'));
        put(createCharacter(24),    new Character('I'));
        put(createCharacter(25),    new Character('3'));
        put(createCharacter(26),    new Character('5'));
        put(createCharacter(34),    new Character('/'));
        put(createCharacter(35),    new Character('9'));
        put(createCharacter(36),    new Character('-'));
        put(createCharacter(45),    new Character('^'));
        put(createCharacter(46),    new Character('.'));
        put(createCharacter(56),    new Character(';'));
        put(createCharacter(123),   new Character('L'));
        put(createCharacter(124),   new Character('F'));
        put(createCharacter(125),   new Character('H'));
        put(createCharacter(126),   new Character('<'));
        put(createCharacter(134),   new Character('M'));
        put(createCharacter(135),   new Character('O'));
        put(createCharacter(136),   new Character('U'));
        put(createCharacter(145),   new Character('D'));
        put(createCharacter(146),   new Character('%'));
        put(createCharacter(156),   new Character(':'));
        put(createCharacter(234),   new Character('S'));
        put(createCharacter(235),   new Character('6'));
        put(createCharacter(236),   new Character('8'));
        put(createCharacter(245),   new Character('J'));
        put(createCharacter(246),   new Character('['));
        put(createCharacter(256),   new Character('4'));
        put(createCharacter(345),   new Character('>'));
        put(createCharacter(346),   new Character('+'));
        put(createCharacter(356),   new Character('0'));
        put(createCharacter(456),   new Character('_'));
        put(createCharacter(1234),  new Character('P'));
        put(createCharacter(1235),  new Character('R'));
        put(createCharacter(1236),  new Character('V'));
        put(createCharacter(1245),  new Character('G'));
        put(createCharacter(1246),  new Character('$'));
        put(createCharacter(1256),  new Character((char)0X5C));
        put(createCharacter(1345),  new Character('N'));
        put(createCharacter(1346),  new Character('X'));
        put(createCharacter(1356),  new Character('Z'));
        put(createCharacter(1456),  new Character('?'));
        put(createCharacter(2345),  new Character('T'));
        put(createCharacter(2346),  new Character('!'));
        put(createCharacter(2356),  new Character('7'));
        put(createCharacter(2456),  new Character('W'));
        put(createCharacter(3456),  new Character('#'));
        put(createCharacter(12345), new Character('Q'));
        put(createCharacter(12346), new Character('&'));
        put(createCharacter(12356), new Character('('));
        put(createCharacter(12456), new Character(']'));
        put(createCharacter(13456), new Character('Y'));
        put(createCharacter(23456), new Character(')'));
        put(createCharacter(123456), new Character('='));
      }
    });

  private static Character createCharacter(int dots) {
    return new Character(unicodeBraille(dotsToBits(dots)));
  }
  public static char unicodeBraille(int bits) {
    return (char)(0X2800 | bits);
  }
  public static int dotsToBits(int dots) {
    int bits = 0;
    while (dots > 0) {
      int number = dots % 10;
      dots /= 10;
      bits |= 1 << (number - 1);
    }
    return bits;
  }

  private static final Braille[] OCTAVES = new Braille[] {
    octave1, octave2, octave3, octave4, octave5, octave6, octave7,
    octave8, octave9
  };
  private static final Braille[] DIGITS = {
    digit0,
    digit1, digit2, digit3,
    digit4, digit5, digit6,
    digit7, digit8, digit9
  };
  private static final Braille[] LOWER_DIGITS = {
    lowerDigit0,
    lowerDigit1, lowerDigit2, lowerDigit3,
    lowerDigit4, lowerDigit5, lowerDigit6,
    lowerDigit7, lowerDigit8, lowerDigit9
  };
  private static final Braille[] INTERVALS = {
    second, third, fourth, fifth, sixth, seventh, octave
  };
  private static final Braille[] fingers = {
    finger1, finger2, finger3, finger4, finger5
  };
}