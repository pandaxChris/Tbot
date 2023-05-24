import chri.discordbot.BotSetup;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;




public class Tests {

	BotSetup b1 = new BotSetup();


	/* Check bot able to create JDA object successfully. */
	@Test
	public void setupJDA(){
		assertTrue(b1.jdaSuccess());
	}

	/*Check if bot was able to  join the discord successfully.*/
	@Test
	public void noGuilds(){
		assertTrue(b1.fresh());
	}

	@Test
	public void commandsSuccess(){
		assertTrue(b1.getCommands().size() == 0);
	}

	@Test
	public void listenersSuccess(){assertTrue(b1.getListeners().size() > 0);}


}
