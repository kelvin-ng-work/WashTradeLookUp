package washtrade.query;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class SearchAndDisplayPanel extends JPanel {

	private Font font = new Font("Tahoma", Font.PLAIN, 16);
	private JTabbedPane tabbedPanel = new JTabbedPane();
	private JPanel firstTab, secondTab;
	private JLabel symbol = new JLabel("Symbol", JLabel.LEFT);
	private JLabel trade_utc_time = new JLabel("Trade_UTC_Time", JLabel.LEFT);
	private JLabel trader_Id = new JLabel("Trader Id", JLabel.LEFT);
	private JLabel trade_qty = new JLabel("Trade Quantity", JLabel.LEFT);
	private JLabel trade_price = new JLabel("Trade Price", JLabel.LEFT);
	private JTextField symbol_field = new JTextField();
	private JTextField trade_utc_time_field = new JTextField();
	private JTextField trader_Id_field = new JTextField();
	private JTextField trade_qty_field = new JTextField("0");
	private JTextField trade_price_field = new JTextField("0.0000");
	private JButton searchButton = new JButton("Search");
	private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private final String DB_URL = "jdbc:mysql://10.60.67.192:3306/Omega";
	private final String USER = "omega_user";
	private final String PASS = "omega_user";
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs;
	private Object[][] listOfWashTrades;
	private int totalWashTrades = 0;
	
	// ActionListener registered to the search button to search and display wash trades
	class searchActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String symbol = symbol_field.getText();
			String time = trade_utc_time_field.getText();
			String Id = trader_Id_field.getText();
			int qty = Integer.parseInt(trade_qty_field.getText());
			double price = Double.parseDouble(trade_price_field.getText());
			secondTab = createDisplayPanel(symbol, time, Id, qty, price);
			tabbedPanel.setComponentAt(1, secondTab);
		}
	}
	
	// Creates the main display panel and the first tab
	public SearchAndDisplayPanel() {
		firstTab = createSearchPanel();
		tabbedPanel.addTab("Search Panel", firstTab);
		tabbedPanel.setSelectedIndex(0);
		secondTab = createDisplayPanel("", "", "", 0, 0.00);
		tabbedPanel.addTab("Wash Trade(s)", secondTab);
		int mainPanelWidth = 500;
		int mainPanelHeight = 600;
		tabbedPanel.setPreferredSize(new Dimension(mainPanelWidth, mainPanelHeight));
		setLayout(new GridLayout(1, 1));
		add(tabbedPanel);
	}
	
	// Creates the search panel
	protected JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel();
		int searchPanelWidth = 500;
		int searchPanelHeight = 600;
		searchPanel.setPreferredSize(new Dimension(searchPanelWidth, searchPanelHeight));
		searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		searchPanel.setLayout(new GridLayout(0, 1));
		searchPanel.setFont(font);
		searchPanel.add(symbol);
		searchPanel.add(symbol_field);
		searchPanel.add(trade_utc_time);
		searchPanel.add(trade_utc_time_field);
		searchPanel.add(trader_Id);
		searchPanel.add(trader_Id_field);
		searchPanel.add(trade_qty);
		searchPanel.add(trade_qty_field);
		searchPanel.add(trade_price);
		searchPanel.add(trade_price_field);
		searchPanel.add(new JPanel());
		searchButton.addActionListener(new searchActionListener());
		searchPanel.add(searchButton);
		return searchPanel;
	}
	
	// Table model class
    class WashTradeTableModel extends AbstractTableModel {
	   int rowPos = 0;
	   int colPos = 0;
	   String symbol, trade_utc_time, trader_Id;
	   int trade_no, trade_qty;
	   double trade_price;
	   
       public WashTradeTableModel(String symbol, String time, String Id, int qty, double price) {
    		  this.symbol = symbol;
    		  this.trade_utc_time = time;
    		  this.trader_Id = Id;
    		  this.trade_qty = qty;
    		  this.trade_price = price;
    		  if(symbol == "") {
    			  listOfWashTrades = new Object[1][10];
    			  listOfWashTrades[rowPos][colPos++] = 0;
				  listOfWashTrades[rowPos][colPos++] = "N/A";
				  listOfWashTrades[rowPos][colPos++] = "N/A";
				  listOfWashTrades[rowPos][colPos++] = "N/A";
				  listOfWashTrades[rowPos][colPos++] = 0;
				  listOfWashTrades[rowPos][colPos++] = 0.0000;
    		  } else {
    			  try {
    				  // Registers the JDBC driver
    	 	          Class.forName("com.mysql.jdbc.Driver");
    	 	          // Opens connection
    	 	          conn = DriverManager.getConnection(DB_URL, USER, PASS);
    	 	          // Creates SQL statement
    	 	          stmt = conn.createStatement();
    	 	          // SQL select statement to retrieve target securities
    	 	          String sql = "SELECT * FROM EOD_TRADE_VIEW WHERE SYMBOL='" + symbol + "'";
    	 	          if(!(time.length() == 0)) {
    	 	        	  double seconds = Double.parseDouble(trade_utc_time.substring(trade_utc_time.length() - 5));
    	 	        	  double prevTimeUnit = seconds - 0.001;
    	 	        	  double nextTimeUnit = seconds + 0.001;
    	 	        	  String oneMsLessThanTradeTime = trade_utc_time.substring(0, trade_utc_time.length() - 5) + prevTimeUnit;
    	 	        	  String oneMsGreaterThanTradeTime = trade_utc_time.substring(0, trade_utc_time.length() - 5) + nextTimeUnit;
    	 	        	  sql += " AND (TRADE_UTC_TIME='" + trade_utc_time + "' OR TRADE_UTC_TIME='" + oneMsLessThanTradeTime + "' OR TRADE_UTC_TIME='" + oneMsGreaterThanTradeTime + "')";
    	 	          }
    	 	          if(!(Id == "")) {
    	 	        	  sql += " AND TRADER_ID='" + trader_Id + "'";
    	 	          }
    	 	          if(!(qty == 0)) {
    	 	        	  sql += " AND TRADE_QTY=" + trade_qty;
    	 	          }
    	 	          if(!(Double.compare(price, 0.0000) == 0)) {
    	 	        	  sql += " AND TRADE_PRICE=" + trade_price;
    	 	          }
    	 	          System.out.println(sql);
    	 	          // Executes the database query
    	 			  rs = stmt.executeQuery(sql);
    	 			  // Handles returned query result
    	 			  if(!rs.first()) {
    					  	JOptionPane.showMessageDialog(new JFrame(), "Could not find wash trade(s)", "Query Error", JOptionPane.ERROR_MESSAGE);
    						throw new Exception("Failed to find wash trade(s)");
    	 			  } else {
    	 				  // Extracts data from result set
    	 				  rs.last();
    	 				  totalWashTrades = rs.getRow();
    	 				  listOfWashTrades = new Object[totalWashTrades][10];
    					  rs.first();
    		 	          do {
    		 	             // Retrieves column data
    		 	        	 trade_no = Integer.parseInt(rs.getString("TRADE_NO"));
    		 	        	 symbol = rs.getString("SYMBOL");
    		 	        	 trade_utc_time = rs.getString("TRADE_UTC_TIME");
    		 	        	 trader_Id = rs.getString("TRADER_ID");
    		 	        	 trade_qty = Integer.parseInt(rs.getString("TRADE_QTY"));
    		 	        	 trade_price = Double.parseDouble(rs.getString("TRADE_PRICE"));
    		 	             // Adds the security record to the 2D storing array
    		 	        	 listOfWashTrades[rowPos][colPos++] = trade_no;
    		 	        	 listOfWashTrades[rowPos][colPos++] = symbol;
    		 	        	 listOfWashTrades[rowPos][colPos++] = trader_Id;
    		 	        	 listOfWashTrades[rowPos][colPos++] = trade_utc_time;
    		 	        	 listOfWashTrades[rowPos][colPos++] = trade_qty;
    		 	        	 listOfWashTrades[rowPos][colPos++] = trade_price;
    		 	             // Iterates to next row
    		 	             rowPos += 1;
    		 	             colPos = 0;
    		 	           } while(rs.next() && rowPos <= totalWashTrades);
    	 			  	}
    	 	       }catch(SQLException se){
    	 	          // Handles JDBC errors
    	 	          se.printStackTrace();
    	 	       }catch(Exception e){
    	 	          // Handle other errors
    	 	          e.printStackTrace();
    	 	       }
    		  }
       	}
       	
		private String[] columnNames = {"Trade No.",
										"Symbol",
										"Trader ID",
		                                "Trade UTC Time",
		                                "Trade Quantity",
		                                "Trade Price"
		                                };
		public final Object[] longValues = {0,
											"TRADE_SYMBOL",
											"TRADE_UTC_TIME",
		                                    "TRADER_ID",
		                                    0,
		                                    0.0000
		                                    };
		
		// Gets the number of columns
		public int getColumnCount() {
		    return columnNames.length;
		}
		
		// Gets the number of rows
		public int getRowCount() {
			return totalWashTrades;
		}
		
		// Gets the column name
		public String getColumnName(int col) {
		    return columnNames[col];
		}
		
		// Gets a specific security record's data value
		public Object getValueAt(int row, int col) {
			return listOfWashTrades[row][col];
		}
		
		// Gets the class of a particular column
		public Class getColumnClass(int c) {
		    return getValueAt(0, c).getClass();
		}
    }
    
    // Sets up column sizes of the table
    private void initColumnSizes(JTable table) {
    	// Creates table model
        WashTradeTableModel model = (WashTradeTableModel)table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.longValues;
        // Retrieves table handler
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        // Configures column properties
        for (int i = 0; i < 5; i++) {
            column = table.getColumnModel().getColumn(i);
            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, longValues[i],
                                 false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }
	
    // Creates the display tab
	protected JPanel createDisplayPanel(String symbol, String time, String Id, int qty, double price) {
	    JPanel displayPanel = new JPanel();
	    displayPanel.setLayout(new GridLayout(0, 1));
	    int displayPanelWidth = 500;
		int displayPanelHeight = 600;
		displayPanel.setPreferredSize(new Dimension(displayPanelWidth, displayPanelHeight));
        // Creates the security records table
	    JTable table = new JTable(new WashTradeTableModel(symbol, time, Id, qty, price));
        table.setAutoCreateRowSorter(true);
        //table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        // Creates the scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        // Sets up column sizes
        initColumnSizes(table);
        Font font = new Font("Tahoma", Font.PLAIN, 16);
        table.setFont(font);
        table.getTableHeader().setFont(font);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        // Adds the scroll pane to this panel.
        displayPanel.add(scrollPane);
        return displayPanel;
	}
	
	// Main program
	public static void main(String[] args) {
		JFrame frame = new JFrame("Wash Trades");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		int framePaneWidth = 400;
		int framePaneHeight = 500;
		frame.setPreferredSize(new Dimension(framePaneWidth, framePaneHeight));
		frame.getContentPane().add(new SearchAndDisplayPanel(), BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
