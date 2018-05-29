/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.lab;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import com.opentext.ia.sdk.sip.BatchSipAssembler;
import com.opentext.ia.sdk.sip.ContentInfo;
import com.opentext.ia.sdk.sip.DefaultPackagingInformationFactory;
import com.opentext.ia.sdk.sip.OneSipPerDssPackagingInformationFactory;
import com.opentext.ia.sdk.sip.PackagingInformation;
import com.opentext.ia.sdk.sip.PackagingInformationFactory;
import com.opentext.ia.sdk.sip.PdiAssembler;
import com.opentext.ia.sdk.sip.SequentialDssIdSupplier;
import com.opentext.ia.sdk.sip.SipAssembler;
import com.opentext.ia.sdk.sip.SipSegmentationStrategy;
import com.opentext.ia.sdk.sip.XmlPdiAssembler;
import com.opentext.ia.sdk.support.io.FileSupplier;
import com.opentext.ia.sipsdk.lab.exercise6.Exercise6;


public class Answer6 extends Exercise6 {

  @Override
  protected void performAssignment() throws Exception {
    // Build a factory for the packaging information.
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .holding("lab")
            .application("exercise6")
            .producer("student")
            .entity("country")
            .schema("urn:emc:ia:sipsdk:lab:country")
        .end()
        .build();
    PackagingInformationFactory factory = new OneSipPerDssPackagingInformationFactory(
        new DefaultPackagingInformationFactory(prototype),
        new SequentialDssIdSupplier("ex6dss", 1));

    // Define how domain objects are serialized into the PDI.
    PdiAssembler<Country> pdiAssembler = new XmlPdiAssembler<Country>(
        URI.create("urn:emc:ia:sipsdk:lab:country"), "country") {
      @Override
      protected void doAdd(Country country, Map<String, ContentInfo> ignored) {
        getBuilder()
            .element("code", country.getCode())
            .element("name", country.getName())
            .element("capital", country.getCapital());
      }
    };

    // Create a SIP assembler for just PDI, no content.
    SipAssembler<Country> sipAssembler = SipAssembler.forPdi(factory, pdiAssembler);

    // Generate a batch of SIPs
    BatchSipAssembler<Country> batchAssembler = new BatchSipAssembler<>(sipAssembler,
        SipSegmentationStrategy.byMaxAius(2), FileSupplier.fromDirectory(getExerciseDir(), "sip-", ".zip"));

    // Select all countries with InfoArchive development teams
    Connection connection = DriverManager.getConnection(
        "jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'lab/resources/ia-countries.sql'");
    Statement statement = connection.createStatement();
    ResultSet countries = statement.executeQuery("select * from Country ORDER BY Code");

    // Generate the SIP based on query results
    while (countries.next()) {
      // Create domain object from database
      Country country = new Country(
          countries.getString("Code"),
          countries.getString("Name"),
          countries.getString("Capital"));

      // Add domain object to SIP
      batchAssembler.add(country);
    }
    countries.close();
    connection.close();

    // Finalize the assembly process
    batchAssembler.end();
  }

}
