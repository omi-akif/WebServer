/**
 * The URL to trigger is: http://localhost:50505/RandomFolder/words.txt
 */


import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.lang.Exception;
import java.io.*;

/**
 * The main() program in this class is designed to read requests from
 * a Web browser and display the requests on standard output.  The
 * program sets up a listener on port 50505.  It can be contacted
 * by a Web browser running on the same machine using a URL of the
 * form  http://localhost:505050/path/to/resource.html  This method
 * does not return any data to the web browser.  It simply reads the
 * request, writes it to standard output, and then closes the connection.
 * The program continues to run, and the server continues to listen
 * for new connections, until the program is terminated (by clicking the
 * red "stop" square in Eclipse or by Control-C on the command line).
 */

public class ReadRequest {
	
	/**
	 * The server listens on this port.  Note that the port number must
	 * be greater than 1024 and lest than 65535.
	 */
	private final static int LISTENING_PORT = 50505;
	
	private final static String rootDirectory = "/mnt/test"; //This will vary from one server to another
	
	private static String pathFile = null;//This does not need to match for handshake

	private static File file;
	
	
	/**
	 * Main program opens a server socket and listens for connection
	 * requests.  It calls the handleConnection() method to respond
	 * to connection requests.  The program runs in an infinite loop,
	 * unless an error occurs.
	 * @param args ignored
	 */
	@SuppressWarnings("resource")
	
	public static void main(String[] args) {
		ServerSocket serverSocket;
		
		
		try {
			
			serverSocket = new ServerSocket(LISTENING_PORT);
			
		}
		catch (Exception e) {
			
			System.out.println("Failed to create listening socket.");
			
			return;
		}
		
		
		System.out.println("Listening on port " + LISTENING_PORT);
		
		
		try {
			
			while (true) {
				
				
				Socket connection = serverSocket.accept();
				System.out.println("\nConnection from "  + connection.getRemoteSocketAddress());
				handleConnection(connection);
				
				
			}
			
			/**
			 * This is beyond scope as the while loop will
			 * keep on running infinitely without bound.
			 * 
			 * Nothing we write in this space will run
			 */
			
		}
		catch (Exception e) {
			System.out.println("Server socket shut down unexpectedly!");
			System.out.println("Error: " + e);
			System.out.println("Exiting.");
		}
		
		
	}

