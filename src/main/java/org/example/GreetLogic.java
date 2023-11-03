package org.example;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.awt.*;


/**
 *
 * This class represents a Greet object, that is responsible
 * for taking care about greetings from given user.
 *
 * The main action the object performs are:
 *
 * - determine if player/user is in current leaderboard
 * - look for updating the player's scores (greetings)
 * - compute top10 players for current greeting
 *
 *
 *
 */

public class GreetLogic {


    // Emoji containing the greeting types
    public final static String night = "\uD83C\uDF19";
    public final static String af = "⛅";
    public final static String morn = "☀\uFE0F";


    /**
     * Default Constructor that creates Greet Logic object
     */

    public GreetLogic(){

    }


    /**
     * This method is responsible for verifying if the user is present in the leaderboard
     * @param collection collection of discord users
     * @param discordId the target discord user
     * @return boolean value containing the answer
     */

    public boolean userPresentNormalGreet(MongoCollection<Document> collection, String discordId){
        Document query = new Document("Discordid", discordId);
        FindIterable<Document> result = collection.find(query);
        return result.iterator().hasNext();
    }


    /**
     *
     * This method is responsible setting the target field for given
     * target value for a specific Discord user, in the comparison to the
     * current collection of Discord users.
     *
     * @param DiscordId the target (current Discord user)
     * @param value the value for new greeting score
     * @param fieldName the field value for type of greeting
     * @param collection the collection of discord users
     */


    public void updatePlayer(String DiscordId, int value, String fieldName,
                              MongoCollection<Document> collection) {
        Document query = new Document("Discordid", DiscordId);

        if (collection.countDocuments() <= 0) {
            System.out.println("Player not found: " + DiscordId);
            return;
        }

        collection.updateOne(query, Updates.inc(fieldName, value));

    }


    /**
     * This method is responsible polling the top 10 players with the highest
     * GM, GA, GN scores compared to the rest of the players in the collection.
     * @param playerCollection collection of users
     * @return formatted string containing detailed top10 information
     */

    public String getTop10Players(MongoCollection<Document> playerCollection) {

        Bson sort = Document.parse("{ 'gn': -1, 'ga': -1, 'gm': -1, 'DiscordId': 1, 'name': 1 }");

        // Fetch the top 10 players
        FindIterable<Document> topPlayers = playerCollection.find()
                .sort(sort)
                .limit(10);

        StringBuilder leaderboardInfo = new StringBuilder();
        int rank = 1;

        for (Document player : topPlayers) {
            String name = player.getString("name");
            int gn = player.getInteger(Greetings.GOOD_NIGHT.toString());
            int ga = player.getInteger(Greetings.GOOD_AFTERNOON.toString());
            int gm = player.getInteger(Greetings.GOOD_MORNING.toString());

            String playerInfo = String.format("**Rank %d: %s" + GreetLogic.night + "Good Nights: %d," + GreetLogic.af + "Good Afternoons: %d," + GreetLogic.morn + "Good Mornings: %d** \n", rank, name, gn, ga, gm);
            leaderboardInfo.append(playerInfo);
            rank++;
        }

        return leaderboardInfo.toString();
    }

    /**
     * Generate a greeting profile for this user who runs the command, uses helper
     * function getPlayerProfile().
     * @param event Slash Command Event
     * @param users collection of users
     * @return a Discord Embed object containing user's greeting stats
     */


    public EmbedBuilder castProfileDirectly(
            SlashCommandInteractionEvent event,
            MongoCollection<Document> users) {

        EmbedBuilder builder = new EmbedBuilder();
        // getting the stats from helper function
        String generatedProfile = this.getPlayerProfile(event, users);

        // building the builder having this user info
        builder.setTitle(event.getUser().getName() + "'s Greeting Profile")
                .setColor(Color.BLUE)
                .setFooter("Today's Greeting Profile")
                .setThumbnail(event.getUser().getAvatarUrl())
                .setDescription(generatedProfile);

        return builder;

    }


    /**
     * This method is responsible for getting the target player's stats in raw form
     * to give it to castProfileDirectly() method to parse it into Embed object.
     *
     * @param event Slash Command Event
     * @param playerCollection collection of users
     * @return raw String containing user stats.
     */

    public String getPlayerProfile(SlashCommandInteractionEvent event, MongoCollection<Document> playerCollection) {

        // checking for this user in the mongo database collection
        Bson filter = Filters.eq("Discordid", event.getUser().getId());

        // the first search of player doc
        Document playerDocument = playerCollection.find(filter).first();

        // get all sort of data for this player
        if (playerDocument != null) {
            int gn = playerDocument.getInteger(Greetings.GOOD_NIGHT.toString());
            int ga = playerDocument.getInteger(Greetings.GOOD_AFTERNOON.toString());
            int gm = playerDocument.getInteger(Greetings.GOOD_MORNING.toString());
            String name = playerDocument.getString("name");

            // format and return
            return String.format("**%s's Profile \n [" + GreetLogic.night +" Good Nights: %d \n " + GreetLogic.af + " Good Afternoons: %d \n" + GreetLogic.morn + " Good Mornings: %d ]** \n", name, gn, ga, gm);
        } else {

            return "User not present! Can't generate proper Greetings profile!";
        }
    }










}
