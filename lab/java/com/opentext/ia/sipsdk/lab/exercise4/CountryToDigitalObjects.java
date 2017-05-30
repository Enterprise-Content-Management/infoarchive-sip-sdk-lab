/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.lab.exercise4;

import java.util.Collections;
import java.util.Iterator;

import com.emc.ia.sdk.sip.assembly.DigitalObject;
import com.emc.ia.sdk.sip.assembly.DigitalObjectsExtraction;
import com.opentext.ia.sipsdk.lab.exercise1.Country;


public class CountryToDigitalObjects implements DigitalObjectsExtraction<Country> {

  @Override
  public Iterator<? extends DigitalObject> apply(Country country) {
    // TODO: Your code here
    return Collections.emptyIterator();
  }

}
