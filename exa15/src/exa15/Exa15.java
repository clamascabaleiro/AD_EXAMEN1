package exa15;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class Exa15 {

     public static Connection conexion = null;
        Connection conn;

    public static Connection getConexion() throws SQLException {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost";
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;

        conexion = DriverManager.getConnection(ulrjdbc);
        return conexion;
    }

    public static void closeConexion() throws SQLException {
        conexion.close();
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, XMLStreamException {
        //codigo aqui
        getConexion();
        File file = new File("/home/oracle/Desktop/examenRepaso/platoss");
        FileInputStream leer2 = new FileInputStream(file);
        ObjectInputStream leerObject = new ObjectInputStream(leer2);
        
        File file1 = new File("/home/oracle/Desktop/examenRepaso/totalgraxas.xml");
	FileWriter escribir = new FileWriter(file1);
        
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(escribir); 
        
        int totalgrasas; 
        
        Platos obj = new Platos();
        //Necesario meter en el bucle para que lea todo
        writer.writeStartDocument("1.0");
        writer.writeStartElement("Platos");
        
         while ((obj = (Platos) leerObject.readObject()) != null) {
            System.out.println(obj);
             totalgrasas=0;//necesario porque si no en la siguiente se suma lagrasa del anterior

                Statement stm = conexion.createStatement();
                ResultSet rs = stm.executeQuery("select * from composicion where codp='"+obj.getCodigop()+"'");
                Statement stm1= conexion.createStatement();
                
            
                

                while (rs.next()) { //Va de fila en fila
                ResultSet rss = stm1.executeQuery("select graxa from componentes where CODC='"+rs.getString(2)+"'");   
                rss.next();
                
                    System.out.print("codigo do componente : " + rs.getString(2) + "-> graxa por cada 100 gr="+rss.getInt(1) + "\nPeso: " + rs.getInt(3) + "\n");
                    System.out.println("Total de graxa do componente= "+(rss.getInt(1)*rs.getInt(3)/100));
                    totalgrasas+=(rss.getInt(1)*rs.getInt(3)/100); // =+ para que lo haga cada vez
                    
                    
           
                }
            System.out.println("TOTAL DE GRASAS "+totalgrasas+"\n"+"\n");
            
            
           
            writer.writeStartElement("Plato");
            writer.writeAttribute("codigo", obj.getCodigop());
            writer.writeEndElement();
            writer.writeStartElement("nombreP");
            writer.writeCharacters(obj.getNomep());
            writer.writeEndElement(); 
            writer.writeStartElement("grasaTotal");
            writer.writeCharacters(Integer.toString(totalgrasas));
            writer.writeEndElement();
           
            
            
        }
         
        writer.writeEndElement();      
        writer.writeEndDocument();
        writer.close();  
        leerObject.close();
        leer2.close();
        closeConexion();
    }
}
