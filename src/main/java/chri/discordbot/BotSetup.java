package chri.discordbot;

import java.io.File;

import java.util.Scanner;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;


public class BotSetup{
    protected JDA jda;
    private String DISC_TOKEN;
    private String SERVERID;

    public String WEATHER_KEY;
    public BotSetup(){
        load_keys();
        this.jda = JDABuilder.createDefault(DISC_TOKEN)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("The way you're on Discord"))
                .addEventListeners(new CustomSlashCommands())
                .build();

    }

    public JDA getBuilder() {
        return this.jda;
    }
    private void load_keys(){
        try{
            File f = new File("src/main/resources/keys.txt");
            Scanner s = new Scanner(f);
            DISC_TOKEN = s.nextLine().strip();
            SERVERID = s.nextLine().strip();
            WEATHER_KEY = s.nextLine().strip();
            s.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }





}
