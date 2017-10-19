/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.lab.exercise2;

import java.io.IOException;
import java.io.InputStream;

import com.opentext.ia.sdk.support.xml.XmlBuilder;
import com.opentext.ia.sipsdk.lab.Exercise;


public class Exercise2 extends Exercise {

  public Exercise2() {
    super(2);
  }

  @Override
  protected void performAssignment() throws Exception {
    // Put your code here, following the guidance provided in the lab documentation
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
