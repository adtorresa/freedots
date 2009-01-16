/* -*- c-basic-offset: 2; -*- */
package org.delysid.freedots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.delysid.freedots.model.AbstractPitch;
import org.delysid.freedots.model.Event;
import org.delysid.freedots.model.StaffElement;
import org.delysid.freedots.model.MusicList;
import org.delysid.freedots.model.StartBar;
import org.delysid.freedots.model.EndBar;
import org.delysid.freedots.model.VerticalEvent;
import org.delysid.freedots.model.Voice;

import org.delysid.freedots.musicxml.Score;
import org.delysid.freedots.musicxml.Note;
import org.delysid.freedots.musicxml.Part;
import org.delysid.freedots.musicxml.Pitch;

public class Transcriber {
  Score score;

  public Score getScore() { return score; }

  Options options;

  String textStore;
  int characterCount;
  int lineCount;
  int pageNumber;

  static final String lineSeparator = System.getProperty("line.separator");

  public Transcriber(Score score, Options options) {
    this.score = score;
    this.options = options;
    clear();
    if (score != null)
      try { transcribe(); }
      catch (Exception e) { e.printStackTrace(); }
  }
  private void clear() {
    textStore = "";
    characterCount = 0;
    lineCount = 0;
    pageNumber = 1;
  }
  void transcribe() throws Exception {
    for (Part part:score.getParts()) {
      printLine(part.getName());
      for (Segment segment:getSegments(part)) {
        int staffCount = segment.getStaffCount();

        for (int staffIndex = 0; staffIndex < staffCount; staffIndex++) {
	  Staff staff = segment.getStaff(staffIndex);
	  MusicList measure = new MusicList();

          if (characterCount > 0) newLine();
          indentTo(2);

	  for (int staffElementIndex = 0; staffElementIndex < staff.size();
	       staffElementIndex++) {
	    
	    Event event = staff.get(staffElementIndex);

	    if (event instanceof EndBar) {
	      List<Voice> voices = measure.getVoices();
	      int voiceCount = voices.size();

	      for (int voiceIndex = 0; voiceIndex < voiceCount; voiceIndex++) {
		BrailleMeasure bm = new BrailleMeasure();
		for (Event voiceEvent:voices.get(voiceIndex)) {
                  bm.add(voiceEvent);
		}
 
                String braille = bm.toString();
                if (characterCount+braille.length() > options.getPageWidth()) {
                  newLine();
                }
		printString(braille);

		if (voiceIndex < voiceCount-1) {
		  printString(Braille.fullVoiceSeparator.toString());
		}
	      }
	      printString(" ");

	      measure = new MusicList();
	    } else {
	      measure.add(event);
	    }
          }
        }
      }
    }
  }
  private void printString(String text) {
    textStore += text;
    characterCount += text.length();
  }
  private void printLine(String text) {
    textStore += text;
    newLine();
  }
  private void newLine() {
    textStore += lineSeparator;
    characterCount = 0;
    lineCount += 1;
    if (lineCount == options.getPageHeight()) {
      indentTo(options.getPageWidth()-5);
      textStore += Integer.toString(pageNumber++) + lineSeparator;
      characterCount = 0;
      lineCount = 0;
    }
  }
  private void indentTo(int column) {
    int difference = column - characterCount;
    while (difference > 0) {
      textStore += " ";
      characterCount += 1;
      difference -= 1;
    }
  }
  public String toString() {
    return textStore;
  }
  class Staff extends MusicList {
    String name;
    public Staff() { super(); }

    public void setName(String name) { this.name = name; }
  }
  class Segment extends MusicList {
    Segment() { super(); }
    public int getStaffCount() {
      for (Event event:this) {
	if (event instanceof StartBar) {
	  StartBar startBar = (StartBar)event;
	  return startBar.getStaffCount();
	}
      }
      return 0;
    }
    public Staff getStaff(int index) {
      List<Staff> staves = new ArrayList<Staff>();
      Map<String, Staff> staffNames = new HashMap<String, Staff>();
      int usedStaves = 0;

      for (int i = 0; i < getStaffCount(); i++)	staves.add(new Staff());
      
      for (Event event:this) {
	if (event instanceof VerticalEvent) {
	  for (Staff staff:staves) staff.add(event);
	} else if (event instanceof StaffElement) {
	  String staffName = ((StaffElement)event).getStaffName();
	  if (!staffNames.containsKey(staffName))
	    staffNames.put(staffName, staves.get(usedStaves++));
	  staffNames.get(staffName).add(event);
	}
      }
      return staves.get(index);
    }
  }
  List<Segment> getSegments(Part part) throws Exception {
    List<Segment> segments = new ArrayList<Segment>();
    Segment currentSegment = new Segment();
    segments.add(currentSegment);
    MusicList musicList = part.getMusicList();
    int index = 0;

    while (true) {
      while (index < musicList.size()) {
	Event event = musicList.get(index++);
	currentSegment.add(event);
	if (event instanceof EndBar) break;
      }

      if (index == musicList.size()) return segments;

      if (!(musicList.get(index) instanceof StartBar))
	throw new Exception();

      StartBar startBar = (StartBar)musicList.get(index);
      if (startBar.getStaffCount() != currentSegment.getStaffCount()) {
	currentSegment = new Segment();
	segments.add(currentSegment);
      }
    }
  }
  class BrailleMeasure {
    List<Object> elements = new ArrayList<Object>();
    public void add(Event staffElement) {
      elements.add(staffElement);
    }
    public String toString() {
      String output = "";
      for (Object element:elements) {
	if (element instanceof Note) {
	  Note note = (Note)element;
	  AbstractPitch pitch = (AbstractPitch)note.getPitch();
          output += note.getAugmentedFraction().toBrailleString(pitch);
	}
      }
      return output;
    }
  }
}
