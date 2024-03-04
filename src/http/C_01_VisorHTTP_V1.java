package http;
import java.io.*;
import java.net.*;

public class C_01_VisorHTTP_V1 {

  public static void main (String[] args) {

	  InputStream in=null;
      
      try {
    	
        // Open the URL for reading
    	URI uri = new URI("https://www.abc.es");
		URL url = uri.toURL();
		
		in = url.openStream();
        
        in = new BufferedInputStream(in);       
        
        Reader r = new InputStreamReader(in);
        
        int c;
        while ((c = r.read()) != -1) {
          System.out.print((char) c);
        } 
      } catch (MalformedURLException ex) {
         System.err.println(args[0] + " is not a parseable URL");
      } catch (IOException ex) {
         System.err.println(ex);
      } catch (URISyntaxException e) {
		 e.printStackTrace();
	} finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException e) {
            // ignore
          }
        }
      }
    }
  }
 