/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sipsdk.presentation;

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


public final class MyFirstSip {

  private static final URI PERSON_NAMESPACE = URI.create("urn:eas-samples:en:xsd:persons.1.0");

  private MyFirstSip() { }

  public static void main(String[] args) throws IOException {
    Person person = new Person("Donald", "Duck");

    // tag::manifest[]
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .application("myapp")
            .holding("myholding")
            .producer("myproducer")
            .entity("myentity")
            .schema("myuri")
        .end()
        .build();
    // end::manifest[]

    // tag::conversion[]
    PdiAssembler<Person> pdiAssembler = new XmlPdiAssembler<Person>(PERSON_NAMESPACE, "person") {
      @Override
      protected void doAdd(Person person, Map<String, Collection<EncodedHash>> ignored) { // <1>
        getBuilder()
            .element("firstname", person.getFirstName())
            .element("lastname", person.getLastName());
      }
    };
    // end::conversion[]

    // tag::generate[]
    SipAssembler<Person> sipAssembler = SipAssembler.forPdi(prototype, pdiAssembler);
    FileGenerator<Person> generator = new FileGenerator<>(sipAssembler, new File(".")); // <1>
    generator.generate(person);
    // end::generate[]
  }

}
