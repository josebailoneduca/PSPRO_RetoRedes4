package http;

import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.InputStream;

public class C_01_VisorHTTP_V4 {

//  static String urlStr = "https://www.w3c.org";
//  static String urlStr = "https://es.wikipedia.org";
//  static String urlStr = "https://www.rae.es";  
//  static String urlStr = "https://www.google.com";
//  static String urlStr = "https://www.oracle.com";
   static String urlStr = "https://abc.es";

  public static void main(String[] args)  {

    try {
    	
    	// Open the URL for reading
    	URI uri;
		
			uri = new URI(urlStr);
			URL url = uri.toURL();
		
    
      try (InputStream is = url.openConnection().getInputStream();
              InputStreamReader isr = new InputStreamReader(is);
              BufferedReader br = new BufferedReader(isr)) 
      {
        System.out.printf("Contenidos de %s\n", urlStr);
        System.out.println("-----------------------------\n");
        String linea;
        while ((linea = br.readLine()) != null) {
          System.out.println(linea);
        }
        System.out.println("-----------------------------\n");
      } catch (IOException ex) {
        System.out.printf("Error de E/S obteniendo contenidos de URL.\n");
        ex.printStackTrace();
      }
      
      
    } catch (MalformedURLException ex) {
      System.out.printf("URL mal formada: %s.\n");
      ex.printStackTrace();
    } catch (URISyntaxException e) {
		e.printStackTrace();
	}

  }

}
