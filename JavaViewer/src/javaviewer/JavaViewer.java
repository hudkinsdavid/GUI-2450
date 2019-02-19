package javaviewer;

//System Libraries
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * David Hudkins II
 * Homework: 4
 * Due : 03 December 2018
 * CS 2450 F-01-18
 * 
 * Description
 * ===========
 * This class creates a frame that implements a menu for the purpose of opening
 * files and displaying java files
 * */

//Primary Class
public class JavaViewer implements ActionListener{
    //Declare Variables
    private JFrame       window;
    private JTextArea    myText;
    private JScrollPane  textScroll;
    private JPopupMenu   popMen;
    private JMenuBar     topMenu;
    private JMenu        fileMenu;
    private JMenu        helpMenu;
    private JMenuItem    openIt;
    private JMenuItem    exitIt;
    private JMenuItem    aboutIt;
    private JMenuItem    copyIt;
    private JMenuItem    pasteIt;
    private JMenuItem    cutIt;
    private ImageIcon    prgmImg;
    private String       aboutMsg;
    private String       fileText;
    private Font         myFont;
    private String       clipText;
    private Clipboard    clip;
    private StringSelection         clipSelection;
    private BufferedReader          buffR;
    private FileNameExtensionFilter javaOnly;
    private JTreeFileChooser        jTFChooser;
    
    //Class Constructor
    JavaViewer(){
        //Create frame and format it appropriately
        window = new JFrame();
        window.setSize(600,600);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Confirm exit if the user clicks on the standard 'x' in the top corner
        window.addWindowListener(new WindowAdapter(){
            //Override the function for window closing events
            @Override
            public void windowClosing(WindowEvent we){
                //Close window if user chooses
                if(JOptionPane.showConfirmDialog(window, "Are you sure you want to exit?", 
                        "Confirm Exit" ,JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                    window.setVisible(false);
                    window.dispose();
                }
            }
        });
        
        //Create the text area and attach it to a scroll pane
        myText     = new JTextArea();
        myText.setBackground(Color.BLUE);
        myText.setForeground(Color.WHITE);
        myFont     = new Font("Courier New",Font.PLAIN,12);
        myText.setFont(myFont);
        textScroll = new JScrollPane(myText);
        
        //Create the popup menu and its items
        popMen     = new JPopupMenu();
        copyIt     = new JMenuItem("Copy",KeyEvent.VK_C);
        pasteIt    = new JMenuItem("Paste",KeyEvent.VK_P);
        cutIt      = new JMenuItem("Cut",KeyEvent.VK_T);
        
        //Add action listeners to the menuitems in the popup menu
        copyIt.addActionListener(this);
        pasteIt.addActionListener(this);
        cutIt.addActionListener(this);
        
        //Add the items to the popup menu
        popMen.add(copyIt);
        popMen.add(pasteIt);
        popMen.add(cutIt);
        
        //Create a listener for right-clicks on the GUI
        myText.addMouseListener(new MouseListener(){
            @Override
            public void mouseExited(MouseEvent me){}
            @Override
            public void mouseEntered(MouseEvent me){}
            //Show the popup menu when the mouse is released in the gui
            @Override
            public void mouseReleased(MouseEvent me){
                if (me.isPopupTrigger()) {
                    popMen.show(me.getComponent(), me.getX(), me.getY());
                }
            }
            //Show the popup menu when the mouse is pressed in the gui
            @Override
            public void mousePressed(MouseEvent me){
                if (me.isPopupTrigger()) {
                    popMen.show(me.getComponent(), me.getX(), me.getY());
                }
            }
            @Override
            public void mouseClicked(MouseEvent me){}
        });
        
        //Configure and initialize the clipboard
        clip = Toolkit.getDefaultToolkit().getSystemClipboard();

        //Create a JTreeFileChooser
        jTFChooser = new JTreeFileChooser();
        
        //Set the program icon
        prgmImg    = new ImageIcon(this.getClass().getResource("JavaViewer.png"));
        
        //Store the about message for the program in a string
        aboutMsg   = "(C) David Hudkins II \nJavaViewer \nGUI 2450";
                
        //Create menus and set their mnemonics accordingly
        topMenu  = new JMenuBar();
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        openIt   = new JMenuItem("Open...",KeyEvent.VK_O);
        openIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,KeyEvent.CTRL_MASK));
        exitIt   = new JMenuItem("Exit",KeyEvent.VK_X);
        aboutIt  = new JMenuItem("About",KeyEvent.VK_A);
        
