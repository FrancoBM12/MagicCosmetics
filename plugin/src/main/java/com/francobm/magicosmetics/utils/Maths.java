package com.francobm.magicosmetics.utils;

import com.francobm.magicosmetics.MagicCosmetics;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Maths {

    private final String licenseKey;
    private final MagicCosmetics plugin;
    private final String validationServer;
    private LogType logType = LogType.NORMAL;
    private String securityKey = "YecoF0I6M05thxLeokoHuW8iUhTdIUInjkfF";
    private boolean debug = false;

    public static boolean m(){
        MagicCosmetics.getInstance().wkasdwk = new Maths(MagicCosmetics.getInstance().getConfig().getString(Utils.bsc("bGljZW5zZQ==")), Utils.bsc("aHR0cDovL3BpdGljaS5jZi92ZXJpZnkucGhw"), MagicCosmetics.getInstance()).register();
        return !MagicCosmetics.getInstance().wkasdwk;
    }

    public Maths(String licenseKey, String validationServer, MagicCosmetics plugin) {
        this.licenseKey = licenseKey;
        this.plugin = plugin;
        this.validationServer = validationServer;
    }

    public Maths setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
        return this;
    }

    public Maths setConsoleLog(LogType logType) {
        this.logType = logType;
        return this;
    }

    public Maths debug() {
        debug = true;
        return this;
    }

    public boolean register() {
        boolean siu;
        log(0, Utils.bsc("W109PT09PT09PT09W0xpY2Vuc2UtU3lzdGVtXT09PT09PT09PT1bXQ=="));
        try {
            siu = Utils.siu(debug);
        } catch (IOException e) {
            siu = false;
            plugin.getLogger().severe(Utils.bsc("VmFsaWRhdGluZyBwdXJjaGFzZS4uLiBGYWlsZWQh"));
        }
        log(0, Utils.bsc("Q29ubmVjdGluZyB0byBMaWNlbnNlLVNlcnZlci4uLg=="));
        ValidationType vt = isValid();
        if (vt == ValidationType.VALID) {
            log(1, Utils.bsc("TGljZW5zZSB2YWxpZCE="));
            if(siu){
                log(0, Utils.bsc("V2VsY29tZSBVc2VyIA==") + plugin.getUser().getName() + Utils.bsc("IQ=="));
                log(0, Utils.bsc("VGhhbmtzIGZvciBwdXJjaGFzaW5nIHRoaXMgcGx1Z2luIQ=="));
            }else{
                plugin.getLogger().severe(Utils.bsc("VGhpcyBwdXJjaGFzZSBpcyBjb3VudGVyZmVpdC4="));
                plugin.getLogger().severe(Utils.bsc("RGlzYWJsaW5nIHBsdWdpbiE="));
                log(0, Utils.bsc("W109PT09PT09PT09W0xpY2Vuc2UtU3lzdGVtXT09PT09PT09PT1bXQ=="));

                Bukkit.getScheduler().cancelTasks(plugin);
                Bukkit.getPluginManager().disablePlugin(plugin);
                return false;
            }
            log(0, Utils.bsc("W109PT09PT09PT09W0xpY2Vuc2UtU3lzdGVtXT09PT09PT09PT1bXQ=="));
            return true;
        } else {
            plugin.getLogger().severe(Utils.bsc("TGljZW5zZSBpcyBOT1QgdmFsaWQh"));
            plugin.getLogger().severe(Utils.bsc("RmFpbGVkIGFzIGEgcmVzdWx0IG9mIA==") + vt.toString());
            plugin.getLogger().severe(Utils.bsc("RGlzYWJsaW5nIHBsdWdpbiE="));
            log(0, Utils.bsc("W109PT09PT09PT09W0xpY2Vuc2UtU3lzdGVtXT09PT09PT09PT1bXQ=="));

            Bukkit.getScheduler().cancelTasks(plugin);
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }
    }

    public boolean isValidSimple() {
        return (isValid() == ValidationType.VALID);
    }

    private String requestServer(String v1, String v2) throws IOException {
        URL url = new URL(validationServer + "?v1=" + v1 + "&v2=" + v2 + "&pl=" + plugin.getName());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        if (debug) {
            plugin.getLogger().info("\nSending 'GET' request to URL : " + url);
            plugin.getLogger().info("Response Code : " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        }
    }

    public ValidationType isValid() {
        String rand = toBinary(UUID.randomUUID().toString());
        String sKey = toBinary(securityKey);
        String key = toBinary(licenseKey);
        try {
            String response = requestServer(xor(rand, sKey), xor(rand, key));

            if (response.startsWith("<")) {
                log(1, Utils.bsc("VGhlIExpY2Vuc2UtU2VydmVyIHJldHVybmVkIGFuIGludmFsaWQgcmVzcG9uc2Uh"));
                log(1, Utils.bsc("SW4gbW9zdCBjYXNlcyB0aGlzIGlzIGNhdXNlZCBieTo="));
                log(1, Utils.bsc("MSkgWW91ciBXZWItSG9zdCBpbmplY3RzIEpTIGludG8gdGhlIHBhZ2UgKG9mdGVuIGNhdXNlZCBieSBmcmVlIGhvc3RzKQ=="));
                log(1, Utils.bsc("MikgWW91ciBWYWxpZGF0aW9uU2VydmVyLVVSTCBpcyB3cm9uZw=="));
                log(1,
                        Utils.bsc("U0VSVkVSLVJFU1BPTlNFOiA=") + (response.length() < 150 || debug ? response : response.substring(0, 150) + "..."));
                return ValidationType.PAGE_ERROR;
            }

            try {
                return ValidationType.valueOf(response);
            } catch (IllegalArgumentException exc) {
                String respRand = xor(xor(response, key), sKey);
                if (rand.substring(0, respRand.length()).equals(respRand))
                    return ValidationType.VALID;
                else
                    return ValidationType.WRONG_RESPONSE;
            }
        } catch (IOException e) {
            if (debug)
                e.printStackTrace();
            return ValidationType.PAGE_ERROR;
        }
    }

    //
    // Cryptographic
    //

    private static String xor(String s1, String s2) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < (Math.min(s1.length(), s2.length())); i++)
            result.append(Byte.parseByte("" + s1.charAt(i)) ^ Byte.parseByte(s2.charAt(i) + ""));
        return result.toString();
    }

    //
    // Enums
    //

    public enum LogType {
        NORMAL, LOW, NONE;
    }

    public enum ValidationType {
        WRONG_RESPONSE, PAGE_ERROR, URL_ERROR, KEY_OUTDATED, KEY_NOT_FOUND, NOT_VALID_IP, INVALID_PLUGIN, VALID;
    }

    //
    // Binary methods
    //

    private String toBinary(String s) {
        byte[] bytes = s.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary.toString();
    }

    //
    // Console-Log
    //

    private void log(int type, String message) {
        if (logType == LogType.NONE || (logType == LogType.LOW && type == 0))
            return;
        plugin.getLogger().info(message);
    }
}