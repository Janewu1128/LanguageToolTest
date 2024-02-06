/* LanguageTool, a natural language style checker 
 * Copyright (C) 2006 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.tools;

import org.junit.Test;
import org.languagetool.FakeLanguage;
import org.languagetool.Language;
import org.languagetool.TestTools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Naber
 */
public class StringToolsTest {

  @Test
  public void testAssureSet() {
    try {
      StringTools.assureSet("", "varName");
      fail();
    } catch (IllegalArgumentException ignored) {}
    try {
      StringTools.assureSet(" \t", "varName");
      fail();
    } catch (IllegalArgumentException ignored) {}
    try {
      StringTools.assureSet(null, "varName");
      fail();
    } catch (NullPointerException ignored) {}
    StringTools.assureSet("foo", "varName");
  }

  @Test
  public void testToId() {
    assertEquals("SS", "ß".toUpperCase());
    FakeLanguage german = new FakeLanguage("de");
    FakeLanguage portuguese = new FakeLanguage("pt");
    assertEquals("BL_Q_A__UEBEL_OEAESSOE", StringTools.toId(" Bl'a (übel öäßÖ ", german));
    assertEquals("ÜSS_ÇÃÔ_OÙ_Ñ", StringTools.toId("üß çãÔ-où Ñ", portuguese));
    assertEquals("FOOÓÉÉ", StringTools.toId("fooóéÉ", german));
  }

  @Test
  public void testReadStream() throws IOException {
    String content = StringTools.readStream(new FileInputStream("src/test/resources/testinput.txt"), "utf-8");
    assertEquals("one\ntwo\nöäüß\nșțîâăȘȚÎÂĂ\n", content);
  }

  @Test
  public void testIsAllUppercase() {
    assertTrue(StringTools.isAllUppercase("A"));
    assertTrue(StringTools.isAllUppercase("ABC"));
    assertTrue(StringTools.isAllUppercase("ASV-EDR"));
    assertTrue(StringTools.isAllUppercase("ASV-ÖÄÜ"));
    assertTrue(StringTools.isAllUppercase(""));
    
    assertFalse(StringTools.isAllUppercase("ß"));
    assertFalse(StringTools.isAllUppercase("AAAAAAAAAAAAq"));
    assertFalse(StringTools.isAllUppercase("a"));
    assertFalse(StringTools.isAllUppercase("abc"));
  }

  @Test
  public void testIsMixedCase() {
    assertTrue(StringTools.isMixedCase("AbC"));
    assertTrue(StringTools.isMixedCase("MixedCase"));
    assertTrue(StringTools.isMixedCase("iPod"));
    assertTrue(StringTools.isMixedCase("AbCdE"));
    
    assertFalse(StringTools.isMixedCase(""));
    assertFalse(StringTools.isMixedCase("ABC"));
    assertFalse(StringTools.isMixedCase("abc"));
    assertFalse(StringTools.isMixedCase("!"));
    assertFalse(StringTools.isMixedCase("Word"));
  }

  @Test
  public void testIsCapitalizedWord() {
    assertTrue(StringTools.isCapitalizedWord("Abc"));
    assertTrue(StringTools.isCapitalizedWord("Uppercase"));
    assertTrue(StringTools.isCapitalizedWord("Ipod"));
    
    assertFalse(StringTools.isCapitalizedWord(""));
    assertFalse(StringTools.isCapitalizedWord("ABC"));
    assertFalse(StringTools.isCapitalizedWord("abc"));
    assertFalse(StringTools.isCapitalizedWord("!"));
    assertFalse(StringTools.isCapitalizedWord("wOrD"));
  }

  @Test
  public void testStartsWithUppercase() {
    assertTrue(StringTools.startsWithUppercase("A"));
    assertTrue(StringTools.startsWithUppercase("ÄÖ"));
    
    assertFalse(StringTools.startsWithUppercase(""));
    assertFalse(StringTools.startsWithUppercase("ß"));
    assertFalse(StringTools.startsWithUppercase("-"));
  }

