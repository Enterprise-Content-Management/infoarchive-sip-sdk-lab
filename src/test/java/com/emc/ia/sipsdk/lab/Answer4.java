/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sipsdk.lab;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

import com.emc.ia.sdk.sip.assembly.FileGenerator;
import com.emc.ia.sdk.sip.assembly.PackagingInformation;
import com.emc.ia.sdk.sip.assembly.PdiAssembler;
import com.emc.ia.sdk.sip.assembly.SipAssembler;
import com.emc.ia.sdk.sip.assembly.XmlPdiAssembler;
import com.emc.ia.sdk.support.io.EncodedHash;
import com.emc.ia.sipsdk.lab.exercise4.Exercise4;


public class Answer4 extends Exercise4 {

  @Override
  protected void performAssignment() throws IOException {
    // Build a prototype for the packaging information.
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .holding("lab")
            .application("exercise4")
            .producer("student")
            .entity("country")
            .schema("urn:emc:ia:sipsdk:lab:country")
        .end()
        .build();

    // Define how domain objects are serialized into the PDI.
    PdiAssembler<Country> pdiAssembler = new XmlPdiAssembler<Country>(
        URI.create("urn:emc:ia:sipsdk:lab:country"), "country") {
      @Override
      protected void doAdd(Country country, Map<String, Collection<EncodedHash>> hashes) {
        getBuilder()
            .element("code", country.getCode())
            .element("name", country.getName())
            .element("capital", country.getCapital())
            .elements("images", "image", hashes.keySet(), (id, imageBuilder) -> {
              imageBuilder.attribute("id", id);
            });
      }
    };

    // Create a SIP assembler for PDI *and* content.
    SipAssembler<Country> sipAssembler = SipAssembler.forPdiAndContent(prototype, pdiAssembler,
        new CountryToDigitalObjects());

    // Use a file generator to drive the SIP assembler and store the result in a file named 'sweden.sip'.
    FileGenerator<Country> generator = new FileGenerator<>(sipAssembler, () -> new File("sweden.sip"));

    // Create a domain object for the country of Sweden.
    Country sweden = new Country("SE", "Sweden", "Stockholm");

    // Generate the SIP based on the single domain object.
    generator.generate(sweden);
  }

}
