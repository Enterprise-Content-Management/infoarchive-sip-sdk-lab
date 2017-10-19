/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.presentation;

import java.io.File;
import java.io.IOException;

import com.opentext.ia.sdk.sip.FileGenerator;
import com.opentext.ia.sdk.sip.PackagingInformation;
import com.opentext.ia.sdk.sip.PdiAssembler;
import com.opentext.ia.sdk.sip.SipAssembler;
import com.opentext.ia.sdk.sip.TemplatePdiAssembler;
import com.opentext.ia.sip.assembly.stringtemplate.StringTemplate;


public final class MyFirstSipByTemplate {

  private MyFirstSipByTemplate() { }

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
    PdiAssembler<Person> pdiAssembler = new TemplatePdiAssembler<>(new StringTemplate<>(
        "<persons xmlns='urn:eas-samples:en:xsd:persons.1.0'>\n",           // <1>
        "</persons>\n",                                                     // <2>
        "  <person>\n    <firstname>$model.firstName$</firstname>\n"
            + "    <lastname>$model.lastName$</lastname>\n  </person>\n")); // <3>
    // end::conversion[]

    // tag::generate[]
    SipAssembler<Person> sipAssembler = SipAssembler.forPdi(prototype, pdiAssembler);
    FileGenerator<Person> generator = new FileGenerator<>(sipAssembler, new File(".")); // <1>
    generator.generate(person);
    // end::generate[]
  }

}
