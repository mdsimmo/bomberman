package com.github.mdsimmo.bomberman.localisation;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * A formattable class can have custom styling when referenced in a phrase
 */
public interface Formattable {

    String format( Message message, List<String> args );

    class ItemWrapper implements Formattable {

        private ItemStack item;

        public ItemWrapper( ItemStack item ) {
            this.item = item;
        }

        @Override
        public String format( Message message, List<String> args ){
            if ( args.size() == 0 )
                return format( message, Collections.singletonList("amount"))
                        + " "
                        + format( message, Collections.singletonList("type"));
            String arg = args.get( 0 ).toLowerCase();
            if ( arg.equals( "amount ") )
                return Integer.toString( item == null ? 0 : item.getAmount() );
            else if ( arg.equals( "type" ) )
                return item == null ? "none" : item.getType().toString().replace( '_', ' ' ).toLowerCase();
            return null;
        }
    }

    class SenderWrapper implements Formattable {

        private final CommandSender sender;

        public SenderWrapper( CommandSender sender ) {
            if ( sender == null )
                throw new NullPointerException( "sender cannot be null" );
            this.sender = sender;
        }

        @Override
        public String format( Message message, List<String> args ){
            return sender.getName();
        }

    }

    class Equation implements Formattable {

        private static class SignFunction extends Function {

            public static SignFunction instance = new SignFunction();

            public SignFunction() {
                super( "sign", 1 );
            }

            @Override
            public double apply( double... args ) {
                if ( args.length != 1 )
                    throw new IllegalArgumentException( "Sign function can only have one argument. (" + args.length + " args given)" );
                double val = args[0];
                if ( val > 0 )
                    return 1;
                else if ( val < 0 )
                    return -1;
                else if ( val == 0 )
                    return 0;
                else
                    return Double.NaN;
            }
        }

        @Override
        public String format( Message message, List<String> args ) {
            if ( args.size() != 1 )
                throw new RuntimeException("Equation must have exactly one argument: " + message.toString());
            try {
                double answer = new ExpressionBuilder( args.get( 0 ) ).function( SignFunction.instance ).build().evaluate();
                return BigDecimal.valueOf( answer ).stripTrailingZeros().toPlainString();
            } catch ( Exception e ) {
                throw new RuntimeException( "Expression has invalid numerical imputs: " + args.get( 0 ), e );
            }
        }
    }

}
