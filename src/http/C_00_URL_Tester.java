package http;

import java.net.*;

//VER
//https://docs.oracle.com/en/java/javase/21/docs/api/java.base/
//java/net/URI.html#toURL()

public class C_00_URL_Tester {

  public static void main(String[] args) {
    
    // hypertext transfer protocol
    testProtocol("http://www.abc.es");  
    
    // secure http
    testProtocol("https://www.amazon.com/"); 
    
    // file transfer protocol
    testProtocol("ftp://languages/java/javafaq/");
  
    // Simple Mail Transfer Protocol 
    testProtocol("mailto:elhar@i.org");

    // telnet 
    testProtocol("telnet://dibner.poly.edu/");
  
    // local file access
    testProtocol("file:///etc/passwd");

  }
  
  private static void testProtocol(String cadena) {
    
	try {
		URI uri = new URI(cadena);
		URL url = uri.toURL();
		System.out.println(url.getProtocol() + " is supported");
	
    } catch (MalformedURLException ex) {
      String protocol = cadena.substring(0, cadena.indexOf(':'));
      System.out.println(protocol + " is not supported");
    } catch (URISyntaxException e) {
    	 System.out.println(" URI sintax exception");
		
	}
  }
}


