package ca.ubc.ece.cpen221.mp4.items.geography;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.items.Item;

public class House implements Item {
    private final static ImageIcon houseImage = Util.loadImage("house.gif");
    
    private Location location;
    private boolean isDead;

    public House(Location location) {
        this.location = location;
        this.isDead = false;
    }

    @Override
    public int getPlantCalories() {
        return 0;
    }

    @Override
    public int getMeatCalories() {
        return 0;
    }

    @Override
    public ImageIcon getImage() {
        return houseImage;
    }

    @Override
    public String getName() {
        return "house";
      
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getStrength() {
        // TODO Auto-generated method stub
        return 400;
    }

    @Override
    public void loseEnergy(int energy) {
        
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

}
