package http;

import java.net.URL;
import java.net.URLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class C_01_VisorHTTP_V5 {

//  static String urlStr = "https://www.w3c.org";
//  static String urlStr = "https://es.wikipedia.org";

//  static String urlStr = "https://www.rae.es";  
  // static String urlStr = "https://www.google.com";
  static String urlStr = "https://www.wikipedia.org";
//  static String urlStr = "https://www.oracle.com";

  public static void main(String[] args) {

    try {
    	URI uri;
		
		uri = new URI(urlStr);
		URL url = uri.toURL();
      try {
        
        URLConnection urlConn = url.openConnection();
        
        System.out.printf("Información de %s\n", urlStr);
        System.out.printf("-----------------------------\n");
        System.out.printf("Codificación: %s\n", urlConn.getContentEncoding());
        System.out.printf("Longitud (int): %d\n", urlConn.getContentLength());
        System.out.printf("Longitud (long): %d\n", urlConn.getContentLengthLong());
        System.out.printf("Tipo (long): %s\n", urlConn.getContentType());
        System.out.printf("Fecha (EPOCH): %d\n", urlConn.getDate());
        System.out.printf("Expira (EPOCH): %d\n", urlConn.getExpiration());

        System.out.printf("Todas las cabeceras para %s\n", urlStr);
        System.out.printf("-----------------------------\n");        
        
        Map<String, List<String>> cabeceras = urlConn.getHeaderFields();
        
        Iterator<Map.Entry<String,List<String>>> itCab = cabeceras.entrySet().iterator();
        
        while(itCab.hasNext()) {
         
        	Map.Entry<String,List<String>> unaCab = itCab.next();
            System.out.printf("%s: [", unaCab.getKey());
            List<String> valCab = unaCab.getValue();
            boolean primerVal = true;
            for(String unValor: valCab) {
             if(!primerVal) {
               System.out.print("\n| ");
             }
             System.out.print(unValor);
             primerVal = false;
          }
          System.out.println("]");
        }

      try (InputStream is = urlConn.getInputStream();
              InputStreamReader isr = new InputStreamReader(is);
              BufferedReader br = new BufferedReader(isr)) {
          System.out.printf("Contenidos de %s\n", urlStr);
          System.out.printf("-----------------------------\n");
          String linea;
          while ((linea = br.readLine()) != null) {
            System.out.println(linea);
          }
          System.out.println("-----------------------------\n");
        }
      } catch (IOException ex) {
        System.out.printf("Error de E/S obteniendo contenidos de URL.\n");
        ex.printStackTrace();
      }
    } catch (MalformedURLException ex) {
      System.out.printf("URL mal formada: %s.\n");
      ex.printStackTrace();
    } catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

  }

}
