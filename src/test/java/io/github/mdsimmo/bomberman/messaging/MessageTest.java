package io.github.mdsimmo.bomberman.messaging;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bukkit.ChatColor;
import org.junit.Test;

public class MessageTest {

	@Test
	public void printsBasicString() {
		Message message = new Message( null, "hello" );
		assertEquals( "hello", message.toString() );
	}
	
	@Test
	public void singleColorIsAdded() {
		Message message = new Message( null, "hi {red|boo}" );
		assertEquals( "hi " + ChatColor.RED + "boo" + ChatColor.RESET, message.toString() );
	}
	
	@Test
	public void imbeddedColorsHandled() {
		System.out.println( "Starting embeded" );
		Message message = new Message( null, "{green|hello, {yellow|fred}.} You Stink" );
		assertEquals( ChatColor.GREEN +  "hello, " + ChatColor.YELLOW + "fred" + ChatColor.GREEN + "." + ChatColor.RESET + " You Stink", message.toString() );
		System.out.println( "Finished embeded" );
	}
	
	@Test
	public void argumentArePassed() {
		Message message = new Message( null, "One, {two}, three" ).put( "two", "hello" );
		assertEquals( "One, hello, three", message.toString() );
	}
	
	@Test
	public void formatablesGetFormatted() {
		Message message = new Message( null, "Steve likes {steve|girlfriend}" ).put( "steve", new Formattable() {
			@Override
			public String format( Message message, List<String> args ) {
				if ( args.size() != 1 || !args.get( 0 ).equals( "girlfriend" ) )
					return args.toString(); // for debug
				
				return "Alex";
			}
		}  );
		assertEquals( "Steve likes Alex", message.toString() );
	}
	
	@Test
	public void messagesEmbededInMessages() {
		Message A = new Message( null, "This is A." );
		Message B = new Message( null, "{a} This is B." ).put( "a", A );
		Message C = new Message( null, "{b} This is C." ).put( "b", B );
		
		assertEquals( "This is A. This is B. This is C.", C.toString() );
	}
	
	@Test
	public void colorsPassedThroughMessages() {
		Message A = new Message( null, "A{red|B}C" );
		Message B = new Message( null, "{Green|X{a}Y}" ).put( "a", A );
		
		assertEquals( ChatColor.GREEN + "XA" + ChatColor.RED + "B" + ChatColor.GREEN + "CY" + ChatColor.RESET, B.toString() );
	}

	@Test
	public void noColorDataRemains() {
		Message A = new Message( null, "A{red|B}C" );
		Message B = new Message( null, "{Green|X{a}Y}" ).put( "a", A );
		
		// run the test a few times with the same objects. If it fails, it's because colors were not
		// fully reset each time
		for ( int i = 0; i < 5; i++ )
			assertEquals( ChatColor.GREEN + "XA" + ChatColor.RED + "B" + ChatColor.GREEN + "CY" + ChatColor.RESET, B.toString() );
	}
	
	@Test
	public void singleMessageThatsSuperComplex() {
		Message message = new Message( null, "Hey, {bold|Mr. {Red|Red {yellow|Bob} the {yellow|Tob} BANG} BOOM} CRASH" );

		assertEquals( 
				"Hey, " + ChatColor.BOLD + "Mr. " + ChatColor.RED + ChatColor.BOLD + "Red " + ChatColor.YELLOW 
						+ ChatColor.BOLD + "Bob" + ChatColor.RED + ChatColor.BOLD + " the " + ChatColor.YELLOW 
						+ ChatColor.BOLD + "Tob" + ChatColor.RED + ChatColor.BOLD + " BANG"
						+ ChatColor.RESET + ChatColor.BOLD + " BOOM" + ChatColor.RESET + " CRASH",
				message.toString() );
	}
	
	@Test
	public void multipleFormatsGetApplied() {
		Message message = new Message( null, "Hello, {bold|{italic|{green|World}}}!" );
		
		assertEquals( 
				"Hello, " + ChatColor.BOLD+ChatColor.ITALIC+ChatColor.GREEN+ChatColor.BOLD+ChatColor.ITALIC+"World"
					+ChatColor.RESET+ChatColor.BOLD+ChatColor.ITALIC+ChatColor.RESET+ChatColor.BOLD+ChatColor.RESET+"!",
					// Yup, it's ugly, but that's how MC formatting codes work :P
				message.toString() );
	}
	
	@Test
	public void resetDoesResetThings() {
		Message message = new Message( null, "{Green|{Bold|Hi {reset|world}!}}" );
		assertEquals( ChatColor.GREEN+""+ChatColor.BOLD+"Hi " + ChatColor.RESET + "world" + ChatColor.GREEN + ChatColor.BOLD + "!" +ChatColor.GREEN+ChatColor.RESET, message.toString() );
	}
	
	@Test
	public void formatCodesThroughMessages() {
		Message a = new Message( null, "{Green|{bold|A}}" );
		Message b = new Message( null, "{Italic|{Blue|{a}}}" ).put( "a", a );
		assertEquals( ChatColor.ITALIC +""+ ChatColor.BLUE + ChatColor.ITALIC + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + "A" + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BLUE + ChatColor.ITALIC + ChatColor.RESET + ChatColor.ITALIC + ChatColor.RESET, 
				b.toString() );
	}
	
}
