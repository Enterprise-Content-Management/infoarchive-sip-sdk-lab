/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.presentation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import com.opentext.ia.sdk.sip.ContentInfo;
import com.opentext.ia.sdk.sip.FileGenerator;
import com.opentext.ia.sdk.sip.PackagingInformation;
import com.opentext.ia.sdk.sip.PdiAssembler;
import com.opentext.ia.sdk.sip.SipAssembler;
import com.opentext.ia.sdk.sip.XmlPdiAssembler;


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
      protected void doAdd(Person person, Map<String, ContentInfo> ignored) { // <1>
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
