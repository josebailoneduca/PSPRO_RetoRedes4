package http;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

/*
 * 
 * sirve paginas web completas
 * necesita RequestProcessor
 * 
 */
public class C_02_JHTTP {
	
	//logger
	private static final Logger logger = Logger.getLogger(C_02_JHTTP.class.getCanonicalName());
	
	//numero de hebras del exeutor
	private static final int NUM_THREADS = 50;
	
	//Archivo indice
	private static final String INDEX_FILE = "index.html";

	//Raiz de recursos a servir (equivalente a htdocs de apache)
	private final File rootDirectory;
	
	//Puerto en el que escuchar peticiones
	private final int port;

	
	/**
	 * Constructor
	 * 
	 * @param rootDirectory Raiz del servidor
	 * @param port Puerto de escucha
	 * @throws IOException Si la ruta del directorio raiz no existe
	 */
	public C_02_JHTTP(File rootDirectory, int port) throws IOException {

		if (!rootDirectory.isDirectory()) {
			throw new IOException(rootDirectory + " does not exist as a directory");
		}
		this.rootDirectory = rootDirectory;
		this.port = port;
	}

	
	/**
	 * Inicia el servidor
	 * Crea un threadpool que se encargará de procesar las peticiones y un serversocket escuchando en el puerto del directorio.
	 * Luego en un bucle infinito va aceptando conexiones y enviando al pool de hebras una 
	 * instancia de C_02_RequestProcessor que se encarga de procesar la conexión creada
	 * 
	 * @throws IOException Si no se puede iniciar el serverSocket
	 */
	public void start() throws IOException {
		
		//crear pool de hebras
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		
		//crear serversocket
		try (ServerSocket server = new ServerSocket(port)) {

			logger.info("Accepting connections on port " + server.getLocalPort());
			logger.info("Document Root: " + rootDirectory);

			//bucle infinito aceptando conexiones y creando un RequestProcessor 
			//para procesar cada peticion
			while (true) {
				try {
					//aceptar conexion
					Socket request = server.accept();
					
					//crear requestProcessor y enviarlo al pool de hebras
					Runnable r = new C_02_RequestProcessor(rootDirectory, INDEX_FILE, request);
					pool.submit(r);
				} catch (IOException ex) {
					logger.log(Level.WARNING, "Error accepting connection", ex);
				}
			}
		}
	}

	
	/**
	 * Punto de entrada al programa
	 * @param args
	 */
	public static void main(String[] args) {

		
		//establecer raiz de documentos a servir
		// document root
		File docroot;
		try {
			docroot = new File("recursos/");
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Usage: java C_02_JHTTP docroot port");
			return;
		}

		//establecer puerto
		int port = 80;
		try {
			if (port < 0 || port > 65535)
				port = 80;
		} catch (RuntimeException ex) {
			port = 80;
		}
		
		//instanciar e iniciar el servidor
		try {
			C_02_JHTTP webserver = new C_02_JHTTP(docroot, port);
			webserver.start();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Server could not start", ex);
		}
	}
}