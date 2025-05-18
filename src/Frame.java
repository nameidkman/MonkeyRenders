/*
 * Name: Sai 
 * Date: today 
 * Des: this is for the frame class 
 */


import javax.swing.*;


public class Frame extends JFrame {
   
  GamePanel panel;

  public Frame() {
        panel = new GamePanel();
        this.add(panel);
        this.setTitle("Monkey renders"); //set title for frame
        this.setResizable(false); //frame can't change size 
        this.pack();//makes components fit in window - don't need to set JFrame size, as it will adjust accordingly
        this.setVisible(true); // makes window visible to user
        this.setLocationRelativeTo(null); //set window in middle of screen
    }


}