  @Test
  public void testUppercaseFirstChar() {
    assertEquals(null, StringTools.uppercaseFirstChar(null));
    assertEquals("", StringTools.uppercaseFirstChar(""));
    assertEquals("A", StringTools.uppercaseFirstChar("A"));
    assertEquals("Öäü", StringTools.uppercaseFirstChar("öäü"));
    assertEquals("ßa", StringTools.uppercaseFirstChar("ßa"));
    assertEquals("'Test'", StringTools.uppercaseFirstChar("'test'"));
    assertEquals("''Test", StringTools.uppercaseFirstChar("''test"));
    assertEquals("''T", StringTools.uppercaseFirstChar("''t"));
    assertEquals("'''", StringTools.uppercaseFirstChar("'''"));
  }

  @Test
  public void testLowercaseFirstChar() {
    assertEquals(null, StringTools.lowercaseFirstChar(null));
    assertEquals("", StringTools.lowercaseFirstChar(""));
    assertEquals("a", StringTools.lowercaseFirstChar("A"));
    assertEquals("öäü", StringTools.lowercaseFirstChar("Öäü"));
    assertEquals("ßa", StringTools.lowercaseFirstChar("ßa"));
    assertEquals("'test'", StringTools.lowercaseFirstChar("'Test'"));
    assertEquals("''test", StringTools.lowercaseFirstChar("''Test"));
    assertEquals("''t", StringTools.lowercaseFirstChar("''T"));
    assertEquals("'''", StringTools.lowercaseFirstChar("'''"));
  }

  @Test
  public void testReaderToString() throws IOException {
    String str = StringTools.readerToString(new StringReader("bla\nöäü"));
    assertEquals("bla\nöäü", str);
    StringBuilder longStr = new StringBuilder();
    for (int i = 0; i < 4000; i++) {
      longStr.append('x');
    }
    longStr.append("1234567");
    assertEquals(4007, longStr.length());
    String str2 = StringTools.readerToString(new StringReader(longStr.toString()));
    assertEquals(longStr.toString(), str2);
  }

  @Test
  public void testEscapeXMLandHTML() {
    assertEquals("foo bar", StringTools.escapeXML("foo bar"));
    assertEquals("!ä&quot;&lt;&gt;&amp;&amp;", StringTools.escapeXML("!ä\"<>&&"));
    assertEquals("!ä&quot;&lt;&gt;&amp;&amp;", StringTools.escapeHTML("!ä\"<>&&"));
  }

  @Test
  public void testListToString() {
    List<String> list = new ArrayList<>();
    list.add("foo");
    list.add("bar");
    list.add(",");
    assertEquals("foo,bar,,", String.join(",", list));
    assertEquals("foo\tbar\t,", String.join("\t", list));
  }

  @Test
  public void testTrimWhitespace() {
    try {
      assertEquals(null, StringTools.trimWhitespace(null));
      fail();
    } catch (NullPointerException ignored) {}
    assertEquals("", StringTools.trimWhitespace(""));
    assertEquals("", StringTools.trimWhitespace(" "));
    assertEquals("XXY", StringTools.trimWhitespace(" \nXX\t Y"));
    assertEquals("XXY", StringTools.trimWhitespace(" \r\nXX\t Y"));
    assertEquals("word", StringTools.trimWhitespace("word"));
    //only one space in the middle of the word is significant:
    assertEquals("1 234,56", StringTools.trimWhitespace("1 234,56"));
    assertEquals("1234,56", StringTools.trimWhitespace("1  234,56"));
  }

  @Test
  public void testAddSpace() {
    Language demoLanguage = TestTools.getDemoLanguage();
    assertEquals(" ", StringTools.addSpace("word", demoLanguage));
    assertEquals("", StringTools.addSpace(",", demoLanguage));
    assertEquals("", StringTools.addSpace(",", demoLanguage));
    assertEquals("", StringTools.addSpace(",", demoLanguage));
    assertEquals("", StringTools.addSpace(".", new FakeLanguage("fr")));
    assertEquals("", StringTools.addSpace(".", new FakeLanguage("de")));
    assertEquals(" ", StringTools.addSpace("!", new FakeLanguage("fr")));
    assertEquals("", StringTools.addSpace("!", new FakeLanguage("de")));
  }

