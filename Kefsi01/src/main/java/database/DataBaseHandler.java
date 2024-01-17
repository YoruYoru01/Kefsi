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
import invoices.Invoice;
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
        
        String createTableInvoiceItems = "CREATE TABLE IF NOT EXISTS InvoiceItems (\n"
        	    + "    invoice_id integer NOT NULL,\n"
        	    + "    item_barcode text NOT NULL,\n"
        	    + "    quantity integer NOT NULL,\n"
        	    + "    FOREIGN KEY (invoice_id) REFERENCES invoices (id),\n"
        	    + "    FOREIGN KEY (item_barcode) REFERENCES items (barcode)\n"
        	    + ");";
        
        String createTableUsers = "CREATE TABLE IF NOT EXISTS users (\n"
                + "    username text PRIMARY KEY,\n"
                + "    password text NOT NULL\n"
                + ");";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(createTableInvoices);
            stmt.execute(createTableItems);
            stmt.execute(createTableInvoiceItems);
            stmt.execute(createTableUsers);
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
    
    ////////////////////////////////////////
    
    //de las facturas
    
    ///////////////////////////////////////
  
    /*
     * Este enfoque utiliza last_insert_rowid() para obtener el ID de la última 
     * fila insertada en la base de datos, que corresponde a la factura recién creada. 
     * Luego, este ID se utiliza para insertar los ítems de la factura
     * */
    public void addInvoice(Invoice invoice) {
        String sqlInvoice = "INSERT INTO Invoices(date, total) VALUES(?,?)";
        String sqlItems = "INSERT INTO InvoiceItems(invoice_id, item_barcode, quantity) VALUES(?,?,?)";
        String sqlGetId = "SELECT last_insert_rowid()";

        Connection conn = null;
        PreparedStatement pstmtInvoice = null;
        PreparedStatement pstmtItems = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.connect();
            conn.setAutoCommit(false);

            // Insertar la factura
            pstmtInvoice = conn.prepareStatement(sqlInvoice);
            pstmtInvoice.setString(1, invoice.getDate());
            pstmtInvoice.setDouble(2, invoice.getTotal());
            pstmtInvoice.executeUpdate();

            // Obtener el ID generado
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlGetId);
            if (rs.next()) {
                invoice.setId(rs.getInt(1));
            }

            // Insertar cada ítem de la factura
            pstmtItems = conn.prepareStatement(sqlItems);
            for (Item item : invoice.getItems()) {
                pstmtItems.setInt(1, invoice.getId());
                pstmtItems.setString(2, item.getBarcode());
                pstmtItems.setInt(3, item.getUnits());
                pstmtItems.executeUpdate();
            }

            conn.commit();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        } finally {
            // Cerrar todos los recursos utilizados
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (pstmtItems != null) pstmtItems.close();
                if (pstmtInvoice != null) pstmtInvoice.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    //metodo para ver las facturas
    public ObservableList<Invoice> getInvoices() {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();
        String sql = "SELECT id, date, total FROM Invoices";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String date = rs.getString("date");
                double total = rs.getDouble("total");

                Invoice invoice = new Invoice(id, date);
                invoice.setTotal(total);
                invoice.addItems(getItemsForInvoice(id));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return invoices;
    }
    
    //metodo para recuperar los items de las facturas
    
    public ObservableList<Item> getItemsForInvoice(int invoiceId) {
        ObservableList<Item> items = FXCollections.observableArrayList();
        String sql = "SELECT i.barcode, i.name, i.units, i.price FROM Items i INNER JOIN InvoiceItems ii ON i.barcode = ii.item_barcode WHERE ii.invoice_id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String barcode = rs.getString("barcode");
                String name = rs.getString("name");
                int units = rs.getInt("units");
                double price = rs.getDouble("price");
                items.add(new Item(barcode, name, units, price));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return items;
    }

    // Método para eliminar una factura
    public void deleteInvoice(int invoiceId) {
        String sqlInvoice = "DELETE FROM Invoices WHERE id = ?";
        String sqlItems = "DELETE FROM InvoiceItems WHERE invoice_id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmtInvoice = conn.prepareStatement(sqlInvoice);
             PreparedStatement pstmtItems = conn.prepareStatement(sqlItems)) {

            conn.setAutoCommit(false);

            // Eliminar los ítems de la factura
            pstmtItems.setInt(1, invoiceId);
            pstmtItems.executeUpdate();

            // Eliminar la factura
            pstmtInvoice.setInt(1, invoiceId);
            pstmtInvoice.executeUpdate();

            conn.commit();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
///////////////////////////////////////////////////////////////////////////////
// CONSULTAS ENTRE TABLAS
///////////////////////////////////////////////////////////////////////////////
    
    public void updateItemUnits(String barcode, int newUnits) {
        String sql = "UPDATE Items SET units = ? WHERE barcode = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newUnits);
            pstmt.setString(2, barcode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    public int getItemUnits(String barcode) {
        String sql = "SELECT units FROM Items WHERE barcode = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barcode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("units");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1; // Retorna -1 si no se encuentra el item
    }
    
///////////////////////////////////////////////////////////////////////////////
//credenciales del usuario
///////////////////////////////////////////////////////////////////////////////
    
    public boolean validateLogin(String username, String password) {
    	String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";

    	 try (Connection conn = this.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                int count = rs.getInt(1);
                return count > 0;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
    }

    
    
}







