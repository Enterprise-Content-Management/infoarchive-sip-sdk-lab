/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.lab.exercise1;

import java.io.IOException;
import java.io.InputStream;

import com.opentext.ia.sdk.support.xml.XmlBuilder;
import com.opentext.ia.sipsdk.lab.Exercise;


public class Exercise1 extends Exercise {

  public Exercise1() {
    super(1);
  }

  @Override
  protected void performAssignment() throws IOException {
    /* Remove this comment marker
    // Build a prototype for the packaging information.
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .holding("lab")
            .application("exercise1")
            .producer("student")
            .entity("country")
            .schema("urn:emc:ia:sipsdk:lab:country")
        .end()
        .build();

    // Define how domain objects are serialized into the PDI.
    PdiAssembler<Country> pdiAssembler = new XmlPdiAssembler<Country>(
        URI.create("urn:emc:ia:sipsdk:lab:country"), "country") {
      @Override
      protected void doAdd(Country country, Map<String, Collection<EncodedHash>> ignored) {
        getBuilder()
            .element("code", country.getCode())
            .element("name", country.getName())
            .element("capital", country.getCapital());
      }
    };

    // Create a SIP assembler for just PDI, no content.
    SipAssembler<Country> sipAssembler = SipAssembler.forPdi(prototype, pdiAssembler);

    // Use a file generator to drive the SIP assembler and store the result in a file named 'sweden.sip'.
    FileGenerator<Country> generator = new FileGenerator<>(sipAssembler, () -> new File("sweden.sip"));

    // Create a domain object for the country of Sweden.
    Country sweden = new Country("SE", "Sweden", "Stockholm");

    // Generate the SIP based on the single domain object.
    generator.generate(sweden);
    */
  }


  // * * * DO NOT EDIT THE FOLLOWING CODE * * *
  // This code checks whether the assignment was implemented correctly.

  @Override
  protected void checkAssignment() throws IOException {
    assertSip(this::assertSweden);
  }

  private void assertSweden(InputStream actual) {
    assertPdi(XmlBuilder.newDocument()
        .namespace("urn:emc:ia:sipsdk:lab:country")
        .element("countries")
            .element("country")
                .element("code", "SE")
                .element("name", "Sweden")
                .element("capital", "Stockholm")
        .build(), actual);
  }

}
