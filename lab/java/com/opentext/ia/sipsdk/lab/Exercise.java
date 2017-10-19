/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.lab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.opentext.ia.sdk.support.io.DefaultDirectoryListener;
import com.opentext.ia.sdk.support.io.DirectoryListener;
import com.opentext.ia.sdk.support.io.RepeatableInputStream;
import com.opentext.ia.sdk.support.xml.XmlUtil;


/**
 * Base class for lab exercises.
 */
public abstract class Exercise {

  /**
   * Do the work required to pass the exercise.
   * @throws Exception When an error occurs
   */
  protected abstract void performAssignment() throws Exception;

  /**
   * Check whether the work performed meets the assignment.
   * @throws IOException When an I/O error occurs
   */
  protected abstract void checkAssignment() throws IOException;


  private final DirectoryListener directoryListener = new DefaultDirectoryListener();
  private final int exerciseNumber;
  private File buildDir;

  public Exercise(int exerciseNumber) {
    this.exerciseNumber = exerciseNumber;
  }

  @Before
  public void init() {
    buildDir = new File("build/exercises/" + getExerciseNumber());
    buildDir.mkdirs();
    directoryListener.listenIn(buildDir);
    directoryListener.addedFiles(); // Not interested in existing files
  }

  private String getExerciseNumber() {
    String result = getClass().getName();
    return result.substring(result.length() - 1);
  }

  protected File getExerciseDir() {
    return buildDir;
  }

  @Test
  public void shouldCompleteAssignment() throws Exception {
    performAssignment();
    checkAssignment();
  }

  protected void assertSip(Consumer<InputStream> pdiChecker, String... digitalObjectNames) throws IOException {
    assertSips(1, pdiChecker, sip -> {
      assertChild(sip, "seqno", Integer.toString(1));
      assertChild(sip, "is_last", Boolean.toString(true));
    }, digitalObjectNames);
  }

  protected void assertSips(int numSips, Consumer<InputStream> pdiChecker, Consumer<Element> sipChecker,
      String... digitalObjectNames) throws IOException {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      // Ignore
    }
    Iterator<File> files = directoryListener.addedFiles();
    List<String> names = new ArrayList<>(Arrays.asList(digitalObjectNames));
    for (int n = 1; n <= numSips; n++) {
      assertTrue("Missing SIP #" + n, files.hasNext());
      File sip = files.next();
      assertSip(sip, pdiChecker, sipChecker, names);
    }
    if (files.hasNext()) {
      fail("Extra file: " + files.next());
    }
  }

  private void assertSip(File sip, Consumer<InputStream> pdiChecker, Consumer<Element> sipChecker,
      List<String> digitalObjectNames) throws IOException {
    assertTrue("SIP is not a file", sip.isFile());
    try (ZipInputStream zip = new ZipInputStream(new FileInputStream(sip))) {
      assertDigitalObjects(zip, digitalObjectNames);
      assertPdi(zip, pdiChecker);
      assertPackagingInformation(zip, sipChecker);
      assertNull("Extra ZIP entry", zip.getNextEntry());
    }
  }

  private void assertDigitalObjects(ZipInputStream zip, List<String> digitalObjectNames) throws IOException {
    ListIterator<String> names = digitalObjectNames.listIterator();
    while (names.hasNext()) {
      String digitalObjectName = names.next();
      ZipEntry entry = zip.getNextEntry();
      if (entry == null) {
        break;
      }
      assertEquals("Digital object", digitalObjectName, entry.getName());
      zip.closeEntry();
      names.remove();
    }
  }

  private void assertPdi(ZipInputStream zip, Consumer<InputStream> pdiChecker) throws IOException {
    ZipEntry pdiEntry = zip.getNextEntry();
    assertEquals("PDI", "eas_pdi.xml", pdiEntry.getName());
    pdiChecker.accept(copyOf(zip));
    zip.closeEntry();
  }

  private InputStream copyOf(InputStream source) throws IOException {
    return new RepeatableInputStream(source).get();
  }

  protected void assertPdi(Document expected, InputStream actual) {
    assertEquals("PDI", toString(expected), toString(XmlUtil.parse(actual)));
  }

  private String toString(Document document) {
    return XmlUtil.toString(document.getDocumentElement());
  }

  private void assertPackagingInformation(ZipInputStream zip, Consumer<Element> sipChecker) throws IOException {
    ZipEntry packagingInformationEntry = zip.getNextEntry();
    assertEquals("Packaging information", "eas_sip.xml", packagingInformationEntry.getName());

    Element sipElement = XmlUtil.parse(copyOf(zip)).getDocumentElement();
    sipChecker.accept(sipElement);

    Element dssElement = XmlUtil.getFirstChildElement(sipElement, "dss");
    assertChild(dssElement, "holding", "lab");
    assertChild(dssElement, "application", "exercise" + exerciseNumber);
    assertChild(dssElement, "producer", "student");
    assertChild(dssElement, "entity", "country");
    assertChild(dssElement, "pdi_schema", "urn:emc:ia:sipsdk:lab:country");

    zip.closeEntry();
  }

  protected void assertChild(Element parent, String childName, String expectedValue) {
    assertEquals(childName, expectedValue, childOf(parent, childName));
  }

  protected String childOf(Element parent, String childName) {
    return XmlUtil.getFirstChildElement(parent, childName).getTextContent();
  }

}