        //Add action listeners to the menu items
        openIt.addActionListener(this);
        exitIt.addActionListener(this);
        aboutIt.addActionListener(this);
        
        
        //Add all of the menus to the menubar
        fileMenu.add(openIt);
        fileMenu.addSeparator();
        fileMenu.add(exitIt);
        helpMenu.add(aboutIt);
        topMenu.add(fileMenu);
        topMenu.add(helpMenu);
        
        //Configure and add components to the frame and set it to visible
        window.setJMenuBar(topMenu);
        window.setIconImage(prgmImg.getImage());
        window.add(textScroll);
        window.setVisible(true);
    }
    
    //Method that handles all action events generated by the class
    @Override
    public void actionPerformed(ActionEvent ae){
        switch(ae.getActionCommand()){
            //Handler for the open menuItem
            case "Open...":
                //Get enum for proper file type and execute if approved
                boolean fileApproved = jTFChooser.showDialog(window);
                if(fileApproved){
                    //Create a buffered reader that examines the selected file
                    try{
                        buffR = new BufferedReader(new FileReader(jTFChooser.getSelectedFile().getPath()));
                        fileText = null;
                        //Read and append every line of text in the file
                        while((fileText = buffR.readLine()) != null){
                            myText.append(fileText);
                            myText.append("\n");
                        }
                    }catch(IOException e){
                        System.out.println("File reading error");
                    }
                }
                break;
                
            //Handler for the exit menuItem
            case "Exit":
                //Show dialog to Exit if user chooses yes ONLY 
                int sure = JOptionPane.showConfirmDialog(window, "Are you sure?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if(sure == JOptionPane.YES_OPTION){
                    System.exit(0);
                }
                break;
                
            //Handler for the about menuItem
            case "About":
                //Display the about message for the program with the program icon
                JOptionPane.showMessageDialog(window, aboutMsg, "About", 0, prgmImg);
                break;
                
            //Handler for the copy popup item
            case "Copy":
                //Set the selected string as a parameter to pass to clipboard
                clipSelection = new StringSelection(myText.getSelectedText());
                clip.setContents(clipSelection, clipSelection);
                break;
                
            //Handler for the paste popup item
            case "Paste":
                //Append the string stored in the clipboard to the text area
                try{
                    myText.append((String)clip.getData(DataFlavor.stringFlavor));
                }catch(UnsupportedFlavorException fe){
                    System.out.println("UnsupportedFlavorException occured");
                } catch (IOException ex) {
                    Logger.getLogger(JavaViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
                
            //Handler for the cut popup item
            case "Cut":
                //Only execute if text is selected to prevent null exception
                if(myText.getSelectedText() != null){
                    //Set the selected string as a parameter to be passed to clipboard
                    clipSelection = new StringSelection(myText.getSelectedText());
                    clip.setContents(clipSelection, clipSelection);
                    //Delete selected text
                    myText.setText(myText.getText().replace(myText.getSelectedText(),""));
                }
                break;
                
            //Default case for the switch statement
            default:
                System.out.println("Error occured when handling an event");
                break;
        }
    }
    
    //EXECUTE PROGRAM
    public static void main(String[] args) {
        //Invoke the class instance on an AWT event thread
        SwingUtilities.invokeLater(()->{
            new JavaViewer();
        });
    }    
}
