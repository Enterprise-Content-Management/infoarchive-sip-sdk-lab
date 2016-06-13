/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sipsdk.lab;

import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

import com.emc.ia.sdk.sip.assembly.BatchSipAssembler;
import com.emc.ia.sdk.sip.assembly.DefaultPackagingInformationFactory;
import com.emc.ia.sdk.sip.assembly.OneSipPerDssPackagingInformationFactory;
import com.emc.ia.sdk.sip.assembly.PackagingInformation;
import com.emc.ia.sdk.sip.assembly.PackagingInformationFactory;
import com.emc.ia.sdk.sip.assembly.PdiAssembler;
import com.emc.ia.sdk.sip.assembly.SequentialDssIdSupplier;
import com.emc.ia.sdk.sip.assembly.SipAssembler;
import com.emc.ia.sdk.sip.assembly.SipSegmentationStrategy;
import com.emc.ia.sdk.sip.assembly.XmlPdiAssembler;
import com.emc.ia.sdk.support.io.EncodedHash;
import com.emc.ia.sdk.support.io.FileSupplier;
import com.emc.ia.sipsdk.lab.exercise6.Exercise6;


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
      protected void doAdd(Country country, Map<String, Collection<EncodedHash>> ignored) {
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
        SipSegmentationStrategy.byMaxAius(2), FileSupplier.fromDirectory(new File("."), "sip-", ".zip"));

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
