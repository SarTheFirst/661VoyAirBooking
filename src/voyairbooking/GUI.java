/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voyairbooking;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SpinnerDateModel;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 *
 * @author Shaun
 */
@SuppressWarnings("serial")
public class GUI extends javax.swing.JFrame {

	VoyAirBooking vab;
	Dimension originalSize;

	/**
	 * Creates new form GUI
	 */
	public GUI() {
		this.vab = new VoyAirBooking(true);
		initComponents();
		this.originalSize = this.getSize();
		this.originalSize.height -= 125;
		this.setLocationRelativeTo(null);
		Point first = this.getLocation();
		first.x -= 140;
		this.setLocation(first);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aboutPopUp = new javax.swing.JDialog();
        registerPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        resultPanel = new javax.swing.JPanel();
        resultTable = new javax.swing.JTable();
        departingCityList = new javax.swing.JComboBox<>();
        deptartingCityText = new javax.swing.JLabel();
        ArrivalCItyText = new javax.swing.JLabel();
        destinationCityList = new javax.swing.JComboBox<>();
        departureTimeText = new javax.swing.JLabel();
        arrivalTimeText = new javax.swing.JLabel();
        findFlightButton = new javax.swing.JButton();
        LoginLogout = new javax.swing.JToggleButton();
        departureDate = new org.freixas.jcalendar.JCalendarCombo();
        arrivalDate = new org.freixas.jcalendar.JCalendarCombo();
        timeSpinnerDeparture = new javax.swing.JSpinner(new SpinnerDateModel());
        timeSpinnerArrival = new javax.swing.JSpinner(new SpinnerDateModel());
        exitButton = new javax.swing.JToggleButton();
        aboutButton = new javax.swing.JToggleButton();

        javax.swing.GroupLayout aboutPopUpLayout = new javax.swing.GroupLayout(aboutPopUp.getContentPane());
        aboutPopUp.getContentPane().setLayout(aboutPopUpLayout);
        aboutPopUpLayout.setHorizontalGroup(
            aboutPopUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 519, Short.MAX_VALUE)
        );
        aboutPopUpLayout.setVerticalGroup(
            aboutPopUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 128, Short.MAX_VALUE)
        );

        jLabel2.setText("Password");

        jLabel1.setText("Username");

        javax.swing.GroupLayout registerPanelLayout = new javax.swing.GroupLayout(registerPanel);
        registerPanel.setLayout(registerPanelLayout);
        registerPanelLayout.setHorizontalGroup(
            registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(30, 30, 30)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passwordField)
                    .addComponent(usernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );
        registerPanelLayout.setVerticalGroup(
            registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(registerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(registerPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));

        javax.swing.GroupLayout resultPanelLayout = new javax.swing.GroupLayout(resultPanel);
        resultPanel.setLayout(resultPanelLayout);
        resultPanelLayout.setHorizontalGroup(
            resultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(resultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(resultPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(resultTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        resultPanelLayout.setVerticalGroup(
            resultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(resultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(resultPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(resultTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        departingCityList.setModel(model);
        departingCityList.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                departingCityListAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        deptartingCityText.setText("Departing City");

        ArrivalCItyText.setText("Arrival City");

        destinationCityList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        destinationCityList.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                destinationCityListAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        departureTimeText.setText("<html>When do you<br>want to leave?</html");

        arrivalTimeText.setText("<html>When do you<br>want to arrive?</html");

        findFlightButton.setText("Find My Flight!");
        findFlightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findFlightButtonActionPerformed(evt);
            }
        });

        LoginLogout.setText("Login");
        LoginLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginLogoutActionPerformed(evt);
            }
        });

        Date dt = new Date();
        DateTime dtOrg = new DateTime(dt);
        arrivalDate.setDate(dtOrg.plusDays(1).toDate());

        timeSpinnerDeparture.setEditor(new JSpinner.DateEditor(timeSpinnerDeparture, "HH:mm"));
        timeSpinnerDeparture.setValue(new Date()); // will only show the current time

        timeSpinnerArrival.setEditor(new JSpinner.DateEditor(timeSpinnerArrival, "HH:mm"));
        timeSpinnerArrival.setValue(new Date()); // will only show the current time

        exitButton.setBackground(new java.awt.Color(254, 102, 58));
        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        aboutButton.setText("About");
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(arrivalTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(departureTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(deptartingCityText, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(15, 15, 15)
                                        .addComponent(departingCityList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ArrivalCItyText)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(destinationCityList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(departureDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(arrivalDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(timeSpinnerDeparture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(timeSpinnerArrival, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(findFlightButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LoginLogout)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(aboutButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LoginLogout)
                    .addComponent(exitButton)
                    .addComponent(aboutButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(departureTimeText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(departingCityList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deptartingCityText)
                            .addComponent(ArrivalCItyText)
                            .addComponent(destinationCityList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(timeSpinnerDeparture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(departureDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(timeSpinnerArrival, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(arrivalDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(arrivalTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(findFlightButton)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void departingCityListAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_departingCityListAncestorAdded
		addCities(departingCityList);
	}//GEN-LAST:event_departingCityListAncestorAdded

	private void destinationCityListAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_destinationCityListAncestorAdded
		addCities(destinationCityList);
	}//GEN-LAST:event_destinationCityListAncestorAdded

	private void findFlightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findFlightButtonActionPerformed
		boolean logged_in = this.vab.vabTools.is_logged_in();
		if (!logged_in) {
			logged_in = showLoginDialog();
		}
		if (logged_in) {
			LoginLogout.setVisible(false);
			String from = String.valueOf(departingCityList.getSelectedItem());
			String to = String.valueOf(destinationCityList.getSelectedItem());
			LocalDate depDate = LocalDate.fromDateFields(departureDate.getDate());
			LocalDate arrDate = LocalDate.fromDateFields(arrivalDate.getDate());
			String departureTime = ((JSpinner.DateEditor) timeSpinnerDeparture.getEditor()).getFormat().format(timeSpinnerDeparture.getValue());
			String arrivalTime = ((JSpinner.DateEditor) timeSpinnerArrival.getEditor()).getFormat().format(timeSpinnerArrival.getValue());
			LocalTime depTime = LocalTime.parse(departureTime);
			LocalTime arrTime = LocalTime.parse(arrivalTime);
			ArrayList<ArrayList<ArrayList<String>>> all_routes = this.vab.vabTools.get_routes(from, to);
                      
			//ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> final_routes = this.vab.vabTools.trim_routes(all_routes, arrDate, depDate, arrTime, depTime);
//			if(final_routes.isEmpty()){
//				JOptionPane.showMessageDialog(aboutPopUp, "There were no flights available for with the given parameters.");
//			}
//			else{
//				displayTable(final_routes);
//			}
		}
	}//GEN-LAST:event_findFlightButtonActionPerformed
	
	  /*
     * HashMap: Route row
     * ArrayList: Leg Options
     * ArrayList: Flight Leg
     * ArrayList: Flight Options
     */
	public void displayTable(ArrayList<ArrayList<ArrayList<HashMap<String, String>>>> final_routes){
            String[] columnNames = this.vab.vabTools.sqld.getColumnNames("route").toArray(new String[final_routes.size()]);;
            ArrayList<String[]> data = new ArrayList<String[]>();
            for(ArrayList<ArrayList<HashMap<String, String>>> flight_options : final_routes){
                    //hashMap.values().toArray(); // returns an array of values
                    for(ArrayList<HashMap<String, String>> flight_leg : flight_options){
                            for(HashMap<String, String> leg_option : flight_leg){
                                    data.add(leg_option.values().toArray(new String[leg_option.size()]));
                            }
                    }
            }

            Object[] data_str_array = new Object[data.size()];
            for(int i = 0; i < data.size(); i++){
                    data_str_array[i] = data.get(i);
            }

            Object[][] data_obj = {data_str_array};
            resultTable = new JTable(data_obj, columnNames);
            JOptionPane.showMessageDialog(resultPanel, resultTable);
	}	
	private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
		System.exit(0);
	}//GEN-LAST:event_exitButtonActionPerformed

	private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
		JOptionPane.showMessageDialog(aboutPopUp,
				"VoyAirBooking was made by Aparna Kaliappan,\n"
						+ "Alison Pfannenstein and Shari Kurland in 2016\n"
						+ "for the University of Maryland, Baltimore County.\n"
				);
	}//GEN-LAST:event_aboutButtonActionPerformed

    private void LoginLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginLogoutActionPerformed
       if(showLoginDialog()){
           LoginLogout.setVisible(false);
       }
    }//GEN-LAST:event_LoginLogoutActionPerformed
   

	private boolean showLoginDialog() {
        int result = JOptionPane.showConfirmDialog(null, registerPanel, "Test",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {

//                JOptionPane.showConfirmDialog(null,
//                        getPanel(),
//                        "JOptionPane Example : ",
//                        JOptionPane.OK_CANCEL_OPTION,
//                        JOptionPane.PLAIN_MESSAGE);

//            int input = JOptionPane.showInputDialog(
//                            registerPopUp,
//                            "Please Log in",
//                            JOptionPane.YES_NO_OPTION);
                    String username = usernameField.getText();
                    String password = String.valueOf(passwordField.getPassword());
                    return this.vab.vabTools.tryLoggingIn(username, password);
            }
            return false;
	}

	private void addCities(JComboBox<String> dropdown) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		dropdown.setModel(model);
		int maxSize = -1;
		ArrayList<String> cities = this.vab.vabTools.get_cities();
		cities.add(0, "Select A City");
		for (String s : cities) {
			if (s.length() > maxSize) {
				maxSize = s.length();
			}
			model.addElement(s);
		}
		Dimension d = this.getSize();
		d.width += maxSize + 125; // [massive shrug of guestimation how this works]
		this.setSize(d);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GUI().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ArrivalCItyText;
    private javax.swing.JToggleButton LoginLogout;
    private javax.swing.JToggleButton aboutButton;
    private javax.swing.JDialog aboutPopUp;
    private org.freixas.jcalendar.JCalendarCombo arrivalDate;
    private javax.swing.JLabel arrivalTimeText;
    private javax.swing.JComboBox<String> departingCityList;
    private org.freixas.jcalendar.JCalendarCombo departureDate;
    private javax.swing.JLabel departureTimeText;
    private javax.swing.JLabel deptartingCityText;
    private javax.swing.JComboBox<String> destinationCityList;
    private javax.swing.JToggleButton exitButton;
    private javax.swing.JButton findFlightButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JPanel registerPanel;
    private javax.swing.JPanel resultPanel;
    private javax.swing.JTable resultTable;
    private javax.swing.JSpinner timeSpinnerArrival;
    private javax.swing.JSpinner timeSpinnerDeparture;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables


}
