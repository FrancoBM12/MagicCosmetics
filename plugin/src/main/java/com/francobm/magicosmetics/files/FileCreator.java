package com.francobm.magicosmetics.files;

import com.francobm.magicosmetics.cache.SecondaryColor;
import com.francobm.magicosmetics.utils.OffsetModel;
import com.francobm.magicosmetics.utils.PositionModelType;
import com.francobm.magicosmetics.utils.Utils;
import org.bukkit.Color;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class FileCreator extends YamlConfiguration {

    private final String fileName;
    private final Plugin plugin;
    private final File file;

    public FileCreator(Plugin plugin, String filename, String fileExtension, File folder){
        this.plugin = plugin;
        this.fileName = filename + (filename.endsWith(fileExtension) ? "" : fileExtension);
        this.file = new File(folder, this.fileName);
        this.createFile();
    }

    public FileCreator(Plugin plugin, String fileName) {
        this(plugin, fileName, ".yml");
    }

    public FileCreator(Plugin plugin, String fileName, String fileExtension) {
        this(plugin, fileName, fileExtension, plugin.getDataFolder());
    }

    private void createFile() {
        try {
            if(!fileName.endsWith(".yml")){
                if(!file.exists()) {
                    if (this.plugin.getResource(this.fileName) != null) {
                        this.plugin.saveResource(this.fileName, false);
                    }
                    return;
                }
            }
            if (!file.exists()) {
                if (this.plugin.getResource(this.fileName) != null) {
                    this.plugin.saveResource(this.fileName, false);
                } else {
                    this.save(file);
                }
                this.load(file);
                return;
            }
            this.load(file);

            this.save(file);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }
    public void saveDefault(){
        this.plugin.saveResource(this.fileName, false);
    }
    public void save() {
        try {
            this.save(file);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Save of the file '" + this.fileName + "' failed.", e);
        }
    }

    public void reload() {
        try {
            load(file);
        } catch(IOException | InvalidConfigurationException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Reload of the file '" + this.fileName + "' failed.", e);
        }
    }

    public String getStringWF(String path) {
        //return ChatColor.translateAlternateColorCodes('&', super.getString(path));
        return super.getString(path, "");
    }

    @Override
    public String getString(String path) {
        //return ChatColor.translateAlternateColorCodes('&', super.getString(path));
        String s = super.getString(path);
        if(s == null) {
            //plugin.getLogger().warning("The path(" + path + ") is null in file '" + getName());
            return null;
        }
        return Utils.ChatColor(s);
    }

    public OffsetModel getOffseTModel(String path){
        String string = getString(path);
        if(string == null || string.isEmpty()) return new OffsetModel(0, 0.5, -0.15, 0, 0);
        String[] split = string.split(",");
        if(split.length >= 5){
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            double z = Double.parseDouble(split[2]);
            float yaw = Float.parseFloat(split[3]);
            float pitch = Float.parseFloat(split[4]);
            return new OffsetModel(x,y,z,yaw,pitch);
        }
        return new OffsetModel(0, 0.5, -0.15, 0, 0);
    }

    public PositionModelType getPositionModelType(String path){
        String string = getString(path);
        if(string == null || string.isEmpty()) return PositionModelType.BODY;
        try{
            return PositionModelType.valueOf(string.toUpperCase());
        }catch (IllegalArgumentException ignored){}
        return PositionModelType.BODY;
    }

    public List<Integer> getIntegerList(String path) {
        List<Integer> integers = new ArrayList<>();
        String list = getString(path);
        if(list.isEmpty()) return integers;
        String[] split = list.split(",");
        for(String s : split){
            try {
                integers.add(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {
            }
        }
        return integers;
    }

    public List<String> getStringListWithComma(String path) {
        List<String> strings = new ArrayList<>();
        String list = getString(path);
        if(list == null || list.isEmpty()) return strings;
        String[] split = list.split(",");
        strings.addAll(Arrays.asList(split));
        return strings;
    }

    public Set<Integer> getIntegerSet(String path) {
        Set<Integer> integers = new HashSet<>();
        String list = getString(path);
        if(list.isEmpty()) return integers;
        String[] split = list.split(",");
        for(String s : split){
            try {
                integers.add(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {
            }
        }
        return integers;
    }

    @Override
    public List<String> getStringList(String path) {
        List<String> list = super.getStringList(path);

        list.replaceAll(Utils::ChatColor);

        return list;
    }

    public List<String> getStringListWF(String path) {

        return super.getStringList(path);
    }

    public List<SecondaryColor> getSecondaryColor(String path){
         List<String> colorText = super.getStringList(path);
         List<SecondaryColor> secondaryColors = new ArrayList<>();
         for (String color : colorText){
             String[] colorTwo = color.split(";");
             Color secondary;
             try{
                 secondary = Utils.hex2Rgb(colorTwo[0]);
             }catch (IllegalArgumentException exception){
                 continue;
             }
             if(colorTwo.length > 1)
                 secondaryColors.add(new SecondaryColor(secondary, colorTwo[1]));
             else
                 secondaryColors.add(new SecondaryColor(secondary));
         }
         return secondaryColors;
    }

    public boolean exists(){
        return file.exists();
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String getName() {
        String name = fileName.replace(".yml", "");
        return name.substring(name.lastIndexOf("/") + 1);
    }
}