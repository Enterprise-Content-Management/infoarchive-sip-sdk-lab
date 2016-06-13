/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sipsdk.lab.exercise4;

import java.util.Collections;
import java.util.Iterator;

import com.emc.ia.sdk.sip.assembly.DigitalObject;
import com.emc.ia.sdk.sip.assembly.DigitalObjectsExtraction;
import com.emc.ia.sipsdk.lab.exercise1.Country;


public class CountryToDigitalObjects implements DigitalObjectsExtraction<Country> {

  @Override
  public Iterator<? extends DigitalObject> apply(Country country) {
    // TODO: Your code here
    return Collections.emptyIterator();
  }

}
