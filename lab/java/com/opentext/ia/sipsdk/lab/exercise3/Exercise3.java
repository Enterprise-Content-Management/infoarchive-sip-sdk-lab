/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sipsdk.lab.exercise3;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.emc.ia.sdk.support.xml.XmlBuilder;
import com.opentext.ia.sipsdk.lab.Exercise;


public class Exercise3 extends Exercise {

  public Exercise3() {
    super(3);
  }

  @Override
  protected void performAssignment() throws Exception {
    // TODO: Your code here

    // Select all countries with InfoArchive development teams
    Connection connection = DriverManager.getConnection(
        "jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'lab/resources/ia-countries.sql'");
    Statement statement = connection.createStatement();
    ResultSet countries = statement.executeQuery("select * from Country ORDER BY Code");

    // Generate the SIP based on query results
    // TODO: Your code here

    // Close the SQL handles
    countries.close();
    connection.close();

    // TODO: Your code here
  }

  @Override
  protected void checkAssignment() throws IOException {
    assertSip(this::assertCountries);
  }

  private void assertCountries(InputStream actual) {
    assertPdi(XmlBuilder.newDocument()
        .namespace("urn:emc:ia:sipsdk:lab:country")
        .element("countries")
            .element("country")
                .element("code", "CA")
                .element("name", "Canada")
                .element("capital", "Ottawa")
            .end()
            .element("country")
                .element("code", "CN")
                .element("name", "China")
                .element("capital", "Beijing")
            .end()
            .element("country")
                .element("code", "FR")
                .element("name", "France")
                .element("capital", "Paris")
            .end()
            .element("country")
                .element("code", "IN")
                .element("name", "India")
                .element("capital", "New Delhi")
            .end()
            .element("country")
                .element("code", "NL")
                .element("name", "Netherlands")
                .element("capital", "Amsterdam")
            .end()
            .element("country")
                .element("code", "RU")
                .element("name", "Russia")
                .element("capital", "Moscow")
            .end()
            .element("country")
                .element("code", "SE")
                .element("name", "Sweden")
                .element("capital", "Stockholm")
            .end()
            .element("country")
                .element("code", "US")
                .element("name", "United States of America")
                .element("capital", "Washington, DC")
            .end()
        .build(), actual);
  }

}
