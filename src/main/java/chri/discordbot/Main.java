package chri.discordbot;


import chri.discordbot.events.JoinListener;

public class Main {


    public static void main(String[] args) throws Exception{
        BotSetup b = new BotSetup();
        b.addEvent(new JoinListener());
    }


}