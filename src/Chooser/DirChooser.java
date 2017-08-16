package Chooser;

import framework.Operations;
import framework.Task;

import javax.imageio.ImageIO;
import javax.swing.*;

import Updater.CoreUpdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirChooser implements Task {
    private JFileChooser f=new JFileChooser();
    private boolean cancel=false;

    private boolean fired=false;

    private static Logger log = Logger.getLogger( CoreUpdater.class.getName() );
    
    public DirChooser(){
    	
    	//Change to look and feel to make it more similar to the Windows' one
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(f);

    }
    public void esegui() {
    	//Functor pattern
    	Operations.LogSetup(log);
        if (!fired) {


            boolean found = false;

            fired = true;
            //Workaround to implement an icon to the JFileChooser
            JFrame icon= new JFrame();
            try {
				icon.setIconImage(ImageIO.read(new File("gw2_64_1-0.png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            f.setDialogTitle("Select Guild Wars 2 directory");

            //Loop until "Cancel" is pressed or a valid directory is selected
            while (!found) {
                int input = f.showDialog(icon, "Select");

                if (input == JFileChooser.APPROVE_OPTION) {
                    File file = f.getSelectedFile();
                    
                    boolean check = DirChooser.validDir(file.getAbsolutePath());
                    if (check) {
                        found = true;
                        log.log( Level.INFO,"Valid dir selected: "+file.getAbsolutePath());
                        changePathProp(file.getAbsolutePath());
                    } else {
                    	log.log( Level.INFO,"Invalid dir selected: "+file.getAbsolutePath());
                        JOptionPane.showMessageDialog(null, "Executable not found. Please select a valid directory", "Directory not valid", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    cancel = true;
                    break;
                }


            }
            Operations.closeLogHandlers(log);
        }
    }


    //Check if a given path is a valid path for GW2
    public static boolean validDir(String path){
        boolean result=new File(path+"\\Gw2-64.exe").exists();
        return result;
    }
    //setter and getter required for the functor pattern
    public boolean getCancel() { return cancel;}

    public JFileChooser getJFileChooser() { return f;}

    public boolean isFired() {
        return fired;
    }

    public void changePathProp(String path){
         Properties prop = new Properties();
         InputStream input= null;

         try {

             input = new FileInputStream("gw2_launcher.cfg");
             //Import settings
             prop.load(input);
             input.close();

         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
         
        OutputStream output= null;
        prop.put("path", path);
        System.out.println("Scrivo");
        try {

            output = new FileOutputStream("gw2_launcher.cfg");
            prop.store(output, "Config file for GW2 Launcher");
            output.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
