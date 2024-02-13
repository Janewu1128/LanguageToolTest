package org.languagetool.gui;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.awt.*;
import java.awt.event.ActionEvent;

public class FontChooserTest {

  private FontChooser fontChooserUnderTest;

  @Before
  public void setUp() {
    Frame dummyOwnerFrame = new Frame();

    // Initialize FontChooser's instance with the required constructor arguments
    fontChooserUnderTest = new FontChooser(dummyOwnerFrame, true);
  }

  @Test
  public void testCancelActionVisible() {
    // mock the cancell ActionEvent
    ActionEvent mockEvent = mock(ActionEvent.class);
    when(mockEvent.getActionCommand()).thenReturn(FontChooser.ACTION_COMMAND_CANCEL);
    fontChooserUnderTest.actionPerformed(mockEvent);
    assertFalse(fontChooserUnderTest.isVisible());
  }
  @Test
  public void testCancelActionFont() {
    // mock the cancell ActionEvent
    ActionEvent mockEvent = mock(ActionEvent.class);
    when(mockEvent.getActionCommand()).thenReturn(FontChooser.ACTION_COMMAND_CANCEL);
    fontChooserUnderTest.actionPerformed(mockEvent);
    assertNull(fontChooserUnderTest.getSelectedFont());
  }
  @Test
  public void testOkAction() {
    // mock the ok ActionEvent
    ActionEvent mockEvent = mock(ActionEvent.class);
    when(mockEvent.getActionCommand()).thenReturn(FontChooser.ACTION_COMMAND_OK);
    fontChooserUnderTest.actionPerformed(mockEvent);
    assertFalse(fontChooserUnderTest.isVisible());
  }
  @Test
  public void testResetAction() {
    // mock the reset ActionEvent
    ActionEvent mockEvent = mock(ActionEvent.class);
    when(mockEvent.getActionCommand()).thenReturn(FontChooser.ACTION_COMMAND_RESET);
    fontChooserUnderTest.actionPerformed(mockEvent);
    assertFalse(fontChooserUnderTest.isVisible());
  }

  }
//}
