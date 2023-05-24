package chri.discordbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.List;

public final class MemberLoggers extends ListenerAdapter {
	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e){
		String user = e.getMember().getUser().getAsTag();
		String avatar = e.getMember().getAvatarUrl();
		TextChannel alertChannel = e.getGuild().getTextChannelsByName("auditChannel", true).get(0);
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("User joined!");
		eb.setColor(Color.GREEN);
		eb.setImage(avatar);
		eb.addField("", user + "has joined at " + (new java.util.Date()), true);
		alertChannel.sendMessageEmbeds(eb.build()).queue();
	}
	/*
		Handles logging for member leave, ban, kick
	 */
	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent e){
		String user = e.getUser().getAsTag();
		String avatar = e.getMember().getAvatarUrl();
		TextChannel alertChannel = e.getGuild().getTextChannelsByName("auditChannel", true).get(0);
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("User joined!");
		eb.setColor(Color.GREEN);
		eb.setImage(avatar);
		eb.addField("", user + "has left at " + (new java.util.Date()), true);
		alertChannel.sendMessageEmbeds(eb.build()).queue();
	}

	/*
		Handles logging when a role is assigned to a user
	 */
	@Override
	public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent e){
		String us = e.getUser().getAsTag();
		List<Role> roles = e.getRoles();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("New roles assigned to: " + us);
		eb.addField("", "List of roles added: ",false);
		for(Role r: roles){
			eb.addField("", r.getName() + " ", false);
		}
		e.getGuild().getTextChannelsByName("auditChannel", false).get(0).sendMessageEmbeds(eb.build()).queue();
	}
}
