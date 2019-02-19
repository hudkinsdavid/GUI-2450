package jnotepad;

/**
 * David Hudkins II
 * Project #5
 * Due : 07 December 2018
 * CS 2450 F-01-18
 * 
 * Description
 * ===========
 * This is a font choosing dialog class that allows the 
 * user to choose from the system provided fonts.
 * */

//Custom dialog class that displays GUI for font select
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

    public class JFontChooser extends JDialog{
        //Declare variables
        private Font          returnFont;
        private String        selectedName;
        private int           selectedSize;
        private int           boldChoice;
        private int           italicChoice;
        private boolean       selectedItalic;
        private boolean       selectedBold;
        private String[]      swingFonts;
        private String[]      fontSizes;
        private JList<String> fonts;
        private JList<String> sizes;
        private JScrollPane   fontScroll;
        private JScrollPane   sizeScroll;
        private JCheckBox     bold;
        private JCheckBox     italic;    
        private JButton       okBtn;
        private JButton       cancelBtn;
        private Font          f;
        private ArrayList<String>sizeChoices;
        
        //Constructor that takes the parent frame as a parameter
        JFontChooser(JFrame parent){
            //Obtain all available Swing fonts
            swingFonts = GraphicsEnvironment.
                    getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

            //Initialize variables
            selectedName = "Courier New";
            selectedSize = 12;
            
            //Set layout for the dialog
            this.setLayout(new GridBagLayout());
            GridBagConstraints gbConstraint = new GridBagConstraints();
            gbConstraint.fill = GridBagConstraints.HORIZONTAL;
            gbConstraint.insets = new Insets(2,2,5,5);
           
            //Create a scrollable list of 0 to 60 pt font
            sizeChoices = new ArrayList<>();
            for(int i=0;i<62;i++){
                sizeChoices.add(Integer.toString(i));
                i++;
            }
            fontSizes = new String[sizeChoices.size()];
            for(int i=0;i<fontSizes.length;i++){
                fontSizes[i] = sizeChoices.get(i);
            }
            
            sizes = new JList<>(fontSizes);
            sizes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            sizeScroll = new JScrollPane(sizes);
            
            //Create a scrollable list of available Swing fonts
            fonts = new JList<>(swingFonts);
            fonts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            fontScroll = new JScrollPane(fonts);
            
            //Initialize the selections to Courier New and 12 pt font
            fonts.setSelectedIndex(17);
            sizes.setSelectedIndex(6);
            
            //Creat an Ok button for the dialog that confirms font choices
            okBtn = new JButton("Ok");
            okBtn.addActionListener(ae -> {
                try{
                    //Copy the selections at 'Ok' press to the fields in class
                    selectedName = fonts.getSelectedValue();
                    selectedSize = Integer.parseInt(sizes.getSelectedValue());
                    if(getBold() && getItalic()){
                        boldChoice   = Font.BOLD;
                        italicChoice = Font.ITALIC;
                    }
                    else if(getItalic()){
                        italicChoice = Font.ITALIC;
                    }
                    else if(getBold()){
                        boldChoice   = Font.BOLD;
                    }
                    else{
                        italicChoice = 0;
                        boldChoice   = 0;
                    }
                }catch(NullPointerException e){}
                this.dispose();
                this.setVisible(false); //Hide dialog
            });
            
            //Creates a button that cancels the font selections
            cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(ae -> {
                this.setVisible(false);
            });
            //Set the dialog position relative to the parent frame
            this.setLocationRelativeTo(parent);
            
            //Create instances of style check boxes and set button sizes     
            bold = new JCheckBox("Bold");
            italic = new JCheckBox("Italic");
            bold.setSize(10,10);
            italic.setSize(10,10);
            okBtn.setSize(20,15);
            cancelBtn.setSize(20,15);
            
            //Add all the components to the dialog box and set the size
            gbConstraint.gridx = 0;
            gbConstraint.gridy = 0;
            gbConstraint.gridheight = 1;
            gbConstraint.gridwidth  = 2;
            this.add(fontScroll, gbConstraint);
            gbConstraint.gridx = 4;
            gbConstraint.gridy = 0;
            gbConstraint.gridheight = 1;
            gbConstraint.gridwidth  = 2;
            this.add(sizeScroll, gbConstraint);
            gbConstraint.gridx = 0;
            gbConstraint.gridy = 5;
            this.add(bold, gbConstraint);
            gbConstraint.gridx = 0;
            gbConstraint.gridy = 4;
            this.add(italic, gbConstraint);
            gbConstraint.gridx = 4;
            gbConstraint.gridy = 4;
            this.add(okBtn, gbConstraint);
            gbConstraint.gridx = 5;
            gbConstraint.gridy = 5;
            this.add(cancelBtn, gbConstraint);
            this.setSize(400,300);
        }
        
        //Method for returning all of the font selections
        public Font getFontAttributes(Font ogFont){
            return f = new Font(selectedName,boldChoice | italicChoice,selectedSize);
        }
        
        //Method to check for bold selection
        public boolean getBold(){
            return bold.isSelected();
        }
        //Method to check for italic selection
        public boolean getItalic(){
            return italic.isSelected();
        }
        
    }

