package ca.ubc.ece.cpen221.mp4.items.geography;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.items.Item;

public class Lake implements Item {
    private final static ImageIcon lakeImage = Util.loadImage("lake.gif");

    private Location location;
    private boolean isDead;

    public Lake(Location location) {
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
        return lakeImage;
    }

    @Override
    public String getName() {
        return "lake";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getStrength() {
        return 900;
    }

    @Override
    public void loseEnergy(int energy) {

    }

    @Override
    public boolean isDead() {
        return isDead;
    }

}
