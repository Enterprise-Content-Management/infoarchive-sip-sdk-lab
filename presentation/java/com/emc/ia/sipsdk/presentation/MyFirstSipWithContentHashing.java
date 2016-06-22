/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sipsdk.presentation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import com.emc.ia.sdk.sip.assembly.DigitalObject;
import com.emc.ia.sdk.sip.assembly.DigitalObjectsExtraction;
import com.emc.ia.sdk.sip.assembly.FileGenerator;
import com.emc.ia.sdk.sip.assembly.PackagingInformation;
import com.emc.ia.sdk.sip.assembly.PdiAssembler;
import com.emc.ia.sdk.sip.assembly.SipAssembler;
import com.emc.ia.sdk.sip.assembly.XmlPdiAssembler;
import com.emc.ia.sdk.support.io.EncodedHash;
import com.emc.ia.sdk.support.io.SingleHashAssembler;


public final class MyFirstSipWithContentHashing {

  private static final URI PERSON_NAMESPACE = URI.create("urn:eas-samples:en:xsd:persons.1.0");

  private MyFirstSipWithContentHashing() { }

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
    PdiAssembler<Person> pdiAssembler = new XmlPdiAssembler<Person>(
        PERSON_NAMESPACE, "person") {
      @Override
      protected void doAdd(Person person, Map<String, Collection<EncodedHash>> ignored) {
        getBuilder()
            .element("firstname", person.getFirstName())
            .element("lastname", person.getLastName());
      }
    };
    // end::conversion[]

    // tag::extraction[]
    DigitalObjectsExtraction<Person> extraction = p -> Stream.of(
        DigitalObject.fromFile(p.getId(), getFileFor(p.getId())) // <1>
    ).iterator();
    // end::extraction[]

    // tag::hashing[]
    SipAssembler<Person> sipAssembler = SipAssembler.forPdiAndContentWithContentHashing(prototype, pdiAssembler,
        extraction, new SingleHashAssembler()); // <1>
    // end::hashing[]

    FileGenerator<Person> generator = new FileGenerator<>(sipAssembler, new File("."));

    generator.generate(person);
  }

  private static File getFileFor(String id) {
    return new File("src/docs/asciidoc/images/" + id.replace(' ', '_') + ".png");
  }

}
