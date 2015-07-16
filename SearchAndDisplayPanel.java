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
	private Font labelFont = new Font("Tahoma", Font.BOLD, 16);
	private Font fieldFont = new Font("Tahoma", Font.PLAIN, 16);
	private JTabbedPane tabbedPanel = new JTabbedPane();
	private JPanel firstTab, secondTab;
	private JPanel statusPanel = new JPanel();
	private JLabel statusLabel = new JLabel("", JLabel.CENTER);
	private JLabel symbol = new JLabel("Symbol", JLabel.LEFT);
	private JLabel trade_utc_time = new JLabel("Trade_UTC_Time", JLabel.LEFT);
	private JLabel broker = new JLabel("Broker", JLabel.LEFT);
	private JLabel trader_Id = new JLabel("Trader Id", JLabel.LEFT);
	private JLabel trade_qty = new JLabel("Trade Quantity", JLabel.LEFT);
	private JLabel trade_price = new JLabel("Trade Price", JLabel.LEFT);
	private JTextField symbol_field = new JTextField();
	private JTextField trade_utc_time_field = new JTextField();
	private JTextField broker_field = new JTextField();
	private JTextField trader_Id_field = new JTextField();
	private JTextField trade_qty_field = new JTextField();
	private JTextField trade_price_field = new JTextField();
	private JButton searchButton = new JButton("Search");
	private JButton clearButton = new JButton("Clear Fields");
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://host:port/database";
    static final String USER = "user";
    static final String PASS = "password";
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs;
	
	// Thread class to create a new display panel
	class washTradeThread extends Thread {
	   private Thread t;
	   private String threadName;
	   
	   washTradeThread(String name){
	       threadName = name;
	       //System.out.println("Creating " +  threadName );
	   }
	   
	   public void run() {
	      //System.out.println("Running " +  threadName );
	      try {
	    	  String symbol = symbol_field.getText();
	    	  String time = trade_utc_time_field.getText();
	    	  String broker = broker_field.getText();
	    	  String Id = trader_Id_field.getText();
	    	  int qty = 0;
	    	  if(!((trade_qty_field.getText()).length() == 0)) {
	    	  	qty = Integer.parseInt(trade_qty_field.getText());
	    	  }
	    	  double price = 0.0000;
	    	  if(!((trade_price_field.getText()).length() == 0)) {
	    	  	price = Double.parseDouble(trade_price_field.getText());
	    	  }
	    	  JPanel newTab = createDisplayPanel(symbol, time, broker, Id, qty, price);
	    	  tabbedPanel.addTab(symbol + " Wash Trade(s)", newTab);
	    	  statusLabel.setText(symbol + " wash trade(s) returned.");
	     } catch (Exception e) {
	         //System.out.println("Thread " +  threadName + " interrupted.");
	     }
	     //System.out.println("Thread " +  threadName + " exiting.");
	   }
	   
	   public void start ()
	   {
	      //System.out.println("Starting " +  threadName );
	      if (t == null)
	      {
	         t = new Thread(this, threadName);
	         t.start ();
	      }
	   }
	}
	
	// ActionListener registered to the Search button to search and display wash trades
	class searchActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			statusLabel.setText(symbol_field.getText() + " wash trade query issued.");
			washTradeThread thread = new washTradeThread("Wash Trade Lookup");
			thread.start();
		}
	}
	
	// ActionListener registered to the Clear Fields button to clear all fields
	class clearActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			symbol_field.setText("");
			trade_utc_time_field.setText("");
			broker_field.setText("");
			trader_Id_field.setText("");
			trade_qty_field.setText("");
			trade_price_field.setText("");
		}
	}
	
	// Creates the main display panel and the first tab
	public SearchAndDisplayPanel() {
		firstTab = createSearchPanel();
		tabbedPanel.addTab("Search Panel", firstTab);
		tabbedPanel.setSelectedIndex(0);
		//secondTab = createDisplayPanel("", "", "", "", 0, 0.00);
		//tabbedPanel.addTab("Wash Trade(s)", secondTab);
		int mainPanelWidth = 500;
		int mainPanelHeight = 600;
		tabbedPanel.setPreferredSize(new Dimension(mainPanelWidth, mainPanelHeight));
		setLayout(new GridLayout(1, 1));
		add(tabbedPanel);
	}
	
	// Label and text field panel unit
	class panelUnit extends JPanel {
		panelUnit(JLabel label, JTextField field) {
			this.setLayout(new GridLayout(2, 1));
			this.add(label);
			this.add(field);
		}		
	}
	
	// Creates the search panel
	protected JPanel createSearchPanel() {
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		searchPanel.setLayout(new GridLayout(0, 3));
		searchPanel.add(new JPanel());
		symbol.setFont(labelFont);
		symbol_field.setFont(fieldFont);
		searchPanel.add(new panelUnit(symbol, symbol_field));
		searchPanel.add(new JPanel());
		searchPanel.add(new JPanel());
		trade_utc_time.setFont(labelFont);
		trade_utc_time_field.setFont(fieldFont);
		searchPanel.add(new panelUnit(trade_utc_time, trade_utc_time_field));
		searchPanel.add(new JPanel());
		searchPanel.add(new JPanel());
		broker.setFont(labelFont);
		broker_field.setFont(fieldFont);
		searchPanel.add(new panelUnit(broker, broker_field));
		searchPanel.add(new JPanel());
		searchPanel.add(new JPanel());
		trader_Id.setFont(labelFont);
		trader_Id_field.setFont(fieldFont);
		searchPanel.add(new panelUnit(trader_Id, trader_Id_field));
		searchPanel.add(new JPanel());
		searchPanel.add(new JPanel());
		trade_qty.setFont(labelFont);
		trade_qty_field.setFont(fieldFont);
		searchPanel.add(new panelUnit(trade_qty, trade_qty_field));
		searchPanel.add(new JPanel());
		searchPanel.add(new JPanel());
		trade_price.setFont(labelFont);
		trade_price_field.setFont(fieldFont);
		searchPanel.add(new panelUnit(trade_price, trade_price_field));
		searchPanel.add(new JPanel());
		searchPanel.add(new JPanel());
		statusPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		statusLabel.setFont(labelFont);
		statusPanel.add(statusLabel);
		searchPanel.add(statusPanel);
		searchPanel.add(new JPanel());
		JPanel buttonPanel = new JPanel();
		FlowLayout buttonLayout = new FlowLayout();
		buttonPanel.setLayout(buttonLayout);
		//buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 130, 10, 130));
		searchButton.addActionListener(new searchActionListener());
		clearButton.addActionListener(new clearActionListener());
		searchButton.setFont(labelFont);
		clearButton.setFont(labelFont);
		buttonPanel.add(clearButton);
		buttonPanel.add(searchButton);
		searchPanel.add(new JPanel());
		searchPanel.add(buttonPanel);
		searchPanel.add(new JPanel());
		return searchPanel;
	}
	
	// Table model class
    class WashTradeTableModel extends AbstractTableModel {
	   int rowPos = 0;
	   int colPos = 0;
	   String symbol, trade_utc_time, broker, trader_Id, side;
	   int trade_no, trade_qty;
	   double trade_price;
	   private Object[][] listOfWashTrades;
	   private int totalWashTrades = 0;
	   
       public WashTradeTableModel(String symbol, String time, String broker, String Id, int qty, double price) {
    		  this.symbol = symbol;
    		  this.trade_utc_time = time;
    		  this.broker = broker;
    		  this.trader_Id = Id;
    		  this.trade_qty = qty;
    		  this.trade_price = price;
    		  if(symbol == "") {
    			  listOfWashTrades = new Object[1][10];
    			  listOfWashTrades[rowPos][colPos++] = 0;
				  listOfWashTrades[rowPos][colPos++] = "N/A";
				  listOfWashTrades[rowPos][colPos++] = "N/A";
				  listOfWashTrades[rowPos][colPos++] = "N/A";
				  listOfWashTrades[rowPos][colPos++] = "N/A";
				  listOfWashTrades[rowPos][colPos++] = 0;
				  listOfWashTrades[rowPos][colPos++] = 0.0000;
				  listOfWashTrades[rowPos][colPos++] = "N/A";
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
    	 	          if(!(trade_utc_time.length() == 0)) {
    	 	        	  double seconds = Double.parseDouble(trade_utc_time.substring(trade_utc_time.length() - 5));
    	 	        	  double prevTimeUnit = seconds - 1.000;
    	 	        	  double nextTimeUnit = seconds + 1.000;
    	 	        	  String oneSecondLessThanTradeTime = trade_utc_time.substring(0, trade_utc_time.length() - 5) + prevTimeUnit;
    	 	        	  String oneSecondsGreaterThanTradeTime = trade_utc_time.substring(0, trade_utc_time.length() - 5) + nextTimeUnit;
    	 	        	  sql += " AND (TRADE_UTC_TIME>='" + oneSecondLessThanTradeTime + "' AND TRADE_UTC_TIME<='" + oneSecondsGreaterThanTradeTime + "')";
    	 	          }
    	 	          if(!(broker.length() == 0)) {
    	 	        	  sql += " AND BROKER='" + broker + "'";
    	 	          }
    	 	          if(!(trader_Id.length() == 0)) {
    	 	        	  sql += " AND TRADER_ID='" + trader_Id + "'";
    	 	          }
    	 	          if(!(trade_qty == 0)) {
    	 	        	  sql += " AND TRADE_QTY=" + trade_qty;
    	 	          }
    	 	          if(!(Double.compare(trade_price, 0.0000) == 0)) {
    	 	        	  sql += " AND TRADE_PRICE=" + trade_price;
    	 	          }
    	 	          //System.out.println(sql);
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
    		 	        	 broker = rs.getString("BROKER");
    		 	        	 trader_Id = rs.getString("TRADER_ID");
    		 	        	 trade_qty = Integer.parseInt(rs.getString("TRADE_QTY"));
    		 	        	 trade_price = Double.parseDouble(rs.getString("TRADE_PRICE"));
    		 	        	 side = rs.getString("SIDE");
    		 	             // Adds the security record to the 2D storing array
    		 	        	 listOfWashTrades[rowPos][colPos++] = trade_no;
    		 	        	 listOfWashTrades[rowPos][colPos++] = symbol;
    		 	        	 listOfWashTrades[rowPos][colPos++] = trade_utc_time;
    		 	        	 listOfWashTrades[rowPos][colPos++] = broker;
    		 	        	 listOfWashTrades[rowPos][colPos++] = trader_Id;
    		 	        	 listOfWashTrades[rowPos][colPos++] = trade_qty;
    		 	        	 listOfWashTrades[rowPos][colPos++] = trade_price;
    		 	        	 listOfWashTrades[rowPos][colPos++] = side;
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
										"Trade UTC Time",
										"Broker",
										"Trader ID",
		                                "Trade Quantity",
		                                "Trade Price",
		                                "SIDE"
		                                };
		public final Object[] longValues = {0,
											"TRADE_SYMBOL",
											"TRADE_UTC_TIME",
											"BROKER",
		                                    "TRADER_ID",
		                                    0,
		                                    0.0000,
		                                    "BUY/SELL SIDE"
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
        Component component = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.longValues;
        // Retrieves table handler
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        // Configures column properties
        for (int i = 0; i < 8; i++) {
            column = table.getColumnModel().getColumn(i);
            component = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = component.getPreferredSize().width;
            component = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, longValues[i],
                                 false, false, 0, i);
            cellWidth = component.getPreferredSize().width;
            column.setPreferredWidth(Math.max(200, cellWidth));
        }
    }
	
    // Creates the display tab
	protected JPanel createDisplayPanel(String symbol, String time, String broker, String Id, int qty, double price) {
	    JPanel displayPanel = new JPanel();
	    displayPanel.setLayout(new GridLayout(0, 1));
	    int displayPanelWidth = 600;
		int displayPanelHeight = 600;
		displayPanel.setPreferredSize(new Dimension(displayPanelWidth, displayPanelHeight));
        // Creates the security records table
	    JTable table = new JTable(new WashTradeTableModel(symbol, time, broker, Id, qty, price));
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        // Creates the scroll pane
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // Sets up column sizes
        initColumnSizes(table);
        Font font = new Font("Tahoma", Font.PLAIN, 24);
        table.setRowHeight(30);
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
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
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
		int framePaneWidth = 1230;
		int framePaneHeight = 500;
		frame.setPreferredSize(new Dimension(framePaneWidth, framePaneHeight));
		frame.getContentPane().add(new SearchAndDisplayPanel(), BorderLayout.CENTER);
		frame.pack();
		int halfWidth = frame.getWidth()/2;
	    int halfHeight = frame.getHeight()/2;
	    int x = (Toolkit.getDefaultToolkit().getScreenSize().width/2)-halfWidth;
	    int y = (Toolkit.getDefaultToolkit().getScreenSize().height/2)-halfHeight;
	    frame.setLocation(x, y);
		frame.setVisible(true);
	}
}
