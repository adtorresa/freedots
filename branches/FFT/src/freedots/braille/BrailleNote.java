/* -*- c-basic-offset: 2; indent-tabs-mode: nil; -*- */
/*
 * FreeDots -- MusicXML to braille music transcription
 *
 * Copyright 2008-2010 Mario Lang  All Rights Reserved.
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
package freedots.braille;

import java.awt.Color;

import freedots.Options;
import freedots.music.AbstractPitch;
import freedots.music.Accidental;
import freedots.music.Articulation;
import freedots.music.AugmentedFraction;
import freedots.music.Fingering;
import freedots.music.Ornament;
import freedots.music.Slur;
import freedots.musicxml.Note;

import freedots.compression.Repeatable;
import freedots.compression.OccurrenceCounter;

/** The braille representation of a note or rest.
 * <p>
 * This includes all signs which must immediately follow or proceed the actual
 * value or rest sign.
 *
 * @see <a href="http://brl.org/music/code/bmb/chap01/index.html">Chapter 1:
 *      Notes and Values</a>
 */
public class BrailleNote extends BrailleList implements Repeatable{
  private final Note note;

  /** Construct a braille note and all of its children.
   * @param note refers to the MusicXML Note object
   * @param lastPitch is used to decide if an octave sign needs to be inserted
   */
  public BrailleNote(final Note note, final AbstractPitch lastPitch) {
    super();
    this.note = note;

    if (note.isGrace()) add(new GraceSign());

    for (Ornament ornament: note.getOrnaments())
      add(createOrnamentSign(ornament));
    for (Articulation articulation: note.getArticulations())
      add(createArticulationSign(articulation));

    Accidental accidental = note.getAccidental();
    if (accidental != null) add(new AccidentalSign(accidental));

    AbstractPitch pitch = (AbstractPitch)note.getPitch();
    if (pitch == null) /* A hack to support unpitched notes */
      pitch = (AbstractPitch)note.getUnpitched();

    final AugmentedFraction value = note.getAugmentedFraction();

    if (pitch != null) { /* A sounding note */
      if (isOctaveSignRequired(pitch, lastPitch))
        add(new OctaveSign(pitch.getOctave()));

      add(new PitchAndValueSign(pitch, value));
    } else {
      add(new RestSign(value));
    }
    for (int i = 0; i < value.getDots(); i++) add(new Dot());

    if (Options.getInstance().getShowFingering()) {
      final Fingering fingering = note.getFingering();
      if (!fingering.getFingers().isEmpty()) {
        add(new BrailleFingering(fingering));
      }
    }

    if (note.isTieStart()) {
      add(new TieSign());
    } else {
      boolean addSlur = false;
      for (Slur<Note> slur:note.getSlurs()) {
        if (!slur.lastNote(note)) {
          addSlur = true;
          break;
        }
      }
      if (addSlur) {
        add(new SlurSign());
      }
    }
  }
  @Override public String getDescription() {
    return "A note.";
  }
  @Override public Object getScoreObject() { return note; }

  public AbstractPitch getPitch() { return note.getPitch(); }

  private static boolean isOctaveSignRequired(final AbstractPitch pitch,
                                              final AbstractPitch lastPitch) {
    if (lastPitch != null) {
      final int halfSteps = Math.abs(pitch.getMIDIPitch()
                                     - lastPitch.getMIDIPitch());
      if ((halfSteps < 5)
          || (halfSteps >= 5 && halfSteps <= 7
           && pitch.getOctave() == lastPitch.getOctave())) return false;
    }
    return true;
  }

  public static class GraceSign extends Sign {
    GraceSign() { super(braille(5, 26)); }
    public String getDescription() {
      return "Indicates the this is a grace note";
    }
	@Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = Color.green;
		
	}
  }

  private static Sign createOrnamentSign(Ornament ornament) {
    switch (ornament) {
    case mordent:         return new MordentSign();
    case invertedMordent: return new InvertedMordentSign();
    case trill:           return new TrillSign();
    case turn:            return new TurnSign();
    default:              throw new AssertionError(ornament);
    }
  }
  public static class MordentSign extends Sign {
    MordentSign() { super(braille(5, 235, 123)); }
    public String getDescription() { return "A mordent sign"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = Color.green;
	}
  }
  public static class InvertedMordentSign extends Sign {
    InvertedMordentSign() { super(braille(6, 235, 123)); }
    public String getDescription() { return "A inverted mordent sign"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = Color.green;
	}
  }
  public static class TrillSign extends Sign {
    TrillSign() { super(braille(235)); }
    public String getDescription() { return "A trill sign"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = Color.green;
	}
  }
  public static class TurnSign extends Sign {
    TurnSign() { super(braille(6, 256)); }
    public String getDescription() { return "A turn sign"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = Color.green;
	}
  }

  private static Sign createArticulationSign(Articulation articulation) {
    switch (articulation) {
    case accent:        return new AccentSign();
    case strongAccent:  return new MartellatoSign();
    case breathMark:    return new BreathSign();
    case staccato:      return new StaccatoSign();
    case mezzoStaccato: return new MezzoStaccatoSign();
    case staccatissimo: return new StaccatissimoSign();
    case tenuto:        return new TenutoSign();
    default:            throw new AssertionError(articulation);
    }
  }
  public static class AccentSign extends Sign {
    AccentSign() { super(braille(46, 236)); }
    public String getDescription() { return "An accent sign"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = new Color(0, 250, 154);
	}
  }
  public static class MartellatoSign extends Sign {
    MartellatoSign() { super(braille(56, 236)); }
    public String getDescription() {
      return "A martellato (strong accent) sign";
    }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = new Color(0, 250, 154);
	}
  }
  public static class BreathSign extends Sign {
    BreathSign() { super(braille(6, 34)); }
    public String getDescription() { return "A breath mark"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = new Color(0, 250, 154);
	}
  }
  public static class StaccatoSign extends Sign {
    StaccatoSign() { super(braille(236)); }
    public String getDescription() { return "A staccato sign"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = new Color(0, 250, 154);
	}
  }
  public static class MezzoStaccatoSign extends Sign {
    MezzoStaccatoSign() { super(braille(5, 236)); }
    public String getDescription() { return "A mezzo staccato sign"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = new Color(0, 250, 154);
	}
  }
  public static class StaccatissimoSign extends Sign {
    StaccatissimoSign() { super(braille(6, 236)); }
    public String getDescription() { return "A staccatissimo sign"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = new Color(0, 250, 154);
	}
  }
  public static class TenutoSign extends Sign {
    TenutoSign() { super(braille(456, 236)); }
    public String getDescription() { return "A tenuto sign"; }
    
    @Override
	public Color getSignColor() {
		return this.signColor;
	}
	@Override
	public void setSignColor() {
		this.signColor = new Color(0, 250, 154);
	}
  }

  /**
   * Methods inherited from the interface Repeatable (itself from Maskable)
   *
   */
  public static OccurrenceCounter<BrailleNote> occurrenceCounter = new OccurrenceCounter<BrailleNote>();
  public OccurrenceCounter<BrailleNote> getCounter(){ 
    return this.occurrenceCounter;
  }
 
  public void addToCounter(){ 
     this.occurrenceCounter.addElement(this);
  }  
  
  public void emptyCounter(){
    this.occurrenceCounter.empty();
  }
}