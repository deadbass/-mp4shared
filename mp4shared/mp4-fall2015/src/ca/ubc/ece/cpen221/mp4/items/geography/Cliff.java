package ca.ubc.ece.cpen221.mp4.items.geography;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.items.Item;

public class Cliff implements Item {
    private final static ImageIcon cliffImage = Util.loadImage("cliff.gif");

    private Location location;
    private boolean isDead;

    public Cliff(Location location) {
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
        // TODO Auto-generated method stub
        return cliffImage;
    }

    @Override
    public String getName() {
        return "cliff";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getStrength() {
        return 500;
    }

    @Override
    public void loseEnergy(int energy) {

    }

    @Override
    public boolean isDead() {
        return isDead;
    }

}