  @Test
  public void testIsWhitespace() {
    assertEquals(true, StringTools.isWhitespace("\uFEFF"));
    assertEquals(true, StringTools.isWhitespace("  "));
    assertEquals(true, StringTools.isWhitespace("\t"));
    assertEquals(true, StringTools.isWhitespace("\u2002"));
    //non-breaking space is also a whitespace
    assertEquals(true, StringTools.isWhitespace("\u00a0"));
    assertEquals(false, StringTools.isWhitespace("abc"));
    //non-breaking OOo field
    assertEquals(false, StringTools.isWhitespace("\\u02"));
    assertEquals(false, StringTools.isWhitespace("\u0001"));
    // narrow nbsp:
    assertEquals(true, StringTools.isWhitespace("\u202F"));
  }

  @Test
  public void testIsPositiveNumber() {
    assertEquals(true, StringTools.isPositiveNumber('3'));
    assertEquals(false, StringTools.isPositiveNumber('a'));
  }

  @Test
  public void testIsEmpty() {
    assertEquals(true, StringTools.isEmpty(""));
    assertEquals(true, StringTools.isEmpty(null));
    assertEquals(false, StringTools.isEmpty("a"));
  }

  @Test
  public void testFilterXML() {
    assertEquals("test", StringTools.filterXML("test"));
    assertEquals("<<test>>", StringTools.filterXML("<<test>>"));
    assertEquals("test", StringTools.filterXML("<b>test</b>"));
    assertEquals("A sentence with a test", StringTools.filterXML("A sentence with a <em>test</em>"));
  }

  @Test
  public void testAsString() {
    assertNull(StringTools.asString(null));
    assertEquals("foo!", "foo!");
  }

  @Test
  public void testIsCamelCase() {
    assertFalse(StringTools.isCamelCase("abc"));
    assertFalse(StringTools.isCamelCase("ABC"));
    assertTrue(StringTools.isCamelCase("iSomething"));
    assertTrue(StringTools.isCamelCase("iSomeThing"));
    assertTrue(StringTools.isCamelCase("mRNA"));
    assertTrue(StringTools.isCamelCase("microRNA"));
    assertTrue(StringTools.isCamelCase("microSomething"));
    assertTrue(StringTools.isCamelCase("iSomeTHING"));
  }

  @Test
  public void testStringForSpeller() {
    String arabicChars = "\u064B \u064C \u064D \u064E \u064F \u0650 \u0651 \u0652 \u0670";
    assertTrue(StringTools.stringForSpeller(arabicChars).equals(arabicChars));

    String russianChars = "а б в г д е ё ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я";
    assertTrue(StringTools.stringForSpeller(russianChars).equals(russianChars));

    String emojiStr = "🧡 Prueva";
    assertTrue(StringTools.stringForSpeller(emojiStr).equals("   Prueva"));

    emojiStr = "\uD83E\uDDE1\uD83D\uDEB4\uD83C\uDFFD♂\uFE0F Prueva";
    assertTrue(StringTools.stringForSpeller(emojiStr).equals("         Prueva"));
  }

  @Test
  public void testTitlecaseGlobal() {
    assertEquals("The Lord of the Rings", StringTools.titlecaseGlobal("the lord of the rings"));
    assertEquals("Rhythm and Blues", StringTools.titlecaseGlobal("rhythm And blues"));
    assertEquals("Memória de Leitura", StringTools.titlecaseGlobal("memória de leitura"));
    assertEquals("Fond du Lac", StringTools.titlecaseGlobal("fond du lac"));
    assertEquals("El Niño de las Islas", StringTools.titlecaseGlobal("el niño de Las islas"));
  }

  @Test
  public void testAllStartWithLowercase() {
    assertTrue(StringTools.allStartWithLowercase("the lord of the rings"));
    assertFalse(StringTools.allStartWithLowercase("the Fellowship of the Ring"));
    assertTrue(StringTools.allStartWithLowercase("bilbo"));
    assertFalse(StringTools.allStartWithLowercase("Baggins"));
  }

  //--------------------------------------------SWE 261P------------------------------------

  //Test Case: readStream in StringTools.java(Line 147)
  @Test
  public void testReadStreamWithNullEncoding() throws IOException {
    InputStream mockStream = new ByteArrayInputStream("Test Stream".getBytes());
    String result = StringTools.readStream(mockStream, null);
    assertEquals("Test Stream\n", result);
    //Test with null encoding
  }

