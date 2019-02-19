package mydialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * David Hudkins II
 * Project #5
 * Due : 07 December 2018
 * CS 2450 F-01-18
 * 
 * Description
 * ===========
 * This is an implementation of a text editor that has 
 * the capability to read and write text files with the 
 * extension java or txt. Implements a JFontChooser class
 * to allow font choice. Does not implement any print
 * functions. Uses an Undo function.
 * */

public class MyDialogs implements ActionListener {
    //Declare Variables
    private JFrame    window;
    private JLabel    displayText;
    private JMenuBar  topMenu;
    private JMenu     prgmMenu;
    private JMenu     fileMenu;
    private JMenu     formatMenu;
    private JMenu     helpMenu;
    private JMenu     colorMenu;
    private JMenuItem openItem;
    private JMenuItem exitItem;
    private JMenuItem fontItem;
    private JMenuItem foregroundItem;
    private JMenuItem backgroundItem;
    private JMenuItem aboutItem;
    private JOptionPane   aboutPane;
    private JOptionPane   exitPane;
    private JFileChooser  myFile;
    private JColorChooser myColor;
    private String        aboutMsg;
    private Color         background;
    private Color         foreground;
    private FileNameExtensionFilter sourceOnly;
    private Font          fontSel;
    private JFontChooser  myFont;
    private ImageIcon     prgmIcon;
    
    //Constructor for GUI layout and execution
    MyDialogs(){
        //Create and format the frame
        window = new JFrame("MyDialogs");
        window.setSize(400,200);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
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
        
        //Center the frame relative to the current screen size
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width/2 - window.getSize().width/2, 
        dim.height/2 - window.getSize().height/2);
        
        //Initialize the JLabel that will display font changes
        displayText = new JLabel("D.Hudkins");
        displayText.setFont(new Font("Courier New",Font.PLAIN,12));
        displayText.setVerticalAlignment(SwingConstants.TOP);
        displayText.setHorizontalAlignment(SwingConstants.CENTER);
        
        //Create all of the menus and set their mnemonics/accelerators
        topMenu    = new JMenuBar(); 
        prgmMenu   = new JMenu("Menus");
        fileMenu   = new JMenu("File");
        formatMenu = new JMenu("Format");
        helpMenu   = new JMenu("Help");
        colorMenu  = new JMenu("Color");
        openItem   = new JMenuItem("Open",KeyEvent.VK_O);
        exitItem   = new JMenuItem("Exit",KeyEvent.VK_X);
        fontItem   = new JMenuItem("Font...",KeyEvent.VK_F);
        foregroundItem = new JMenuItem("Set Foreground...",KeyEvent.VK_F);
        backgroundItem = new JMenuItem("Set Background...",KeyEvent.VK_B);
        aboutItem  = new JMenuItem("About",KeyEvent.VK_A);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        formatMenu.setMnemonic(KeyEvent.VK_O);
        helpMenu.setMnemonic(KeyEvent.VK_H);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
        foregroundItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F,ActionEvent.CTRL_MASK+ActionEvent.ALT_MASK));
        
        //Add actionListeners to all of the menuItems that require it
        openItem.addActionListener(this);
        exitItem.addActionListener(this);
        fontItem.addActionListener(this);
        foregroundItem.addActionListener(this);
        backgroundItem.addActionListener(this);
        aboutItem.addActionListener(this);
        
        //Obtain all font names in swing and initialize dialogs
        myFont     = new JFontChooser(window);
        myColor    = new JColorChooser();
        myFile     = new JFileChooser();
        sourceOnly = new FileNameExtensionFilter("JAVA Source Files","java");
        myFile.setFileFilter(sourceOnly);
        aboutMsg   = "(C) David Hudkins II \nMyDialogs \nGUI 2450";
        prgmIcon   = new ImageIcon(this.getClass().getResource("MyDialogs.png"));
        
        //Add all of the menu items into their respective menus
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        colorMenu.add(foregroundItem);
        colorMenu.add(backgroundItem);
        formatMenu.add(fontItem);
        formatMenu.addSeparator();
        formatMenu.add(colorMenu);
        helpMenu.add(aboutItem);
        
        //Add the menus into the primary menu and attach it to the menu bar
        topMenu.add(fileMenu);
        topMenu.add(formatMenu);
        topMenu.add(helpMenu);
        
        //Add all of the components to the frame
        window.setJMenuBar(topMenu);
        window.add(displayText);
        window.setIconImage(prgmIcon.getImage());
        
        //Show the frame
        window.setVisible(true);
    }
    
    //Listens for action events and handles them accordingly
    @Override
    public void actionPerformed(ActionEvent ae){
        switch(ae.getActionCommand()){
            case "Open":    //Display a file exploring menu that show .java files
                int fileStatus = myFile.showOpenDialog(window);
                if(fileStatus == JFileChooser.APPROVE_OPTION){
                    displayText.setText(myFile.getSelectedFile().getAbsolutePath());
                }
                break;
                
            case "Exit":    //Confirm exit through dialog
                int ok = JOptionPane.showConfirmDialog(window,"Are you sure?",
                        "Confirm Exit",JOptionPane.OK_CANCEL_OPTION);
                if(ok == JOptionPane.OK_OPTION){
                    System.exit(0);
                }
               break;
               
            case "Font...": //Displays JFontChooser dialog
                //Make the dialog modal and make visible
                myFont.setModal(true);
                myFont.setVisible(true);
                //Get the font selection and set the display text to chosen font
                fontSel = myFont.getFontAttributes(fontSel);
                displayText.setFont(fontSel);
                break;
                
            case "Set Foreground...":   //Show colorchooser for foreground
                foreground = JColorChooser.showDialog(window,
                        "Choose the foreground Color:", Color.BLACK);
                if(foreground != null){
                    displayText.setForeground(foreground);
                }
                break;
                
            case "Set Background...":   //Show colorchooser for background
                background = JColorChooser.showDialog(window, 
                        "Choose the background color:", Color.WHITE);
                if(background != null){
                    window.getContentPane().setBackground(background);
                }
                break;
                
            case "About":               //Display about message for the program
                JOptionPane.showMessageDialog(window, aboutMsg, "About",0,prgmIcon);
                break;
                
            default:
                break;
        }
    }
    
    //EXECUTE PROGRAM!
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new MyDialogs();
    });
    }
}
