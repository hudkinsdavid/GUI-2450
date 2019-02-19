package jnotepad;

/**
 * David Hudkins II
 * Project #5
 * Due : 07 December 2018
 * CS 2450 F-01-18
 * 
 * Description
 * ===========
 * This is a text editor similar to microsoft's notepad. It displays 
 * a GUI that accepts user text and can read and write files. It utilizes 
 * a JFontChooser class to handle font related changes made to the program.
 * It offers most of the basic functionality of notepad, however it omits
 * the printing functions, the status bar, replace, goto, and view help. 
 * */

import javax.swing.*;
import java.awt.Font;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.undo.*;

public class JNotepad implements ActionListener {
    //Declare variables
    private JFrame      window;
    private JScrollPane textScroll;
    private JMenuBar    topMenus;    
    private JMenu       fontColor;    
    private JMenu       fileMenu;
    private JMenu       format;
    private JMenu       help;
    private JMenu       editMenu;
    private JMenuItem   newMenuItem;
    private JMenuItem   saveIt;
    private JMenuItem   saveAs;
    private JMenuItem   pageSetup;
    private JMenuItem   undo;
    private JMenuItem   delete;
    private JMenuItem   find;
    private JMenuItem   print;
    private JMenuItem   open;
    private JMenuItem   exit;
    private JMenuItem   font;
    private JMenuItem   foregroundMenu;
    private JMenuItem   backgroundMenu;
    private JMenuItem   cutIt;
    private JMenuItem   copyIt;
    private JMenuItem   pasteIt;
    private JMenuItem   cut;
    private JMenuItem   copy;
    private JMenuItem   paste;
    private JMenuItem   about;
    private JMenuItem   wrapIt;
    private JMenuItem   selectItAll;
    private JMenuItem   findIt;
    private JMenuItem   findNext;
    private JMenuItem   tDateItm;
    private JDialog     findDialog;
    private Font        fontSel;
    private Color       foreground;
    private Color       background;
    private ImageIcon   prgmIcon;
    private JTextArea   displayText;
    private JPopupMenu  popUp;
    private String      fileText;
    private String      aboutMsg;
    private String      currentFile;
    private String      unchanged;
    private String      change;
    private String      found;
    private String      highlightThis;
    private Highlighter shiny;
    private HighlightPainter painty;
    private JFontChooser     myFont;
    private JFileChooser     myFile;
    private FileNameExtensionFilter sourceOnly;
    private FileNameExtensionFilter textOnly;
    private BufferedReader          fileIn;
    private BufferedWriter          fileOut;
    private SimpleDateFormat        tDateFormat;
    private Calendar                todayDate;
    private Date                    currTime;
    private static int              start;
    private static int              end;
    private boolean                 findActive;
    private UndoManager             undoIt;
    
