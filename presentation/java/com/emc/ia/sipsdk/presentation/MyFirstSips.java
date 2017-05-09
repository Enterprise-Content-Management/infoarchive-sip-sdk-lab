/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sipsdk.presentation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import com.emc.ia.sdk.sip.assembly.*;


public final class MyFirstSips {

  private static final URI PERSON_NAMESPACE = URI.create("urn:eas-samples:en:xsd:persons.1.0");

  private MyFirstSips() { }

  public static void main(String[] args) throws IOException {
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
      protected void doAdd(Person person, Map<String, ContentInfo> ignored) {
        getBuilder()
            .element("firstname", person.getFirstName())
            .element("lastname", person.getLastName());
      }
    };
    // end::conversion[]

    // tag::generate[]
    SipAssembler<Person> sipAssembler = SipAssembler.forPdi(prototype, pdiAssembler);
    SipSegmentationStrategy<Person> segmentationStrategy = SipSegmentationStrategy.byMaxAius(10);
    BatchSipAssembler<Person> batchAssembler = new BatchSipAssembler<>(sipAssembler,
        segmentationStrategy, new File("."));

    for (int i = 0; i < 30; ++i) {
      batchAssembler.add(newPerson(i));
    }
    batchAssembler.end();
    // end::generate[]
  }

  private static Person newPerson(int i) {
    return new Person("FirstName-" + i, "LastName-" + i);
  }

}
