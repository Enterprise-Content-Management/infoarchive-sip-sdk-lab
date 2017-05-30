/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.lab;

import java.io.File;

import com.emc.ia.sdk.sip.assembly.*;
import com.emc.ia.sip.assembly.stringtemplate.StringTemplate;
import com.opentext.ia.sipsdk.lab.exercise2.Exercise2;


public class Answer2 extends Exercise2 {

  @Override
  protected void performAssignment() throws Exception {
    // Build a prototype for the packaging information.
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .holding("lab")
            .application("exercise2")
            .producer("student")
            .entity("country")
            .schema("urn:emc:ia:sipsdk:lab:country")
        .end()
        .build();

    // Define how domain objects are serialized into the PDI.
    PdiAssembler<Country> pdiAssembler = new TemplatePdiAssembler<>(new StringTemplate<>(
        "<countries xmlns=\"urn:emc:ia:sipsdk:lab:country\">",
        "</countries>",
        "<country><code>$model.code$</code><name>$model.name$</name><capital>$model.capital$</capital></country>"));

    // Create a SIP assembler for just PDI, no content.
    SipAssembler<Country> sipAssembler = SipAssembler.forPdi(prototype, pdiAssembler);

    // Use a file generator to drive the SIP assembler and store the result in a file named 'sweden.sip'.
    FileGenerator<Country> generator = new FileGenerator<>(sipAssembler, () -> new File("sweden.sip"));

    // Create a domain object for the country of Sweden.
    Country sweden = new Country("SE", "Sweden", "Stockholm");

    // Generate the SIP based on the single domain object.
    generator.generate(sweden);
  }

}
