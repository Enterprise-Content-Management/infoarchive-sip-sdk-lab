/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.lab.exercise6;

import java.io.IOException;
import java.util.function.Consumer;

import org.w3c.dom.Element;

import com.opentext.ia.sdk.support.xml.XmlUtil;
import com.opentext.ia.sipsdk.lab.Exercise;


public class Exercise6 extends Exercise {

  public Exercise6() {
    super(6);
  }

  @Override
  protected void performAssignment() throws Exception {
    // TODO: Your code here
  }


  // * * * DO NOT EDIT THE FOLLOWING CODE * * *
  // This code checks whether the assignment was implemented correctly.

  @Override
  protected void checkAssignment() throws IOException {
    assertSips(4, pdiStream -> { }, new Consumer<Element>() {
      private int count = 1;

      @Override
      public void accept(Element sip) {
        assertChild(XmlUtil.getFirstChildElement(sip, "dss"), "id", "ex6dss" + count);
        assertChild(sip, "seqno", Integer.toString(1));
        assertChild(sip, "is_last", Boolean.toString(true));
        count++;
      }
    });
  }

}
