/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Grid.Utilities;

import Grid.GridSimulation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jens Buysse - Jens.Buysse@intec.ugent.be
 */
public class HtmlWriter  {

    private PrintStream printStream;
    double salidas =0; 
    double maximoSalidas = 5000; 
    int pagina=1; 
    String fileName; 
    static int countObject= 1; 
    final String folderName = "Results_HTML";  File folder ; 
    public HtmlWriter() throws FileNotFoundException 
    {
        
        this.fileName = GridSimulation.configuration.getProperty(Config.ConfigEnum.outputFileName.toString()); 
        folder = new File(folderName+"_"+HtmlWriter.countObject); 
        if(!folder.exists())
        {
            folder.mkdir();            
        }
        else
        {
            deleteHTMLFiles(folder);
        }
        init();
        HtmlWriter.countObject++; 
    }
    
     public void deleteHTMLFiles(File file) {
       
         String[] archivosHTML=null;
         int totalFiles = 0; 

        if (file.exists()) {
            archivosHTML = file.list();
            totalFiles = archivosHTML.length;
        }
        for (int i = 0; i < totalFiles; i++) 
        {
            File fileHtml = new File(file.getPath() + File.separator + archivosHTML[i]);
            fileHtml.delete();
        }
    }

    
    private void init() throws FileNotFoundException 
    {
        String nombreArchivo = "" ; 
        int totalCeros = 10-String.valueOf(pagina).length(); 
        for(int i =0; i<totalCeros; i++)
        {
            nombreArchivo+="0"; 
        }    
        nombreArchivo+= pagina ; 
       
        String path = folder.getPath()+File.separator+fileName.replace(".html",  nombreArchivo+".html"); 
        printStream = new PrintStream(new FileOutputStream(path), false);                
        printStream.println(" <html>");
        printStream.println("<head>");
        printStream.println("<title>");
        printStream.print("Output for " + fileName);
        printStream.println("</title>");
        printStream.println("</head>");
        printStream.println("<body>");
        printStream.println("<FONT FACE=\"courier\">");
       
    }
    public void close()
    {
        fini();
        printStream.flush();
        printStream.close();
   
       
    }
    
    public void fini() {
        printStream.println("</FONT>");
        printStream.println("</body>");
        printStream.println("</html>");
    }
    public void println(String log) 
    {
        salidas++; 
        if(salidas>maximoSalidas)
        {
            pagina++; 
            System.out.println("Pagina "+pagina);
            salidas=0; 
            fini();
            printStream.flush();
            printStream.close();           
            try {
                init();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(HtmlWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }    
        printStream.println(log);         
    }        
}
