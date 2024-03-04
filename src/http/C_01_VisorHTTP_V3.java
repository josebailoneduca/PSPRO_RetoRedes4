package http;
import java.io.*;
import java.net.*;

public class C_01_VisorHTTP_V3 {

  public static void main (String[] args) {

	  try {
    	URI uri = new URI("https://www.abc.es");
		URL url = uri.toURL();  
       
		Object o = url.getContent();
        System.out.println("I got a " + o.getClass().getName());
        
        int c;
        if (o instanceof String) {
        	System.out.println(o);
        } else if (o instanceof Reader) {
        	
        	Reader r = (Reader) o;
        	while ((c = r.read()) != -1) System.out.print((char) c);
        	r.close();
        } else if (o instanceof InputStream) {
        	
        	InputStream in = (InputStream) o;
        	while ((c = in.read()) != -1) System.out.write(c);
        	in.close();
        }else {
		  System.out.println("Error: unexpected type " + o.getClass());
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