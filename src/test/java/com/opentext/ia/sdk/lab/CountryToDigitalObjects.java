/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.lab;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import com.opentext.ia.sdk.sip.DigitalObject;
import com.opentext.ia.sdk.sip.DigitalObjectsExtraction;


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
