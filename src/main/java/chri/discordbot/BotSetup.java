package chri.discordbot;

import java.io.File;

import java.util.List;
import java.util.Scanner;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;


public class BotSetup {
    protected JDA jda;

    public BotSetup(){
        String tok = getToken();
        this.jda = JDABuilder.createDefault(tok)
                .setStatus(OnlineStatus.ONLINE)
                //.enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();
        try {
            jda.awaitReady();
        }catch(Exception e){
            e.printStackTrace();
        }

        List<GuildChannel> channels = jda.getGuildById("").getChannels();

    }

    public JDA getBuilder() {
        return this.jda;
    }
    private String getToken(){
        try{
            File f = new File("src/main/resources/disc_token.txt");
            Scanner s = new Scanner(f);
            String tok = s.nextLine().strip();
            s.close();
            return tok.strip();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void addEvent(Object o){
        jda.addEventListener(o);
    }


}
