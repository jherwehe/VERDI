package anl.verdi.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXHeader;

import anl.verdi.util.Tools;
import anl.verdi.util.VersionInfo;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;



/**
 * @author User #2
 */
public class HelpDialog extends JDialog {
	private static final long serialVersionUID = -8261331914096627049L;
	
	public HelpDialog(Frame owner) {
		super(owner);
		try {
			initComponents();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xHeader1.setDescription("Version: " + VersionInfo.getVersion() + " " + VersionInfo.getDate());
	}

	public HelpDialog(Dialog owner) {
		super(owner);
		try {
			initComponents();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xHeader1.setDescription("Version: " + VersionInfo.getVersion() + " " + VersionInfo.getDate());
	}

	private void okButtonActionPerformed(ActionEvent e) {
		this.dispose();
	}

	private void initComponents() throws URISyntaxException {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		scrollPane1 = new JScrollPane();
		xHeader1 = new JXHeader();
		buttonBar = new JPanel();
		okButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle("Help VERDI");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"default:grow",
					"default"));

				//======== scrollPane1 ========
				{
					String verdiHome = Tools.getVerdiHome();
					String path = verdiHome + File.separator + "plugins" + File.separator + "bootstrap" + File.separator + "help" + File.separator;
					// changed from using URI to File
				    // old URI was: "http://www.verdi-tool.org/verdiUserManual_URI_uri.htm"
					URL url = null;
					try {
						url = new URL("https://github.com/CEMPD/VERDI/blob/master/doc/User_Manual/README.md");
					} catch (Exception e) {
						e.printStackTrace();
					}
					final URL userManual = url;
					try {
						url = new URL("https://github.com/CEMPD/VERDI/blob/master/doc/Developer_Manual/VERDIDevInstructions.md");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					final URL devManual = url;
					//final File file1 = new File(path + "VerdiUserManual1.6alpha.pdf");
				    // old URI was: "http://www.cmascenter.org/help/model_docs/verdi/1.4/VerdiUserManual_URI_uri2.pdf"
					//final File file2 = new File(path + "VerdiDevInstructions1.6alpha.pdf");
					class OpenUrlAction implements ActionListener {
				      @Override public void actionPerformed(ActionEvent e) {
				        open(userManual);
				      }
				    }
				    class OpenUrlAction2 implements ActionListener {
					      @Override public void actionPerformed(ActionEvent e) {
					        open(devManual);
					      }
					    }
				    JButton htmlLinkButton = new JButton();
				    htmlLinkButton.setText("<HTML><FONT color=\"#000099\"><U>User Manual</U></FONT></HTML>");
				    htmlLinkButton.setHorizontalAlignment(SwingConstants.LEFT);
				    htmlLinkButton.setBorderPainted(false);
				    htmlLinkButton.setOpaque(false);
				    htmlLinkButton.setBackground(Color.WHITE);
				    htmlLinkButton.setToolTipText(userManual.toString());
				    htmlLinkButton.addActionListener(new OpenUrlAction());
					
					
				    JButton pdfLinkButton = new JButton();
				    pdfLinkButton.setText("<HTML><FONT color=\"#000099\"><U>Developer Instructions</U></FONT></HTML>");
				    pdfLinkButton.setHorizontalAlignment(SwingConstants.LEFT);
				    pdfLinkButton.setBorderPainted(false);
				    pdfLinkButton.setOpaque(false);
				    pdfLinkButton.setBackground(Color.WHITE);
				    pdfLinkButton.setToolTipText(devManual.toString());
				    pdfLinkButton.addActionListener(new OpenUrlAction2());
					
					
					JPanel panel = new JPanel(new BorderLayout());
					panel.add(htmlLinkButton, BorderLayout.NORTH);
					panel.add(pdfLinkButton, BorderLayout.SOUTH);
					scrollPane1.setViewportView(panel);
				}
				contentPanel.add(scrollPane1, cc.xy(1, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				// 2014
				RowSpec[] aRowSpec = RowSpec.decodeSpecs("pref");
				buttonBar.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormFactory.GLUE_COLSPEC,
						FormFactory.BUTTON_COLSPEC
					},
					aRowSpec));
//				buttonBar.setLayout(new FormLayout(
//						new ColumnSpec[] {
//							FormFactory.GLUE_COLSPEC,
//							FormFactory.BUTTON_COLSPEC
//						},
//						RowSpec.decodeSpecs("pref")));

				//---- okButton ----
				okButton.setText("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});
				buttonBar.add(okButton, cc.xy(2, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JScrollPane scrollPane1;
	private JXHeader xHeader1;
	private JPanel buttonBar;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	  private static void open(File file) {
		    if (Desktop.isDesktopSupported()) {
		      try {
		        Desktop.getDesktop().open(file);
		      } catch (IOException e) { /* TODO: error handling */ }
		    } else { /* TODO: error handling */ }
		  }
		  
	  private static void open(URL url) {
		    if (Desktop.isDesktopSupported()) {
		      try {
		        Desktop.getDesktop().browse(url.toURI());
		      } catch (Exception e) { /* TODO: error handling */ }
		    } else { /* TODO: error handling */ }
		  }
		  
	    public void setVisible(boolean b) {
	    	Point p = getLocation();
	    	setLocation(0, p.y);
	        super.setVisible(b);
	    }
}
