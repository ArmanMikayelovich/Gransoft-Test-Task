package com;

import com.client.gwtTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class gwtSuite extends GWTTestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for gwt");
    suite.addTestSuite(gwtTest.class);
    return suite;
  }
}
