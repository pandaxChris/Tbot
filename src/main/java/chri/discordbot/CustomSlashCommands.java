package chri.discordbot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomSlashCommands extends ListenerAdapter {

	//Add some custom slash commands
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
		super.onSlashCommandInteraction(event);
		String cmd = event.getName();
		System.out.println(event.getUser().getName() + " has used this command:" + event.getName());

		if(cmd.equals("cmds")){
			event.deferReply();
			//event.reply("You have asked for help? " + event.getUser().getName()).queue();
			String res = "";
			List<Command> cd = event.getGuild().retrieveCommands(false).complete();
			for(Command c: cd){
				res.concat(c.getName() + " ");
			}
			event.getHook().sendMessage(res).queue();
		}else if(cmd.equals("stats")){
			//Only allow owner to use this command
			if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){

				event.deferReply().queue();
				String owner = event.getUser().getName();
				int numRoles = event.getGuild().getRoles().size();
				int totalMembers = event.getGuild().getMembers().size();

				AtomicInteger numBanned = new AtomicInteger();
				AtomicInteger numKicked = new AtomicInteger();

				event.getGuild().retrieveAuditLogs().queueAfter(1, TimeUnit.SECONDS, (logs)->{
					for(AuditLogEntry l: logs) {
						if (l.getType() == ActionType.BAN) {
							numBanned.getAndIncrement();
						}
						if (l.getType() == ActionType.KICK) {
							numKicked.getAndIncrement();
						}

					}
				});

				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Stats for: " + event.getGuild().getName(), null);
				eb.setColor(Color.GREEN);
				eb.setAuthor("Provided by TBot Stats");

				eb.setDescription("Current stats for server: " + event.getGuild().getName());

				eb.addField("Current owner: ", owner, false);

				eb.addField("Number of roles", String.valueOf(numRoles), true);
				eb.addField("Number of members", String.valueOf(totalMembers), true);
				eb.addBlankField(true);
				eb.addField("Number of banned users", String.valueOf(numBanned),true);
				eb.addField("Number of kicked users", String.valueOf(numKicked),true);
				event.getHook().sendMessageEmbeds(eb.build()).queue();

			}else{
				event.deferReply();
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.RED);
				eb.addField("Failed","No permission to use this command",false);
				event.getHook().sendMessageEmbeds(eb.build()).queue();
			}

		}else if(cmd.equals("weather")){
			//Gets weather for the zipcode specified
			String weatherEndpoint = "https://api.weatherbit.io/v2.0/current?";

		}
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		List<CommandData> cd = new ArrayList();
		cd.add(Commands.slash("cmds", "Test commands working"));
		cd.add(Commands.slash("stats", "Gives stats of the server."));
		event.getGuild().updateCommands().addCommands(cd).queue();
	}

}