	/**
	 * Handle commuincation with one client connection.  This method reads
	 * lines of text from the client and prints them to standard output.
	 * It continues to read until the client closes the connection or
	 * until an error occurs or until a blank line is read.  In a connection
	 * from a Web browser, the first blank line marks the end of the request.
	 * This method can run indefinitely,  waiting for the client to send a
	 * blank line.
	 * NOTE:  This method does not throw any exceptions.  Exceptions are
	 * caught and handled in the method, so that they will not shut down
	 * the server.
	 * @param connection the connected socket that will be used to
	 *    communicate with the client.
	 */
	private static void handleConnection(Socket connection) throws SocketException{
		
		
		String[] tokens = null;
		
		try {
			
			Scanner in = new Scanner(connection.getInputStream());
			
			
			while (true) {
				
				if ( ! in.hasNextLine() )
					break;
				
				String line = in.nextLine();
				
				if (line.trim().length() == 0)
					break;
				
				System.out.println("  " + line);
				
				
				tokens = line.split(" ");
				
				pathFile = tokens[1];
				
				file = new File(rootDirectory + pathFile); //Read the file
				
				
				if(tokens[0].contains("GET") && (tokens[2].contains("HTTP/1.1") || tokens[2].contains("HTTP/1.1")) && file.canRead() && file.exists()) {
					
					
					PrintWriter outgoing = new PrintWriter(connection.getOutputStream()); //This can cause malicious way of intruding
					
					
					outgoing.print("HTTP/1.1 200 OK \r\n");
					
					outgoing.print("Connection: close \r\n");
					
					outgoing.print("Content-Type: " + getMimeType(file.getName()) + " \r\n");
					
					outgoing.print("Content-Type: " + file.length() + " \r\n");
					
					outgoing.print("\r\n");
					
					outgoing.flush();
					
					
					
					sendFile(file, connection.getOutputStream());
					
					
					
					connection.close(); //Closing all connections
					
//					break;
						
					}else if(!file.exists() || !file.isDirectory()) {
						
						sendErrorResponse(404, connection.getOutputStream());
						
						connection.close();
						
//						break;
						
					}else if(!tokens[0].contains("GET")) {
						
						sendErrorResponse(501, connection.getOutputStream());
						
						connection.close();
						
//						break;
						
					}
				
				
				
									
			} //End of while loop
			
			

			
		} //End of try statement 
		
		catch (Exception e) {
			
			System.out.println("Access of file contents or Attempt of access");
			System.out.println("New connection will open after socket close");
			
		}
		
		finally {  // make SURE connection is closed before returning!
			try {
				connection.close();
			}
			catch (Exception e) {
			}
			System.out.println();
			System.out.println("-------------------------");
			System.out.println("Connection closed. Starting new connection.");
		}
	}
	
	
	/**
	 * This is a method that will return the type of file as a String
	 * @param fileName
	 * @return returns the file type as String
	 */
	
	
	private static String getMimeType(String fileName) {
        int pos = fileName.lastIndexOf('.');
        if (pos < 0)  // no file extension in name
            return "x-application/x-unknown";
        String ext = fileName.substring(pos+1).toLowerCase();
        if (ext.equals("txt")) return "text/plain";
        else if (ext.equals("html")) return "text/html";
        else if (ext.equals("htm")) return "text/html";
        else if (ext.equals("css")) return "text/css";
        else if (ext.equals("js")) return "text/javascript";
        else if (ext.equals("java")) return "text/x-java";
        else if (ext.equals("jpeg")) return "image/jpeg";
        else if (ext.equals("jpg")) return "image/jpeg";
        else if (ext.equals("png")) return "image/png";
        else if (ext.equals("gif")) return "image/gif";
        else if (ext.equals("ico")) return "image/x-icon";
        else if (ext.equals("class")) return "application/java-vm";
        else if (ext.equals("jar")) return "application/java-archive";
        else if (ext.equals("zip")) return "application/zip";
        else if (ext.equals("xml")) return "application/xml";
        else if (ext.equals("xhtml")) return"application/xhtml+xml";
        else return "x-application/x-unknown";
           // Note:  x-application/x-unknown  is something made up;
           // it will probably make the browser offer to save the file.
     }
	
	
	
	/*
	 * This is a method that will print the contents of the file
	 * @param file 
	 * @param OutputStream object
	 * 
	 */
	
	private static void sendFile(File file, OutputStream socketOut) throws IOException {
	    InputStream in = new BufferedInputStream(new FileInputStream(file));
	    OutputStream out = new BufferedOutputStream(socketOut);
	    while (true) {
	      int x = in.read(); // read one byte from file
	      if (x < 0)
	         break; // end of file reached
	      out.write(x);  // write the byte to the socket
	   }
	   out.flush();
	}
	
	
	
	
	static void sendErrorResponse(int errorCode,  OutputStream socketOut) throws Exception{

		
		PrintWriter output = new PrintWriter(socketOut);
		
		output.print("\r\n");
		
		output.print("HTTP/1.1 " + errorCode +  " \r\n");
		
		output.print("Connection: close" + " \r\n");
		
		output.print("Content-Type:  + "  + "text/html" + "\r\n");
		
		output.print("\r\n");
		
		if(errorCode == 404) {
			
			output.print("<html><head><title>Error</title></head><body>\n"
					+ "<h2>Error:" + errorCode + " Not Found</h2>\n"
					+ "<p>The resource that you requested does not exist on this server.</p>\n"
					+ "</body></html>" + "\r\n");
			
		}else if(errorCode == 501) {
			
			output.print("<html><head><title>Error</title></head><body>\n <h2>Error: " + errorCode + " Not Implemented </h2>\n <p> The request is not GET </p>\n </body></html>" + "\r\n");
			
		}
		
		
		output.flush();
		
	
	}
	

}
