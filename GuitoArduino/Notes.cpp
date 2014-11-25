// Course:      ENSC 100W
// Group:       Kappa
// Authors:     Mateen Ulhaq, 301250828, <mulhaq@sfu.ca>
// Description: Determine pitch (Hz) of note.
// License:     GPLv3


#include "Arduino.h"
#include "Notes.h"


String tuning_notes[TUNING_COUNT] = {
  // <Tuning Name>, <Notes from top/thickest to bottom/thinnest string>
  "S, E2 A2 D3 G3 B3 E4" // Standard Tuning
};

Tuning tuning_table[TUNING_COUNT];


// Checks if valid character
// (a-z)(A-Z)(0-9)(#)
bool isValidNoteChar(char c)
{
  if(
    ((c >= 'a') && (c <= 'z')) ||
    ((c >= 'A') && (c <= 'Z')) ||
    ((c >= '0') && (c <= '9')) ||
    (c == '#')
  )
  {
    return true;
  }

  return false;
}


// Accepts note string and returns steps from A4
int stepsFromA4(String s)
{
  int i = 0;
  int n = 0;
  int octave = 4;
  String letter = "A";

  // Check for empty string
  if(s.length() == 0)
  {
    return 0;
  }

  // Get letter and convert to uppercase
  letter[0] = s[i++];
  letter.toUpperCase();

  // Check if sharp, flat, or natural
  // Find note in scale
  // Add index to n
  if(s[i] == '#')
  {
    letter = letter + s[i++];

    for(int j = 0; j < 12; ++j)
    {
      if(letter.equals(chromatic_scale_sharp[j]))
      {
        n = j;
        break;
      }
    }
  }
  else
  {
    letter = letter + s[i++];

    for(int j = 0; j < 12; ++j)
    {
      if(letter.equals(chromatic_scale_flat[j]))
      {
        n = j;
        break;
      }
    }
  }

  n -= 9; // Center at index of "A" (9)

  octave = s.substring(i).toInt();

  n += (octave - 4) * 12;

  return 0;
}


// Initializes tuning table
void InitializeTuningTable()
{
  for(int i = 0; i < TUNING_COUNT; ++i)
  {
    int j = 0;

    String s = tuning_notes[i];
    int len = s.length();

    String note = "";

    // Skip tuning name
    for(; j < len; ++j)
    {
      if(s[j] == ',')
      {
        ++j;
        break;
      }
    }

    // Parse string
    for(; j < len; ++j)
    {
      if(!isValidNoteChar(s[j]))
      {
        ++j;
        continue;
      }

      // Get note
      for(; isValidNoteChar(s[j]); ++j)
      {
        note = note + s[j];
      }

      tuning_table[i].pitch = NOTE_A4 + pow(TWELFTH_ROOT_OF_TWO, stepsFromA4(note));

      // T (Tension) = (UW x (2 x L x F)2) / 386.4
      // tuning_table[i].tension = ????? WTFF?????;

    }
  }
}

