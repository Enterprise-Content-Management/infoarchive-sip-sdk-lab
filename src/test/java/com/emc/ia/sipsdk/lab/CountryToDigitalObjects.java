/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sipsdk.lab;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import com.emc.ia.sdk.sip.assembly.DigitalObject;
import com.emc.ia.sdk.sip.assembly.DigitalObjectsExtraction;


public class CountryToDigitalObjects implements DigitalObjectsExtraction<Country> {

  @Override
  public Iterator<? extends DigitalObject> apply(Country country) {
    File dir = new File("lab/resources/" + country.getName().toLowerCase(Locale.ENGLISH));
    return Arrays.stream(dir.listFiles())
        .sorted((a, b) -> a.getName().compareTo(b.getName()))
        .map(file -> DigitalObject.fromFile(file.getName(), file))
        .iterator();
  }

}
