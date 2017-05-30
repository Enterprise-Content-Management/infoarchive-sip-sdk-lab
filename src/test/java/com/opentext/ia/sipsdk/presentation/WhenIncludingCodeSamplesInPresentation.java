/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.presentation;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.emc.ia.sdk.support.io.DefaultDirectoryListener;
import com.emc.ia.sdk.support.io.Delete;
import com.emc.ia.sdk.support.io.DirectoryListener;
import com.emc.ia.sdk.support.io.RepeatableInputStream;
import com.emc.ia.sdk.support.xml.XmlBuilder;
import com.emc.ia.sdk.support.xml.XmlUtil;


public class WhenIncludingCodeSamplesInPresentation {

  private static final String NS_PERSON = "urn:eas-samples:en:xsd:persons.1.0";

  private final DirectoryListener directoryListener = new DefaultDirectoryListener(1);

  @Before
  public void init() {
    directoryListener.listenIn(new File("."));
    directoryListener.addedFiles(); // Not interested in existing files
  }

  @After
  public void done() {
    directoryListener.addedFiles().forEachRemaining(file -> Delete.file(file));
  }

  @Test
  public void shouldEnsureMyFirstSipWorkAsExpected() throws IOException {
    MyFirstSip.main(null);
    assertSip(this::assertPerson);
  }

  private void assertPerson(InputStream pdi) {
    Document expected = XmlBuilder.newDocument()
        .namespace(NS_PERSON)
        .element("persons")
            .element("person")
                .element("firstname", "Donald")
                .element("lastname", "Duck")
        .build();
    Document actual = XmlUtil.parse(pdi);

    assertEquals("PDI", XmlUtil.toString(expected.getDocumentElement()), XmlUtil.toString(actual.getDocumentElement()));
  }

  private void assertSip(Consumer<InputStream> pdiChecker, String... digitalObjectNames) throws IOException {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      // Ignore
    }
    Iterator<File> files = directoryListener.addedFiles();
    assertTrue("Missing SIP", files.hasNext());
    File sip = files.next();
    try {
      assertSip(sip, pdiChecker, digitalObjectNames);
      assertFalse("Extra files", files.hasNext());
    } finally {
      Delete.file(sip);
    }
  }

  private void assertSip(File sip, Consumer<InputStream> pdiChecker, String[] digitalObjectNames) throws IOException {
    assertTrue("SIP is not a file", sip.isFile());
    try (ZipInputStream zip = new ZipInputStream(new FileInputStream(sip))) {
      assertDigitalObjects(zip, digitalObjectNames);
      assertPdi(zip, pdiChecker);
      assertPackagingInformation(zip);
      assertNull("Extra ZIP entry", zip.getNextEntry());
    }
  }

  private void assertDigitalObjects(ZipInputStream zip, String[] digitalObjectNames) throws IOException {
    for (String digitalObjectName : digitalObjectNames) {
      ZipEntry entry = zip.getNextEntry();
      assertEquals("Digital object", digitalObjectName, entry.getName());
      zip.closeEntry();
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

  private void assertPackagingInformation(ZipInputStream zip) throws IOException {
    ZipEntry packagingInformationEntry = zip.getNextEntry();
    assertEquals("Packaging information", "eas_sip.xml", packagingInformationEntry.getName());
    Element dssElement = XmlUtil.getFirstChildElement(XmlUtil.parse(copyOf(zip)).getDocumentElement(), "dss");
    assertEquals("Application", "myapp", XmlUtil.getFirstChildElement(dssElement, "application").getTextContent());
    zip.closeEntry();
  }

  @Test
  public void shouldEnsureMyFirstSipByTemplateWorkAsExpected() throws IOException {
    MyFirstSipByTemplate.main(null);
    assertSip(this::assertPerson);
  }

  @Test
  public void shouldEnsureMyFirstSipWithContentWorkAsExpected() throws IOException {
    MyFirstSipWithContent.main(null);
    assertSip(this::assertPerson, "Donald Duck");
  }

  @Test
  public void shouldEnsureMyFirstSipWithContentHashingWorkAsExpected() throws IOException {
    MyFirstSipWithContentHashing.main(null);
    assertSip(this::assertPerson, "Donald Duck");
  }

}
