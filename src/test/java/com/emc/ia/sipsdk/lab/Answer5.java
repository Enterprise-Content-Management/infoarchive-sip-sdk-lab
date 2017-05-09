/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sipsdk.lab;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.*;
import java.util.Map;

import com.emc.ia.sdk.sip.assembly.*;
import com.emc.ia.sdk.support.io.FileSupplier;
import com.emc.ia.sipsdk.lab.exercise5.Exercise5;


public class Answer5 extends Exercise5 {

  @Override
  protected void performAssignment() throws IOException, SQLException {
    // Build a prototype for the packaging information.
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .holding("lab")
            .application("exercise5")
            .producer("student")
            .entity("country")
            .schema("urn:emc:ia:sipsdk:lab:country")
        .end()
        .build();

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
    SipAssembler<Country> sipAssembler = SipAssembler.forPdi(prototype, pdiAssembler);

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
