/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sipsdk.lab;


public class Country {

  private final String code;
  private final String name;
  private final String capital;

  public Country(String code, String name, String capital) {
    this.code = code;
    this.name = name;
    this.capital = capital;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getCapital() {
    return capital;
  }

}