  @Test
  public void testReadEmptyStream() throws IOException {
    InputStream mockStream = new ByteArrayInputStream("".getBytes());
    String result = StringTools.readStream(mockStream, null);
    assertEquals("", result);
    //Test with empty input
  }

  @Test
  public void testReadStreamWithMultipleLines() throws IOException {
    InputStream mockStream = new ByteArrayInputStream("Line 1\nLine 2\nLine 3".getBytes());
    String result = StringTools.readStream(mockStream, "UTF-8");
    assertEquals("Line 1\nLine 2\nLine 3\n", result);
    //Test with regular input
  }

  @Test
  public void testReadStreamWithIOException() throws IOException {
    InputStream mockStream = mock(InputStream.class);
    when(mockStream.read(any(byte[].class))).thenThrow(new IOException("Mocked IOException"))
      .thenReturn(-1);
    assertThrows(IOException.class, () -> StringTools.readStream(mockStream, null));
    //Test throwing exception
  }

  //Test Case: readtoString in StringTools.java(Line 398)
  @Test
  public void testReaderToStringWithNormalInput() throws IOException {
    String inputString = "This is a test.";
    Reader reader = new StringReader(inputString);
    String result = StringTools.readerToString(reader);
    assertEquals(inputString, result);
    //Test normal input
  }

  @Test
  public void testReaderToStringWithEmptyInput() throws IOException {
    String inputString = "";
    Reader reader = new StringReader(inputString);
    String result = StringTools.readerToString(reader);
    assertEquals(inputString, result);
    //Test empyt input
  }

  @Test
  public void testReaderToStringWithSpecialCharacters() throws IOException {
    String inputString = "特殊%^73*test";
    Reader reader = new StringReader(inputString);
    String result = StringTools.readerToString(reader);
    assertEquals(inputString, result);
    //Test input with special characters
  }

  @Test
  public void testReaderToStringWithIOException() throws IOException {
    Reader mockreader = mock(Reader.class);
    when(mockreader.read(any(char[].class), anyInt(), anyInt())).thenThrow(new IOException("Mocked IOException")).thenReturn(-1);
    assertThrows(IOException.class, () -> StringTools.readerToString(mockreader));
    //Test throwing exception
  }

//Test Case: streamtoString in StringTools.java(Line 412)

  @Test
  public void testStreamToStringWithNormalInput() throws IOException {
    String inputString = "Hello, World!";
    InputStream inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
    String result = StringTools.streamToString(inputStream, StandardCharsets.UTF_8.name());
    assertEquals(inputString, result);
    //Test normal input
  }

  @Test
  public void testStreamToStringWithEmptyInput() throws IOException {
    InputStream emptyInputStream = new ByteArrayInputStream(new byte[0]);
    String result = StringTools.streamToString(emptyInputStream, StandardCharsets.UTF_8.name());
    assertEquals("", result);
    //Test empty input
  }
  @Test
  public void testStreamToStringWithIOException() throws IOException {
    InputStream mockStream = mock(InputStream.class);
    when(mockStream.read(any(byte[].class))).thenThrow(new IOException("Mocked IOException"))
      .thenReturn(-1);
    assertThrows(IOException.class, () -> StringTools.streamToString(mockStream, StandardCharsets.UTF_8.name()));
    //Test throwing exception
  }

  //Test Case: getDifference in StringTools.java(Line 759)
  @Test
  public void testGetDifferenceWithEqualStrings() {
    String s = "Hello, World!";
    List<String> result = StringTools.getDifference(s, s);
    assertEquals(Arrays.asList(s, "", "", ""), result);
    //Test equal strings
  }
  @Test
  public void testGetDifferenceCommonStringAtStart() {
    String s1 = "Hello, World!";
    String s2 = "Hello, Universe!";
    List<String> result = StringTools.getDifference(s1, s2);
    System.out.println(result.get(0));
    assertEquals("Hello, ", result.get(0));
    //Get common string at start
  }
  @Test
  public void testGetDifferenceDiffInString1() {
    String s1 = "Hello, World!";
    String s2 = "Hello, Universe!";
    List<String> result = StringTools.getDifference(s1, s2);
    System.out.println(result.get(1));
    assertEquals("World", result.get(1));
    // Get diff in string1
  }
  @Test
  public void testGetDifferenceDiffInString2() {
    String s1 = "Hello, World!";
    String s2 = "Hello, Universe!";
    List<String> result = StringTools.getDifference(s1, s2);
    System.out.println(result.get(2));
    assertEquals("Universe", result.get(2));
    // Get diff in string2
  }

