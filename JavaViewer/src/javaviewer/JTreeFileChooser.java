package javaviewer;

//System Libraries
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * David Hudkins II
 * Homework: 4
 * Due : 03 December 2018
 * CS 2450 F-01-18
 * 
 * Description
 * ===========
 * This is a class that creates a dialog box, showing a File navigator of the 
 * current user directory. Returns boolean depending on whether or not the file
 * chosen during dialog was a .java extension or not
 * */

//Class that creates a JTree file navigation of the current directory
public class JTreeFileChooser extends JDialog{
    //Declare Variables
    private DefaultMutableTreeNode  rootNode;
    private DefaultMutableTreeNode  child;
    private DefaultTreeModel        fsModel;
    private File                    rootPath;
    private TreeFile                selectedFile;    
    private File[]                  allFiles;
    private JTree                   fileTree;
    private Boolean                 isJava;
    private JScrollPane             treeScroll;
    private JButton                 okBtn;
    private JButton                 canclBtn;
    
    //Constructor
    JTreeFileChooser(){
        //Set the size of the dialog box
        this.setSize(400,600);
        
        //Specify the root node and the associated file
        rootNode = new DefaultMutableTreeNode(System.getProperty("user.dir"));
        rootPath = new File(System.getProperty("user.dir"));
        
        //Recursively create children of the root node creating a tree
        createChildNodes(rootPath, rootNode);
        //Create a tree model with the given tree starting with rootNode
        fsModel  = new DefaultTreeModel(rootNode);
        fileTree = new JTree(fsModel);
        fileTree.setShowsRootHandles(true);     //Allow file expansion
        
        //Listnener that determines if the selected file is a java file
        fileTree.addTreeSelectionListener(new TreeSelectionListener(){
            @Override
            public void valueChanged(TreeSelectionEvent tse){
                //Create a variable to save the selected node
                DefaultMutableTreeNode selectedNode = 
                        (DefaultMutableTreeNode)fileTree.getLastSelectedPathComponent();
                //Mutate the selected node into a file and determine if it is java
                selectedFile = (TreeFile)selectedNode.getUserObject();
                isJava = selectedFile.getName().contains(".java");
            }
        });
        //Attach the JTree to a scrollpane and create the ok button
        treeScroll = new JScrollPane(fileTree);
        okBtn      = new JButton("Ok");
        okBtn.addActionListener((ActionEvent ae) -> {
            this.setVisible(false);
            this.dispose();
            if(!this.selectedFile.getName().contains(".java")){
                System.out.println();
            }
        });
        
        //Add components to the dialog box
        this.add(treeScroll);
        this.add(okBtn,BorderLayout.SOUTH);
    }
    
    //Method to display the modal dialog for the file navigator
    public boolean showDialog(JFrame parent){
        //Initialize the false return flag
        isJava = false;
        //Show the dialog box relative to the parent frame
        this.setLocationRelativeTo(parent);
        this.setModal(true);
        this.setVisible(true);
        //If the file is not a java file, print the name of the file to the console
        if(!isJava && this.selectedFile.getName() != null){
            System.out.println(this.selectedFile.fileNode.getAbsoluteFile());
        }
        return isJava;      //Returns if the file is a java file
    }
    
    /* Method to create child nodes of parent receiving parameters of the file
    path 'f', and (initially) the root node 'parent'. Executes recursively to find
    nested children
    */
    public void createChildNodes(File f, DefaultMutableTreeNode parent){
        //Create an array of all of the files along the path of a given root file
        File[] treeFiles = f.listFiles();
        
        if(treeFiles == null){
            return;     //Exit recursion
        } 
        
        //Enhanced for loop to create children of the respective parent tree node
        for (File treeFile : treeFiles) 
        {   
            
            //Specify the child node being created and add it to the parent
            child = new DefaultMutableTreeNode(new TreeFile(treeFile));
            parent.add(child);
            //If the currenty focused child node has children of its own execute
            if(treeFile.isDirectory()){
                createChildNodes(treeFile, child);  //Recursively create chidlren
            }
        }
    }
    
    //Method that returns the file that was selected in the dialog
    public File getSelectedFile(){
        return selectedFile.returnFile();
    }
    
    //Internal class that wraps the file for accessiblity purposes
    public class TreeFile{
        //Variables
        private File fileNode;
        
        //Constructor that initializes the file that is being wrapped
        TreeFile(File f){
            this.fileNode = f;
        }
        //Method to return the wrapped file
        public File returnFile(){
            return this.fileNode;
        }
        //Method to return the file name for JTree creation
        @Override
        public String toString(){
            return fileNode.getName();
        }
        //Method to return the file name to the caller
        public String getName(){
            return fileNode.getName();
        }
    }
}
