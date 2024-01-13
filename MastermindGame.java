import java.util.Scanner;

public class MastermindGame
{

    // Length of the codeword
    final static int GUESS_LENGTH = 4;

    // Maximum value for a random digit in the codeword
    final static int RANDOM_MAX = 6; // Maximum of 9 (breaks the game if above 9).

    // Minimum value for a random digit in the codeword
    final static int RANDOM_MIN = 1; // Maximum of 8 (breaks the game if above 8).

    // <--------------------- ONLY CHANGE CONSTANTS ABOVE --------------------->

    // Maximum character representation of a random digit in the codeword
    final static char MAX_CHAR = RANDOM_MAX + '0';

    // Minimum character representation of a random digit in the codeword
    final static char MIN_CHAR = RANDOM_MIN + '0';

    // Number of possible digit values in the codeword
    final static int POSSIBLE_DIGIT_VALUES = ( RANDOM_MAX - RANDOM_MIN ) + 1;

    public static void main( String[] args )
    {
        Scanner keyboard = new Scanner( System.in );

        playMastermindGame( keyboard );

        keyboard.close();

    } // End of main()

    public static boolean isGuessValid( String guess, int length_level, int digit_level )
    {
        // Check if the length of the guess is not equal to the expected length.
        if ( guess.length() != ( GUESS_LENGTH + length_level ) )
        {
            return false; // Return false if the length is incorrect
        }

        for ( int i = 0; i < ( GUESS_LENGTH + length_level ); i++ )
        {
            // Extract each character into variable `digit` from the guess at positions 0 to (3 + `length_level`) (or 1
            // to (4 + `length_level`) in terms of length).
            char digit = guess.charAt( i );

            // Check if the character stored in `digit` is outside the valid range (1 to (6 + `digit_level`)).
            if ( digit < MIN_CHAR || digit > MAX_CHAR + ( digit_level + '0' ) )
            {
                return false; // Return false if an invalid digit is found
            }
        }

        // If all checks pass, return true to indicate a valid guess
        return true;

    } // End of `isGuessValid(String guess, int current_level)`

    public static String generateCodeword( int length_level, int digit_level )
    {
        String codeword;
        StringBuilder builder = new StringBuilder();

        // Generate random numbers based on the current level; then append each to the StringBuilder builder.
        for ( int i = 0; i < ( GUESS_LENGTH + length_level ); i++ )
        {
            // Formula for random numbers: MIN + (int) (Math.random() * ((MAX + 1) - MIN));
            int random_number =
                RANDOM_MIN + (int)( Math.random() * ( ( ( RANDOM_MAX + digit_level ) + 1 ) - RANDOM_MIN ) );
            builder.append( random_number );
        }

        // Change the StringBuilder back to a string
        codeword = builder.toString();

        return codeword;

    } // End of `generateCodeword(int digit_level)`