  @Test
  public void testGetDifferenceCommonStringAtEnd() {
    String s1 = "Hello, World!";
    String s2 = "Hello, Universe!";
    List<String> result = StringTools.getDifference(s1, s2);
    System.out.println(result.get(3));
    assertEquals("!", result.get(3));
    //Get common string at end
  }

  //Test Case: isEmoji in StringTools.java(Line 912)
  @Test
  public void testSingleRegularCharacter() {
    assertFalse(StringTools.isEmoji("a"));
    // Test with a regular character
  }

  @Test
  public void testSingleCodeEmoji() {
    assertFalse(StringTools.isEmoji("\uD83D"));
    // Test with a single code emoji
  }

  @Test
  public void testMultiCharacterString() {
    assertFalse(StringTools.isEmoji("emoji"));
    // Test with a string
  }

  @Test
  public void testMultiCodePointEmoji() {
    assertTrue(StringTools.isEmoji("😂"));
    // Test with a multi-code point emoji
  }

  @Test
  public void testStringWithEmoji() {
    assertTrue(StringTools.isEmoji("test😂"));
    // Test with a string with an emoji
  }


  //Test Case: splitCamelCase in StringTools.java(Line 938)

  @Test
  public void testAllUppercase() {
    assertArrayEquals(new String[]{"CAMELTEST"}, StringTools.splitCamelCase("CAMELTEST"));
    // Test with a string that is entirely in uppercase
  }

  @Test
  public void testStandardCamelCase() {
    assertArrayEquals(new String[]{"Java", "Test"}, StringTools.splitCamelCase("JavaTest"));
    // Test with a standard camel case string
  }

  @Test
  public void testEmptyString() {
    assertArrayEquals(new String[]{""},StringTools.splitCamelCase(""));
    // Test with an empty string
  }

  @Test
  public void testNullInput() {
    assertNull(StringTools.splitCamelCase(null));
    // Test with a null input
  }

  @Test
  public void testNoUppercase() {
    assertArrayEquals(new String[]{"testspringtool"}, StringTools.splitCamelCase("testspringtool"));
    // Test with a string that contains no uppercase letters
  }

  // Test Case: splitDigitsAtEnd in StringTools.java(Line 962)
  @Test
  public void testStringEndInDigit() {
    assertArrayEquals(new String[]{"abc", "123"}, StringTools.splitDigitsAtEnd("abc123"));
    // Test with string end in digits.
  }
  @Test
  public void testOnlyDigit() {
    assertArrayEquals(new String[]{"12345"}, StringTools.splitDigitsAtEnd("12345"));
    // Test with string only have digits.
  }
  @Test
  public void testNoDigit() {
    assertArrayEquals(new String[]{"test"}, StringTools.splitDigitsAtEnd("test"));
    // Test with string do not have digits.
  }
  @Test
  public void testNewEmptyString() {
    assertArrayEquals(new String[]{""}, StringTools.splitDigitsAtEnd(""));
    // Test with empty string.
  }
  @Test
  public void testDigitNotOnlyAtEnd() {
    assertArrayEquals(new String[]{"1abc", "23"}, StringTools.splitDigitsAtEnd("1abc23"));
    // Test with string not only have digits at the end.
  }

  // Test Case: isAnagram in StringTools.java(Line 975)
  @Test
  public void testIsAnagram() {
    assertTrue(StringTools.isAnagram("anagram", "nagamra"));
    // Test with same string length and is anagram.
  }
  @Test
  public void testNotAnagram() {
    assertFalse(StringTools.isAnagram("rain", "snow"));
    // Test with same string length but not anagram.
  }
  @Test
  public void testCaseSensitivity() {
    assertFalse(StringTools.isAnagram("hello", "HELLO"));
    // Test with same string but is different cases.
  }
  @Test
  public void testDifferentLengths() {
    assertFalse((StringTools.isAnagram("testing", "test")));
    // Test with different string length.
  }
}



