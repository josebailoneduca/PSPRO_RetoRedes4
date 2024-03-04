package http;

import java.net.URL;
import java.net.URLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * escritura en un formulario
 * 
 * visualizar la salida como html
 */
public class C_01_VisorHTTPconEnvio_V6 {

  static String urlStr = "https://www.rfc-editor.org/search/rfc_search_detail.php";

  public static void main(String[] args) {

    try {
    	URI uri = new URI(urlStr);
		URL url = uri.toURL();
      try {

        URLConnection urlConn = url.openConnection();
        urlConn.setDoOutput(true);

        PrintWriter pw = new PrintWriter(urlConn.getOutputStream());
        pw.write("rfc=793");
        pw.close();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()))) 
        {
          
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
		e.printStackTrace();
	}

  }

}
