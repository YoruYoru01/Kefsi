package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import almacen.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



/**
 *
 * @author sqlitetutorial.net
 */
public class DataBaseHandler {
	
	private Connection conn;
     /**
     * Connect to a sample database
     * @return 
     */
	
	 public DataBaseHandler() {
	        connect();
	        createNewTables();
	    }

    public Connection connect() {
        
    	 try {
    	        String url = "jdbc:sqlite:Kefsi.db";
    	        conn = DriverManager.getConnection(url);
    	        System.out.println("Conexión establecida.");
    	    } catch (SQLException e) {
    	        System.out.println(e.getMessage());
    	    }
    	    return conn;
    }
    
    public void createNewTables() {
        // SQL statement for creating new tables
        String createTableInvoices = "CREATE TABLE IF NOT EXISTS invoices (\n"
                + "    id integer PRIMARY KEY,\n"
                + "    date text NOT NULL,\n"
                + "    total real NOT NULL\n"
                + ");";

        String createTableItems = "CREATE TABLE IF NOT EXISTS items (\n"
                + "    barcode text PRIMARY KEY,\n"
                + "    name text NOT NULL,\n"
                + "    units integer NOT NULL,\n"
                + "    price real NOT NULL,\n"
                + "    invoice_id integer,\n"
                + "    FOREIGN KEY (invoice_id) REFERENCES invoices (id)\n"
                + ");";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(createTableInvoices);
            stmt.execute(createTableItems);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
 // Método para agregar un ítem -- he implementado transacción con rollback para probar su funcionamiento
    public void addItem(Item item) {
        String sql = "INSERT INTO Items(barcode, name, units, price) VALUES(?,?,?,?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = this.connect();
            conn.setAutoCommit(false); // Desactivar auto-commit

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, item.getBarcode());
            pstmt.setString(2, item.getName());
            pstmt.setInt(3, item.getUnits());
            pstmt.setDouble(4, item.getPrice());
            pstmt.executeUpdate();

            conn.commit(); // Confirmar la transacción si todo va bien
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir cambios si algo sale mal
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            System.out.println(e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    // Método para actualizar un ítem
    public void updateItem(Item item) {
        String sql = "UPDATE Items SET name = ? , units = ? , price = ? WHERE barcode = ?";
        
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getUnits());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setString(4, item.getBarcode());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para eliminar un ítem
    public void deleteItem(String barcode) {
        String sql = "DELETE FROM Items WHERE barcode = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para obtener todos los ítems
    public ObservableList<Item> getItems() {
        String sql = "SELECT barcode, name, units, price FROM items";
        ObservableList<Item> items = FXCollections.observableArrayList();

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new Item(rs.getString("barcode"), rs.getString("name"), rs.getInt("units"), rs.getDouble("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }
    
    public boolean itemExists(String barcode) {
        String sql = "SELECT COUNT(*) FROM Items WHERE barcode = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    
    
}
