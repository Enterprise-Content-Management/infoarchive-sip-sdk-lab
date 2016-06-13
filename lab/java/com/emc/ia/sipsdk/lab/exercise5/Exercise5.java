/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sipsdk.lab.exercise5;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;

import org.w3c.dom.Element;

import com.emc.ia.sdk.support.xml.XmlUtil;
import com.emc.ia.sipsdk.lab.Exercise;


public class Exercise5 extends Exercise {

  public Exercise5() {
    super(5);
  }

  @Override
  protected void performAssignment() throws IOException, SQLException {
    // TODO: Your code here
  }


  // * * * DO NOT EDIT THE FOLLOWING CODE * * *
  // This code checks whether the assignment was implemented correctly.

  @Override
  protected void checkAssignment() throws IOException {
    assertSips(4, pdiStream -> { }, new Consumer<Element>() {
      private int count = 1;
      private String dssId;

      @Override
      public void accept(Element sip) {
        Element dss = XmlUtil.getFirstChildElement(sip, "dss");
        if (dssId == null) {
          dssId = childOf(dss, "id");
        } else {
          assertChild(dss, "id", dssId);
        }
        assertChild(sip, "seqno", Integer.toString(count));
        assertChild(sip, "is_last", Boolean.toString(count == 4));
        count++;
      }
    });
  }

}