    JNotepad(){
        //Create a frame for the main GUI and set up the size and location
        window = new JFrame("JNotepad");
        window.setSize(800,600);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        //Set the program icon
        prgmIcon = new ImageIcon(this.getClass().getResource("JNotepad.png"));
        window.setIconImage(prgmIcon.getImage());
        
        //Create the default font
        fontSel  = new Font("Courier New",Font.PLAIN,12);
        //Create the main area of the notepad and make it scrollable
        displayText = new JTextArea();
        displayText.setFont(new Font("Courier New",Font.PLAIN,12));
        textScroll  = new JScrollPane(displayText);
        findDialog  = new JDialog(window,"Enter a word to find:");

        //Create the file navigator with java and txt filters
        myFile     = new JFileChooser();
        sourceOnly = new FileNameExtensionFilter("JAVA Source Files","java");
        textOnly   = new FileNameExtensionFilter("TXT Files","txt");
        myFile.setFileFilter(sourceOnly);
        myFile.addChoosableFileFilter(textOnly);
        unchanged  = displayText.getText();
        
        //Create and add the swing manager for undo property
        undoIt = new UndoManager();
        displayText.getDocument().addUndoableEditListener(undoIt);
        
        //Create the jmenubar
        topMenus    = new JMenuBar();
        //Create a menu item for new file
        newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(this);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        
        //Create a menu item for the save feature
        saveIt = new JMenuItem("Save");
        saveIt.addActionListener(this);
        saveIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveIt.setEnabled(false);
        
        //Create a menu item for the save as feature
        saveAs = new JMenuItem("Save As...");
        saveAs.addActionListener(this);
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        
        //Create a menu item for the page setup
        pageSetup = new JMenuItem("Page Setup...");
        pageSetup.addActionListener(this);
        pageSetup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        pageSetup.setEnabled(false);
        
        //Create a menu item for the undo function
        undo = new JMenuItem("Undo");
        undo.addActionListener(this);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        
        //Create a menu item for the delete function
        delete = new JMenuItem("Delete");
        delete.addActionListener(this);
        delete.setMnemonic(KeyEvent.VK_L);
        
        //Create a menu item for the find function
        findIt = new JMenuItem("Find");
        findIt.addActionListener(this);
        findIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        
        //Create a menu for the edit function
        editMenu = new JMenu("Edit");
        editMenu.addActionListener(this);
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        //Creates menutimes for the cut copy and paste features
        cutIt   = new JMenuItem(new DefaultEditorKit.CutAction());
        copyIt  = new JMenuItem(new DefaultEditorKit.CopyAction());
        pasteIt = new JMenuItem(new DefaultEditorKit.PasteAction());
        
        //Set the text of the cut/copy/paste menu items and their mnemonics
        pasteIt.setText("Paste");
        cutIt.setText("Cut");
        copyIt.setText("Copy");
        pasteIt.setMnemonic(KeyEvent.VK_P);
        pasteIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,KeyEvent.CTRL_DOWN_MASK));
        cutIt.setMnemonic(KeyEvent.VK_T);
        cutIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,KeyEvent.CTRL_DOWN_MASK));
        copyIt.setMnemonic(KeyEvent.VK_C);
        copyIt.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_DOWN_MASK));
        
        //Create a menu item for the print function
        print = new JMenuItem("Print...");
        print.addActionListener(this);
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        print.setEnabled(false);
        
        //Creates a menu for file 
        fileMenu = new JMenu("File");
        fileMenu.addActionListener(this);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        //Creates a menu for format
        format = new JMenu("Format");
        format.addActionListener(this);
        format.setMnemonic(KeyEvent.VK_O);
        
        //Creates a help menu
        help = new JMenu("Help");
        help.addActionListener(this);
        help.setMnemonic(KeyEvent.VK_H);
        
        //Creates a menu for color
        fontColor = new JMenu("Color");
        fontColor.addActionListener(this);
        fontColor.setMnemonic(KeyEvent.VK_C);
        
        //Creates an open menu item that creates a dialog
        open = new JMenuItem("Open...");
        open.setMnemonic(KeyEvent.VK_O);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        open.addActionListener(this);
        
        //Creates a menu item for the exit function
        exit = new JMenuItem("Exit...");
        exit.setMnemonic(KeyEvent.VK_E);
        exit.addActionListener(this);
        
        //Creates a menu item for the font changing functionality creates dialog
        font = new JMenuItem("Font...");
        font.addActionListener(this);
        font.setMnemonic(KeyEvent.VK_F);
        
        //Creates a menu item for the about dialog for the program
        about = new JMenuItem("About");
        about.addActionListener(this);
        about.setMnemonic(KeyEvent.VK_A);
        
        //Creates a menu item for setting the foreground color
        foregroundMenu = new JMenuItem("Set Foreground...");
        foregroundMenu.addActionListener(this);
        foregroundMenu.setMnemonic(KeyEvent.VK_F);
        foregroundMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK| ActionEvent.ALT_MASK));
        
        //Creates a menu item for setting the background color
        backgroundMenu = new JMenuItem("Set Background...");
        backgroundMenu.addActionListener(this);
        backgroundMenu.setMnemonic(KeyEvent.VK_B);

        //Creates a menu item for the word wrap function
        wrapIt = new JMenuItem("Word Wrap");
        wrapIt.addActionListener(this);
        wrapIt.setMnemonic(KeyEvent.VK_W);
        
        //Creates a menu item for the select all function
        selectItAll = new JMenuItem("Select All");
        selectItAll.addActionListener(this);
        selectItAll.setMnemonic(KeyEvent.VK_A);
        selectItAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_MASK));
        
        //Creates a menu item for the time/date function
        tDateItm = new JMenuItem("Time/Date");
        tDateItm.addActionListener(this);
        tDateItm.setMnemonic(KeyEvent.VK_F5);
        
        //Creates a menu item for the find next function
        findNext = new JMenuItem("Find Next");
        findNext.addActionListener(this);
        findNext.setMnemonic(KeyEvent.VK_N);
        
        //Set the about message
        aboutMsg   = "(C) David Hudkins II \nJNotepad \nGUI 2450";
        //Create an instance of the jfontchooser class        
        myFont = new JFontChooser(window);

        //Adds the components to the edit menu
        editMenu.add(undo);
        editMenu.addSeparator();
        editMenu.add(cutIt);
        editMenu.add(copyIt);
        editMenu.add(pasteIt);
        editMenu.add(delete);
        editMenu.addSeparator();
        editMenu.add(findIt);
        editMenu.add(findNext);
        editMenu.addSeparator();
        editMenu.add(selectItAll);
        editMenu.add(tDateItm);
        
        //Adds the components to the color change menu
        fontColor.add(foregroundMenu);
        fontColor.add(backgroundMenu);
        
        //Adds the components to the file menu
        fileMenu.add(newMenuItem);
        fileMenu.add(open);
        fileMenu.add(saveIt);
        fileMenu.add(saveAs);
        fileMenu.addSeparator();
        fileMenu.add(pageSetup);
        fileMenu.add(print);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        
        //Adds the components to the format menu
        format.add(wrapIt);
        format.add(font);
        format.addSeparator();
        format.add(fontColor);
        
        //Adds the components to the help menu
        help.add(about);
        
        //Creates a popup menu for the cut copy and paste functions
        popUp = new JPopupMenu();
        cut   = new JMenuItem(new DefaultEditorKit.CutAction());
        copy  = new JMenuItem(new DefaultEditorKit.CopyAction());
        paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        //Set the text of the popup menu items and their mnemonics
        paste.setText("Paste");
        cut.setText("Cut");
        copy.setText("Copy");
        paste.setMnemonic(KeyEvent.VK_P);
        cut.setMnemonic(KeyEvent.VK_T);
        copy.setMnemonic(KeyEvent.VK_C);
        //Add components to the popup menu
        popUp.add(cut);
        popUp.add(copy);
        popUp.add(paste);
        
        //Handle the mouse listening actions for the popup menu placement
        displayText.addMouseListener(new MouseListener(){
            @Override
            public void mouseExited(MouseEvent me){}
            @Override
            public void mouseEntered(MouseEvent me){}
            //Show the menu at the mouse release location
            @Override
            public void mouseReleased(MouseEvent me){
                if (me.isPopupTrigger()) {
                    popUp.show(me.getComponent(), me.getX(), me.getY());
                }
            }
            //Show the menu at the mouse press location
            @Override
            public void mousePressed(MouseEvent me){
                if (me.isPopupTrigger()) {
                    popUp.show(me.getComponent(), me.getX(), me.getY());
                }
            }
            @Override
            public void mouseClicked(MouseEvent me){}
        });
        
        //Adds the Menus to the menu bar
        topMenus.add(fileMenu);
        topMenus.add(editMenu);
        topMenus.add(format);
        topMenus.add(help);
        
        //Set the menu bar and add the text area to the frame
        window.setJMenuBar(topMenus);
        window.add(textScroll);
                
        //Confirm exit if the user clicks on the standard 'x' in the top corner
        window.addWindowListener(new WindowAdapter(){
            //Override the function for window closing events
            @Override
            public void windowClosing(WindowEvent we){
                change = displayText.getText();
                if(!change.equals(unchanged)){
                    int i = JOptionPane.showConfirmDialog(window, "Would you like to save before exiting", 
                            "Confirm Exit" ,JOptionPane.YES_NO_CANCEL_OPTION);
                    //Close window if user chooses
                    switch (i) {
                        case JOptionPane.YES_OPTION:
                            myFile.showSaveDialog(window);
                            System.exit(0);
                            break;
                        case JOptionPane.NO_OPTION:
                            System.exit(0);
                            break;
                        case JOptionPane.CANCEL_OPTION:
                            break;
                        default:
                            break;
                    }
                }else{
                    System.exit(0);
                }
            }
        });
        
        //Set the frame to visible
        window.setVisible(true);
    }
    
    //EXECUTE PROGRAM!
    public static void main(String args[]) throws Exception {
        //Invoke the class on an AWT thread
        SwingUtilities.invokeLater(() -> {
            new JNotepad();
        });
    }

    //Handler for all action events generated in this class
    @Override
    public void actionPerformed(ActionEvent ae) {
        switch(ae.getActionCommand()){
            //Handler for the open menuItem
            case "Open...":
                int fileStatus = myFile.showOpenDialog(window);
                if(fileStatus == JFileChooser.APPROVE_OPTION){
                    currentFile = myFile.getSelectedFile().getAbsolutePath();  
                try {
                    displayText.setText(null);
                    InputStream fis = new FileInputStream(currentFile);
                    fileIn          = new BufferedReader(new InputStreamReader(fis));
                    while(fileIn.readLine()!=null){
                        displayText.append(fileIn.readLine());
                        displayText.append("\n");
                    }
                    saveIt.setEnabled(true);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(JNotepad.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(JNotepad.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
                unchanged = displayText.getText();
                break;
                
            case "Exit...":    //Confirm exit through dialog
                change = displayText.getText();
                if(!change.equals(unchanged)){
                    int i = JOptionPane.showConfirmDialog(window, "Would you like to save before exiting", 
                            "Confirm Exit" ,JOptionPane.YES_NO_CANCEL_OPTION);
                    //Close window if user chooses
                    switch (i) {
                        case JOptionPane.YES_OPTION:
                            myFile.showSaveDialog(window);
                            System.exit(0);
                            break;
                        case JOptionPane.NO_OPTION:
                            System.exit(0);
                            break;
                        case JOptionPane.CANCEL_OPTION:
                            break;
                        default:
                            break;
                    }
                }else{
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
                
            case "New":
                change = displayText.getText();
                if(!change.equals(unchanged)){
                    int confirm = JOptionPane.showConfirmDialog(window, "Would you like to save changes?");
                    if(confirm == JOptionPane.OK_OPTION){
                        int temp = myFile.showSaveDialog(window);
                        
                        displayText.setText(null);
                        currentFile = "";
                    }
                }
                break;
                
            case "Delete":
                Robot r;
                try {
                    r = new Robot();
                    int key = KeyEvent.VK_DELETE;
                    r.keyPress(key);

                } catch (AWTException ex) {
                    Logger.getLogger(JNotepad.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

            case "Save":
                change = displayText.getText();
                if(!change.equals(unchanged)){
                    try{
                        fileOut = new BufferedWriter(new FileWriter(new File(currentFile)));
                        displayText.write(fileOut);
                    }catch(IOException io){

                    }
                }
                break;
                
            case "Save As...":
                try{
                    myFile.showSaveDialog(window);
                    fileOut = new BufferedWriter(new FileWriter(myFile.getSelectedFile()));
                    displayText.write(fileOut);
                    currentFile = myFile.getSelectedFile().getAbsolutePath();
                    saveIt.setEnabled(true);
                }catch(IOException io){
                    
                }
                break;
                
            case "Select All":
                displayText.selectAll();
                break;
                
            case "Time/Date":
                //Pulls the time and date from the classes and appends to textarea
                tDateFormat = new SimpleDateFormat(" h:mm a MM/d/yyyy");
                todayDate   = Calendar.getInstance();
                currTime    = todayDate.getTime();
                displayText.append(tDateFormat.format(currTime));
                break;
                
            case "Undo":
                //Removes the last action in notepad
                undoIt.undo();
                break;
                
            case "Find":
                //Find the given word after dialog is shown
                findActive = true;
                change = displayText.getText();
                JTextField jt = new JTextField();
                JButton okay  = new JButton("Ok");
                //Actionlistener for the button implemented
                okay.addActionListener((ActionEvent ae1) -> {
                    found = jt.getText();
                    highlightThis = displayText.getText();
                    start = highlightThis.indexOf(found);
                    end   = start + found.length();
                    
                    try{
                        shiny.addHighlight(start, end, painty);
                    }catch(BadLocationException baddy){
                        System.out.println("Location could not be found");
                    }
                });
                //Listens for close action to remove highlights
                findDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowDeactivated(WindowEvent e) {
                        shiny.removeAllHighlights();
                        findActive = false;
                    }
                });
                //Add components to the dialog 
                findDialog.add(okay,BorderLayout.SOUTH);
                findDialog.setLocationRelativeTo(window);
                findDialog.setSize(200,100);
                findDialog.add(jt);
                //Creates highlighters to find words in textarea 
                shiny  = displayText.getHighlighter();
                painty = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
                findDialog.setVisible(true);   
                break;
                
            case "Find Next":
                start = highlightThis.indexOf(found,end);
                end   = start + found.length();
                try{
                    shiny.addHighlight(start, end, painty);
                }catch(BadLocationException baddy){
                    System.out.println("Location could not be found");
                }
                break;
                
            case "Word Wrap":
                if(displayText.getLineWrap()){
                    displayText.setLineWrap(false);
                }else{
                    displayText.setLineWrap(true);
                }
                break;
                
            default:
                break;
        }
    }
    
}