/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.lab.exercise4;

import java.io.IOException;
import java.io.InputStream;

import com.emc.ia.sdk.support.xml.XmlBuilder;
import com.opentext.ia.sipsdk.lab.Exercise;


public class Exercise4 extends Exercise {

  public Exercise4() {
    super(4);
  }

  @Override
  protected void performAssignment() throws IOException {
    // TODO: Your code here
  }


  // * * * DO NOT EDIT THE FOLLOWING CODE * * *
  // This code checks whether the assignment was implemented correctly.

  @Override
  protected void checkAssignment() throws IOException {
    assertSip(this::assertSweden, "abba.png", "flag.png", "king.png", "map.png", "meatballs.png");
  }

  private void assertSweden(InputStream actual) {
    assertPdi(XmlBuilder.newDocument()
        .namespace("urn:emc:ia:sipsdk:lab:country")
        .element("countries")
            .element("country")
                .element("code", "SE")
                .element("name", "Sweden")
                .element("capital", "Stockholm")
                .element("images")
                    .element("image")
                        .attribute("id", "abba.png")
                    .end()
                    .element("image")
                        .attribute("id", "flag.png")
                    .end()
                    .element("image")
                        .attribute("id", "king.png")
                    .end()
                    .element("image")
                        .attribute("id", "map.png")
                    .end()
                    .element("image")
                        .attribute("id", "meatballs.png")
                    .end()
        .build(), actual);
  }

}
