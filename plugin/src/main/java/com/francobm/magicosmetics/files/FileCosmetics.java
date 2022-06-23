package com.francobm.magicosmetics.files;

import com.francobm.magicosmetics.MagicCosmetics;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileCosmetics {

    private final MagicCosmetics plugin = MagicCosmetics.getInstance();
    private final Map<String, FileCreator> files = new ConcurrentHashMap<>();

    public FileCosmetics() {
        loadFiles();
    }

    public Map<String, FileCreator> getFiles() {
        return files;
    }

    public FileCreator getFile(String name) {
        return files.get(name);
    }

    public void loadFiles(){
        File path = new File(plugin.getDataFolder(), "cosmetics");
        if(!path.exists()) {
            MagicCosmetics.getInstance().getLogger().info("Loading file cosmetic: cosmetics.yml");
            files.put("cosmetics.yml", new FileCreator(plugin, "cosmetics/cosmetics"));
            return;
        }
        for(File file : path.listFiles()){
            MagicCosmetics.getInstance().getLogger().info("Loading file cosmetic: " + file.getName());
            files.put(file.getName(), new FileCreator(plugin, "cosmetics/" + file.getName()));
        }
    }

    public void loadFile(String name){
        name = name.endsWith(".yml") ? name : name + ".yml";
        MagicCosmetics.getInstance().getLogger().info("Loading file cosmetic: " + name);
        files.put(name, new FileCreator(plugin, "cosmetics/" + name));
    }

    public void saveFiles(){
        for(FileCreator file : files.values()){
            MagicCosmetics.getInstance().getLogger().info("Saving file cosmetic: " + file.getFileName());
            file.save();
        }
    }

    public void saveFile(String name){
        name = name.endsWith(".yml") ? name : name + ".yml";
        MagicCosmetics.getInstance().getLogger().info("Saving file cosmetic: " + name);
        files.get(name).save();
    }

    public void reloadFiles(){
        File path = new File(plugin.getDataFolder(), "cosmetics");
        if(!path.exists()) {
            MagicCosmetics.getInstance().getLogger().info("Loading file cosmetic: cosmetics.yml");
            files.put("cosmetics.yml", new FileCreator(plugin, "cosmetics/cosmetics"));
            return;
        }
        for(FileCreator file : files.values()){
            if(!file.exists()) {
                deleteFile(file.getFileName());
                continue;
            }
            MagicCosmetics.getInstance().getLogger().info("Reloading file cosmetic: " + file.getFileName());
            file.reload();
        }
        for(File file : path.listFiles()){
            if(files.containsKey(file.getName())) continue;
            loadFile(file.getName());
        }
    }

    public void reloadFile(String name){
        name = name.endsWith(".yml") ? name : name + ".yml";
        MagicCosmetics.getInstance().getLogger().info("Reloading file cosmetic: " + name);
        files.get(name).reload();
    }

    public void deleteFile(String name){
        if(name.contains("/")){
            name = name.split("/")[1];
        }
        name = name.endsWith(".yml") ? name : name + ".yml";
        MagicCosmetics.getInstance().getLogger().info("Deleting file cosmetic: " + name);
        files.remove(name);
    }

}
