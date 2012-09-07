/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gcsf.routerip;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

/**
 *
 * @author Virginia
 */
public class MainFrame extends javax.swing.JFrame {
    
    private final static String newline = "\n";

    private enum LogLevel {

        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
    }
    org.slf4j.Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private URL url = null;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        if(loadCredentialsFromFile()){
            // simulate the pressing of the submit button
            mySubmitButtonActionPerformed(new ActionEvent(new Object(), 1, ""));
        } else {
            myUserNameTextField.setEditable(true);
            myPasswordField.setEditable(true);
            myAuthenticationUrl.setEditable(true);
        }
    }

    private HttpURLConnection connectToServer(String aWebPagePath) {
        HttpURLConnection urlConnection = null;
        try {
            handleMessage("trying to connect to server", LogLevel.DEBUG, false);
            url = new URL(aWebPagePath);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(IApplicationConstants.READ_TIMEOUT);
            urlConnection.setConnectTimeout(IApplicationConstants.CONNECT_TIMEOUT);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            String authString = myUserNameTextField.getText() + ":" + myPasswordField.getText();
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            handleMessage("Base64 encoded auth string: " + authStringEnc, LogLevel.DEBUG, false);
            urlConnection.setRequestProperty("Authorization", "Basic "
                    + authStringEnc);
            urlConnection.connect();
            myProgressBar.setBackground(Color.green);
            myProgressBar.setValue(20);
        } catch (IOException ex) {
            handleError(ex, "error when connecting to server", true);
        }
        return urlConnection;
    }

    private String getResponse(HttpURLConnection urlConnection) {
        String result = null;
        if (null != urlConnection) {
            try {
                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return result;
                }
                String contentType = urlConnection.getContentType();
                handleMessage("The content type is: " + contentType, LogLevel.DEBUG, false);
                handleMessage("The read timeout is: " + urlConnection.getReadTimeout(), LogLevel.DEBUG, false);
                handleMessage("The connection timeout is: " + urlConnection.getConnectTimeout(), LogLevel.DEBUG, false);
                InputStream is = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                int numCharsRead;
                char[] charArray = new char[1024];
                StringBuffer sb = new StringBuffer();
                while ((numCharsRead = isr.read(charArray)) > 0) {
                    sb.append(charArray, 0, numCharsRead);
                }
                result = sb.toString();
                isr.close();
                is.close();
            } catch (IOException ex) {
                handleError(ex, "I/O error while getting content", true);
            }
        }
        return result;
    }

    private String detectIPFromFile(String aFilePath) {
        String detectedIp = "";
        org.w3c.dom.Document dom = fileDom(aFilePath);
        org.w3c.dom.Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("ExternalIp");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                detectedIp = nl.item(i).getTextContent();
                handleMessage("File IP: "
                        + detectedIp, LogLevel.INFO, true);
            }
        }
        return detectedIp;
    }

    private String detectRouterIP(String aPageContent) {
        String detectedIp = "";
        Document doc = Jsoup.parse(aPageContent);
        Elements tds = doc.getElementsByTag("td");
        Iterator<Element> it = tds.iterator();
        while (it.hasNext()) {
            Element td = it.next();
            if (td.toString().contains("IP Address ")) {
                detectedIp = it.next().html();
                handleMessage("Router IP: " + detectedIp, LogLevel.INFO, true);
                break;
            }
        }
        return detectedIp;
    }

    private void handleError(Exception ex, String errorMessage, boolean changeUI) {
        if (errorMessage != null && !errorMessage.equals("")) {
            if (ex != null) {
                logger.error(errorMessage, ex);
            } else {
                logger.error(errorMessage);
            }
        }
        if (changeUI) {
            if (ex != null) {
                myMessagesTextArea.append(ex.toString() + newline);
            }
            myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR);
            myProgressBar.setBackground(Color.red);
        }
    }

    private void handleMessage(String message, LogLevel logLevel, boolean changeUI) {
        switch (logLevel) {
            case DEBUG:
                logger.debug(message);
                break;
            case INFO:
                logger.info(message);
                if(changeUI){
                    myMessagesTextArea.append(message + newline);
                }
                break;
            default:
                break;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        myOverallPanel = new javax.swing.JPanel();
        myAuthenticationPanel = new javax.swing.JPanel();
        myUserNameTextField = new javax.swing.JTextField();
        myPasswordField = new javax.swing.JPasswordField();
        myAuthenticationUrl = new javax.swing.JFormattedTextField();
        mySubmitButton = new javax.swing.JButton();
        myMessagesPanel = new javax.swing.JPanel();
        myJScrollPane = new javax.swing.JScrollPane();
        myMessagesTextArea = new javax.swing.JTextArea();
        myProgressBarPanel = new javax.swing.JPanel();
        myProgressBar = new javax.swing.JProgressBar();
        myDetectedPanels = new javax.swing.JPanel();
        myDetectedRouterPanel = new javax.swing.JPanel();
        myRouterIpTextField = new javax.swing.JTextField();
        myDetectedFilePanel = new javax.swing.JPanel();
        myFileIpTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RouterIP changer application");
        setPreferredSize(new java.awt.Dimension(600, 400));

        myOverallPanel.setLayout(new java.awt.GridBagLayout());

        myAuthenticationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Authentication", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N
        java.awt.GridBagLayout jPanel2Layout = new java.awt.GridBagLayout();
        jPanel2Layout.columnWidths = new int[] {0};
        jPanel2Layout.rowHeights = new int[] {0, 3, 0, 3, 0, 3, 0};
        myAuthenticationPanel.setLayout(jPanel2Layout);

        myUserNameTextField.setEditable(false);
        myUserNameTextField.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        myAuthenticationPanel.add(myUserNameTextField, gridBagConstraints);

        myPasswordField.setEditable(false);
        myPasswordField.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        myAuthenticationPanel.add(myPasswordField, gridBagConstraints);

        myAuthenticationUrl.setEditable(false);
        myAuthenticationUrl.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        myAuthenticationPanel.add(myAuthenticationUrl, gridBagConstraints);

        mySubmitButton.setText("Submit");
        mySubmitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mySubmitButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        myAuthenticationPanel.add(mySubmitButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        myOverallPanel.add(myAuthenticationPanel, gridBagConstraints);

        myMessagesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Messages", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N
        myMessagesPanel.setLayout(new java.awt.GridBagLayout());

        myMessagesTextArea.setColumns(20);
        myMessagesTextArea.setEditable(false);
        myMessagesTextArea.setRows(5);
        myJScrollPane.setViewportView(myMessagesTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        myMessagesPanel.add(myJScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 1.0;
        myOverallPanel.add(myMessagesPanel, gridBagConstraints);

        myProgressBarPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Progress", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N
        myProgressBarPanel.setLayout(new java.awt.GridBagLayout());

        myProgressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.weightx = 1.0;
        myProgressBarPanel.add(myProgressBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BELOW_BASELINE;
        myOverallPanel.add(myProgressBarPanel, gridBagConstraints);

        myDetectedPanels.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detected IP's", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N
        myDetectedPanels.setLayout(new java.awt.GridBagLayout());

        myDetectedRouterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DetectedRouterIP", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N
        myDetectedRouterPanel.setLayout(new java.awt.GridBagLayout());

        myRouterIpTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        myDetectedRouterPanel.add(myRouterIpTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        myDetectedPanels.add(myDetectedRouterPanel, gridBagConstraints);

        myDetectedFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DetectedFileIP", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 10))); // NOI18N
        myDetectedFilePanel.setLayout(new java.awt.GridBagLayout());

        myFileIpTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        myDetectedFilePanel.add(myFileIpTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        myDetectedPanels.add(myDetectedFilePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        myOverallPanel.add(myDetectedPanels, gridBagConstraints);

        getContentPane().add(myOverallPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mySubmitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mySubmitButtonActionPerformed
        // TODO add your handling code here:
        myProgressBar.setValue(IApplicationConstants.MINVALUE_PROGRESSBAR);
        myMessagesTextArea.setText("");
        String result = "";
        String ipDetectedFromRouter = "";
        String ipDetectedFromFile = "";
        // TODO -1: check credential files if exists
        boolean checkCredentialsFile = checkCredentialsFile();
        // TODO 0. save credentials to file
        if(false == checkCredentialsFile){
            saveCredentialsToFile();
        }
        myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR / 6);
        // TODO 1. connect to server
        HttpURLConnection urlConnection = connectToServer(myAuthenticationUrl.getText());
        // TODO 2. get the page content from server
        if (null != urlConnection) {
            result = getResponse(urlConnection);
            myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR / 5);
        } else {
            handleError(null, "Unable to connect to server", true);
        }
        // TODO 3. detect the router ip address
        if (result != null) {
            ipDetectedFromRouter = detectRouterIP(result);
            myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR / 4);
        } else {
            handleError(null, "Unable to get content from server", true);
        }
        // TODO 4. detect DC++ ip address
        ipDetectedFromFile = detectIPFromFile(dcPlusPlusFileName());
        myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR / 3);
        // TODO 5. compare and overwrite if needed
        if (!ipDetectedFromRouter.equalsIgnoreCase("")) {
            handleMessage("IP from router found.", LogLevel.INFO, false);
            myRouterIpTextField.setText(ipDetectedFromRouter);
            myRouterIpTextField.setBackground(Color.green);
            if(!ipDetectedFromFile.equalsIgnoreCase("")){
                myFileIpTextField.setText(ipDetectedFromFile);
                myFileIpTextField.setBackground(Color.green);
                if (!ipDetectedFromRouter.equalsIgnoreCase(ipDetectedFromFile)) {
                    try {
                        myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR / 2);
                        handleMessage("IP from file is not the same ... write the new value.", LogLevel.INFO, false);
                        // write the new value to file
                        org.w3c.dom.Document dom = fileDom(dcPlusPlusFileName());
                        org.w3c.dom.Element docEle = dom.getDocumentElement();
                        NodeList nl = docEle.getElementsByTagName("ExternalIp");

                        nl.item(0).setTextContent(ipDetectedFromRouter);
                        // write the content into xml file
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(dom);
                        StreamResult resultFile = new StreamResult(new File(dcPlusPlusFileName()));
                        transformer.transform(source, resultFile);

                        handleMessage("File saved!", LogLevel.INFO, true);
                        myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR);
                    } catch (TransformerException ex) {
                        handleError(ex, "TransformationException", true);
                    }
                } else {
                    handleMessage("The IP addresses are identical. Nothing to do.", LogLevel.INFO, true);
                    myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR);
                }
            } else {
                handleMessage("IP from file not detected.", LogLevel.INFO, true);
                myFileIpTextField.setText("");
                myFileIpTextField.setBackground(Color.red);
                myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR);
            }
        } else {
            handleMessage("IP from router not detected.", LogLevel.INFO, true);
            myRouterIpTextField.setText("");
            myRouterIpTextField.setBackground(Color.red);
            myProgressBar.setValue(IApplicationConstants.MAXVALUE_PROGRESSBAR);
        }
    }//GEN-LAST:event_mySubmitButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel myAuthenticationPanel;
    private javax.swing.JFormattedTextField myAuthenticationUrl;
    private javax.swing.JPanel myDetectedFilePanel;
    private javax.swing.JPanel myDetectedPanels;
    private javax.swing.JPanel myDetectedRouterPanel;
    private javax.swing.JTextField myFileIpTextField;
    private javax.swing.JScrollPane myJScrollPane;
    private javax.swing.JPanel myMessagesPanel;
    private javax.swing.JTextArea myMessagesTextArea;
    private javax.swing.JPanel myOverallPanel;
    private javax.swing.JPasswordField myPasswordField;
    private javax.swing.JProgressBar myProgressBar;
    private javax.swing.JPanel myProgressBarPanel;
    private javax.swing.JTextField myRouterIpTextField;
    private javax.swing.JButton mySubmitButton;
    private javax.swing.JTextField myUserNameTextField;
    // End of variables declaration//GEN-END:variables

    private void saveCredentialsToFile() {
        Properties props = new Properties();
        props.setProperty(IApplicationConstants.KEY_AUTHENTICATIONURL, myAuthenticationUrl.getText());
        props.setProperty(IApplicationConstants.KEY_USERNAME, myUserNameTextField.getText());
        props.setProperty(IApplicationConstants.KEY_PASSWORD, myPasswordField.getText());

        try {
            File authFile = new File(credentialsFileName());
            FileOutputStream fos = new FileOutputStream(authFile);
            authFile.createNewFile();
            props.store(fos, "-- Credentials -- ");
            fos.close();
        } catch (IOException ex) {
            handleError(ex, "I/O exception occured", false);
        }
    }
    
    private boolean loadCredentialsFromFile() {
        boolean credentialsLoaded = false;
        Properties props = new Properties();

        try {
            File credentialsFile = new File(credentialsFileName());
            if(credentialsFile.exists()){
                FileInputStream fis = new FileInputStream(credentialsFile);
                props.load(fis);
                if(props.size() == 3){
                    credentialsLoaded = true;
                    // the authentication file contains all the required info
                    myUserNameTextField.setText(props.getProperty(IApplicationConstants.KEY_USERNAME));
                    myUserNameTextField.setVisible(false);
                    myPasswordField.setText(props.getProperty(IApplicationConstants.KEY_PASSWORD));
                    myPasswordField.setVisible(false);
                    myAuthenticationUrl.setText(props.getProperty(IApplicationConstants.KEY_AUTHENTICATIONURL));
                    myAuthenticationUrl.setVisible(false);
                }
                fis.close();
            }
        } catch (IOException ex) {
            handleError(ex, "I/O exception occured", false);
        }
        return credentialsLoaded;
    }
    
    private String credentialsFileName() {
        StringBuilder sbl = new StringBuilder();
        sbl.append(System.getProperty("user.home"));
        sbl.append(System.getProperty("file.separator"));
        sbl.append(IApplicationConstants.AUTHENTICATIONFILE);
        String fileName = sbl.toString();
        return fileName;
    }

    private String dcPlusPlusFileName() {
        StringBuilder sbl = new StringBuilder();
        sbl.append(System.getProperty("user.home"));
        sbl.append(System.getProperty("file.separator"));
        sbl.append("Application Data");
        sbl.append(System.getProperty("file.separator"));
        sbl.append("DC++");
        sbl.append(System.getProperty("file.separator"));
        sbl.append(IApplicationConstants.DCPPCONFIGURATIONFILE);
        String fileName = sbl.toString();
        return fileName;
    }

    private org.w3c.dom.Document fileDom(String aFilePath) {
        org.w3c.dom.Document dom = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(aFilePath);
        } catch (SAXException ex) {
            handleError(ex, "", true);
        } catch (IOException ex) {
            handleError(ex, "", true);
        } catch (ParserConfigurationException ex) {
            handleError(ex, "", true);
        }
        return dom;
    }
    
private boolean checkCredentialsFile() {
        boolean isFileOK = false;
        File credentialsFile = new File(credentialsFileName());
        if(credentialsFile.exists()){
            FileInputStream fis = null;
            try {
                Properties properties = new Properties();
                fis = new FileInputStream(credentialsFile);
                properties.load(fis);
                if(!properties.getProperty(IApplicationConstants.KEY_USERNAME).equalsIgnoreCase("") && 
                   !properties.getProperty(IApplicationConstants.KEY_PASSWORD).equalsIgnoreCase("") &&
                   !properties.getProperty(IApplicationConstants.KEY_AUTHENTICATIONURL).equalsIgnoreCase("")){
                    handleMessage("Credentials file OK", LogLevel.INFO, true);
                    isFileOK = true;
                }
            } catch (IOException ex) {
                handleError(ex, "I/O error while checking credentials file", true);
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    handleError(ex, "I/O error while closing input stream to credentials file", true);
                }
            }
        }
        return isFileOK;
    }
}
