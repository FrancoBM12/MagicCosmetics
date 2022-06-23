package com.francobm.magicosmetics.cache.renderer;

import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageRenderer extends MapRenderer {

    private BufferedImage image;
    private boolean loaded;

    public ImageRenderer(){
        this.image = null;
        this.loaded = false;
    }

    public ImageRenderer(String url){
        this.image = null;
        this.loaded = false;
    }

    public boolean load(BufferedImage image){
        if(image == null) return false;
        this.image = image;
        return true;
    }

    public boolean load(String url){
        BufferedImage image;
        try {
            if(url.startsWith("http")) {
                image = ImageIO.read(new URL(url));
            }else{
                File file = new File(MagicCosmetics.getInstance().getDataFolder(), "sprays/" + url);
                MagicCosmetics.getInstance().getLogger().info("Loading spray from file: " + file.getAbsolutePath());
                if(!file.exists()) return false;
                image = ImageIO.read(file);
            }
            image = MapPalette.resizeImage(image);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        this.image = image;
        return true;
    }

    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        if(loaded) return;
        mapCanvas.drawImage(0, 0, image);
        mapView.setTrackingPosition(false);
        loaded = true;
    }
}
