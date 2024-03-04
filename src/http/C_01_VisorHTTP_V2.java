package http;

import java.io.*;
import java.net.*;

public class C_01_VisorHTTP_V2 {
	
	

  public static void main (String[] args) {

    
	  try {
	       
		    URI uri = new URI("https://www.abc.es");
			URL url = uri.toURL();  
	        URLConnection uc = url.openConnection();
	       
	        
	        try (InputStream is = uc.getInputStream()) { // autoclose
	        	
	          InputStream buffer = new BufferedInputStream(is);       
	         
	          Reader reader = new InputStreamReader(buffer);
	          
	          int c;
	          while ((c = reader.read()) != -1) {
	            System.out.print((char) c);
	          }
	        }
	        
	      } catch (MalformedURLException ex) {
	        System.err.println(args[0] + " is not a parseable URL");
	      } catch (IOException ex) {
	        System.err.println(ex);
	      } catch (URISyntaxException e) {
			e.printStackTrace();
		}
	    }
    }
  
