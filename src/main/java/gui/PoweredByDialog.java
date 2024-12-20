package gui;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;


@SuppressWarnings("serial")
public class PoweredByDialog extends JDialog {
	
	private JLabel lblInformation, lblPhoto, lblVitae, lblContact;
	
	public PoweredByDialog(Frame parent) {
		super(parent, true);
		setSize(new Dimension(600, 360));
		setLocationRelativeTo(null);	
		setResizable(false);
		setTitle("Powered by");
		init();
		setVisible(true);
	}
	
	protected void init() {
		String info, vitae, contact;
		
		info = "<html> <em>Graphical user interface of prioritized multiple change on credibility orders. </em><br>";
		info += "<em>Version: 2021-08 (1.0.0). </em><br>";
		info += "<em>This application was developed by Federico Joaquín. </em></html>";
		
		lblInformation = new JLabel(info);
		lblInformation.setBorder(new EmptyBorder(25,25,25,25));
		getContentPane().add(lblInformation, BorderLayout.NORTH);
		
		lblPhoto = new JLabel();
		lblPhoto.setBorder(new EmptyBorder(0,25,25,25));
		lblPhoto.setIcon(new ImageIcon(this.getClass().getResource("/images/fj_photo.jpg")));
		getContentPane().add(lblPhoto, BorderLayout.WEST);
		
		vitae = "<html> <strong>Federico Joaquín</strong> received his B.S. degree in computer engineering from Universidad Nacional del Sur (2016), ";
		vitae += "Bahía Blanca, Argentina. He is currently a PhD student under the supervision of Dr. Alejandro J. García and Dr. ";
		vitae += "Luciano H. Tamargo in Universidad Nacional del Sur. His main research interests include trust modeling and belief revision. ";
		vitae += "Federico Joaquín is currently a head teaching assistant at Department of Computer Science and Engineering, ";
		vitae += "Universidad Nacional del Sur, Bahía Blanca. </html>";
		
		lblVitae = new JLabel(vitae);
		lblVitae.setBorder(new EmptyBorder(0,0,25,25));
		getContentPane().add(lblVitae, BorderLayout.CENTER);
		
		contact = "<html>Contact information: <br>";
		contact += "https://cs.uns.edu.ar/~federico.joaquin <br>";
		contact += "federico.joaquin@cs.uns.edu.ar <br> </html>";
		
		lblContact = new JLabel(contact);
		lblContact.setBorder(new EmptyBorder(0,25,25,25));
		getContentPane().add(lblContact, BorderLayout.SOUTH);
				
	}
}