    public static void playMastermindGame( Scanner keyboard )
    {
        int digit_level = 0;
        int length_level = 0;
        int level_counter = 1;
        String level_increase_decision;

        String replay_decision = "y";

        // Main game loop: Replays the game if the user decides to continue.
        while ( replay_decision.charAt( 0 ) == 'y' )
        {
            // Output the game header and the current level.
            printGameHeader( level_counter );

            // Generate the codeword for the game.
            String codeword = generateCodeword( length_level, digit_level );

            // Ask the user if they want to reveal the codeword.
            revealCodeword( keyboard, codeword );

            // Initialize counters for tracking rounds, hits, and misses.
            int rounds_counter = 1;
            int hit_counter = 0;
            int miss_counter;

            // Main game loop: Continues until the user has guessed the entire codeword or decides to stop.
            while ( hit_counter < ( GUESS_LENGTH + length_level ) )
            {
                System.out.printf(
                    "Round = %d | Length: %d | Range: %d to %d | Your guess (0 to stop): ", rounds_counter,
                    ( GUESS_LENGTH + length_level ), RANDOM_MIN, ( RANDOM_MAX + digit_level ) );
                String guess = keyboard.next();

                // Exit the game if the user inputs '0'.
                if ( guess.equals( "0" ) )
                {
                    System.out.printf( "The codeword was %s. \n", codeword );
                    break;
                }

                // Validate the user's guess.
                boolean guess_check = isGuessValid( guess, length_level, digit_level );

                if ( !guess_check )
                {
                    System.out.println( "An invalid guess." );
                    continue;
                }

                // Resetting counters after every round.
                hit_counter = 0;
                miss_counter = 0;

                /*
                Initialize arrays to count occurrences of each digit in the codeword and guess.
                Each index in the arrays represents a possible digit value.
                 */

                // Array formula: <type>[] <variable_name> = new <type>[size];
                int[] codeword_digit_count = new int[( POSSIBLE_DIGIT_VALUES + digit_level )];
                int[] guess_digit_count = new int[( POSSIBLE_DIGIT_VALUES + digit_level )];

                // Calculate the number of hits and update the digit count arrays.
                for ( int i = 0; i < ( GUESS_LENGTH + length_level ); i++ )
                {
                    if ( guess.charAt( i ) == codeword.charAt( i ) )
                    {
                        // Increment the hit counter when a digit is in the correct position.
                        hit_counter++;
                    }

                    else
                    {
                        // ------------------ Convert characters to integers for comparison ------------------//

                        /*
                        For example, if codeword.charAt(i) contains '3', codeword_digit will be set to 3 (the integer
                        value), rather than the character '3'.

                             +-----------------------+
                             |  '3' in ASCII is 51   |
                             |  '0' in ASCII is 48   |
                             |                       |
                             |   51 - 48 = 3         |
                             +-----------------------+

                         */

                        int codeword_digit = codeword.charAt( i ) - '0';
                        int guess_digit = guess.charAt( i ) - '0';

                        // ------------------------------------------------------------------------------------//

                        // Increment the counts for the respective digits in the codeword and guess.
                        codeword_digit_count[codeword_digit - 1]++;
                        guess_digit_count[guess_digit - 1]++;
                    }

                } // End of for-loop

                // Calculate the number of misses.
                for ( int d = 0; d < ( POSSIBLE_DIGIT_VALUES + digit_level ); d++ )
                {
                    // Increment the total miss counter by the minimum count of digit `d + 1` between the codeword and
                    // guess. This counts the digits that are in the codeword but not in the correct positions.
                    miss_counter += Math.min( codeword_digit_count[d], guess_digit_count[d] );
                }

                // Check if the guess is correct
                if ( hit_counter == ( GUESS_LENGTH + length_level ) )
                {
                    System.out.printf( "You've got it! Level %d completed! \n", level_counter );
                    continue;
                }

                System.out.printf( "%d hits, %d misses. \n", hit_counter, miss_counter );
                rounds_counter++;

            } // // End of `hit_counter` while-loop

            // Ask the users if they want to replay.
            System.out.print( "\nAnother game (y/n)? " );
            replay_decision = keyboard.next().toLowerCase();

            if ( replay_decision.charAt( 0 ) != 'y' )
            {
                System.out.printf( "\nThank you for playing! You reached level %d! \n", level_counter );
            }

            // If the user wants to replay, ask the user if they want to increase the level of difficulty.
            if ( ( replay_decision.charAt( 0 ) == 'y' ) && ( hit_counter == ( GUESS_LENGTH + length_level ) ) )
            {
                System.out.print( "Do you want to increase the level (y/n)? " );
                level_increase_decision = keyboard.next().toLowerCase();

                // If the user chooses to increase the level of difficulty, update the `level_counter` and
                // `digit_level`.
                if ( level_increase_decision.charAt( 0 ) == 'y' )
                {
                    level_counter++;

                    if ( digit_level < ( 9 - RANDOM_MAX ) )
                    {
                        digit_level++;
                    }

                    length_level++;
                }

            } // End of level increase if-statement

            System.out.println();

        } // End of `replay_decision` while-loop

    } // End of `playMastermindGame(Scanner in)`

    public static void printGameHeader( int level_counter )
    {
        System.out.println( "---- Let's play the game of Mastermind. ----" );
        System.out.printf( "----------------[ Level %d ]-----------------\n", level_counter );

    } // End of printGameHeader(int level_counter)

    public static void revealCodeword( Scanner in, String codeword )
    {
        System.out.print( "Reveal the codeword (y/n)? " );
        String reveal_codeword_decision = in.next().toLowerCase();

        if ( reveal_codeword_decision.charAt( 0 ) == 'y' )
        {
            System.out.printf( "The codeword is %s. \n", codeword );
        }

    } // End of revealCodeword(Scanner in, String codeword)

} // End of `MastermindPart1DifficultyVersionA`
