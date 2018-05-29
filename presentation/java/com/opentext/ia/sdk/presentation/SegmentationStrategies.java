/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.presentation;

import com.opentext.ia.sdk.sip.SipMetrics;
import com.opentext.ia.sdk.sip.SipSegmentationStrategy;
import com.opentext.ia.sdk.support.io.FileSize;


public final class SegmentationStrategies {

  private SegmentationStrategies() { }

  public static void main(String[] args) {
    int maxSize = 10;

    // tag::default[]
    SipSegmentationStrategy.byMaxAius(maxSize);
    SipSegmentationStrategy.byMaxDigitalObjects(maxSize);
    SipSegmentationStrategy.byMaxDigitalObjectsSize(maxSize); // <1>
    SipSegmentationStrategy.byMaxPdiSize(maxSize);
    SipSegmentationStrategy.byMaxSipSize(maxSize);
    // end::default[]

    // tag::combining[]
    SipSegmentationStrategy.combining(
        SipSegmentationStrategy.byMaxAius(200000),
        SipSegmentationStrategy.byMaxPdiSize(FileSize.of(2).gigaBytes()));
    // end::combining[]

    @SuppressWarnings("unused")
    // tag::custom[]
    SipSegmentationStrategy<Person> byFirstLetterOfLastName =
        new SipSegmentationStrategy<Person>() {
      private char current = ' ';

      @Override
      public boolean shouldStartNewSip(Person person, SipMetrics ignored) {
        char letter = person.getLastName().charAt(0);
        if (letter == current) {
          return false;
        }
        current = letter;
        return true;
      }
    };
    // end::custom[]
  }

}
