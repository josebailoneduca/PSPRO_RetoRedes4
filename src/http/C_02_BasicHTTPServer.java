package http;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Servidor HTTP que sirve la misma respuesta siempre.
 * En su método main está configurado para escuchar al puerto 80
 * y servir un html hubicado en recursos/Index.html
 * Sin embargo no solo puede servir HTML puede servir cualquier archivo
 * siempre que en su main se configuren las rutas donde se inicializan
 * path y contentType.
 */

public class C_02_BasicHTTPServer {

	// logger
	private static final Logger logger = Logger.getLogger("C_02_BasicHTTPServer");

	// contenido a servir
	private final byte[] content;

	// cabecera con la que responder
	private final byte[] header;

	// puerto al que escuchar
	private final int port;

	// coficiacion
	private final String encoding;

	/**
	 * Constructor a partir de String
	 * 
	 * @param data     Datos a servir como String
	 * @param encoding Codificacion
	 * @param mimeType MIME type del contenido a servir
	 * @param port     Puerto a escuchar
	 * @throws UnsupportedEncodingException
	 */
	public C_02_BasicHTTPServer(String data, String encoding, String mimeType, int port)
			throws UnsupportedEncodingException {
		this(data.getBytes(encoding), encoding, mimeType, port);
	}

	/**
	 * Constructor a partir de array de bytes
	 * 
	 * @param data     Datos a servidr como array de bytes
	 * @param encoding Codificiacion
	 * @param mimeType MIME type del contenido a servidr
	 * @param port     puerto a escuchar
	 */
	public C_02_BasicHTTPServer(byte[] data, String encoding, String mimeType, int port) {
		this.content = data;
		this.port = port;
		this.encoding = encoding;
		
		//construir un header de respuesta OK con la longitud de datos del archivo a servir, su MIME type y el codificado 
		String header = "HTTP/1.0 200 OK\r\n" + "Server: OneFile 2.0\r\n" + "Content-length: " + this.content.length
				+ "\r\n" + "Content-type: " + mimeType + "; charset=" + encoding + "\r\n\r\n";
		//guardar la cabecera codificada con "US-ASCII" segun el estandar HTML
		this.header = header.getBytes(Charset.forName("US-ASCII"));
	}

	/**
	 * Inicio del servidor
	 */
	public void start() {
		// pool de hebras que se encarga de procesar peticiones
		ExecutorService pool = Executors.newFixedThreadPool(100);

		// Seversocket que escucha en el puerto definido
		try (ServerSocket server = new ServerSocket(this.port)) {
			logger.info("Accepting connections on port " + server.getLocalPort());
			logger.info("Data to be sent:");
			logger.info(new String(this.content, encoding));

			// bucle infinito para ir aceptando conexiones
			while (true) {
				try {
					//aceptar una conexión
					Socket connection = server.accept();
					//crea un nuevo callable HTTPHandler con la conexión
					//y lo envía al pool de hebras para que lo procese
					pool.submit(new HTTPHandler(connection));
				} catch (IOException ex) {
					logger.log(Level.WARNING, "Exception accepting connection", ex);
				} catch (RuntimeException ex) {
					logger.log(Level.SEVERE, "Unexpected error", ex);
				}
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Could not start server", ex);
		}
	}

	/**
	 * Clase anidada que se encarga de procesar una peticion
	 * 
	 */
	private class HTTPHandler implements Callable<Void> {
		
		//Socket de la conexion
		private final Socket connection;

		/**
		 * Constructor
		 * @param connection Conexion de la que procesar la peticion
		 */
		HTTPHandler(Socket connection) {
			this.connection = connection;
		}

		@Override
		public Void call() throws IOException {
			try {
				//recoger output e input stream de la conexion, ambos envueltos en un Buffered stream
				OutputStream out = new BufferedOutputStream(connection.getOutputStream());
				InputStream in = new BufferedInputStream(connection.getInputStream());

				//recoger la primera línea de la petición
				StringBuilder request = new StringBuilder(80);
				while (true) {
					int c = in.read();
					if (c == '\r' || c == '\n' || c == -1)
						break;
					request.append((char) c);
				}

				//si la petición tiene HTTP/ entonces
				//escribir la cabecera de respuesta
				if (request.toString().indexOf("HTTP/") != -1) {
					out.write(header);
				}
				//escribir el contenido
				out.write(content);
				//forzar al bufferedwriter que envie los datos
				out.flush();
			} catch (IOException ex) {
				logger.log(Level.WARNING, "Error writing to client", ex);
			} finally {
				connection.close();
			}
			return null;
		}
	}

	/**
	 * Punto de entrada del programa BasicHTTPServer
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// puerto a usar
		int port = 80;

		// codificacion a usar
		String encoding = "UTF-8";

		try {
			// leer contenido del archivo recursos/Index.html
			Path path = Paths.get("recursos/Index.html");
			byte[] data = Files.readAllBytes(path);

			// detectar MIME type del archivo
			String contentType = URLConnection.getFileNameMap().getContentTypeFor("recursos/Index.html");

			// iniciar el servidor pasando el contenido de recursos/Index.html, la
			// codificación UTF8, el MIME type del archivo y el puerto e esuchar
			C_02_BasicHTTPServer server = new C_02_BasicHTTPServer(data, encoding, contentType, port);
			server.start();

		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Usage: java C_02_BasicHTTPServer filename port encoding");
		} catch (IOException ex) {
			logger.severe(ex.getMessage());
		}
	}
}