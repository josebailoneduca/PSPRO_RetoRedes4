package http;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.*;

/**
 * Clase para procesar una peticion
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class C_02_RequestProcessor implements Runnable {

	//logger de la clase
	private final static Logger logger = Logger.getLogger(C_02_RequestProcessor.class.getCanonicalName());

	//directorio raiz
	private File rootDirectory;
	
	//documento por defecto
	private String indexFileName = "index.html";
	
	//conexion para la peticion
	private Socket connection;

	
	/**
	 * Constructor. Almacena los atributos
	 * 
	 * @param rootDirectory Directorio raiz
	 * @param indexFileName Documento por defecto
	 * @param connection Conexion de la petición
	 */
	public C_02_RequestProcessor(File rootDirectory, String indexFileName, Socket connection) {

		if (rootDirectory.isFile()) {
			throw new IllegalArgumentException("rootDirectory must be a directory, not a file");
		}
		try {
			rootDirectory = rootDirectory.getCanonicalFile();
		} catch (IOException ex) {
		}
		this.rootDirectory = rootDirectory;

		if (indexFileName != null)
			this.indexFileName = indexFileName;
		this.connection = connection;
	}

	
	/**
	 * Carrera del runnable
	 */
	@Override
	public void run() {

		//raiz de los documentos
		String root = rootDirectory.getPath();

		try {
			//recoger output e input streams de la conexion
			OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
			Writer out = new OutputStreamWriter(raw);
			Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()), "US-ASCII");
			
			//leer primera linea e la peticion
			StringBuilder requestLine = new StringBuilder();
			while (true) {
				int c = in.read();
				if (c == '\r' || c == '\n')
					break;
				requestLine.append((char) c);
			}

			
			String get = requestLine.toString();

			logger.info(connection.getRemoteSocketAddress() + " " + get);

			//extraer partes de la peticion
			String[] tokens = get.split("\\s+");
			
			//Regoger metodo (GET-PUT-POST...)
			String method = tokens[0];
			String version = "";
			
			//si el metodo de la peticion es GET se procesa la peticion
			if (method.equals("GET")) {
				
				//recoger la ruta de la peticion
				String fileName = tokens[1];
				
				//si la ruta termina en /, agregar el archivo por defecto
				if (fileName.endsWith("/"))
					fileName += indexFileName;
				
				//detectar el MIME type del archivo
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
				
				//recoger la version HTTP
				if (tokens.length > 2) {
					version = tokens[2];
				}
				
				//recoger el archivo
				File theFile = new File(rootDirectory, fileName.substring(1, fileName.length()));
				
				//if else comprobando qeu la ruta del archivo está dentro del directorio raiz
				
				//si se puede leer y esta dentro de la ruta raiz lee el archivo
				//y envia una respuesta 200 OK y el contenido del archivo
				if (theFile.canRead() && theFile.getCanonicalPath().startsWith(root)) {
					
					byte[] theData = Files.readAllBytes(theFile.toPath());

					if (version.startsWith("HTTP/")) { // send a MIME header
						sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
					}

					raw.write(theData);
					raw.flush();
					
				//si no esta dentro del directorio raiz responder co un 404 file not found 
				} else {
					String body = new StringBuilder("<HTML>\r\n").append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
							.append("</HEAD>\r\n").append("<BODY>")
							.append("<H1>HTTP Error 404: File Not Found</H1>\r\n").append("</BODY></HTML>\r\n")
							.toString();
					if (version.startsWith("HTTP/")) { // send a MIME header
						sendHeader(out, "HTTP/1.0 404 File Not Found", "text/html; charset=utf-8", body.length());
					}
					out.write(body);
					out.flush();
				}
				
			//si la peticion no es GET responder con un error 501 not implemented	
			} else {
				String body = new StringBuilder("<HTML>\r\n").append("<HEAD><TITLE>Not Implemented</TITLE>\r\n")
						.append("</HEAD>\r\n").append("<BODY>").append("<H1>HTTP Error 501: Not Implemented</H1>\r\n")
						.append("</BODY></HTML>\r\n").toString();
				if (version.startsWith("HTTP/")) { // send a MIME header
					sendHeader(out, "HTTP/1.0 501 Not Implemented", "text/html; charset=utf-8", body.length());
				}
				out.write(body);
				out.flush();
			}
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Error talking to " + connection.getRemoteSocketAddress(), ex);
		} finally {
			try {
				connection.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Escribe la cabecera de respuesta al out, con el codigo de respueta, el content type y el tamaño
	 * Agrega tambien la fecha,hora actual como Date de la cabecera
	 *  
	 * @param out writer en el que escribir la respuesta
	 * @param responseCode Codigo de respuesta
	 * @param contentType contentType
	 * @param length Tamaño
	 * @throws IOException si hay ido algo mal en la escritura
	 */
	private void sendHeader(Writer out, String responseCode, String contentType, int length) throws IOException {
		out.write(responseCode + "\r\n");
		Date now = new Date();
		out.write("Date: " + now + "\r\n");
		out.write("Server: JHTTP 2.0\r\n");
		out.write("Content-length: " + length + "\r\n");
		out.write("Content-type: " + contentType + "\r\n\r\n");
		out.flush();
	}
}