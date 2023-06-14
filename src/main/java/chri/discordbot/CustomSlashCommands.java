package chri.discordbot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class CustomSlashCommands extends ListenerAdapter {
	//Add some custom slash commands
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
		super.onSlashCommandInteraction(event);
		String cmd = event.getName();
		//Audit/log channel
		if(!event.getUser().isBot()){
			TextChannel t = Objects.requireNonNull(event.getGuild()).getTextChannelsByName("auditChannel", true).get(0);
			t.sendMessage(event.getUser().getName() + " used the command: " + event.getName() + event.getOptions()).queue();
		}

		//Handle the actual command
		if(cmd.equals("stats")){
			//Only allow owner to use this command
			if(Objects.requireNonNull(event.getMember()).hasPermission(Permission.ADMINISTRATOR)){
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
			event.deferReply().queue();
			EmbedBuilder eb = new EmbedBuilder();
			eb.addField("Weather", event.getOption("lat").getAsDouble() + ", " + event.getOption("long").getAsDouble(), false);
			// try{
			// 	String weatherEndpoint = "https://api.weather.gov/points/"
			// 			+ event.getOption("lat").getAsDouble()
			// 			+ "," + event.getOption("long").getAsDouble();

			// 	URL url = new URL(weatherEndpoint);
			// 	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 	conn.setRequestMethod("GET");

			// 	int status = conn.getResponseCode();
			// 	eb.addField("Got status code for end point: " + weatherEndpoint, String.valueOf(status), true);
			// 	if(status == HttpURLConnection.HTTP_OK){
			// 		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			// 		String inputLine;
			// 		StringBuffer res = new StringBuffer();

			// 		while((inputLine=in.readLine()) != null){
			// 			res.append(inputLine);
			// 		}
			// 		in.close();
			// 		System.out.println(res);



			// 	}
			// }catch(Exception e){
			// 	eb.addField("Failed to get weather", "Invalid lat/long maybe", true);
			// }

			try{
				WeatherEndpoint we = new WeatherEndpoint();
				String ep = we.getEndpoint("data");
				ep += "?lat=" + event.getOption("lat").getAsDouble()
						+ "&lon="+event.getOption("lon").getAsDouble()
						+ "&appid=" + we.key;
				URL url = new URL(ep);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");

				int status = conn.getResponseCode();
				if(status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_ACCEPTED){
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String inputLine;
					StringBuffer res = new StringBuffer();

					while((inputLine=in.readLine()) != null){
					 	res.append(inputLine);
					}
					in.close();
					System.out.println(res);


				}else{

				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			event.getHook().sendMessageEmbeds(eb.build()).queue();
		}else if(cmd.equals("/geolocation")){				//Find location based on city,state,country

		} else if(cmd.equals("/purge_invites")) {			//Purges all invites on server
			event.deferReply().queue();
			if (event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
				try {
					//List<Invite> invites = event.getGuild().retrieveInvites().complete();

					event.getGuild().retrieveInvites().queueAfter(1, TimeUnit.SECONDS, (invites) -> {
						for (Invite i : invites) {
							i.delete();
						}
						event.getHook().sendMessage("All invites have been deleted").queue();
						event.getGuild().getTextChannelsByName("auditChannel", true)
								.get(0)
								.sendMessage("All invites deleted by " + event.getUser().getAsTag()).queue();
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(cmd.equals("purge_text")){
			if(!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)){return;}
			event.deferReply().queue();
			int n = 10;
			if(event.getOption("n") != null && event.getOption("n").getAsInt() > 0){
				n = event.getOption("n").getAsInt();
			}
			//MessageHistory m = event.getChannel().getHistory().retrievePast(n).complete();

			event.getChannel().getHistory().retrievePast(n).queueAfter(1, TimeUnit.SECONDS,(msgs)->{
				for(Message m: msgs){
					event.getChannel().deleteMessageById(m.getId()).queue();
				}
				event.getGuild().getTextChannelsByName("auditChannel",true ).get(0)
							.sendMessage("Removed " + String.valueOf(msgs.size()) + " messages from " + event.getChannel().getName()).queue();
				event.getHook().sendMessage("Deleted messages successfully");
			});
		}

	}



	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		List<CommandData> cd = new ArrayList();
		//cd.add(Commands.slash("cmds", "Test commands working"));

		cd.add(Commands.slash("stats", "Gives stats of the server."));
		cd.add(Commands.slash("weather", "Gets current weather")
				.addOption(OptionType.NUMBER, "lat", "Latitude", true, false)
				.addOption(OptionType.NUMBER, "long", "Longitude",true, false));
		cd.add(Commands.slash("purge_invites", "Remove all invites"));
		cd.add(Commands.slash("purge_text", "Remove the last [n] messages in this channel. (Default n=10; max 100)")
				.addOption(OptionType.INTEGER, "n", "number of messages", false, false));

		event.getGuild().updateCommands().addCommands(cd).queue();

		/*
			Create an audit channel to help us log some stuff. -- easier than checking audit log in settings
		 */
		List<TextChannel> channels = event.getGuild().getTextChannels();//.getChannels();
		//System.out.println(channels);
		boolean found = false;
		for(GuildChannel g: channels){
			//System.out.println(g.getName());
			if(g.getName().equalsIgnoreCase("auditChannel")){
				found=true;  break;
			}

			if(!found){
				event.getGuild().createTextChannel("auditChannel").queue();
			}

		}

	}

	private class WeatherEndpoint{
		private String key;
		private HashMap<String,String> links;
		public WeatherEndpoint(){
			JSONParser jp = new JSONParser();
			JSONObject obj;
			try{
				FileReader f = new FileReader("src/main/java/chri/discordbot/data/weather_endpoint.json");
				Object o = jp.parse(f);
				obj = (JSONObject) o;
				Object ep = obj.get("links");
				JSONArray ja = (JSONArray) ep;

				ja.forEach(e -> parseEPObject((JSONObject) e));
				f.close();
			}catch(Exception e){
				e.printStackTrace();
			}

		}

		private void parseEPObject(JSONObject e){
			String name = (String) e.get("name");
			String ep = (String) e.get("endpoint");
			links.put(name,ep);
		}

		public String getEndpoint(String k){
			return links.containsKey(k) ? links.get(k) : null;
		}

	}
}


