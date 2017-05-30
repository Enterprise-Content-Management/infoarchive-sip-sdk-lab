/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.lab;

import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import com.emc.ia.sdk.sip.assembly.*;
import com.emc.ia.sdk.support.io.FileBuffer;
import com.opentext.ia.sipsdk.lab.exercise3.Exercise3;


public class Answer3 extends Exercise3 {

  @Override
  protected void performAssignment() throws Exception {
    // Build a prototype for the packaging information.
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .holding("lab")
            .application("exercise3")
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
    sipAssembler.start(new FileBuffer(new File("countries.sip")));

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
      sipAssembler.add(country);
    }
    countries.close();
    connection.close();

    // Finalize the SIP
    sipAssembler.end();
  }

}
